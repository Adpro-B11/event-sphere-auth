package id.ac.ui.cs.advprog.eventsphereauth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testRegisterRequestConstructorAndGetters() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String phone = "1234567890";
        
        // Act
        RegisterRequest registerRequest = new RegisterRequest(username, email, password, phone);
        
        // Assert
        assertEquals(username, registerRequest.getUsername());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
        assertEquals(phone, registerRequest.getPhone());
    }
    
    @Test
    void testRegisterRequestSetters() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String phone = "1234567890";
        
        // Act
        registerRequest.setUsername(username);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setPhone(phone);
        
        // Assert
        assertEquals(username, registerRequest.getUsername());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
        assertEquals(phone, registerRequest.getPhone());
    }
}