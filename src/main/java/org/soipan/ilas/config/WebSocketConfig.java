package org.soipan.ilas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.soipan.ilas.config.MonitoringWebSocketHandler;

/**
 * WebSocket configuration for real-time monitoring
 * Enables WebSocket support and registers endpoints for monitoring updates
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MonitoringWebSocketHandler monitoringWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(monitoringWebSocketHandler, "/ws/monitoring")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173")
                .withSockJS();
    }
}

