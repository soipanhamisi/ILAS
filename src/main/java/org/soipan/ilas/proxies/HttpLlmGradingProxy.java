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

    private final GradingCredentialResolver credentialResolver;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpLlmGradingProxy(GradingCredentialResolver credentialResolver) {
        this.credentialResolver = credentialResolver;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder().build();
    }

    @Override
    public LlmCompletion complete(String prompt) {
        GradingCredentialResolver.ResolvedGradingConfig resolved = credentialResolver.resolve();
        String endpointUrl = resolved.endpointUrl();
        String apiKey = resolved.apiKey();
        String model = resolved.model();
        long timeoutSeconds = resolved.timeoutSeconds();
        double temperature = resolved.temperature();
        String provider = resolved.provider();

        if (endpointUrl == null || endpointUrl.isBlank()) {
            throw new IllegalStateException("grading.llm.endpoint-url is not configured");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("LLM API key is not configured");
        }

        boolean geminiStudio = "gemini-studio".equals(provider);

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            if (geminiStudio) {
                payload.put("contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of(
                                                "text",
                                                "You are a strict exam grading assistant. Return only valid JSON with overallScore, overallFeedback, overallJustification, requiresInstructorReview, and a questions array.\n\n"
                                                        + prompt
                                        )
                                )
                        )
                ));
                payload.put("generationConfig", Map.of("temperature", temperature));
            } else {
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
            }

            String requestBody = objectMapper.writeValueAsString(payload);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(endpointUrl))
                    .timeout(Duration.ofSeconds(Math.max(1, timeoutSeconds)))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody));

            if (geminiStudio) {
                requestBuilder.header("x-goog-api-key", apiKey.trim());
            } else if (apiKey != null && !apiKey.isBlank()) {
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

            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    for (JsonNode part : parts) {
                        JsonNode text = part.path("text");
                        if (!text.isMissingNode() && !text.isNull() && !text.asText().isBlank()) {
                            if (!builder.isEmpty()) {
                                builder.append('\n');
                            }
                            builder.append(text.asText());
                        }
                    }
                    if (!builder.isEmpty()) {
                        return builder.toString();
                    }
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

