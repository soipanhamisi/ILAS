package org.soipan.ilas.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.soipan.ilas.dto.MonitoringMessageDTO;
import org.soipan.ilas.services.AdminService;
import org.soipan.ilas.services.MonitoringWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * WebSocket handler for real-time monitoring connections
 * Manages client connections, authentication, and message routing
 */
@Component
public class MonitoringWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private MonitoringWebSocketService webSocketService;

    @Autowired
    private AdminService adminService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentMap<String, WebSocketSession> adminSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("[WebSocket] Connection attempt. URI: " + session.getUri());
        
        // Extract admin ID from query parameters in the URI
        String adminIdStr = null;
        
        try {
            URI uri = session.getUri();
            if (uri != null && uri.getQuery() != null) {
                String query = uri.getQuery();
                System.out.println("[WebSocket] Query string: " + query);
                // Parse query string for adminId parameter
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("adminId=")) {
                        adminIdStr = param.substring("adminId=".length());
                        System.out.println("[WebSocket] Extracted adminId: " + adminIdStr);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[WebSocket] Error parsing query parameters: " + e.getMessage());
        }

        // If we couldn't get admin ID, close connection
        if (adminIdStr == null || adminIdStr.isBlank()) {
            System.err.println("[WebSocket] No adminId provided in query parameters");
            session.close(CloseStatus.BAD_DATA.withReason("adminId parameter is required"));
            return;
        }

        final String adminId = adminIdStr;
        System.out.println("[WebSocket] Validating admin with ID: " + adminId);

        // Validate admin exists
        try {
            int id = Integer.parseInt(adminId);
            if (!adminService.adminExists(id)) {
                System.err.println("[WebSocket] Admin not found with ID: " + id);
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid admin ID"));
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("[WebSocket] Invalid admin ID format: " + adminId);
            session.close(CloseStatus.BAD_DATA.withReason("Invalid admin ID format"));
            return;
        }

        // Register session
        adminSessions.put(adminId, session);
        webSocketService.registerSession(adminId, session);

        // Send initial connection acknowledgment
        sendMessage(session, "acknowledge", "Connection established");
        System.out.println("[WebSocket] ✓ Connection established for admin: " + adminId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            MonitoringMessageDTO msg = objectMapper.readValue(message.getPayload(), MonitoringMessageDTO.class);

            switch (msg.getType()) {
                case "heartbeat":
                    // Respond to heartbeat
                    sendMessage(session, "pong", "");
                    break;
                case "subscribe":
                    // Client subscribes to monitoring updates
                    String adminId = findAdminIdBySession(session);
                    if (adminId != null) {
                        webSocketService.registerSession(adminId, session);
                        sendMessage(session, "acknowledge", "Subscribed to monitoring updates");
                    }
                    break;
                default:
                    // Unknown message type
                    sendMessage(session, "error", "Unknown message type: " + msg.getType());
            }
        } catch (Exception e) {
            System.err.println("[WebSocket] Error handling message: " + e.getMessage());
            try {
                sendMessage(session, "error", "Failed to process message");
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String adminId = findAdminIdBySession(session);
        if (adminId != null) {
            adminSessions.remove(adminId);
            webSocketService.unregisterSession(adminId);
            System.out.println("[WebSocket] ✓ Connection closed for admin: " + adminId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("[WebSocket] ✗ Transport error: " + exception.getMessage());
        exception.printStackTrace();
        String adminId = findAdminIdBySession(session);
        if (adminId != null) {
            adminSessions.remove(adminId);
            webSocketService.unregisterSession(adminId);
        }
    }

    /**
     * Find admin ID by WebSocket session
     */
    private String findAdminIdBySession(WebSocketSession session) {
        return adminSessions.entrySet().stream()
                .filter(e -> e.getValue().getId().equals(session.getId()))
                .map(java.util.Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Send a message to a WebSocket session
     */
    private void sendMessage(WebSocketSession session, String type, String message) throws IOException {
        if (session != null && session.isOpen()) {
            try {
                MonitoringMessageDTO response = new MonitoringMessageDTO(type, "1.0",
                        objectMapper.valueToTree(java.util.Map.of("message", message)), 
                        System.currentTimeMillis());
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            } catch (IOException e) {
                System.err.println("[WebSocket] Failed to send message: " + e.getMessage());
            }
        }
    }

    /**
     * Broadcast message to all connected admin sessions
     */
    public void broadcast(String type, Object payload) throws IOException {
        String message = objectMapper.writeValueAsString(
                new MonitoringMessageDTO(type, "1.0", objectMapper.valueToTree(payload), System.currentTimeMillis())
        );

        for (WebSocketSession session : adminSessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("[WebSocket] Failed to send to session: " + e.getMessage());
                }
            }
        }
    }
}
