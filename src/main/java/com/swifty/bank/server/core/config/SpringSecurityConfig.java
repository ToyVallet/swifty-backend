package com.swifty.bank.server.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {
    // After Spring Security 6.0 You need to register bean of component-based security settings

    // Datasource
    @Autowired
    private DataSource dataSource;  // replacement of Autowired (Field injection)

    // HttpSecurity
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) ->
                        auth
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/employees").hasRole("EMPLOYEE")
                                .requestMatchers("/leaders/**").hasRole("MANAGER")
                                .requestMatchers("/systems/**").hasRole("ADMIN")
                )
                .formLogin((login) ->
                        login.loginPage("/showLoginPage")
                                .loginProcessingUrl("/authenticateUser")
                                .permitAll()

                )
                .logout((logout) ->
                        logout.logoutSuccessUrl("/")
                                .permitAll()
                )
                .exceptionHandling((ex) ->
                        ex.accessDeniedPage("/access-denied")
                );
        return http.build();
    }

    // Web Security Setting -> ignore or allow specific URL
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer( ) {
        return (web) -> web.ignoring().requestMatchers("/ignore1", "ignore2");
    }

    // JDBC Settings
    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        UserDetails user = User
                .withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.createUser(user);
        return users;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder( ) {
        return new BCryptPasswordEncoder( );
    }
}
