package id.ac.ui.cs.advprog.eventsphereauth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void testAuthResponseConstructorAndGetters() {
        // Arrange
        String token = "jwt-token-example";
        
        // Act
        AuthResponse authResponse = new AuthResponse(token);
        
        // Assert
        assertEquals(token, authResponse.getToken());
    }
    
    @Test
    void testAuthResponseSetters() {
        // Arrange
        AuthResponse authResponse = new AuthResponse();
        String token = "jwt-token-example";
        
        // Act
        authResponse.setToken(token);
        
        // Assert
        assertEquals(token, authResponse.getToken());
    }
}