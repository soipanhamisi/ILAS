package org.soipan.ilas.repository;

import org.soipan.ilas.models.Course;
import org.soipan.ilas.models.Instructor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {

    // Find course by ID
    Optional<Course> findByCourseId(int courseId);

    // Find courses by instructor
    List<Course> findByInstructor(Instructor instructor);

    // Find courses by instructor ID
    List<Course> findByInstructor_InstructorId(int instructorId);

    // Find course by title
    Optional<Course> findByCourseTitle(String courseTitle);

    // Find courses by title containing (search)
    List<Course> findByCourseTitleContainingIgnoreCase(String courseTitle);

    // Check if course exists by title
    boolean existsByCourseTitle(String courseTitle);

    // Delete course by ID
    void deleteByCourseId(int courseId);

    // Count courses by instructor
    long countByInstructor_InstructorId(int instructorId);
}

