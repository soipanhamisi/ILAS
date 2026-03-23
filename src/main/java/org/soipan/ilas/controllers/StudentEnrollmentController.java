package org.soipan.ilas.controllers;

import org.soipan.ilas.dto.ApiResponse;
import org.soipan.ilas.dto.CourseEnrollmentDTO;
import org.soipan.ilas.models.Course;
import org.soipan.ilas.models.Enrollment;
import org.soipan.ilas.services.StudentExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for student enrollment operations
 */
@RestController
@RequestMapping("/api/student/courses")
@CrossOrigin(origins = "*")
public class StudentEnrollmentController {

    @Autowired
    private StudentExamService studentExamService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseEnrollmentDTO>>> getAllCourses(
            @RequestParam Integer studentId) {

        List<CourseEnrollmentDTO> courses = studentExamService.getAllCoursesForStudent(studentId)
                .stream()
                .map(course -> toCourseEnrollmentDTO(course, false))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/enrolled")
    public ResponseEntity<ApiResponse<List<CourseEnrollmentDTO>>> getEnrolledCourses(
            @RequestParam Integer studentId) {

        List<CourseEnrollmentDTO> enrolledCourses = studentExamService.getEnrolledCourses(studentId)
                .stream()
                .map(course -> toCourseEnrollmentDTO(course, true))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(enrolledCourses));
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<ApiResponse<CourseEnrollmentDTO>> enrollInCourse(
            @PathVariable Integer courseId,
            @RequestParam Integer studentId) {

        Enrollment enrollment = studentExamService.enrollStudentInCourse(studentId, courseId);

        CourseEnrollmentDTO dto = toCourseEnrollmentDTO(enrollment.getCourse(), true);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Enrolled successfully", dto));
    }

    private CourseEnrollmentDTO toCourseEnrollmentDTO(Course course, boolean enrolled) {
        Integer instructorId = null;
        String instructorName = "TBA";

        if (course.getInstructor() != null) {
            instructorId = course.getInstructor().getInstructorId();
            instructorName = course.getInstructor().getName();
        }

        return new CourseEnrollmentDTO(
                course.getCourseId(),
                course.getCourseTitle(),
                instructorId,
                instructorName,
                enrolled
        );
    }
}

