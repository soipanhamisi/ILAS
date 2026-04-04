package org.soipan.ilas.proxies;

/**
 * Proxy contract for talking to an external LLM grading endpoint.
 */
public interface LlmGradingProxy {

    LlmCompletion complete(String prompt);

    record LlmCompletion(String model, String content, String rawResponse) {}
}

