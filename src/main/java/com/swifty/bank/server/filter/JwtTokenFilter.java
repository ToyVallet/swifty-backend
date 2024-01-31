package com.swifty.bank.server.filter;

import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomerRepository customerRepository;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil, CustomerRepository customerRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.customerRepository = customerRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/auth") || request.getRequestURI().equals("/sign_in")) {



        }

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isEmpty( ) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();
        if (!jwtTokenUtil.validateToken(token)) {
            chain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = customerRepository
                .findOneByUUID(UUID.fromString(jwtTokenUtil.getUuidFromToken(token)))
                .orElse(null);

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null,
                userDetails == null ?
                        List.of( ) : userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource( ).buildDetails(request));

        if (userDetails == null) {
            chain.doFilter(request, response);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
