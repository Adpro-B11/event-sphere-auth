package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.service.AdminService;
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
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private RegisterRequest registerRequest;
    private UserResponse userResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        registerRequest = RegisterRequest.builder()
                .username("newaccount")
                .email("new@example.com")
                .phoneNumber("123")
                .password("password")
                .role(Role.ORGANIZER)
                .build();

        userResponse = UserResponse.builder()
                .id(userId)
                .username("newaccount")
                .email("new@example.com")
                .phoneNumber("123")
                .role(Role.ORGANIZER)
                .balance(null)
                .build();
    }

    @Test
    void testCreateAdminOrOrganizerSuccess() throws IllegalAccessException {
        when(adminService.createAdminOrOrganizer(registerRequest)).thenReturn(userResponse);

        ResponseEntity<UserResponse> responseEntity = adminController.createAdminOrOrganizer(registerRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());

        verify(adminService, times(1)).createAdminOrOrganizer(registerRequest);
    }

    @Test
    void testCreateAdminOrOrganizerUnauthorized() throws IllegalAccessException {
        when(adminService.createAdminOrOrganizer(registerRequest)).thenThrow(new IllegalAccessException("Only admins can create accounts"));

        assertThrows(IllegalAccessException.class, () -> {
            adminController.createAdminOrOrganizer(registerRequest);
        });

        verify(adminService, times(1)).createAdminOrOrganizer(registerRequest);
    }

    @Test
    void testCreateAdminOrOrganizerInvalidRole() throws IllegalAccessException {
        when(adminService.createAdminOrOrganizer(registerRequest)).thenThrow(new IllegalArgumentException("Role must be ADMIN or ORGANIZER"));

        assertThrows(IllegalArgumentException.class, () -> {
            adminController.createAdminOrOrganizer(registerRequest);
        });

        verify(adminService, times(1)).createAdminOrOrganizer(registerRequest);
    }
}
