package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserUpdateRequest;
import id.ac.ui.cs.advprog.eventsphereauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.getAuthenticatedUserResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest updateRequest
    ) throws IllegalAccessException {
        return ResponseEntity.ok(userService.updateUser(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) throws IllegalAccessException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}