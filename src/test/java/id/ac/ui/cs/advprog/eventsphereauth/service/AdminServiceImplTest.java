package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User adminUser;
    private RegisterRequest registerRequestOrganizer;
    private RegisterRequest registerRequestUser;
    private UUID adminUserId;
    private UUID newUserId;

    @BeforeEach
    void setUp() {
        adminUserId = UUID.randomUUID();
        newUserId = UUID.randomUUID();

        adminUser = new User();
        adminUser.setId(adminUserId);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPhoneNumber("999");
        adminUser.setRole(Role.ADMIN);

        registerRequestOrganizer = RegisterRequest.builder()
                .username("organizer")
                .email("organizer@example.com")
                .phoneNumber("111")
                .password("password")
                .role(Role.ORGANIZER)
                .build();

        registerRequestUser = RegisterRequest.builder()
                .username("regularuser")
                .email("regular@example.com")
                .phoneNumber("222")
                .password("password")
                .role(Role.USER)
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateAdminOrOrganizerSuccessOrganizer() throws IllegalAccessException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        User newUser = new User();
        newUser.setId(newUserId);
        newUser.setUsername(registerRequestOrganizer.getUsername());
        newUser.setEmail(registerRequestOrganizer.getEmail());
        newUser.setPhoneNumber(registerRequestOrganizer.getPhoneNumber());
        newUser.setPassword("encodedPassword");
        newUser.setRole(registerRequestOrganizer.getRole());

        when(passwordEncoder.encode(registerRequestOrganizer.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserResponse userResponse = adminService.createAdminOrOrganizer(registerRequestOrganizer);

        assertNotNull(userResponse);
        assertEquals(newUserId, userResponse.getId());
        assertEquals("organizer@example.com", userResponse.getUsername());
        assertEquals(Role.ORGANIZER, userResponse.getRole());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByEmail("admin@example.com");
        verify(passwordEncoder, times(1)).encode(registerRequestOrganizer.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateAdminOrOrganizerSuccessAdmin() throws IllegalAccessException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        RegisterRequest registerRequestAdmin = RegisterRequest.builder()
                .username("newadmin")
                .email("newadmin@example.com")
                .phoneNumber("333")
                .password("password")
                .role(Role.ADMIN)
                .build();

        User newUser = new User();
        newUser.setId(newUserId);
        newUser.setUsername(registerRequestAdmin.getUsername());
        newUser.setEmail(registerRequestAdmin.getEmail());
        newUser.setPhoneNumber(registerRequestAdmin.getPhoneNumber());
        newUser.setPassword("encodedPassword");
        newUser.setRole(registerRequestAdmin.getRole());

        when(passwordEncoder.encode(registerRequestAdmin.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserResponse userResponse = adminService.createAdminOrOrganizer(registerRequestAdmin);

        assertNotNull(userResponse);
        assertEquals(newUserId, userResponse.getId());
        assertEquals("newadmin@example.com", userResponse.getUsername());
        assertEquals(Role.ADMIN, userResponse.getRole());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByEmail("admin@example.com");
        verify(passwordEncoder, times(1)).encode(registerRequestAdmin.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateAdminOrOrganizerUnauthorizedNonAdmin() {
        User regularUser = new User();
        regularUser.setId(UUID.randomUUID());
        regularUser.setUsername("user");
        regularUser.setEmail("user@example.com");
        regularUser.setPhoneNumber("111");
        regularUser.setRole(Role.USER);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(regularUser));

        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            adminService.createAdminOrOrganizer(registerRequestOrganizer);
        });

        assertEquals("Only admins can create admin or organizer accounts", exception.getMessage());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByEmail("user@example.com");
        verifyNoMoreInteractions(passwordEncoder, userRepository); // No further interactions
    }

    @Test
    void testCreateAdminOrOrganizerInvalidRole() throws IllegalAccessException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.createAdminOrOrganizer(registerRequestUser); // Trying to create a regular user
        });

        assertEquals("Role must be ADMIN or ORGANIZER", exception.getMessage());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByEmail("admin@example.com");
        verifyNoMoreInteractions(passwordEncoder, userRepository); // No further interactions
    }

    @Test
    void testGetCurrentUserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.createAdminOrOrganizer(registerRequestOrganizer);
        });

        assertEquals("Current user not found", exception.getMessage());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }
}
