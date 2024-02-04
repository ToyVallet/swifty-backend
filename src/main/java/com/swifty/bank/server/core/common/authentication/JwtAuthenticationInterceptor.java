package com.swifty.bank.server.core.common.authentication;

import com.swifty.bank.server.core.common.authentication.exception.TokenContentNotValidException;
import com.swifty.bank.server.core.common.authentication.exception.TokenFormatNotValidException;
import com.swifty.bank.server.core.domain.customer.Customer;
import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.core.domain.customer.service.CustomerService;
import com.swifty.bank.server.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomerService customerService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        try {
            UUID uuid = jwtTokenUtil.getUuidFromToken(
                    req.getHeader("Authentication")
            );

            Customer customer = customerService.findByUuid(uuid);
            res.sendError(
                    HttpServletResponse.SC_OK,
                    "[ERROR] Customer doesn't exist"
            );
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
        return false;
    }
}
