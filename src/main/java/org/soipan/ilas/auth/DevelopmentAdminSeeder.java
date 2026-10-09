package org.soipan.ilas.auth;

import org.soipan.ilas.models.Admin;
import org.soipan.ilas.repository.AdminRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(Ordered.LOWEST_PRECEDENCE)
public class DevelopmentAdminSeeder implements ApplicationRunner {
    private static final String DEV_USERNAME = "admin";
    private static final String DEV_PASSWORD = "password123";

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DevelopmentAdminSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminRepository.findByUsername(DEV_USERNAME).isEmpty()) {
            adminRepository.save(new Admin("Development Administrator", "admin@localhost.test",
                    DEV_USERNAME, passwordEncoder.encode(DEV_PASSWORD)));
        }
    }
}
