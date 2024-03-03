package com.swifty.bank.server.interceptor;

import com.swifty.bank.server.api.controller.annotation.CustomerAuth;
import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.controller.annotation.TemporaryAuth;
import com.swifty.bank.server.core.common.authentication.service.AuthenticationService;
import com.swifty.bank.server.core.utils.JwtUtil;
import com.swifty.bank.server.exception.authentication.NotLoggedInCustomerException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    private final AuthenticationService authenticationService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
        // 화이트리스트로 preHandle 관리
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
    }

    private void validateCustomerAuth() {
        String accessToken = JwtUtil.extractJwtFromCurrentRequestHeader();
        JwtUtil.validateToken(accessToken);
        if (authenticationService.isLoggedOut(
                UUID.fromString(JwtUtil.getClaimByKey(accessToken, "customerId").toString())
        )) {
            throw new NotLoggedInCustomerException("로그아웃 상태의 토큰입니다.");
        }
    }

    private <A extends Annotation> boolean hasProperAnnotation(Object handler, Class<A> annotation) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.getMethodAnnotation(annotation) != null
                || handlerMethod.getBeanType().getAnnotation(annotation) != null) {
            return true;
        }

        return false;
    }
}