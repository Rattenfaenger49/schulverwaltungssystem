package com.school_system.util;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

@Slf4j
public class CookiesUtility{

    public static void addResponseCookies(HttpServletResponse response, String name, String value, Integer exp) {
        ResponseCookie cookie = ResponseCookie.from(name, value )
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(exp)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

    }
    public static void removeResponseCookies(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
