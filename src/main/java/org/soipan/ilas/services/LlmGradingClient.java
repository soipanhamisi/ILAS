package org.soipan.ilas.services;

import org.soipan.ilas.proxies.HttpLlmGradingProxy;
import org.soipan.ilas.proxies.LlmGradingProxy;

/**
 * Backward-compatible adapter kept for older wiring while the proxy lives in `proxies/`.
 */
@Deprecated
public class LlmGradingClient extends HttpLlmGradingProxy implements LlmGradingProxy {

    public LlmGradingClient(GradingCredentialResolver credentialResolver) {
        super(credentialResolver);
    }
}



