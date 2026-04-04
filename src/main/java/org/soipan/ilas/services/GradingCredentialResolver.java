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

    private final String githubEndpointUrl;
    private final String githubApiKey;
    private final String githubModel;

    public GradingCredentialResolver(
            @Value("${grading.llm.provider:generic}") String provider,
            @Value("${grading.llm.endpoint-url:}") String endpointUrl,
            @Value("${grading.llm.api-key:}") String apiKey,
            @Value("${grading.llm.model:gpt-4o-mini}") String model,
            @Value("${grading.llm.timeout-seconds:30}") long timeoutSeconds,
            @Value("${grading.llm.temperature:0.0}") double temperature,
            @Value("${grading.llm.github.endpoint-url:https://models.inference.ai.azure.com/chat/completions}") String githubEndpointUrl,
            @Value("${grading.llm.github.api-key:}") String githubApiKey,
            @Value("${grading.llm.github.model:openai/gpt-4.1-mini}") String githubModel) {
        this.provider = provider;
        this.endpointUrl = endpointUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.timeoutSeconds = timeoutSeconds;
        this.temperature = temperature;
        this.githubEndpointUrl = githubEndpointUrl;
        this.githubApiKey = githubApiKey;
        this.githubModel = githubModel;
    }

    public ResolvedGradingConfig resolve() {
        boolean githubModels = isGithubModelsProvider(provider);

        String resolvedEndpoint = githubModels
                ? firstNonBlank(githubEndpointUrl, endpointUrl)
                : endpointUrl;

        String resolvedApiKey = githubModels
                ? firstNonBlank(githubApiKey, apiKey)
                : apiKey;

        String resolvedModel = githubModels
                ? firstNonBlank(githubModel, model)
                : model;

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
                githubModels ? "github-models" : "generic",
                resolvedEndpoint,
                resolvedApiKey,
                resolvedModel,
                Math.max(1, timeoutSeconds),
                temperature
        );
    }

    private boolean isGithubModelsProvider(String providerValue) {
        if (providerValue == null) {
            return false;
        }
        String normalized = providerValue.trim().toLowerCase();
        return normalized.equals("github")
                || normalized.equals("github-models")
                || normalized.equals("gh-models");
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

