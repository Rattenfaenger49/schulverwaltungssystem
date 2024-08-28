package com.school_system.auth;

import com.school_system.config.Databases.TenantContext;
import com.school_system.service.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
@Order(1)
@AllArgsConstructor
public class JwtTokenVerifierFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    // TODO seperate tenant filter from jwtFilter specialy for request without tenantId
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);


        // TODO diff between request that dose not required credentials and that dose
        // TODO like example logout and login with header and without
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.substring(7);
        if(token.isBlank() || token.length() < 20){
            throw new JwtException("Token is blank or incorrect");
        }
        try{
            Jws<Claims> claimsJws = jwtService.validateToken(token, false);
            Claims body = claimsJws.getBody();
            // Set the tenant context based on the JWT claims
            String tenantIdPayload = (String) body.get("tenantId");
            String tenantIdHeader = request.getHeader("X-TenantID");
            if(tenantIdHeader == null || !tenantIdHeader.equals(tenantIdPayload)) {
                throw new JwtException("Die Sitzung ist ungültig. Bitte melden Sie sich erneut an.");
            }
            TenantContext.setCurrentTenant(tenantIdHeader);

            String authorities = (String) body.get("roles");
            //String roles = (String) body.get("roles");
            if(authorities == null) {
                throw new MissingClaimException(claimsJws.getHeader(), body, "Die Sitzung ist ungültig.");
            }
            String[] a  = authorities.split(" ");
            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = Arrays.stream(a).sequential()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    body,
                    token,
                    simpleGrantedAuthorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            throw new JwtException("Die Sitzung ist abgelaufen.");

        } catch (JwtException e){
            log.error("An error occurred while processing the JWT token", e);
            throw new JwtException("Ein Fehler ist beim Verarbeiten des Sitzungs-Tokens aufgetreten. Loggen Sie sich bitte nochmal ein!");
        }
        finally {
            // Clear the tenant context after processing
            TenantContext.clear();
        }

        filterChain.doFilter(request, response);
    }


}

