package org.soipan.ilas.auth;

import org.soipan.ilas.models.Instructor;
import org.soipan.ilas.models.Student;
import org.soipan.ilas.repository.AdminRepository;
import org.soipan.ilas.repository.InstructorRepository;
import org.soipan.ilas.repository.StudentRepository;
import org.soipan.ilas.services.SystemMonitoringService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final AdminRepository adminRepository;
    private final SystemMonitoringService monitoringService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;

    public AuthService(StudentRepository studentRepository, InstructorRepository instructorRepository,
                       AdminRepository adminRepository, SystemMonitoringService monitoringService,
                       PasswordEncoder passwordEncoder, JwtTokenService tokenService) {
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.adminRepository = adminRepository;
        this.monitoringService = monitoringService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthResponse login(AuthRequest request) {
        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())
                || isBlank(request.getUserType())) {
            throw new IllegalArgumentException("Username, password, and user type are required");
        }

        String username = request.getUsername().trim();
        String role = request.getUserType().trim().toLowerCase(Locale.ROOT);
        return switch (role) {
            case "student" -> studentRepository.findByUsername(username)
                    .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                    .map(user -> response(user.getStudentId(), user.getName(), user.getUsername(), user.getEmail(), role))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
            case "instructor" -> instructorRepository.findByUsername(username)
                    .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                    .map(user -> response(user.getInstructorId(), user.getName(), user.getUsername(), user.getEmail(), role))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
            case "admin" -> adminRepository.findByUsername(username)
                    .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                    .map(user -> response(user.getAdminId(), user.getName(), user.getUsername(), user.getEmail(), role))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
            default -> throw new IllegalArgumentException("Invalid user type");
        };
    }

    public AuthResponse signup(SignupRequest request) {
        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())
                || isBlank(request.getName()) || isBlank(request.getEmail()) || isBlank(request.getUserType())) {
            throw new IllegalArgumentException("Name, email, username, password, and user type are required");
        }

        String username = request.getUsername().trim();
        String email = request.getEmail().trim();
        String role = request.getUserType().trim().toLowerCase(Locale.ROOT);
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        return switch (role) {
            case "student" -> {
                if (studentRepository.findByUsername(username).isPresent()) {
                    throw new IllegalArgumentException("Username already exists");
                }
                if (studentRepository.findByEmail(email).isPresent()) {
                    throw new IllegalArgumentException("Email already exists");
                }
                Student student = studentRepository.save(new Student(request.getName().trim(), email,
                        username, encodedPassword));
                yield response(student.getStudentId(), student.getName(), username, email, role);
            }
            case "instructor" -> {
                if (instructorRepository.findByUsername(username).isPresent()) {
                    throw new IllegalArgumentException("Username already exists");
                }
                if (instructorRepository.findByEmail(email).isPresent()) {
                    throw new IllegalArgumentException("Email already exists");
                }
                Instructor instructor = instructorRepository.save(new Instructor(request.getName().trim(), email,
                        username, encodedPassword));
                yield response(instructor.getInstructorId(), instructor.getName(), username, email, role);
            }
            case "admin" -> throw new IllegalArgumentException("Admin accounts cannot be created through signup");
            default -> throw new IllegalArgumentException("Invalid user type");
        };
    }

    private AuthResponse response(int userId, String name, String username, String email, String role) {
        monitoringService.touchUser(role, userId);
        return new AuthResponse(userId, name, username, email, role,
                tokenService.createToken(userId, username, role));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

