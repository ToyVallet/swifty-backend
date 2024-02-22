package com.swifty.bank.server.config;

import com.swifty.bank.server.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class JwtInterceptorConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/**", "/graphiql", "/graphql",
                        "/swagger-ui/**", "/api-docs", "/swagger-ui-custom.html",
                        "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html");
    }
}
