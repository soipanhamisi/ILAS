package org.soipan.ilas.services;

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
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks lightweight runtime metrics for the admin dashboard.
 */
@Service
public class SystemMonitoringService {

    private final ConcurrentMap<String, AtomicInteger> endpointActiveRequests = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AtomicLong> endpointTotalRequests = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AtomicInteger> endpointRequestsSinceLastSample = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Double> endpointRequestsPerSecond = new ConcurrentHashMap<>();
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

    @Value("${monitoring.sample-interval-ms:5000}")
    private long sampleIntervalMs;

    public void onRequestStart(String endpoint) {
        endpointActiveRequests.computeIfAbsent(endpoint, key -> new AtomicInteger(0)).incrementAndGet();
        endpointTotalRequests.computeIfAbsent(endpoint, key -> new AtomicLong(0)).incrementAndGet();
        endpointRequestsSinceLastSample.computeIfAbsent(endpoint, key -> new AtomicInteger(0)).incrementAndGet();
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

    public Map<String, Long> getEndpointTotalRequests() {
        Map<String, Long> snapshot = new HashMap<>();
        endpointTotalRequests.forEach((endpoint, count) -> snapshot.put(endpoint, Math.max(count.get(), 0L)));
        return snapshot;
    }

    public Map<String, Double> getEndpointRequestsPerSecond() {
        return new HashMap<>(endpointRequestsPerSecond);
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
        double windowSeconds = Math.max(sampleIntervalMs / 1000.0, 0.001);

        endpointRequestsSinceLastSample.forEach((endpoint, count) -> {
            int requestsInWindow = Math.max(count.getAndSet(0), 0);
            endpointRequestsPerSecond.put(endpoint, requestsInWindow / windowSeconds);
        });

        Map<String, Object> requestPoint = new HashMap<>();
        requestPoint.put("timestamp", now);
        requestPoint.put("value", getGlobalUtilization());
        appendPoint(requestUtilizationHistory, requestPoint);

        Map<String, Object> activeUsersPoint = new HashMap<>();
        activeUsersPoint.put("timestamp", now);
        activeUsersPoint.put("value", getActiveUsers());
        appendPoint(activeUsersHistory, activeUsersPoint);
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

