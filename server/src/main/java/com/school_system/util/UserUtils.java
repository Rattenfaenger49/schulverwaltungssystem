package com.school_system.util;

import com.school_system.enums.authentication.RoleName;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.List;
import java.util.stream.Collectors;
@Slf4j
public class UserUtils {

    public static String getUsername(Authentication authentication) {
        if(!authentication.isAuthenticated())
            return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Claims userDetails) {
            // If the principal is of type Claims (typically from JWT authentication)
            return userDetails.get("username", String.class);
        } else if (principal instanceof String) {
            // If the principal is a String (typically the username or user identifier)
            return (String) principal;
        } else {
            // Handle other types of principal if needed
            return null;
        }
    }

    public static boolean hasRole(Authentication authentication, RoleName roleName) {
        List<String> roles = getRoles(authentication);

        return roles.contains(roleName.getValue());
    }
    public static List<String> getRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public static String getUserFullName(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Claims userDetails = (Claims) principal;
        return userDetails.get("firstName").toString() + " " + userDetails.get("lastName").toString();
    }

    public static Long getUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Claims userDetails = (Claims) principal;
        return Long.parseLong(userDetails.get("id").toString());
    }



    public static String getClientIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
