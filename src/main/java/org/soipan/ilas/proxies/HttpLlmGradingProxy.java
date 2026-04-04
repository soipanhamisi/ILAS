package org.soipan.ilas.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.soipan.ilas.services.GradingCredentialResolver;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP proxy for provider-agnostic LLM grading endpoints.
 */
@Service
public class HttpLlmGradingProxy implements LlmGradingProxy {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String provider;
    private final String endpointUrl;
    private final String apiKey;
    private final String model;
    private final long timeoutSeconds;
    private final double temperature;

    public HttpLlmGradingProxy(GradingCredentialResolver credentialResolver) {
        GradingCredentialResolver.ResolvedGradingConfig resolved = credentialResolver.resolve();
        this.objectMapper = new ObjectMapper();
        this.provider = resolved.provider();
        this.endpointUrl = resolved.endpointUrl();
        this.apiKey = resolved.apiKey();
        this.model = resolved.model();
        this.timeoutSeconds = resolved.timeoutSeconds();
        this.temperature = resolved.temperature();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(Math.max(1, timeoutSeconds)))
                .build();
    }

    @Override
    public LlmCompletion complete(String prompt) {
        if (endpointUrl == null || endpointUrl.isBlank()) {
            throw new IllegalStateException("grading.llm.endpoint-url is not configured");
        }

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", model);
            payload.put("temperature", temperature);
            payload.put("messages", List.of(
                    Map.of(
                            "role", "system",
                            "content", "You are a strict exam grading assistant. Return only valid JSON with overallScore, overallFeedback, overallJustification, requiresInstructorReview, and a questions array."
                    ),
                    Map.of(
                            "role", "user",
                            "content", prompt
                    )
            ));
            payload.put("response_format", Map.of("type", "json_object"));

            String requestBody = objectMapper.writeValueAsString(payload);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(endpointUrl))
                    .timeout(Duration.ofSeconds(Math.max(1, timeoutSeconds)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody));

            if (apiKey != null && !apiKey.isBlank()) {
                requestBuilder.header("Authorization", "Bearer " + apiKey.trim());
            }

            if ("github-models".equals(provider)) {
                requestBuilder.header("User-Agent", "ILAS-AutoGrader");
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("LLM grading endpoint returned HTTP " + response.statusCode());
            }

            String content = extractContent(response.body());
            return new LlmCompletion(model, content, response.body());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to complete LLM grading request", ex);
        }
    }

    private String extractContent(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "";
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode messageContent = choices.get(0).path("message").path("content");
                if (!messageContent.isMissingNode() && !messageContent.isNull()) {
                    return messageContent.asText();
                }
            }

            JsonNode content = root.path("content");
            if (!content.isMissingNode() && !content.isNull()) {
                return content.asText();
            }

            JsonNode response = root.path("response");
            if (!response.isMissingNode() && !response.isNull()) {
                return response.asText();
            }
        } catch (Exception ignored) {
            // Fall back to raw response body.
        }

        return responseBody;
    }
}

