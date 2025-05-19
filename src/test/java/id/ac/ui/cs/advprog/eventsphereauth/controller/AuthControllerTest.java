package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.dto.AuthResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.LoginRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private UserResponse userResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .password("password")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        userResponse = UserResponse.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .role(Role.USER)
                .balance(null)
                .build();

        authResponse = AuthResponse.builder()
                .token("mockedToken")
                .user(userResponse)
                .build();
    }

    @Test
    void testRegisterSuccess() {
        when(authService.register(registerRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> responseEntity = authController.register(registerRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(authResponse, responseEntity.getBody());

        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void testRegisterFailure() {
        when(authService.register(registerRequest)).thenThrow(new IllegalArgumentException("Email already exists"));

        assertThrows(IllegalArgumentException.class, () -> {
            authController.register(registerRequest);
        });

        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void testLoginSuccess() {
        when(authService.login(loginRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> responseEntity = authController.login(loginRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(authResponse, responseEntity.getBody());

        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void testLoginFailure() {
        when(authService.login(loginRequest)).thenThrow(new IllegalArgumentException("User not found"));

        assertThrows(IllegalArgumentException.class, () -> {
            authController.login(loginRequest);
        });

        verify(authService, times(1)).login(loginRequest);
    }
}
