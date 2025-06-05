package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserUpdateRequest;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private User adminUser;
    private UUID user1Id;
    private UUID user2Id;
    private UUID adminUserId;

    @BeforeEach
    void setUp() {
        user1Id = UUID.randomUUID();
        user2Id = UUID.randomUUID();
        adminUserId = UUID.randomUUID();

        user1 = new User();
        user1.setId(user1Id);
        user1.setUsername("userone");
        user1.setEmail("userone@example.com");
        user1.setPhoneNumber("111");
        user1.setRole(Role.USER);

        user2 = new User();
        user2.setId(user2Id);
        user2.setUsername("usertwo");
        user2.setEmail("usertwo@example.com");
        user2.setPhoneNumber("222");
        user2.setRole(Role.USER);

        adminUser = new User();
        adminUser.setId(adminUserId);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPhoneNumber("999");
        adminUser.setRole(Role.ADMIN);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllUsersSuccess() {
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> userResponses = userService.getAllUsers();

        assertNotNull(userResponses);
        assertEquals(2, userResponses.size());
        assertEquals(user1Id, userResponses.get(0).getId());
        assertEquals("userone", userResponses.get(0).getUsername());
        assertEquals(user2Id, userResponses.get(1).getId());
        assertEquals("usertwo", userResponses.get(1).getUsername());


        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByIdSuccess() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));

        UserResponse userResponse = userService.getUserById(user1Id);

        assertNotNull(userResponse);
        assertEquals(user1Id, userResponse.getId());
        assertEquals("userone", userResponse.getUsername());

        verify(userRepository, times(1)).findById(user1Id);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserById(user1Id);
        });

        assertEquals("User not found with id: " + user1Id, exception.getMessage());

        verify(userRepository, times(1)).findById(user1Id);
    }

    @Test
    void testGetAuthenticatedUserResponseSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("userone@example.com");
        when(authentication.getName()).thenReturn("userone@example.com");
        when(userRepository.findByEmail("userone@example.com")).thenReturn(Optional.of(user1));

        UserResponse userResponse = userService.getAuthenticatedUserResponse();

        assertNotNull(userResponse);
        assertEquals(user1Id, userResponse.getId());
        assertEquals("userone", userResponse.getUsername());


        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).isAuthenticated();
        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByEmail("userone@example.com");
    }


    @Test
    void testGetAuthenticatedUserResponseNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getAuthenticatedUserResponse();
        });

        assertEquals("No authenticated user found or user is anonymous.", exception.getMessage());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).isAuthenticated();
        verify(authentication, never()).getPrincipal();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUserSuccessCurrentUser() throws IllegalAccessException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("userone@example.com");
        when(authentication.getName()).thenReturn("userone@example.com");
        when(userRepository.findByEmail("userone@example.com")).thenReturn(Optional.of(user1));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        Mockito.lenient().when(userRepository.save(any(User.class))).thenReturn(user1);


        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .username("updatedUsername")
                .build();

        UserResponse updatedUserResponse = userService.updateUser(user1Id, updateRequest);

        assertNotNull(updatedUserResponse);
        assertEquals(user1Id, updatedUserResponse.getId());
        assertEquals("updatedUsername", updatedUserResponse.getUsername());


        verify(userRepository, times(1)).findById(user1Id);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserSuccessAdmin() throws IllegalAccessException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("admin@example.com");
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        Mockito.lenient().when(userRepository.save(any(User.class))).thenReturn(user1);


        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .username("updatedUsername")
                .build();

        UserResponse updatedUserResponse = userService.updateUser(user1Id, updateRequest);

        assertNotNull(updatedUserResponse);
        assertEquals(user1Id, updatedUserResponse.getId());
        assertEquals("updatedUsername", updatedUserResponse.getUsername());


        verify(userRepository, times(1)).findById(user1Id);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserUnauthorized() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("usertwo@example.com");
        when(authentication.getName()).thenReturn("usertwo@example.com");
        when(userRepository.findByEmail("usertwo@example.com")).thenReturn(Optional.of(user2));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .username("updatedUsername")
                .build();

        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            userService.updateUser(user1Id, updateRequest);
        });

        assertEquals("You are not authorized to update this user", exception.getMessage());

        verify(userRepository, times(1)).findById(user1Id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("userone@example.com");
        when(authentication.getName()).thenReturn("userone@example.com");
        when(userRepository.findByEmail("userone@example.com")).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.empty());

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .username("updatedUsername")
                .build();

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateUser(user2Id, updateRequest);
        });

        assertEquals("User to update not found with id: " + user2Id, exception.getMessage());

        verify(userRepository, times(1)).findById(user2Id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUserEmailAlreadyExists() throws IllegalAccessException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("userone@example.com");
        when(authentication.getName()).thenReturn("userone@example.com");
        when(userRepository.findByEmail("userone@example.com")).thenReturn(Optional.of(user1));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email("existing@example.com")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(user1Id, updateRequest);
        });

        assertEquals("New email is already in use by another account.", exception.getMessage());

        verify(userRepository, times(1)).findById(user1Id);
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void testDeleteUserSuccessAdmin() throws IllegalAccessException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("admin@example.com");
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        doNothing().when(userRepository).deleteById(user1Id);

        userService.deleteUser(user1Id);

        verify(userRepository, times(1)).findById(user1Id);
        verify(userRepository, times(1)).deleteById(user1Id);
    }

    @Test
    void testDeleteUserUnauthorizedNonAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("userone@example.com");
        when(authentication.getName()).thenReturn("userone@example.com");
        when(userRepository.findByEmail("userone@example.com")).thenReturn(Optional.of(user1));

        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            userService.deleteUser(user2Id);
        });

        assertEquals("Only admins can delete users", exception.getMessage());

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDeleteUserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("admin@example.com");
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(user1Id)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.deleteUser(user1Id);
        });

        assertEquals("User to delete not found with id: " + user1Id, exception.getMessage());

        verify(userRepository, times(1)).findById(user1Id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDeleteUserAdminDeletingSelf() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("admin@example.com");
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(adminUserId);
        });

        assertEquals("Admin cannot delete their own account through this operation.", exception.getMessage());

        verify(userRepository, times(1)).findById(adminUserId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testMapToUserResponse() {
        User user = new User();
        user.setId(user1Id);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setRole(Role.ORGANIZER);

        UserResponse userResponse = userService.mapToUserResponse(user);

        assertNotNull(userResponse);
        assertEquals(user1Id, userResponse.getId());
        assertEquals("testuser", userResponse.getUsername());
        assertEquals("test@example.com", userResponse.getEmail());
        assertEquals("1234567890", userResponse.getPhoneNumber());
        assertEquals(Role.ORGANIZER, userResponse.getRole());
        assertNull(userResponse.getBalance());
    }

    @Test
    void testMapToUserResponseNullInput() {
        UserResponse userResponse = userService.mapToUserResponse(null);

        assertNull(userResponse);
    }

    @Test
    void testAddBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(100.25);
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));

        userService.addBalance(user1Id.toString(), amount);

        verify(userRepository).save(argThat(u ->
                u.getId().equals(user1Id) && u.getBalance().compareTo(amount) == 0
        ));
    }

    @Test
    void testAddBalanceInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.addBalance(user1Id.toString(), BigDecimal.ZERO)
        );
    }

    @Test
    void testAddBalanceInvalidUUID() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.addBalance("not-a-uuid", BigDecimal.valueOf(10))
        );
    }

    @Test
    void testAddBalanceUserNotFound() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.addBalance(user1Id.toString(), BigDecimal.valueOf(10))
        );
    }

    @Test
    void testDeductBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(50);
        user1.setBalance(BigDecimal.valueOf(100));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));

        userService.deductBalance(user1Id.toString(), amount);

        verify(userRepository).save(argThat(u ->
                u.getId().equals(user1Id) && u.getBalance().compareTo(BigDecimal.valueOf(50)) == 0
        ));
    }

    @Test
    void testDeductBalanceInsufficientFunds() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        assertThrows(ResponseStatusException.class,
                () -> userService.deductBalance(user1Id.toString(), BigDecimal.valueOf(1))
        );
    }

    @Test
    void testDeductBalanceInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.deductBalance(user1Id.toString(), BigDecimal.ZERO)
        );
    }

    @Test
    void testDeductBalanceInvalidUUID() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.deductBalance("not-a-uuid", BigDecimal.valueOf(5))
        );
    }

    @Test
    void testDeductBalanceUserNotFound() {
        when(userRepository.findById(user2Id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.deductBalance(user2Id.toString(), BigDecimal.valueOf(10))
        );
    }

    @Test
    void testGetBalanceSuccess() {
        user1.setRole(Role.USER);
        user1.setBalance(BigDecimal.valueOf(123.45));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));

        BigDecimal balance = userService.getBalance(user1Id.toString());

        assertEquals(0, balance.compareTo(BigDecimal.valueOf(123.45)));
    }

    @Test
    void testGetBalanceInvalidUUID() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.getBalance("invalid")
        );
    }

    @Test
    void testGetBalanceUserNotFound() {
        when(userRepository.findById(user2Id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.getBalance(user2Id.toString())
        );
    }
}