package org.soipan.ilas.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Resolves runtime LLM credentials/settings with provider-aware fallback rules.
 */
@Component
public class GradingCredentialResolver {

    private final String provider;
    private final String endpointUrl;
    private final String apiKey;
    private final String model;
    private final long timeoutSeconds;
    private final double temperature;

    private final String geminiEndpointUrl;
    private final String geminiApiKey;
    private final String geminiModel;

    public GradingCredentialResolver(
            @Value("${grading.llm.provider:generic}") String provider,
            @Value("${grading.llm.endpoint-url:}") String endpointUrl,
            @Value("${grading.llm.api-key:}") String apiKey,
            @Value("${grading.llm.model:gpt-4o-mini}") String model,
            @Value("${grading.llm.timeout-seconds:30}") long timeoutSeconds,
            @Value("${grading.llm.temperature:0.0}") double temperature,
            @Value("${grading.llm.gemini.endpoint-url:https://generativelanguage.googleapis.com/v1beta/models}") String geminiEndpointUrl,
            @Value("${grading.llm.gemini.api-key:}") String geminiApiKey,
            @Value("${grading.llm.gemini.model:gemini-2.0-flash}") String geminiModel) {
        this.provider = provider;
        this.endpointUrl = endpointUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.timeoutSeconds = timeoutSeconds;
        this.temperature = temperature;
        this.geminiEndpointUrl = geminiEndpointUrl;
        this.geminiApiKey = geminiApiKey;
        this.geminiModel = geminiModel;
    }

    public ResolvedGradingConfig resolve() {
        boolean geminiStudio = isGeminiStudioProvider(provider);

        String resolvedEndpoint = endpointUrl;
        String resolvedApiKey = apiKey;
        String resolvedModel = model;

        if (geminiStudio) {
            resolvedEndpoint = normalizeGeminiEndpoint(firstNonBlank(geminiEndpointUrl, endpointUrl),
                    firstNonBlank(geminiModel, model));
            resolvedApiKey = firstNonBlank(geminiApiKey, apiKey);
            resolvedModel = firstNonBlank(geminiModel, model);
        }

        if (resolvedEndpoint == null || resolvedEndpoint.isBlank()) {
            throw new IllegalStateException("LLM endpoint URL is not configured");
        }
        if (resolvedApiKey == null || resolvedApiKey.isBlank()) {
            throw new IllegalStateException("LLM API key is not configured");
        }
        if (resolvedModel == null || resolvedModel.isBlank()) {
            throw new IllegalStateException("LLM model is not configured");
        }

        return new ResolvedGradingConfig(
                geminiStudio ? "gemini-studio" : "generic",
                resolvedEndpoint,
                resolvedApiKey,
                resolvedModel,
                Math.max(1, timeoutSeconds),
                temperature
        );
    }


    private boolean isGeminiStudioProvider(String providerValue) {
        if (providerValue == null) {
            return false;
        }
        String normalized = providerValue.trim().toLowerCase();
        return normalized.equals("gemini")
                || normalized.equals("gemini-studio")
                || normalized.equals("google-ai-studio")
                || normalized.equals("ai-studio");
    }

    private String normalizeGeminiEndpoint(String endpoint, String resolvedModel) {
        if (endpoint == null || endpoint.isBlank()) {
            return endpoint;
        }
        String modelName = (resolvedModel == null || resolvedModel.isBlank()) ? "gemini-2.0-flash" : resolvedModel;
        String normalized = endpoint.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (normalized.contains(":generateContent")) {
            return normalized;
        }
        if (normalized.matches(".*/models/[^/]+$")) {
            return normalized + ":generateContent";
        }
        if (normalized.endsWith("/models")) {
            return normalized + "/" + modelName + ":generateContent";
        }
        return normalized + "/models/" + modelName + ":generateContent";
    }

    private String firstNonBlank(String first, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return fallback;
    }

    public record ResolvedGradingConfig(
            String provider,
            String endpointUrl,
            String apiKey,
            String model,
            long timeoutSeconds,
            double temperature
    ) {
    }
}

