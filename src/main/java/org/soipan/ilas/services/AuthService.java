package org.soipan.ilas.services;

import org.soipan.ilas.dto.AuthRequest;
import org.soipan.ilas.dto.AuthResponse;
import org.soipan.ilas.dto.SignupRequest;
import org.soipan.ilas.models.Admin;
import org.soipan.ilas.models.Instructor;
import org.soipan.ilas.models.Student;
import org.soipan.ilas.repository.AdminRepository;
import org.soipan.ilas.repository.InstructorRepository;
import org.soipan.ilas.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for authentication operations
 * Handles login and signup for students, instructors, and admins
 */
@Service
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Login a user with username and password
     * @param request AuthRequest containing username, password, and userType
     * @return AuthResponse with user details and token
     * @throws IllegalArgumentException if credentials are invalid
     */
    public AuthResponse login(AuthRequest request) {
        String userType = request.getUserType();
        String username = request.getUsername();
        String password = request.getPassword();

        if ("student".equalsIgnoreCase(userType)) {
            Optional<Student> studentOpt = studentRepository.findByUsername(username);
            if (studentOpt.isEmpty()) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            Student student = studentOpt.get();
            if (!student.getPassword().equals(password)) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            return new AuthResponse(
                    student.getStudentId(),
                    student.getName(),
                    student.getUsername(),
                    student.getEmail(),
                    "student",
                    generateToken()
            );
        } else if ("instructor".equalsIgnoreCase(userType)) {
            Optional<Instructor> instructorOpt = instructorRepository.findByUsername(username);
            if (instructorOpt.isEmpty()) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            Instructor instructor = instructorOpt.get();
            if (!instructor.getPassword().equals(password)) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            return new AuthResponse(
                    instructor.getInstructorId(),
                    instructor.getName(),
                    instructor.getUsername(),
                    instructor.getEmail(),
                    "instructor",
                    generateToken()
            );
        } else if ("admin".equalsIgnoreCase(userType)) {
            Optional<Admin> adminOpt = adminRepository.findByUsername(username);
            if (adminOpt.isEmpty()) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            Admin admin = adminOpt.get();
            if (!admin.getPassword().equals(password)) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            return new AuthResponse(
                    admin.getAdminId(),
                    admin.getName(),
                    admin.getUsername(),
                    admin.getEmail(),
                    "admin",
                    generateToken()
            );
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }
    }

    /**
     * Signup a new user
     * @param request SignupRequest containing user details
     * @return AuthResponse with user details and token
     * @throws IllegalArgumentException if username already exists
     */
    public AuthResponse signup(SignupRequest request) {
        String userType = request.getUserType();
        String username = request.getUsername();
        String email = request.getEmail();
        String name = request.getName();
        String password = request.getPassword();

        if ("student".equalsIgnoreCase(userType)) {
            // Check if username or email already exists
            if (studentRepository.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            if (studentRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Create new student
            Student student = new Student(name, email, username, password);
            student = studentRepository.save(student);

            return new AuthResponse(
                    student.getStudentId(),
                    student.getName(),
                    student.getUsername(),
                    student.getEmail(),
                    "student",
                    generateToken()
            );
        } else if ("instructor".equalsIgnoreCase(userType)) {
            // Check if username or email already exists
            if (instructorRepository.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            if (instructorRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Create new instructor
            Instructor instructor = new Instructor(name, email, username, password);
            instructor = instructorRepository.save(instructor);

            return new AuthResponse(
                    instructor.getInstructorId(),
                    instructor.getName(),
                    instructor.getUsername(),
                    instructor.getEmail(),
                    "instructor",
                    generateToken()
            );
        } else if ("admin".equalsIgnoreCase(userType)) {
            // Check if username or email already exists
            if (adminRepository.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
            if (adminRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Create new admin
            Admin admin = new Admin(name, email, username, password);
            admin = adminRepository.save(admin);

            return new AuthResponse(
                    admin.getAdminId(),
                    admin.getName(),
                    admin.getUsername(),
                    admin.getEmail(),
                    "admin",
                    generateToken()
            );
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }
    }

    /**
     * Generate a simple token (in production, use JWT)
     * @return token string
     */
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}

