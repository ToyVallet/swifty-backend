package com.swifty.bank.server.interceptor;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenRedisService;
import com.swifty.bank.server.core.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    private final LogoutAccessTokenRedisService logoutAccessTokenRedisService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        log.info("interceptor={}",req.getRequestURL().toString());
        // 화이트 리스트로 preHandle 관리
        // 프리패스 권한
        if (hasProperAnnotation(handler, PassAuth.class)) {
            return true;
        }

        // 임시 회원가입 권한
        if (hasProperAnnotation(handler, TemporaryAuth.class)) {
            validateTemporaryAuth(extractJwtFromCurrentRequestHeader());
            return true;
        }

        // 로그인된 일반 고객 권한
        if (hasProperAnnotation(handler, CustomerAuth.class)) {
            validateCustomerAuth(extractJwtFromCurrentRequestHeader());
            return true;
        }

        // 이외의 경우 거절
        return false;
    }

    private void validateTemporaryAuth(String temporaryToken) {
        JwtUtil.validateToken(temporaryToken);
        // TemporaryToken인지 검증
        String sub = JwtUtil.getSubject(temporaryToken);
        if (!sub.equals("TemporaryToken")) {
            throw new IllegalArgumentException("temporary token이 아닙니다.");
        }
    }

    private void validateCustomerAuth(String accessToken) {
        JwtUtil.validateToken(accessToken);
        // AccessToken인지 검증
        String sub = JwtUtil.getSubject(accessToken);
        if (!sub.equals("AccessToken")) {
            throw new IllegalArgumentException("access token이 아닙니다.");
        }

        // customerUuid가 claim에 존재하는지 검증
        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

        // 로그아웃 요청한 AccessToken인지 검증
        String isLoggedOut = logoutAccessTokenRedisService.getData(accessToken);
        if (isLoggedOut != null && isLoggedOut.equals("false")) {
            throw new IllegalArgumentException("로그아웃된 access token입니다.");
        }
    }

    private <A extends Annotation> boolean hasProperAnnotation(Object handler, Class<A> annotation) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getMethodAnnotation(annotation) != null
                    || handlerMethod.getBeanType().getAnnotation(annotation) != null;
        }
        return false;
    }

    private String extractJwtFromCurrentRequestHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        try {
            String token = JwtUtil.removeType(request.getHeader("Authorization"));
            JwtUtil.validateToken(token);
            return token;
        } catch (Exception e) {
            throw new IllegalArgumentException("Authorization 헤더에 올바른 jwt가 존재하지 않습니다.");
        }
    }
}