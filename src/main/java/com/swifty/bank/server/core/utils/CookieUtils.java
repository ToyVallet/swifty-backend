package com.swifty.bank.server.core.utils;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {
    @Value("${domain}")
    private static String domainAddr;

    public static ResponseCookie createCookie(String name, String token) {
        ResponseCookie cookie = ResponseCookie.from(name, token)
                .httpOnly(false)
                .maxAge(60 * 60 * 12)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();

        return cookie;
    }
}
