package org.soipan.ilas.controllers;

import org.soipan.ilas.dto.ApiResponse;
import org.soipan.ilas.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for admin operations
 * Endpoints for system management and statistics
 * 
 * Real-time Monitoring:
 * - Long polling endpoint: /admin/dashboard/monitoring
 * - Heartbeat endpoint: /admin/monitoring/heartbeat
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173"})
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Get admin dashboard summary
     * @param adminId the admin user ID
     * @return dashboard statistics
     */
    @GetMapping("/dashboard/summary")
    public ApiResponse<Map<String, Object>> getDashboardSummary(@RequestParam int adminId) {
        try {
            Map<String, Object> summary = adminService.getDashboardSummary(adminId);
            return new ApiResponse<>(true, "Dashboard summary retrieved successfully", summary);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving dashboard summary: " + e.getMessage(), null);
        }
    }

    /**
     * Get system statistics
     * @param adminId the admin user ID
     * @return statistics map
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getSystemStats(@RequestParam int adminId) {
        try {
            if (!adminService.adminExists(adminId)) {
                return new ApiResponse<>(false, "Admin not found", null);
            }

            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalStudents", adminService.getTotalStudents());
            stats.put("totalInstructors", adminService.getTotalInstructors());
            stats.put("totalCourses", adminService.getTotalCourses());
            stats.put("totalExams", adminService.getTotalExams());

            return new ApiResponse<>(true, "System statistics retrieved successfully", stats);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving statistics: " + e.getMessage(), null);
        }
    }

    /**
     * Get total student count
     * @param adminId the admin user ID
     * @return total student count
     */
    @GetMapping("/students/count")
    public ApiResponse<Long> getTotalStudents(@RequestParam int adminId) {
        try {
            if (!adminService.adminExists(adminId)) {
                return new ApiResponse<>(false, "Admin not found", null);
            }
            long count = adminService.getTotalStudents();
            return new ApiResponse<>(true, "Student count retrieved", count);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving student count: " + e.getMessage(), null);
        }
    }

    /**
     * Get total instructor count
     * @param adminId the admin user ID
     * @return total instructor count
     */
    @GetMapping("/instructors/count")
    public ApiResponse<Long> getTotalInstructors(@RequestParam int adminId) {
        try {
            if (!adminService.adminExists(adminId)) {
                return new ApiResponse<>(false, "Admin not found", null);
            }
            long count = adminService.getTotalInstructors();
            return new ApiResponse<>(true, "Instructor count retrieved", count);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving instructor count: " + e.getMessage(), null);
        }
    }

    /**
     * Get total course count
     * @param adminId the admin user ID
     * @return total course count
     */
    @GetMapping("/courses/count")
    public ApiResponse<Long> getTotalCourses(@RequestParam int adminId) {
        try {
            if (!adminService.adminExists(adminId)) {
                return new ApiResponse<>(false, "Admin not found", null);
            }
            long count = adminService.getTotalCourses();
            return new ApiResponse<>(true, "Course count retrieved", count);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving course count: " + e.getMessage(), null);
        }
    }

    /**
     * Get total exam count
     * @param adminId the admin user ID
     * @return total exam count
     */
    @GetMapping("/exams/count")
    public ApiResponse<Long> getTotalExams(@RequestParam int adminId) {
        try {
            if (!adminService.adminExists(adminId)) {
                return new ApiResponse<>(false, "Admin not found", null);
            }
            long count = adminService.getTotalExams();
            return new ApiResponse<>(true, "Exam count retrieved", count);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving exam count: " + e.getMessage(), null);
        }
    }

    @GetMapping("/dashboard/monitoring")
    public ApiResponse<Map<String, Object>> getMonitoringSummary(@RequestParam int adminId) {
        try {
            Map<String, Object> summary = adminService.getMonitoringSummary(adminId);
            return new ApiResponse<>(true, "Monitoring summary retrieved successfully", summary);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving monitoring summary: " + e.getMessage(), null);
        }
    }

    @PostMapping("/monitoring/heartbeat")
    public ApiResponse<Void> heartbeat(@RequestParam String userType, @RequestParam int userId) {
        try {
            adminService.heartbeat(userType, userId);
            return new ApiResponse<>(true, "Heartbeat recorded", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error recording heartbeat: " + e.getMessage(), null);
        }
    }
}

