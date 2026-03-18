package org.soipan.ilas.dto;

import org.soipan.ilas.models.Exam;
import org.soipan.ilas.models.ExamSubmission;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between entities and DTOs
 */
@Component
public class ExamMapper {

    /**
     * Convert Exam entity to ExamDTO
     */
    public ExamDTO toDTO(Exam exam) {
        if (exam == null) {
            return null;
        }

        ExamDTO dto = new ExamDTO();
        dto.setExamId(exam.getExamId());
        dto.setExamTitle(exam.getExamTitle());
        dto.setMaxScore(exam.getMaxScore());
        dto.setCsvFilePath(exam.getCsvFilePath());

        if (exam.getCourse() != null) {
            dto.setCourseId(exam.getCourse().getCourseId());
            dto.setCourseTitle(exam.getCourse().getCourseTitle());

            if (exam.getCourse().getInstructor() != null) {
                dto.setInstructorId(exam.getCourse().getInstructor().getInstructorId());
                dto.setInstructorName(exam.getCourse().getInstructor().getName());
            }
        }

        return dto;
    }

    /**
     * Convert ExamSubmission entity to ExamSubmissionDTO
     */
    public ExamSubmissionDTO toDTO(ExamSubmission submission) {
        if (submission == null) {
            return null;
        }

        ExamSubmissionDTO dto = new ExamSubmissionDTO();
        dto.setSubmissionId(submission.getSubmissionId());
        dto.setSubmissionCsvPath(submission.getSubmissionCsvPath());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setGrade(submission.getGrade());
        dto.setFeedback(submission.getFeedback());
        dto.setGradeJustification(submission.getGradeJustification());
        dto.setGradedAt(submission.getGradedAt());

        if (submission.getStudent() != null) {
            dto.setStudentId(submission.getStudent().getStudentId());
            dto.setStudentName(submission.getStudent().getName());
        }

        if (submission.getExam() != null) {
            dto.setExamId(submission.getExam().getExamId());
            dto.setExamTitle(submission.getExam().getExamTitle());
            dto.setMaxScore(submission.getExam().getMaxScore());
        }

        if (submission.getGradedBy() != null) {
            dto.setGradedByInstructorId(submission.getGradedBy().getInstructorId());
            dto.setGradedByInstructorName(submission.getGradedBy().getName());
        }

        return dto;
    }
}

