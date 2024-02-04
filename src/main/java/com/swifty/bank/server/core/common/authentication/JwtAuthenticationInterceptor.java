package com.swifty.bank.server.core.common.authentication;

import com.swifty.bank.server.core.common.authentication.annotation.PassAuth;
import com.swifty.bank.server.core.common.authentication.exception.TokenContentNotValidException;
import com.swifty.bank.server.core.common.authentication.exception.TokenFormatNotValidException;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.exceptions.NoSuchCustomerByUUID;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomerService customerService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if (checkAnnotation(handler, PassAuth.class)) {
            res.setStatus(200);
            return true;
        }

        try {
            String uri = req.getRequestURI();
            UUID uuid = jwtTokenUtil.getUuidFromToken(
                    req.getHeader("Authorization")
            );

            Customer customer = customerService.findByUuid(uuid);
            res.setStatus(200);
            return true;
        }
        catch (TokenFormatNotValidException e) {
            res.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        }
        catch (TokenContentNotValidException e) {
            res.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage( )
            );
        }
        catch (NoSuchCustomerByUUID e) {
            res.sendError(
                    HttpServletResponse.SC_OK,
                    e.getMessage( )
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
}
