package org.soipan.ilas.config;

import org.soipan.ilas.models.*;
import org.soipan.ilas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Database seeder for initial data
 * Runs on application startup to populate the database
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SMITH_USERNAME = "smith_instructor";
    private static final String JOHNSON_USERNAME = "johnson_instructor";

    @Override
    public void run(String... args) throws Exception {
        repairSubmissionTextColumns();
        repairCoursesWithoutInstructor();
        repairEnrollmentsWithoutStatus();

        // Only seed if database is empty
        if (instructorRepository.count() > 0 || studentRepository.count() > 0
                || courseRepository.count() > 0 || enrollmentRepository.count() > 0) {
            System.out.println("Database already seeded, skipping...");
            return;
        }

        System.out.println("Starting database seeding...");

        // Create Instructors
        Instructor instructor1 = new Instructor(
                "Dr. Smith",
                "dr.smith@university.edu",
                SMITH_USERNAME,
                "password123"
        );
        Instructor instructor2 = new Instructor(
                "Prof. Johnson",
                "prof.johnson@university.edu",
                JOHNSON_USERNAME,
                "password123"
        );

        instructor1 = instructorRepository.save(instructor1);
        instructor2 = instructorRepository.save(instructor2);

        System.out.println("Created 2 instructors: " + instructor1.getName() + ", " + instructor2.getName());

        // Create Courses with instructor references
        Course javaCourse = new Course();
        javaCourse.setCourseTitle("Introduction to Java");
        javaCourse.setCourseId(101);
        javaCourse.setInstructor(instructor1);
        javaCourse = courseRepository.save(javaCourse);

        Course dataStructuresCourse = new Course();
        dataStructuresCourse.setCourseTitle("Data Structures");
        dataStructuresCourse.setCourseId(102);
        dataStructuresCourse.setInstructor(instructor2);
        dataStructuresCourse = courseRepository.save(dataStructuresCourse);

        Course webDevCourse = new Course();
        webDevCourse.setCourseTitle("Web Development");
        webDevCourse.setCourseId(103);
        webDevCourse.setInstructor(instructor1);
        webDevCourse = courseRepository.save(webDevCourse);

        // Create Students
        Student student1 = new Student(
                "John Doe",
                "john.doe@student.edu",
                "john_student",
                "password123"
        );
        Student student2 = new Student(
                "Jane Smith",
                "jane.smith@student.edu",
                "jane_student",
                "password123"
        );
        Student student3 = new Student(
                "Alice Johnson",
                "alice.johnson@student.edu",
                "alice_student",
                "password123"
        );

        student1 = studentRepository.save(student1);
        student2 = studentRepository.save(student2);
        student3 = studentRepository.save(student3);

        System.out.println("Created 3 students: " + student1.getName() + ", " + student2.getName() + ", " + student3.getName());

        // Create enrollments (students enroll in courses)
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setStudent(student1);
        enrollment1.setCourse(javaCourse);
        enrollment1.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment1);

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setStudent(student2);
        enrollment2.setCourse(javaCourse);
        enrollment2.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment2);

        Enrollment enrollment3 = new Enrollment();
        enrollment3.setStudent(student3);
        enrollment3.setCourse(dataStructuresCourse);
        enrollment3.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment3);

        Enrollment enrollment4 = new Enrollment();
        enrollment4.setStudent(student1);
        enrollment4.setCourse(webDevCourse);
        enrollment4.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment4);

        System.out.println("Database seeding completed successfully!");
    }

    private void repairCoursesWithoutInstructor() {
        var smithOpt = instructorRepository.findByUsername(SMITH_USERNAME);
        var johnsonOpt = instructorRepository.findByUsername(JOHNSON_USERNAME);

        if (smithOpt.isEmpty() || johnsonOpt.isEmpty()) {
            return;
        }

        Instructor smith = smithOpt.get();
        Instructor johnson = johnsonOpt.get();
        int repairedCount = 0;

        for (Course course : courseRepository.findAll()) {
            if (course.getInstructor() != null) {
                continue;
            }

            String title = course.getCourseTitle();
            if ("Data Structures".equalsIgnoreCase(title)) {
                course.setInstructor(johnson);
            } else {
                course.setInstructor(smith);
            }

            courseRepository.save(course);
            repairedCount++;
        }

        if (repairedCount > 0) {
            System.out.println("Repaired " + repairedCount + " course(s) with missing instructor references.");
        }
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

