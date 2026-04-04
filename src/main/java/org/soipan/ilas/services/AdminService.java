package org.soipan.ilas.services;

import org.soipan.ilas.models.Admin;
import org.soipan.ilas.repository.AdminRepository;
import org.soipan.ilas.repository.StudentRepository;
import org.soipan.ilas.repository.InstructorRepository;
import org.soipan.ilas.repository.CourseRepository;
import org.soipan.ilas.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for admin-related operations
 * Handles system statistics, user management, and administrative queries
 */
@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SystemMonitoringService monitoringService;

    @Autowired
    private EndpointCatalogService endpointCatalogService;

    /**
     * Get dashboard summary statistics
     * @param adminId the admin ID (for validation)
     * @return map of system statistics
     */
    public Map<String, Object> getDashboardSummary(int adminId) {
        // Verify admin exists
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + adminId));

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalStudents", studentRepository.count());
        summary.put("totalInstructors", instructorRepository.count());
        summary.put("totalCourses", courseRepository.count());
        summary.put("totalExams", examRepository.count());
        summary.put("adminName", admin.getName());

        return summary;
    }

    /**
     * Get total student count
     * @return count of all students
     */
    public long getTotalStudents() {
        return studentRepository.count();
    }

    /**
     * Get total instructor count
     * @return count of all instructors
     */
    public long getTotalInstructors() {
        return instructorRepository.count();
    }

    /**
     * Get total course count
     * @return count of all courses
     */
    public long getTotalCourses() {
        return courseRepository.count();
    }

    /**
     * Get total exam count
     * @return count of all exams
     */
    public long getTotalExams() {
        return examRepository.count();
    }

    /**
     * Verify admin exists
     * @param adminId the admin ID
     * @return true if admin exists, false otherwise
     */
    public boolean adminExists(int adminId) {
        return adminRepository.findByAdminId(adminId).isPresent();
    }

    public void heartbeat(String userType, int userId) {
        if (userType == null || userType.isBlank()) {
            throw new IllegalArgumentException("User type is required");
        }
        String normalized = userType.trim().toLowerCase();
        if (!normalized.equals("student") && !normalized.equals("instructor") && !normalized.equals("admin")) {
            throw new IllegalArgumentException("Unsupported user type: " + userType);
        }
        monitoringService.touchUser(normalized, userId);
    }

    public Map<String, Object> getMonitoringSummary(int adminId) {
        if (!adminExists(adminId)) {
            throw new IllegalArgumentException("Admin not found with ID: " + adminId);
        }

        int threadPoolCapacity = monitoringService.getThreadPoolCapacity();
        int totalActiveRequests = monitoringService.getTotalActiveRequests();
        int activeUsers = monitoringService.getActiveUsers();
        long totalUsers = studentRepository.count() + instructorRepository.count() + adminRepository.count();

        Map<String, Integer> activeByEndpoint = monitoringService.getEndpointActiveRequests();
        Map<String, Long> totalByEndpoint = monitoringService.getEndpointTotalRequests();
        Map<String, Double> rpsByEndpoint = monitoringService.getEndpointRequestsPerSecond();
        Set<String> endpointCatalog = new HashSet<>(endpointCatalogService.getClientFacingEndpoints());
        endpointCatalog.addAll(activeByEndpoint.keySet());
        endpointCatalog.addAll(totalByEndpoint.keySet());
        endpointCatalog.addAll(rpsByEndpoint.keySet());

        List<Map<String, Object>> endpointHealth = endpointCatalog.stream()
                .map(endpointPath -> {
                    int activeRequests = activeByEndpoint.getOrDefault(endpointPath, 0);
                    long totalRequests = totalByEndpoint.getOrDefault(endpointPath, 0L);
                    double requestsPerSecond = rpsByEndpoint.getOrDefault(endpointPath, 0.0);
                    double utilization = (double) activeRequests / threadPoolCapacity;
                    Map<String, Object> endpointDetails = new HashMap<>();
                    endpointDetails.put("endpoint", endpointPath);
                    endpointDetails.put("activeRequests", activeRequests);
                    endpointDetails.put("totalRequests", totalRequests);
                    endpointDetails.put("requestsPerSecond", requestsPerSecond);
                    endpointDetails.put("utilization", utilization);
                    endpointDetails.put("status",
                            utilization >= 0.8 || requestsPerSecond >= 5.0 ? "HIGH"
                                    : utilization >= 0.5 || requestsPerSecond >= 1.0 ? "ELEVATED"
                                    : "HEALTHY");
                    return endpointDetails;
                })
                .sorted((a, b) -> {
                    int activeCompare = Integer.compare((int) b.get("activeRequests"), (int) a.get("activeRequests"));
                    if (activeCompare != 0) {
                        return activeCompare;
                    }
                    return String.valueOf(a.get("endpoint")).compareTo(String.valueOf(b.get("endpoint")));
                })
                .collect(Collectors.toList());

        Map<String, Object> users = new HashMap<>();
        users.put("totalUsers", totalUsers);
        users.put("activeUsers", activeUsers);

        Map<String, Object> series = new HashMap<>();
        series.put("requestUtilization", monitoringService.getRequestUtilizationHistory());
        series.put("activeUsers", monitoringService.getActiveUsersHistory());

        Map<String, Object> summary = new HashMap<>();
        summary.put("threadPoolCapacity", threadPoolCapacity);
        summary.put("globalActiveRequests", totalActiveRequests);
        summary.put("globalUtilization", monitoringService.getGlobalUtilization());
        summary.put("endpointHealth", endpointHealth);
        summary.put("users", users);
        summary.put("series", series);
        return summary;
    }
}

