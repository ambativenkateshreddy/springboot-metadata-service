package com.saas.metadata.filter;

import com.saas.metadata.context.TenantContext;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class TenantResolutionFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    // Read tenants from YAML OR use default values
    @Value("${multitenancy.tenants:tenant_acme,tenant_globex,tenant_initech}")
    private String tenants;

    private List<String> validTenants;

    // Convert comma-separated string → List
    @PostConstruct
    public void init() {
        validTenants = Arrays.stream(tenants.split(","))
                             .map(String::trim)   // remove spaces
                             .toList();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String tenantId = request.getHeader(TENANT_HEADER);

        // ❌ Missing header
        if (tenantId == null || tenantId.isBlank()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Missing required header: " + TENANT_HEADER);
            return;
        }

        // ❌ Invalid tenant
        if (!validTenants.contains(tenantId)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unknown tenant: " + tenantId);
            return;
        }

        try {
            // ✅ Set tenant
            TenantContext.setCurrentTenant(tenantId);

            // Continue request
            filterChain.doFilter(request, response);

        } finally {
            // ✅ Always clear (important)
            TenantContext.clear();
        }
    }

    // Skip filter for actuator endpoints
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/health");
    }

    // Helper method for sending JSON error response
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}