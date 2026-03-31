package org.soipan.ilas.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.soipan.ilas.services.SystemMonitoringService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Tracks concurrent request load for outward-facing API endpoints.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class ApiRequestTrackingFilter extends OncePerRequestFilter {

    private final SystemMonitoringService monitoringService;

    public ApiRequestTrackingFilter(SystemMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String normalized = normalizeEndpoint(uri);
        monitoringService.onRequestStart(normalized);

        try {
            filterChain.doFilter(request, response);
        } finally {
            monitoringService.onRequestEnd(normalized);
        }
    }

    private String normalizeEndpoint(String uri) {
        // Collapse variable IDs/UUIDs to a stable key for dashboard grouping.
        return uri
                .replaceAll("/[0-9]+", "/{id}")
                .replaceAll("/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}", "/{id}");
    }
}

