package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    private User testUser;
    private String validUserId;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);

        UUID validUserUUID = UUID.randomUUID();
        validUserId = validUserUUID.toString();
        testUser = new User(validUserId, "testuser", "test@example.com", "password123", "1234567890", 100.0);
    }

    private boolean isValidUUID(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    void testFindUsernameById() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        User mockUser = new User(userId, "john_doe", "john@example.com", "Password123", "08123456789");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        String username = userService.getUsernameById(userId);

        // Assert
        assertEquals("john_doe", username);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindUsernameById_NotFound() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUsernameById(userId);
        });
    }

    @Test
    void testAddBalance_PositiveAmount() {
        double initialBalance = testUser.getBalance();
        double amountToAdd = 50.0;

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(testUser));

        userService.addBalance(validUserId, amountToAdd);

        assertEquals(initialBalance + amountToAdd, testUser.getBalance());
        verify(userRepository).findById(validUserId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testAddBalance_ZeroAmount() {
        double amountToAdd = 0.0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addBalance(validUserId, amountToAdd)
        );

        assertEquals("Amount must be positive", exception.getMessage());
        verify(userRepository, never()).findById(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddBalance_NegativeAmount() {
        double amountToAdd = -50.0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addBalance(validUserId, amountToAdd)
        );

        assertEquals("Amount must be positive", exception.getMessage());
        verify(userRepository, never()).findById(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddBalance_IdInvalid() {
        String invalidUserId = "abc";
        double amountToAdd = 50.0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addBalance(invalidUserId, amountToAdd)
        );

        assertEquals("Invalid user ID format: " + invalidUserId, exception.getMessage());
        verify(userRepository, never()).findById(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddBalance_UserNotFound() {
        double amountToAdd = 50.0;
        when(userRepository.findById(validUserId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.addBalance(validUserId, amountToAdd)
        );

        assertEquals("User not found with ID: " + validUserId, exception.getMessage());
        verify(userRepository).findById(validUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeductBalance_PositiveAmount() {
        double initialBalance = testUser.getBalance();
        double amountToDeduct = 50.0;

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(testUser));
        
        boolean result = userService.deductBalance(validUserId, amountToDeduct);

        assertTrue(result);
        assertEquals(initialBalance - amountToDeduct, testUser.getBalance());
        verify(userRepository).findById(validUserId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testDeductBalance_EqualBalance() {
        double amountToDeduct = testUser.getBalance();

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(testUser));

        boolean result = userService.deductBalance(validUserId, amountToDeduct);

        assertTrue(result);
        assertEquals(0.0, testUser.getBalance());
        verify(userRepository).findById(validUserId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testDeductBalance_ExceededBalance() {
        double initialBalance = testUser.getBalance();
        double amountToDeduct = initialBalance + 50.0;

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(testUser));

        boolean result = userService.deductBalance(validUserId, amountToDeduct);

        assertFalse(result);
        assertEquals(initialBalance, testUser.getBalance());
        verify(userRepository).findById(validUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeductBalance_ZeroAmount() {
        double amountToDeduct = 0.0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deductBalance(validUserId, amountToDeduct)
        );

        assertEquals("Amount must be positive", exception.getMessage());
        verify(userRepository, never()).findById(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeductBalance_NegativeAmount() {
        double amountToDeduct = -50.0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deductBalance(validUserId, amountToDeduct)
        );

        assertEquals("Amount must be positive", exception.getMessage());
        verify(userRepository, never()).findById(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeductBalance_IdInvalid() {
        String invalidUserId = "invalid-uuid";
        double amountToDeduct = 50.0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deductBalance(invalidUserId, amountToDeduct)
        );

        assertEquals("Invalid user ID format: " + invalidUserId, exception.getMessage());
        verify(userRepository, never()).findById(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeductBalance_UserNotFound() {
        double amountToDeduct = 50.0;
        when(userRepository.findById(validUserId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deductBalance(validUserId, amountToDeduct)
        );

        assertEquals("User not found with ID: " + validUserId, exception.getMessage());
        verify(userRepository).findById(validUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetBalance_UserFound() {
        when(userRepository.findById(validUserId)).thenReturn(Optional.of(testUser));

        double balance = userService.getBalance(validUserId);

        assertEquals(testUser.getBalance(), balance);
        verify(userRepository).findById(validUserId);
    }

    @Test
    void testGetBalance_IdUserInvalid() {
        String invalidUserId = "not-a-uuid";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getBalance(invalidUserId)
        );

        assertEquals("Invalid user ID format: not-a-uuid", exception.getMessage());
        verify(userRepository, never()).findById(any(String.class));
    }

    @Test
    void testGetBalance_UserNotFound() {
        when(userRepository.findById(validUserId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getBalance(validUserId)
        );

        assertEquals("User not found with ID: " + validUserId, exception.getMessage());
        verify(userRepository).findById(validUserId);
    }
}