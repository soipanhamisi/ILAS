package org.soipan.ilas.repository;

import org.soipan.ilas.models.Exam;
import org.soipan.ilas.models.ExamSubmission;
import org.soipan.ilas.models.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ExamSubmissionRepository extends CrudRepository<ExamSubmission, Long> {

    // Find submission by ID
    Optional<ExamSubmission> findBySubmissionId(Long submissionId);

    // Find all submissions for an exam
    List<ExamSubmission> findByExam(Exam exam);

    // Find all submissions for an exam by exam ID
    List<ExamSubmission> findByExam_ExamId(Long examId);

    // Find all submissions by a student
    List<ExamSubmission> findByStudent(Student student);

    // Find all submissions by a student ID
    List<ExamSubmission> findByStudent_StudentId(int studentId);

    // Find submission by student and exam
    Optional<ExamSubmission> findByStudentAndExam(Student student, Exam exam);

    // Find submission by student ID and exam ID
    Optional<ExamSubmission> findByStudent_StudentIdAndExam_ExamId(int studentId, Long examId);

    // Find graded submissions for an exam
    List<ExamSubmission> findByExam_ExamIdAndGradeIsNotNull(Long examId);

    // Find ungraded submissions for an exam
    List<ExamSubmission> findByExam_ExamIdAndGradeIsNull(Long examId);

    // Find submissions graded by instructor
    List<ExamSubmission> findByGradedBy_InstructorId(int instructorId);

    // Check if student has submitted exam
    boolean existsByStudent_StudentIdAndExam_ExamId(int studentId, Long examId);

    // Count submissions for an exam
    long countByExam_ExamId(Long examId);

    // Count graded submissions for an exam
    long countByExam_ExamIdAndGradeIsNotNull(Long examId);

    // Count ungraded submissions for an exam
    long countByExam_ExamIdAndGradeIsNull(Long examId);
}

