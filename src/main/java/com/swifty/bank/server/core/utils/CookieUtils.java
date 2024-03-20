package com.swifty.bank.server.core.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {
    @Value("${domain}")
    private static String domainAddr;

    public static ResponseCookie createCookie(String name, String token, Long maxAge) {

        return ResponseCookie.from(name, token)
                .httpOnly(false)
                .maxAge(maxAge)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();
    }
}
