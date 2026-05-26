package org.soipan.ilas.proxies;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock LLM grading proxy for local development/testing.
 */
@Service
@ConditionalOnProperty(name = "grading.llm.provider", havingValue = "mock", matchIfMissing = false)
public class MockLlmGradingProxy implements LlmGradingProxy {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public LlmCompletion complete(String prompt) {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("overallScore", 8);
		response.put("overallFeedback", "Mock grading response generated locally.");
		response.put("overallJustification", "This is a deterministic local fallback for testing.");
		response.put("requiresInstructorReview", false);

		List<Map<String, Object>> questions = new ArrayList<>();
		Map<String, Object> item = new LinkedHashMap<>();
		item.put("question_id", "MOCK-001");
		item.put("score", 8);
		item.put("feedback", "Mock response");
		item.put("justification", "Used only when grading.llm.provider=mock.");
		questions.add(item);
		response.put("questions", questions);

		try {
			String json = objectMapper.writeValueAsString(response);
			return new LlmCompletion("mock", json, json);
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to serialize mock LLM grading response", ex);
		}
	}
}

