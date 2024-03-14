package com.swifty.bank.server.core.utils;

import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {
    public static ResponseCookie createCookie(String name, String token) {
        ResponseCookie cookie = ResponseCookie.from(name, token)
                .httpOnly(false)
                .maxAge(60 * 60 * 12)
                .domain("localhost")
                .secure(true)
                .path("/")
                .build();

        return cookie;
    }
}
