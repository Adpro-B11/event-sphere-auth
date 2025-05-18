package id.ac.ui.cs.advprog.eventsphereauth.repository;

import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    void testSaveAndFindUser() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "john_doe", "john@example.com", "Password123", "08123456789");

        // Act
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(userId);

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("john_doe", foundUser.get().getUsername());
        assertEquals("john@example.com", foundUser.get().getEmail());
        assertEquals("08123456789", foundUser.get().getPhone());
    }

    @Test
    void testFindUserNotFound() {
        // Arrange
        String userId = UUID.randomUUID().toString();

        // Act
        Optional<User> foundUser = userRepository.findById(userId);

        // Assert
        assertFalse(foundUser.isPresent());
    }
}