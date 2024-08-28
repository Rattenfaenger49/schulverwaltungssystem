package com.school_system.service.impl;

import com.school_system.config.Databases.TenantContext;
import com.school_system.config.properties.AuthenticationProperties;
import com.school_system.dto.CustomUserDetails;
import com.school_system.enums.authentication.RoleName;
import com.school_system.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final AuthenticationProperties authenticationProperties;




    @Override
    public String generateAccessToken(CustomUserDetails userDetails) {

        List<SimpleGrantedAuthority> authoritiesList = (List<SimpleGrantedAuthority>)userDetails.getAuthorities();

        String authorities = authoritiesList.stream().map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        Map<String, Object> tokenBody = new HashMap<>();
        tokenBody.put("id", userDetails.getId());
        tokenBody.put("firstName", userDetails.getFirstName());
        tokenBody.put("lastName", userDetails.getLastName());
        tokenBody.put("username", userDetails.getUsername());
        tokenBody.put("verified", userDetails.isVerified());
        tokenBody.put("authorities",  userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" ")));
        tokenBody.put("roles", userDetails.getRoles().stream().map(RoleName::getValue).collect(Collectors.joining(" ")));
        tokenBody.put("tenantId", TenantContext.getCurrentTenant() != null && !TenantContext.getCurrentTenant().isEmpty() ? TenantContext.getCurrentTenant() : "test-001" );

        byte[] key = authenticationProperties.getSecretKey().getBytes();
        Integer expiration = authenticationProperties.getAccessToken().getExpirationInSeconds();

        return this.buildJwt(tokenBody, expiration, key);
    }


    @Override
    public String generateRefreshToken(CustomUserDetails userDetails) {

        Map<String, Object> tokenBody = new HashMap<>();
        tokenBody.put("id", userDetails.getId());
        tokenBody.put("username", userDetails.getUsername());
        tokenBody.put("tenantId", TenantContext.getCurrentTenant() != null && !TenantContext.getCurrentTenant().isEmpty() ? TenantContext.getCurrentTenant() : "test-001" );

        byte[] key = authenticationProperties.getSecretKey().getBytes();
        Integer expiration = authenticationProperties.getRefreshToken().getExpirationInSeconds();
        return this.buildJwt(tokenBody, expiration, key);
    }

    @Override
    public Jws<Claims> validateToken(@NotBlank String token, boolean isRefreshToken) {
    /* // no need to check for the token in the DB
        if(isRefreshToken){
            tokenRepository.findByTokenAndRevoked(token, false).orElseThrow(() -> new JwtException("Token is invalid"));
        }
*/
        Key key = Keys.hmacShaKeyFor(authenticationProperties.getSecretKey().getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token);
    }

    private String buildJwt(Map<String, Object> body, Integer expiration, byte[] signingKey){

        try {
            return Jwts.builder()
                    .addClaims(body)
                    .setIssuedAt(new Date())
                    .setIssuer("self")
                    .setSubject((String)body.get("username"))
                    .setExpiration(java.sql.Timestamp.valueOf(LocalDateTime.now().plusSeconds(expiration)))
                    .signWith(Keys.hmacShaKeyFor(signingKey))
                    .compact();

        } catch (JwtException e){
            throw new JwtException(e.getMessage());
        }
    }
}
