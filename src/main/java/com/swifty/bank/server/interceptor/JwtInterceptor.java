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

        Cookie[] cookies = extractCookiesFromCurrentRequestHeader();
        // 임시 회원가입 권한
        if (hasProperAnnotation(handler, TemporaryAuth.class)) {
            validateTemporaryAuth(cookies);
            return true;
        }

        // 로그인된 일반 고객 권한
        if (hasProperAnnotation(handler, CustomerAuth.class)) {
            validateCustomerAuth(cookies);
            return true;
        }

        // 이외의 경우 거절
        return false;
    }

    private void validateTemporaryAuth(Cookie[] cookies) {
        Cookie cookie = extractCookieByName(cookies, "temporary-token");
        if (cookie == null) {
            throw new IllegalArgumentException("temporary token이 존재하지 않습니다.");
        }

        String temporaryToken = cookie.getValue();

        JwtUtil.validateToken(temporaryToken);
        // subject가 temporary-token인지 검증
        String sub = JwtUtil.getSubject(temporaryToken);
        if (!sub.equals("temporary-token")) {
            throw new IllegalArgumentException(temporaryToken + ": temporary token이 아닙니다.");
        }
    }

    private void validateCustomerAuth(Cookie[] cookies) {
        Cookie cookie = extractCookieByName(cookies, "access-token");

        if (cookie == null) {
            throw new IllegalArgumentException("access token이 존재하지 않습니다.");
        }

        String accessToken = cookie.getValue();

        JwtUtil.validateToken(accessToken);
        // subject가 access-token인지 검증
        String sub = JwtUtil.getSubject(accessToken);
        if (!sub.equals("access-token")) {
            throw new IllegalArgumentException(accessToken + ": access token이 아닙니다.");
        }
        // customerUuid가 존재하는지 검증
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

    private Cookie[] extractCookiesFromCurrentRequestHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getCookies();
    }

    private Cookie extractCookieByName(Cookie[] cookies, String cookieName) {
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}