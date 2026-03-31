package org.soipan.ilas.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for monitoring data sent via WebSocket to connected admin clients
 * Contains all real-time system metrics and statistics
 */
public class MonitoringUpdateDTO {
    private int threadPoolCapacity;
    private int globalActiveRequests;
    private double globalUtilization;
    private Map<String, Object> users;
    private List<Map<String, Object>> endpointHealth;
    private Map<String, Object> series;
    private long timestamp;

    // Constructors
    public MonitoringUpdateDTO() {
    }

    public MonitoringUpdateDTO(int threadPoolCapacity, int globalActiveRequests, double globalUtilization,
                               Map<String, Object> users, List<Map<String, Object>> endpointHealth,
                               Map<String, Object> series, long timestamp) {
        this.threadPoolCapacity = threadPoolCapacity;
        this.globalActiveRequests = globalActiveRequests;
        this.globalUtilization = globalUtilization;
        this.users = users;
        this.endpointHealth = endpointHealth;
        this.series = series;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getThreadPoolCapacity() {
        return threadPoolCapacity;
    }

    public void setThreadPoolCapacity(int threadPoolCapacity) {
        this.threadPoolCapacity = threadPoolCapacity;
    }

    public int getGlobalActiveRequests() {
        return globalActiveRequests;
    }

    public void setGlobalActiveRequests(int globalActiveRequests) {
        this.globalActiveRequests = globalActiveRequests;
    }

    public double getGlobalUtilization() {
        return globalUtilization;
    }

    public void setGlobalUtilization(double globalUtilization) {
        this.globalUtilization = globalUtilization;
    }

    public Map<String, Object> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Object> users) {
        this.users = users;
    }

    public List<Map<String, Object>> getEndpointHealth() {
        return endpointHealth;
    }

    public void setEndpointHealth(List<Map<String, Object>> endpointHealth) {
        this.endpointHealth = endpointHealth;
    }

    public Map<String, Object> getSeries() {
        return series;
    }

    public void setSeries(Map<String, Object> series) {
        this.series = series;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

