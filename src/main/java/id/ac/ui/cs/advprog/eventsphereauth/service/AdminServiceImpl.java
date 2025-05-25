package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MetricsService metricsService;

    public UserResponse createAdminOrOrganizer(RegisterRequest request) throws IllegalAccessException {
        log.info("Admin operation: Creating {} account for email: {}", request.getRole(), request.getEmail());
        
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN) {
            log.warn("Unauthorized admin operation attempt by user: {}", currentUser.getEmail());
            throw new IllegalAccessException("Only admins can create admin or organizer accounts");
        }

        if (request.getRole() != Role.ADMIN && request.getRole() != Role.ORGANIZER) {
            log.warn("Invalid role creation attempt: {}", request.getRole());
            throw new IllegalArgumentException("Role must be ADMIN or ORGANIZER");
        }

        var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        
        metricsService.incrementAdminOperations();
        log.info("Admin operation successful: Created {} account for email: {}", request.getRole(), request.getEmail());

        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .role(savedUser.getRole())
                .balance(savedUser.getBalance())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}