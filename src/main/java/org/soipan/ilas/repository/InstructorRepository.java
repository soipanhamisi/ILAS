package org.soipan.ilas.repository;

import org.soipan.ilas.models.Instructor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends CrudRepository<Instructor, Integer> {

    // Find instructor by ID
    Optional<Instructor> findByInstructorId(int instructorId);

    // Find instructor by email
    Optional<Instructor> findByEmail(String email);

    // Find instructor by username
    Optional<Instructor> findByUsername(String username);

    // Find instructor by name
    List<Instructor> findByName(String name);

    // Find instructors by name containing (search)
    List<Instructor> findByNameContainingIgnoreCase(String name);

    // Check if instructor exists by email
    boolean existsByEmail(String email);

    // Delete instructor by ID
    void deleteByInstructorId(int instructorId);
}

