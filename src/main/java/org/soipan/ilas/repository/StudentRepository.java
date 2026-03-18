package org.soipan.ilas.repository;

import org.soipan.ilas.models.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Integer> {

    // Find student by ID
    Optional<Student> findByStudentId(int studentId);

    // Find student by email
    Optional<Student> findByEmail(String email);

    // Find student by name
    List<Student> findByName(String name);

    // Find students by name containing (search)
    List<Student> findByNameContainingIgnoreCase(String name);

    // Check if student exists by email
    boolean existsByEmail(String email);

    // Delete student by ID
    void deleteByStudentId(int studentId);
}
