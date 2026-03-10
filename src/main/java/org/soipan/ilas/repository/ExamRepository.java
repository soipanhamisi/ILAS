package org.soipan.ilas.repository;

import org.soipan.ilas.models.Course;
import org.soipan.ilas.models.Exam;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends CrudRepository<Exam, Long> {

    // Find exam by ID
    Optional<Exam> findByExamId(Long examId);

    // Find all exams for a course
    List<Exam> findByCourse(Course course);

    // Find all exams for a course by course ID
    List<Exam> findByCourse_CourseId(int courseId);

    // Find exam by title
    Optional<Exam> findByExamTitle(String examTitle);

    // Find exams by title containing (search)
    List<Exam> findByExamTitleContainingIgnoreCase(String examTitle);

    // Find exam by CSV file path
    Optional<Exam> findByCsvFilePath(String csvFilePath);

    // Find exams by course and title
    Optional<Exam> findByCourse_CourseIdAndExamTitle(int courseId, String examTitle);

    // Check if exam exists by title
    boolean existsByExamTitle(String examTitle);

    // Check if exam exists for course
    boolean existsByCourse_CourseIdAndExamTitle(int courseId, String examTitle);

    // Count exams by course
    long countByCourse_CourseId(int courseId);

    // Delete exam by ID
    void deleteByExamId(Long examId);
}

