package com.swifty.bank.server.interceptor;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.core.common.redis.service.LogoutAccessTokenRedisService;
import com.swifty.bank.server.core.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
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
        log.info("interceptor={}", req.getRequestURL().toString());
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

    private void validateTemporaryAuth(Cookie[] cookies) {
        String temporaryToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("temporary-token")) {
                temporaryToken = cookie.getValue();
                break;
            }
        }
        if (temporaryToken.isEmpty()) {
            throw new IllegalArgumentException("temporary token이 아닙니다.");
        }
        JwtUtil.validateToken(temporaryToken);
        // TemporaryToken인지 검증
        String sub = JwtUtil.getSubject(temporaryToken);
    }

    private void validateCustomerAuth(Cookie[] cookies) {
        String accessToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access-token")) {
                accessToken = cookie.getValue();
                break;
            }
        }
        if (accessToken.isEmpty()) {
            throw new IllegalArgumentException("access token이 아닙니다.");
        }
        JwtUtil.validateToken(accessToken);
        String sub = JwtUtil.getSubject(accessToken);

        UUID customerUuid = JwtUtil.getValueByKeyWithObject(accessToken, "customerUuid", UUID.class);

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

    private Cookie[] extractJwtFromCurrentRequestHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Cookie[] cookies = request.getCookies();
        try {
            for (Cookie cookie : cookies) {
                String token = JwtUtil.removeType(cookie.getValue());
                JwtUtil.validateToken(token);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Coockie에 올바른 jwt가 존재하지 않습니다.");
        }
        return cookies;
    }
}