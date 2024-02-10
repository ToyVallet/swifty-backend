package com.swifty.bank.server.core.common.authentication;

import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.authentication.exception.StoredAuthValueNotExistException;
import com.swifty.bank.server.core.common.authentication.exception.TokenContentNotValidException;
import com.swifty.bank.server.core.common.authentication.exception.TokenExpiredException;
import com.swifty.bank.server.core.common.authentication.exception.TokenFormatNotValidException;
import com.swifty.bank.server.utils.JwtUtil;
import com.swifty.bank.server.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if (checkAnnotation(handler, PassAuth.class)) {
            res.setStatus(200);
            return true;
        }

        // IndexOutOfBound error expected
        String token = req.getHeader("Authorization").split(" ")[1].trim();

        try {
            if (!jwtUtil.getSubjectFromToken(
                    token
            ).equals("ACCESS")) {
                res.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "[ERROR] token is not valid -> not access token value"
                );
            }

            UUID uuid = UUID.fromString(jwtUtil.getClaimByKeyFromToken(
                    "id",
                    req.getHeader("Authorization")
            ).toString());

            if (isLoggedOut(uuid.toString())) {
                res.sendError(
                        HttpServletResponse.SC_OK,
                        "[ERROR] Tried with token which is logged out"
                );
                return false;
            }

            res.setStatus(200);
            return true;
        } catch (TokenFormatNotValidException e) {
            res.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    e.getMessage()
            );
        } catch (TokenContentNotValidException e) {
            res.sendRedirect("/auth/reissue");
        } catch (TokenExpiredException e) {
            res.sendError(
                    HttpServletResponse.SC_OK,
                    e.getMessage()
            );
        } catch (IndexOutOfBoundsException e) {
            res.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        }
        return false;
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
        if (res == null) {
            throw new StoredAuthValueNotExistException("[ERROR] No value referred by those key");
        }
        return res.isLoggedOut();
    }
}
