package com.swifty.bank.server.core.config;

import com.swifty.bank.server.core.common.authentication.JwtAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class JwtAuthenticationInterceptorConfig implements WebMvcConfigurer {
    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/**", "/graphiql", "/graphql",
                        "/swagger-ui/**", "/api-docs", "/swagger-ui-custom.html",
                        "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html");
    }
}
