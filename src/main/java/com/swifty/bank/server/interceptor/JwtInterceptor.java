package com.swifty.bank.server.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swifty.bank.server.api.controller.annotation.PassAuth;
import com.swifty.bank.server.api.service.dto.ResponseResult;
import com.swifty.bank.server.api.service.dto.Result;
import com.swifty.bank.server.core.common.redis.entity.RefreshTokenCache;
import com.swifty.bank.server.core.common.redis.service.impl.RefreshTokenRedisServiceImpl;
import com.swifty.bank.server.core.common.utils.JwtUtil;
import com.swifty.bank.server.exception.StoredAuthValueNotExistException;
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
public class JwtInterceptor implements HandlerInterceptor {
    private final RefreshTokenRedisServiceImpl refreshTokenRedisService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
        try {
            if (checkAnnotation(handler, PassAuth.class)) {
                return true;
            }

            String accessToken = JwtUtil.extractJwtFromCurrentRequestHeader();
            JwtUtil.validateToken(accessToken);
            if (isLoggedOut(JwtUtil.getClaimByKey(accessToken, "customerId").toString())) {
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
        // Warning: redis에서만 검증하고 있습니다.
        RefreshTokenCache res = refreshTokenRedisService.getData(key);
        if (ObjectUtils.isEmpty(res)) {
            throw new StoredAuthValueNotExistException("[ERROR] No value referred by those key");
        }
        return res.getRefreshToken().equals("LOGOUT");
    }
}
