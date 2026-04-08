package org.soipan.ilas.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Discovers request-mapped endpoints so monitoring can display health for all client-facing routes.
 */
@Service
public class EndpointCatalogService {

    private final RequestMappingHandlerMapping handlerMapping;

    public EndpointCatalogService(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public List<String> getClientFacingEndpoints() {
        Set<String> endpoints = new TreeSet<>();

        for (RequestMappingInfo mappingInfo : handlerMapping.getHandlerMethods().keySet()) {
            for (String pattern : mappingInfo.getPatternValues()) {
                if (shouldInclude(pattern)) {
                    endpoints.add(normalizePattern(pattern));
                }
            }
        }

        return endpoints.stream().toList();
    }

    private boolean shouldInclude(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return false;
        }

        return !pattern.startsWith("/actuator")
                && !pattern.startsWith("/api/admin")
                && !pattern.startsWith("/error")
                && !pattern.startsWith("/v3/api-docs")
                && !pattern.startsWith("/swagger-ui");
    }

    private String normalizePattern(String pattern) {
        // Align Spring path variables with runtime normalization used by request tracking.
        return pattern.replaceAll("\\{[^/]+}", "{id}");
    }
}
