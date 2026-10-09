package org.soipan.ilas.config;

import org.soipan.ilas.models.Enrollment;
import org.soipan.ilas.models.EnrollmentStatus;
import org.soipan.ilas.repository.EnrollmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Applies backwards-compatible data repairs at application startup.
 */
@Component
public class DataMaintenanceRunner implements CommandLineRunner {
    private final EnrollmentRepository enrollmentRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataMaintenanceRunner(EnrollmentRepository enrollmentRepository, JdbcTemplate jdbcTemplate) {
        this.enrollmentRepository = enrollmentRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        repairSubmissionTextColumns();
        repairEnrollmentsWithoutStatus();
    }

    private void repairEnrollmentsWithoutStatus() {
        int repairedCount = 0;

        for (Enrollment enrollment : enrollmentRepository.findAll()) {
            boolean updated = false;

            if (enrollment.getEnrollmentStatus() == null) {
                enrollment.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
                updated = true;
            }

            if (enrollment.getEnrolledAt() == null) {
                enrollment.setEnrolledAt(LocalDateTime.now());
                updated = true;
            }

            if (updated) {
                enrollmentRepository.save(enrollment);
                repairedCount++;
            }
        }

        if (repairedCount > 0) {
            System.out.println("Repaired " + repairedCount + " enrollment(s) with missing status.");
        }
    }

    private void repairSubmissionTextColumns() {
        try {
            jdbcTemplate.execute("ALTER TABLE exam_submissions_tbl MODIFY COLUMN feedback TEXT");
            jdbcTemplate.execute("ALTER TABLE exam_submissions_tbl MODIFY COLUMN grade_justification TEXT");
            jdbcTemplate.execute("ALTER TABLE exam_submissions_tbl MODIFY COLUMN submission_text TEXT");
        } catch (Exception ex) {
            System.out.println("Submission text column repair skipped: " + ex.getMessage());
        }
    }
}


