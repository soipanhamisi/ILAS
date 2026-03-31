package org.soipan.ilas.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for managing WebSocket connections and broadcasting monitoring updates
 * Handles session registry and message distribution to connected clients
 */
@Service
public class MonitoringWebSocketService {

    private final ConcurrentMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Register a WebSocket session for an admin
     */
    public void registerSession(String adminId, WebSocketSession session) {
        activeSessions.put(adminId + ":" + session.getId(), session);
    }

    /**
     * Unregister a WebSocket session
     */
    public void unregisterSession(String adminId) {
        activeSessions.entrySet().removeIf(e -> e.getKey().startsWith(adminId + ":"));
    }

    /**
     * Broadcast monitoring update to all connected clients
     */
    public void broadcastMonitoringUpdate(Object updateData) {
        if (activeSessions.isEmpty()) {
            return;
        }

        try {
            String message = createMessage("monitoring_update", updateData);
            broadcastMessage(message);
        } catch (IOException e) {
            System.err.println("Failed to broadcast monitoring update: " + e.getMessage());
        }
    }

    /**
     * Broadcast a message to all connected clients
     */
    private void broadcastMessage(String message) {
        for (WebSocketSession session : activeSessions.values()) {
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new org.springframework.web.socket.TextMessage(message));
                } catch (IOException e) {
                    // Session might have been closed, remove it
                    activeSessions.values().remove(session);
                }
            }
        }
    }

    /**
     * Create a WebSocket message
     */
    private String createMessage(String type, Object payload) throws IOException {
        java.util.Map<String, Object> message = new java.util.HashMap<>();
        message.put("type", type);
        message.put("version", "1.0");
        message.put("payload", payload);
        message.put("timestamp", System.currentTimeMillis());
        return objectMapper.writeValueAsString(message);
    }

    /**
     * Get the number of connected clients
     */
    public int getConnectedClientCount() {
        return activeSessions.size();
    }

    /**
     * Check if there are any connected clients
     */
    public boolean hasConnectedClients() {
        return !activeSessions.isEmpty();
    }
}

