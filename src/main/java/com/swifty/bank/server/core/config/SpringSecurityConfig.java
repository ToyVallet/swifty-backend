package com.swifty.bank.server.core.config;

import com.swifty.bank.server.core.domain.customer.repository.CustomerRepository;
import com.swifty.bank.server.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {
    // After Spring Security 6.0 You need to register bean of component-based security settings
    private final CustomerRepository customerRepository;
    private final JwtTokenUtil jwtTokenUtil;

    // HttpSecurity
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) ->
                        auth
                                .anyRequest().permitAll()
                )
                .formLogin((l) -> l.disable())
                .httpBasic((hb) -> hb.disable())
                .logout((l) -> l.disable()
                );
        http.cors((cors) -> cors.disable());
        http.csrf((csrf) -> csrf.disable());
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}