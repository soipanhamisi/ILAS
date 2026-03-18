package org.soipan.ilas.repository;

import org.soipan.ilas.models.Course;
import org.soipan.ilas.models.Enrollment;
import org.soipan.ilas.models.EnrollmentStatus;
import org.soipan.ilas.models.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends CrudRepository<Enrollment, Integer> {

    // Find enrollment by ID
    Optional<Enrollment> findById(int id);

    // Find all enrollments for a student
    List<Enrollment> findByStudent(Student student);

    // Find all enrollments for a student by student ID
    List<Enrollment> findByStudent_StudentId(int studentId);

    // Find all enrollments for a course
    List<Enrollment> findByCourse(Course course);

    // Find all enrollments for a course by course ID
    List<Enrollment> findByCourse_CourseId(int courseId);

    // Find enrollment by student and course
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);

    // Find enrollment by student ID and course ID
    Optional<Enrollment> findByStudent_StudentIdAndCourse_CourseId(int studentId, int courseId);

    // Find enrollments by status
    List<Enrollment> findByEnrollmentStatus(EnrollmentStatus status);

    // Find enrollments by student and status
    List<Enrollment> findByStudent_StudentIdAndEnrollmentStatus(int studentId, EnrollmentStatus status);

    // Find enrollments by course and status
    List<Enrollment> findByCourse_CourseIdAndEnrollmentStatus(int courseId, EnrollmentStatus status);

    // Check if enrollment exists for student and course
    boolean existsByStudent_StudentIdAndCourse_CourseId(int studentId, int courseId);

    // Count enrollments by course
    long countByCourse_CourseId(int courseId);

    // Count enrollments by student
    long countByStudent_StudentId(int studentId);

    // Delete enrollment by student and course
    void deleteByStudent_StudentIdAndCourse_CourseId(int studentId, int courseId);
}

