package com.swifty.bank.server.core.common.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.authentication.exception.StoredAuthValueNotExistException;
import com.swifty.bank.server.core.common.constant.Result;
import com.swifty.bank.server.core.common.service.JwtService;
import com.swifty.bank.server.core.common.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
        try {
            if (checkAnnotation(handler, PassAuth.class)) {
                return true;
            }

            String jwtAccessToken = jwtService.getAccessToken();

            if (!jwtService.isValidateToken(jwtAccessToken)) {
                throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.");
            }

            if (jwtService.isExpiredToken(jwtAccessToken)) {
                throw new IllegalArgumentException("만료된 JWT 토큰입니다.");
            }

            if (isLoggedOut(jwtService.getCustomerId().toString())) {
                throw new IllegalArgumentException("로그아웃 상태의 토큰입니다.");
            }

            return true;
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();

            String failResult = mapper.writeValueAsString(ResponseResult.builder()
                    .result(Result.FAIL)
                    .message(e.getMessage())
                    .build());

            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.setCharacterEncoding("utf-8");
            res.getWriter().write(failResult);
            return false;
        }
    }

    private boolean checkAnnotation(Object handler, Class<PassAuth> passAuthClass) {
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        if (handlerMethod.getMethodAnnotation(passAuthClass) != null
                || null != handlerMethod.getBeanType().getAnnotation(passAuthClass)) {
            return true;
        }

        return false;
    }

    private boolean isLoggedOut(String key) {
        Auth res = redisUtil.getRedisAuthValue(key);
        if (ObjectUtils.isEmpty(res)) {
            throw new StoredAuthValueNotExistException("[ERROR] No value referred by those key");
        }
        return res.getRefreshToken().equals("LOGOUT");
    }
}
