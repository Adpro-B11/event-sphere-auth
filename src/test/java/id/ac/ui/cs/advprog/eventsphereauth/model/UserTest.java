package id.ac.ui.cs.advprog.eventsphereauth.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testCreateUser() {
        // Arrange
        String id = "user-123";
        String username = "testuser";
        String email = "test@example.com";
        String password = "Password123!";
        String phone = "1234567890";
        double balance = 10.0;

        // Act
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setBalance(balance);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(phone, user.getPhone());
        assertEquals(balance, user.getBalance());
    }

    @Test
    public void testInvalidEmail() {
        User user = new User();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail("invalid-email");
        });
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @Test
    public void testWeakPassword() {
        User user = new User();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            user.setPassword("weak");
        });
        assertTrue(exception.getMessage().contains("Password must be at least 8 characters"));
    }
}