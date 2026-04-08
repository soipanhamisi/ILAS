package org.soipan.ilas.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.soipan.ilas.dto.ExamQuestionDTO;
import org.soipan.ilas.dto.QuestionRubricDTO;
import org.soipan.ilas.proxies.LlmGradingProxy;
import org.soipan.ilas.models.Course;
import org.soipan.ilas.models.Exam;
import org.soipan.ilas.models.ExamSubmission;
import org.soipan.ilas.models.Instructor;
import org.soipan.ilas.repository.ExamRepository;
import org.soipan.ilas.repository.ExamSubmissionRepository;
import org.soipan.ilas.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service that stores instructor rubrics and uses an LLM endpoint to auto-grade submissions.
 */
@Service
public class AutoGradingService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamSubmissionRepository submissionRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private InstructorExamService instructorExamService;

    @Autowired
    private LlmGradingProxy llmGradingProxy;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Exam saveRubrics(int instructorId, Long examId, List<QuestionRubricDTO> rubrics) {
        instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        Exam exam = examRepository.findByExamId(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        Course course = exam.getCourse();
        if (course == null || course.getInstructor() == null || course.getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own the course for this exam");
        }

        List<QuestionRubricDTO> normalizedRubrics = normalizeRubrics(rubrics);
        int expectedQuestionCount = instructorExamService.getExamQuestionDetails(instructorId, examId).size();
        if (normalizedRubrics.size() != expectedQuestionCount) {
            throw new IllegalArgumentException("Rubric count must match the number of exam questions");
        }

        String rubricJson = serializeRubrics(normalizedRubrics);
        exam.setGradingRubricsJson(rubricJson);
        exam.setGradingRubricsUpdatedAt(LocalDateTime.now());
        exam.setGradingRubricsVersion(exam.getGradingRubricsVersion() == null ? 1 : exam.getGradingRubricsVersion() + 1);
        return examRepository.save(exam);
    }

    @Transactional
    public ExamSubmission autoGradeSubmission(int instructorId, Long submissionId) {
        Instructor instructor = instructorRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        ExamSubmission submission = submissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found with ID: " + submissionId));

        Exam exam = submission.getExam();
        if (exam == null || exam.getCourse() == null || exam.getCourse().getInstructor() == null
                || exam.getCourse().getInstructor().getInstructorId() != instructorId) {
            throw new IllegalArgumentException("Instructor does not own the course for this exam");
        }

        List<QuestionRubricDTO> rubrics = readRubrics(exam.getGradingRubricsJson());
        if (rubrics.isEmpty()) {
            throw new IllegalArgumentException("This exam does not have grading rubrics yet");
        }

        List<ExamQuestionDTO> questions = instructorExamService.getExamQuestionDetails(instructorId, exam.getExamId());
        if (questions.size() != rubrics.size()) {
            throw new IllegalArgumentException("Rubric count does not match the exam question count");
        }

        List<String> answers = parseSubmissionAnswers(submission.getSubmissionText());
        if (answers.size() != questions.size()) {
            throw new IllegalArgumentException("Submission answers do not match the exam question count");
        }

        String prompt = buildPrompt(exam, questions, rubrics, answers);
        LlmGradingProxy.LlmCompletion completion = llmGradingProxy.complete(prompt);
        AutoGradeResult parsed = parseGradingResponse(completion.content());

        Map<Integer, AutoGradeQuestionResult> resultByQuestion = parsed.questions().stream()
                .collect(Collectors.toMap(AutoGradeQuestionResult::questionNumber, result -> result, (left, right) -> right, HashMap::new));

        List<QuestionGradeLine> gradeLines = new ArrayList<>();
        int totalScore = 0;
        for (int i = 0; i < questions.size(); i++) {
            int questionNumber = i + 1;
            ExamQuestionDTO question = questions.get(i);
            QuestionRubricDTO rubric = rubrics.get(i);
            AutoGradeQuestionResult questionResult = resultByQuestion.get(questionNumber);

            if (questionResult == null) {
                throw new IllegalArgumentException("LLM result is missing question " + questionNumber);
            }

            int boundedScore = clamp(questionResult.score(), Math.min(question.getMaxGrade(), rubric.getMaxScore() == null ? question.getMaxGrade() : rubric.getMaxScore()));
            totalScore += boundedScore;
            gradeLines.add(new QuestionGradeLine(
                    questionNumber,
                    boundedScore,
                    questionResult.feedback() == null ? "" : questionResult.feedback().trim(),
                    questionResult.justification() == null ? "" : questionResult.justification().trim()
            ));
        }

        totalScore = clamp(totalScore, exam.getMaxScore());

        String feedback = gradeLines.stream()
                .map(line -> "Q" + line.questionNumber() + " Feedback: " + line.feedback())
                .collect(Collectors.joining("\n\n"));

        String justification = gradeLines.stream()
                .map(line -> "Q" + line.questionNumber() + " Justification: " + line.justification())
                .collect(Collectors.joining("\n\n"));

        submission.setGrade(totalScore);
        submission.setFeedback(feedback);
        submission.setGradeJustification(justification);
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedBy(instructor);
        submission.setGradingSource("LLM");
        submission.setGradingModel(completion.model());
        submission.setRubricVersionUsed(exam.getGradingRubricsVersion());
        submission.setRequiresInstructorReview(parsed.requiresInstructorReview());
        submission.setAutoGradingResultJson(normalizeJson(parsed, completion.rawResponse()));

        return submissionRepository.save(submission);
    }

    private List<QuestionRubricDTO> normalizeRubrics(List<QuestionRubricDTO> rubrics) {
        if (rubrics == null || rubrics.isEmpty()) {
            throw new IllegalArgumentException("Rubrics are required");
        }

        List<QuestionRubricDTO> normalized = rubrics.stream()
                .filter(Objects::nonNull)
                .map(rubric -> new QuestionRubricDTO(
                        rubric.getQuestionNumber(),
                        rubric.getMaxScore(),
                        rubric.getRubricText() == null ? "" : rubric.getRubricText().trim()))
                .sorted(Comparator.comparingInt(r -> r.getQuestionNumber() == null ? Integer.MAX_VALUE : r.getQuestionNumber()))
                .collect(Collectors.toList());

        for (int i = 0; i < normalized.size(); i++) {
            QuestionRubricDTO rubric = normalized.get(i);
            int expectedQuestionNumber = i + 1;
            if (rubric.getQuestionNumber() == null || rubric.getQuestionNumber() != expectedQuestionNumber) {
                throw new IllegalArgumentException("Question numbering is invalid at question " + expectedQuestionNumber);
            }
            if (rubric.getMaxScore() == null || rubric.getMaxScore() <= 0) {
                throw new IllegalArgumentException("Max score for question " + expectedQuestionNumber + " must be greater than zero");
            }
            if (rubric.getRubricText() == null || rubric.getRubricText().isBlank()) {
                throw new IllegalArgumentException("Rubric text for question " + expectedQuestionNumber + " is required");
            }
        }

        return normalized;
    }

    private String serializeRubrics(List<QuestionRubricDTO> rubrics) {
        try {
            return objectMapper.writeValueAsString(rubrics);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to serialize rubrics", ex);
        }
    }

    private List<QuestionRubricDTO> readRubrics(String rubricsJson) {
        if (rubricsJson == null || rubricsJson.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(rubricsJson, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read grading rubrics", ex);
        }
    }

    private String buildPrompt(Exam exam, List<ExamQuestionDTO> questions, List<QuestionRubricDTO> rubrics, List<String> answers) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Grade the student's exam using the instructor rubric.\n");
        prompt.append("Return STRICT JSON only with this shape: {\"overallScore\":number,\"overallFeedback\":string,\"overallJustification\":string,\"requiresInstructorReview\":boolean,\"questions\":[{\"questionNumber\":number,\"score\":number,\"feedback\":string,\"justification\":string}]}.\n");
        prompt.append("Do not include markdown fences or explanations outside JSON.\n");
        prompt.append("Exam title: ").append(exam.getExamTitle()).append('\n');
        prompt.append("Exam max score: ").append(exam.getMaxScore()).append('\n');

        for (int i = 0; i < questions.size(); i++) {
            ExamQuestionDTO question = questions.get(i);
            QuestionRubricDTO rubric = rubrics.get(i);
            String answer = answers.get(i);
            prompt.append("Question ").append(i + 1).append(':').append('\n');
            prompt.append("Question text: ").append(question.getQuestionText()).append('\n');
            prompt.append("Rubric max score: ").append(rubric.getMaxScore()).append('\n');
            prompt.append("Rubric guidance: ").append(rubric.getRubricText()).append('\n');
            prompt.append("Student answer: ").append(answer).append('\n');
            prompt.append('\n');
        }

        return prompt.toString();
    }

    private AutoGradeResult parseGradingResponse(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("LLM response was empty");
        }

        String json = extractJsonPayload(content);
        try {
            JsonNode root = objectMapper.readTree(json);
            boolean requiresInstructorReview = root.path("requiresInstructorReview").asBoolean(false);
            List<AutoGradeQuestionResult> questions = new ArrayList<>();
            JsonNode questionNodes = root.path("questions");
            if (questionNodes.isArray()) {
                for (JsonNode questionNode : questionNodes) {
                    questions.add(new AutoGradeQuestionResult(
                            questionNode.path("questionNumber").asInt(),
                            questionNode.path("score").asInt(0),
                            questionNode.path("feedback").asText(""),
                            questionNode.path("justification").asText("")
                    ));
                }
            }

            if (questions.isEmpty()) {
                throw new IllegalArgumentException("LLM response did not include per-question results");
            }

            return new AutoGradeResult(requiresInstructorReview, questions);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to parse LLM grading response", ex);
        }
    }

    private String extractJsonPayload(String content) {
        String trimmed = content.trim();
        try {
            objectMapper.readTree(trimmed);
            return trimmed;
        } catch (IOException ignored) {
            // Continue with tolerant extraction for wrapped/model-formatted output.
        }

        if (trimmed.startsWith("```")) {
            int firstBrace = trimmed.indexOf('{');
            int lastBrace = trimmed.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                return trimmed.substring(firstBrace, lastBrace + 1);
            }
        }

        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1);
        }

        return trimmed;
    }

    private String normalizeJson(AutoGradeResult parsed, String rawResponse) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "requiresInstructorReview", parsed.requiresInstructorReview(),
                    "questions", parsed.questions(),
                    "rawResponse", rawResponse
            ));
        } catch (Exception ex) {
            return rawResponse;
        }
    }

    private List<String> parseSubmissionAnswers(String submissionText) {
        if (submissionText == null || submissionText.isBlank()) {
            return List.of();
        }

        List<String> answers = new ArrayList<>();
        String[] chunks = submissionText.split("\\n\\nQ\\d+:\\n");
        for (int i = 0; i < chunks.length; i++) {
            if (i == 0 && chunks[i].startsWith("Q1:\n")) {
                answers.add(chunks[i].replaceFirst("^Q1:\\n", "").trim());
            } else if (i > 0) {
                answers.add(chunks[i].trim());
            }
        }
        return answers.stream()
                .map(answer -> answer == null ? "" : answer.trim())
                .collect(Collectors.toList());
    }

    private int clamp(int value, int max) {
        return Math.max(0, Math.min(max, value));
    }

    private record AutoGradeResult(boolean requiresInstructorReview, List<AutoGradeQuestionResult> questions) {}

    private record AutoGradeQuestionResult(int questionNumber, int score, String feedback, String justification) {}

    private record QuestionGradeLine(int questionNumber, int score, String feedback, String justification) {}
}



