package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.AuthResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.LoginRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.exception.AuthenticationException;
import id.ac.ui.cs.advprog.eventsphereauth.exception.UserAlreadyExistsException;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

    }

    @Override
    public AuthResponse login(LoginRequest request) {
        
    }
}