package org.soipan.ilas.config;

import org.soipan.ilas.models.*;
import org.soipan.ilas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (instructorRepository.count() > 0 || studentRepository.count() > 0) {
            System.out.println("Database already seeded, skipping...");
            return;
        }

        System.out.println("Starting database seeding...");

        // Create Courses
        Course javaCourse = new Course();
        javaCourse.setCourseTitle("Introduction to Java");
        javaCourse.setCourseId(101);
        courseRepository.save(javaCourse);

        Course dataStructuresCourse = new Course();
        dataStructuresCourse.setCourseTitle("Data Structures");
        dataStructuresCourse.setCourseId(102);
        courseRepository.save(dataStructuresCourse);

        Course webDevCourse = new Course();
        webDevCourse.setCourseTitle("Web Development");
        webDevCourse.setCourseId(103);
        courseRepository.save(webDevCourse);

        // Create Instructors
        Instructor instructor1 = new Instructor(
                "Dr. Smith",
                "dr.smith@university.edu",
                "smith_instructor",
                "password123"
        );
        Instructor instructor2 = new Instructor(
                "Prof. Johnson",
                "prof.johnson@university.edu",
                "johnson_instructor",
                "password123"
        );

        instructor1 = instructorRepository.save(instructor1);
        instructor2 = instructorRepository.save(instructor2);

        System.out.println("Created 2 instructors: " + instructor1.getName() + ", " + instructor2.getName());

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
        enrollmentRepository.save(enrollment1);

        Enrollment enrollment2 = new Enrollment();
        enrollment2.setStudent(student2);
        enrollment2.setCourse(javaCourse);
        enrollmentRepository.save(enrollment2);

        Enrollment enrollment3 = new Enrollment();
        enrollment3.setStudent(student3);
        enrollment3.setCourse(dataStructuresCourse);
        enrollmentRepository.save(enrollment3);

        Enrollment enrollment4 = new Enrollment();
        enrollment4.setStudent(student1);
        enrollment4.setCourse(webDevCourse);
        enrollmentRepository.save(enrollment4);

        System.out.println("Database seeding completed successfully!");
    }
}

