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
import java.util.List;
import java.util.Map;
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

        List<Map<String, Object>> endpointHealth = monitoringService.getEndpointActiveRequests().entrySet()
                .stream()
                .map(entry -> {
                    double utilization = (double) entry.getValue() / threadPoolCapacity;
                    Map<String, Object> endpoint = new HashMap<>();
                    endpoint.put("endpoint", entry.getKey());
                    endpoint.put("activeRequests", entry.getValue());
                    endpoint.put("utilization", utilization);
                    endpoint.put("status", utilization >= 0.8 ? "HIGH" : utilization >= 0.5 ? "ELEVATED" : "HEALTHY");
                    return endpoint;
                })
                .sorted((a, b) -> Integer.compare((int) b.get("activeRequests"), (int) a.get("activeRequests")))
                .limit(12)
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

