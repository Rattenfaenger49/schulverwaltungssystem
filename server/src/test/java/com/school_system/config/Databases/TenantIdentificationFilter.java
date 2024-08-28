package com.school_system.config.Databases;

import com.school_system.exception.TenantException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
@Slf4j
@Component
public class TenantIdentificationFilter extends OncePerRequestFilter {



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract tenant information from the request header


        String tenantId = request.getHeader("X-TenantID");

        if (StringUtils.isBlank(tenantId)) {
            log.error("Die Mandanten-ID fehlt im Anforderungsheader.");
            throw new TenantException( "Die Mandanten-ID fehlt.");
        }
        if (!TenantRegistry.containsTenant(tenantId)) {
            log.error("Ungültige Mandanten-ID: {}", tenantId);
            throw new TenantException("Ungültige Mandanten-ID.");
        }
        // Set the current tenant based on the extracted tenant name
        TenantContext.setCurrentTenant(tenantId);

        try {
            // Proceed with the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Clear the tenant context after processing
            TenantContext.clear();

        }
    }
}
