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
import java.util.Map;

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
}

