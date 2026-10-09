package org.soipan.ilas.auth;

import org.soipan.ilas.models.Admin;
import org.soipan.ilas.models.Instructor;
import org.soipan.ilas.models.Student;
import org.soipan.ilas.repository.AdminRepository;
import org.soipan.ilas.repository.InstructorRepository;
import org.soipan.ilas.repository.StudentRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PasswordHashMigration implements ApplicationRunner {
    private static final Pattern BCRYPT_HASH = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    private final StudentRepository students;
    private final InstructorRepository instructors;
    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;

    public PasswordHashMigration(StudentRepository students, InstructorRepository instructors,
                                  AdminRepository admins, PasswordEncoder passwordEncoder) {
        this.students = students;
        this.instructors = instructors;
        this.admins = admins;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int migrated = 0;
        for (Student student : students.findAll()) {
            if (needsHash(student.getPassword())) {
                student.setPassword(passwordEncoder.encode(student.getPassword()));
                students.save(student);
                migrated++;
            }
        }
        for (Instructor instructor : instructors.findAll()) {
            if (needsHash(instructor.getPassword())) {
                instructor.setPassword(passwordEncoder.encode(instructor.getPassword()));
                instructors.save(instructor);
                migrated++;
            }
        }
        for (Admin admin : admins.findAll()) {
            if (needsHash(admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
                admins.save(admin);
                migrated++;
            }
        }
        if (migrated > 0) {
            System.out.println("Encoded passwords for " + migrated + " existing account(s).");
        }
    }

    private boolean needsHash(String password) {
        return password != null && !BCRYPT_HASH.matcher(password).matches();
    }
}
