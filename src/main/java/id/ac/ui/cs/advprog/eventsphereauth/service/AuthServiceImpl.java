package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.AuthResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.LoginRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import id.ac.ui.cs.advprog.eventsphereauth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final MetricsService metricsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Registration failed - email already exists: {}", request.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }

            Role role = request.getRole() != null ? request.getRole() : Role.USER;

            var user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(role);

            User savedUser = userRepository.save(user);

            var jwtToken = jwtService.generateToken(savedUser);
            UserResponse userResponse = userService.mapToUserResponse(savedUser);

            // Increment metrics
            metricsService.incrementUserRegistrations();
            metricsService.incrementJwtTokenGeneration();

            log.info("User registration successful for email: {}", request.getEmail());

            return AuthResponse.builder()
                    .token(jwtToken)
                    .user(userResponse)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration for email: {}", request.getEmail(), e);
            throw new RuntimeException("Registration failed due to system error", e);
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        try {
            // Authenticate user
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );
            } catch (BadCredentialsException e) {
                metricsService.incrementLoginFailures();
                log.warn("Authentication failed for email: {}", request.getEmail());
                throw new BadCredentialsException("Invalid email or password");
            }

            // Find user
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        metricsService.incrementLoginFailures();
                        log.error("User not found after successful authentication: {}", request.getEmail());
                        return new UsernameNotFoundException("User not found with email: " + request.getEmail());
                    });

            // Generate JWT token
            var jwtToken = jwtService.generateToken(user);
            UserResponse userResponse = userService.mapToUserResponse(user);

            // Increment metrics for successful login
            metricsService.incrementUserLogins();
            metricsService.incrementJwtTokenGeneration();

            log.info("Login successful for email: {}", request.getEmail());

            return AuthResponse.builder()
                    .token(jwtToken)
                    .user(userResponse)
                    .build();
                    
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            // These are expected exceptions, just rethrow
            throw e;
        } catch (Exception e) {
            metricsService.incrementLoginFailures();
            log.error("Unexpected error during login for email: {}", request.getEmail(), e);
            throw new RuntimeException("Login failed due to system error", e);
        }
    }
}