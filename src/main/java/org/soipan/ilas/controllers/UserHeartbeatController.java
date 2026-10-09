package org.soipan.ilas.controllers;

import org.soipan.ilas.auth.AuthenticatedUser;
import org.soipan.ilas.dto.ApiResponse;
import org.soipan.ilas.services.AdminService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitoring")
public class UserHeartbeatController {
    private final AdminService adminService;

    public UserHeartbeatController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/heartbeat")
    public ApiResponse<Void> heartbeat(@AuthenticationPrincipal AuthenticatedUser user) {
        adminService.heartbeat(user.role(), user.userId());
        return new ApiResponse<>(true, "Heartbeat recorded", null);
    }
}
