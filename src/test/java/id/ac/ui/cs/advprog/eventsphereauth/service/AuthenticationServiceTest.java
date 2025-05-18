package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.AuthResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.LoginRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.exception.AuthenticationException;
import id.ac.ui.cs.advprog.eventsphereauth.exception.UserAlreadyExistsException;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest(
                "testuser",
                "test@example.com",
                "Password123!",
                "1234567890"
        );

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        
        AuthResponse response = authenticationService.register(request);
        
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("jwt-token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "existingUser",
                "existing@example.com",
                "Password123!",
                "1234567890"
        );
        
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));
        
        assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationService.register(request);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("testuser", "Password123!");
        
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        
        AuthResponse response = authenticationService.login(request);
        
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void testLoginFailInvalidCredentials() {
        LoginRequest request = new LoginRequest("testuser", "wrongPassword");
        
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);
        
        assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(request);
        });
    }

    @Test
    void testLoginFailUserNotFound() {
        LoginRequest request = new LoginRequest("nonexistentuser", "Password123!");
        
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        
        assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(request);
        });
    }
}