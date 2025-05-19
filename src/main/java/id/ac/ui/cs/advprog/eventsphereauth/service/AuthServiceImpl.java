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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
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

        return AuthResponse.builder()
                .token(jwtToken)
                .user(userResponse)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        var jwtToken = jwtService.generateToken(user);
        UserResponse userResponse = userService.mapToUserResponse(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .user(userResponse)
                .build();
    }
}