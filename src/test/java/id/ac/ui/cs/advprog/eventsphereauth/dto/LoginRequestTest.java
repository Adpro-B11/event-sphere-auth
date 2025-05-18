package id.ac.ui.cs.advprog.eventsphereauth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testLoginRequestConstructorAndGetters() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        
        // Act
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        // Assert
        assertEquals(username, loginRequest.getUsername());
        assertEquals(password, loginRequest.getPassword());
    }
    
    @Test
    void testLoginRequestSetters() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        String username = "testuser";
        String password = "password123";
        
        // Act
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        
        // Assert
        assertEquals(username, loginRequest.getUsername());
        assertEquals(password, loginRequest.getPassword());
    }
}