package org.soipan.ilas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks lightweight runtime metrics for the admin dashboard.
 * Broadcasts updates via WebSocket to connected monitoring clients.
 */
@Service
public class SystemMonitoringService {

    @Autowired(required = false)
    private MonitoringWebSocketService webSocketService;

    private final ConcurrentMap<String, AtomicInteger> endpointActiveRequests = new ConcurrentHashMap<>();
    private final AtomicInteger totalActiveRequests = new AtomicInteger(0);
    private final ConcurrentMap<String, Long> userLastSeenEpochMs = new ConcurrentHashMap<>();

    private final Deque<Map<String, Object>> requestUtilizationHistory = new ArrayDeque<>();
    private final Deque<Map<String, Object>> activeUsersHistory = new ArrayDeque<>();

    @Value("${server.tomcat.threads.max:200}")
    private int threadPoolCapacity;

    @Value("${monitoring.active-user-timeout-ms:120000}")
    private long activeUserTimeoutMs;

    @Value("${monitoring.history.max-points:72}")
    private int maxHistoryPoints;

    public void onRequestStart(String endpoint) {
        endpointActiveRequests.computeIfAbsent(endpoint, key -> new AtomicInteger(0)).incrementAndGet();
        totalActiveRequests.incrementAndGet();
    }

    public void onRequestEnd(String endpoint) {
        endpointActiveRequests.computeIfPresent(endpoint, (key, value) -> {
            if (value.decrementAndGet() <= 0) {
                return null;
            }
            return value;
        });

        int remaining = totalActiveRequests.decrementAndGet();
        if (remaining < 0) {
            totalActiveRequests.set(0);
        }
    }

    public void touchUser(String userType, int userId) {
        userLastSeenEpochMs.put(userType + ":" + userId, System.currentTimeMillis());
    }

    public int getTotalActiveRequests() {
        return Math.max(totalActiveRequests.get(), 0);
    }

    public int getThreadPoolCapacity() {
        return Math.max(threadPoolCapacity, 1);
    }

    public double getGlobalUtilization() {
        return (double) getTotalActiveRequests() / getThreadPoolCapacity();
    }

    public Map<String, Integer> getEndpointActiveRequests() {
        Map<String, Integer> snapshot = new HashMap<>();
        endpointActiveRequests.forEach((endpoint, count) -> snapshot.put(endpoint, Math.max(count.get(), 0)));
        return snapshot;
    }

    public int getActiveUsers() {
        long threshold = System.currentTimeMillis() - activeUserTimeoutMs;
        userLastSeenEpochMs.entrySet().removeIf(entry -> entry.getValue() < threshold);
        return userLastSeenEpochMs.size();
    }

    public List<Map<String, Object>> getRequestUtilizationHistory() {
        return new ArrayList<>(requestUtilizationHistory);
    }

    public List<Map<String, Object>> getActiveUsersHistory() {
        return new ArrayList<>(activeUsersHistory);
    }

    @Scheduled(fixedRateString = "${monitoring.sample-interval-ms:5000}")
    public void recordSnapshot() {
        long now = Instant.now().toEpochMilli();

        Map<String, Object> requestPoint = new HashMap<>();
        requestPoint.put("timestamp", now);
        requestPoint.put("value", getGlobalUtilization());
        appendPoint(requestUtilizationHistory, requestPoint);

        Map<String, Object> activeUsersPoint = new HashMap<>();
        activeUsersPoint.put("timestamp", now);
        activeUsersPoint.put("value", getActiveUsers());
        appendPoint(activeUsersHistory, activeUsersPoint);

        // Broadcast updates via WebSocket if there are connected clients
        if (webSocketService != null && webSocketService.hasConnectedClients()) {
            broadcastMonitoringSnapshot();
        }
    }

    /**
     * Broadcast current monitoring snapshot to all connected WebSocket clients
     */
    private void broadcastMonitoringSnapshot() {
        try {
            int threadPoolCapacity = getThreadPoolCapacity();
            int totalActiveRequests = getTotalActiveRequests();
            int activeUsers = getActiveUsers();

            List<Map<String, Object>> endpointHealth = new ArrayList<>();
            List<Map<String, Object>> finalEndpointHealth = endpointHealth;
            getEndpointActiveRequests().forEach((endpoint, count) -> {
                double utilization = (double) count / threadPoolCapacity;
                Map<String, Object> health = new HashMap<>();
                health.put("endpoint", endpoint);
                health.put("activeRequests", count);
                health.put("utilization", utilization);
                health.put("status", utilization >= 0.8 ? "HIGH" : utilization >= 0.5 ? "ELEVATED" : "HEALTHY");
                finalEndpointHealth.add(health);
            });

            // Sort by active requests (descending) and limit to 12
            endpointHealth.sort((a, b) -> Integer.compare((int) b.get("activeRequests"), (int) a.get("activeRequests")));
            if (endpointHealth.size() > 12) {
                endpointHealth = endpointHealth.subList(0, 12);
            }

            Map<String, Object> series = new HashMap<>();
            series.put("requestUtilization", new ArrayList<>(requestUtilizationHistory));
            series.put("activeUsers", new ArrayList<>(activeUsersHistory));

            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("threadPoolCapacity", threadPoolCapacity);
            snapshot.put("globalActiveRequests", totalActiveRequests);
            snapshot.put("globalUtilization", getGlobalUtilization());
            snapshot.put("endpointHealth", endpointHealth);
            snapshot.put("users", Map.of("totalUsers", 0, "activeUsers", activeUsers));
            snapshot.put("series", series);

            webSocketService.broadcastMonitoringUpdate(snapshot);
        } catch (Exception e) {
            System.err.println("Failed to broadcast monitoring snapshot: " + e.getMessage());
        }
    }

    private void appendPoint(Deque<Map<String, Object>> history, Map<String, Object> point) {
        synchronized (history) {
            history.addLast(point);
            while (history.size() > maxHistoryPoints) {
                history.removeFirst();
            }
        }
    }
}

