package org.soipan.ilas.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Wrapper DTO for WebSocket messages
 * Enables versioning and flexible message handling
 */
public class MonitoringMessageDTO {
    private String type;           // Message type: "monitoring_update", "heartbeat", "acknowledge", etc.
    private String version;        // Message format version
    private JsonNode payload;      // Actual message content
    private long timestamp;        // Message timestamp

    // Constructors
    public MonitoringMessageDTO() {
    }

    public MonitoringMessageDTO(String type, String version, JsonNode payload, long timestamp) {
        this.type = type;
        this.version = version;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public MonitoringMessageDTO(String type, JsonNode payload) {
        this(type, "1.0", payload, System.currentTimeMillis());
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

