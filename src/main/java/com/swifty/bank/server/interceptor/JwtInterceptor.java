package com.swifty.bank.server.interceptor;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenRedisService;
import com.swifty.bank.server.core.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    private final LogoutAccessTokenRedisService logoutAccessTokenRedisService;
    private final AuthenticationService authenticationService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        // 리소스 접근을 위한 요청은 JwtInterceptor에서 처리하지 않습니다.
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        // 화이트 리스트로 preHandle 관리
        // 프리패스 권한
        if (hasProperAnnotation(handler, PassAuth.class)) {
            return true;
        }

        // 임시 회원가입 권한
        if (hasProperAnnotation(handler, TemporaryAuth.class)) {
            validateTemporaryAuth(handler, req);
            return true;
        }

        // 로그인된 일반 고객 권한
        if (hasProperAnnotation(handler, CustomerAuth.class)) {
            validateCustomerAuth();
            return true;
        }

        // 이외의 경우 거절
        return false;
    }

    private void validateTemporaryAuth(Object handler, HttpServletRequest req) {
        String temporaryToken = JwtUtil.extractJwtFromCurrentRequestHeader();
        JwtUtil.validateToken(temporaryToken);
        // temporary token인지 검증
        String sub = JwtUtil.getSubject(temporaryToken);
        if (!sub.equals("TemporaryToken")) {
            throw new IllegalArgumentException("temporary token이 아닙니다.");
        }
    }

    private void validateCustomerAuth() {
        String accessToken = JwtUtil.extractJwtFromCurrentRequestHeader();
        JwtUtil.validateToken(accessToken);
        // Access Token인지 검증
        String sub = JwtUtil.getSubject(accessToken);
        if (!sub.equals("AccessToken")) {
            throw new IllegalArgumentException("access token이 아닙니다.");
        }

        // customerUuid가 claim에 존재하는지 검증
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        // 로그아웃 요청한 access token인지 검증
        String isLoggedOut = logoutAccessTokenRedisService.getData(accessToken);
        if (isLoggedOut != null && isLoggedOut.equals("false")) {
            throw new IllegalArgumentException("로그아웃된 access token입니다.");
        }
        authenticationService.findLogoutAccessToken(accessToken).ifPresent(logoutAccessToken -> {
            if (logoutAccessToken.getIsLoggedIn().equals("false")) {
                throw new IllegalArgumentException("로그아웃된 access token입니다.");
            }
        });
    }

    private <A extends Annotation> boolean hasProperAnnotation(Object handler, Class<A> annotation) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getMethodAnnotation(annotation) != null
                    || handlerMethod.getBeanType().getAnnotation(annotation) != null;
        }
        return false;
    }
}