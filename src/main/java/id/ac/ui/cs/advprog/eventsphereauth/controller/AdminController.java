package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/create-account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createAdminOrOrganizer(@RequestBody RegisterRequest request) throws IllegalAccessException {
        return ResponseEntity.ok(adminService.createAdminOrOrganizer(request));
    }
}