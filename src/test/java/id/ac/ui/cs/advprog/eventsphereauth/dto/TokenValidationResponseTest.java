package id.ac.ui.cs.advprog.eventsphereauth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenValidationResponseTest {

    @Test
    void testTokenValidationResponseConstructorAndGetters() {
        // Arrange
        boolean valid = true;
        String username = "testuser";
        String userId = "user-123";
        
        // Act
        TokenValidationResponse response = new TokenValidationResponse(valid, username, userId);
        
        // Assert
        assertEquals(valid, response.isValid());
        assertEquals(username, response.getUsername());
        assertEquals(userId, response.getUserId());
    }
    
    @Test
    void testTokenValidationResponseSetters() {
        // Arrange
        TokenValidationResponse response = new TokenValidationResponse();
        boolean valid = true;
        String username = "testuser";
        String userId = "user-123";
        
        // Act
        response.setValid(valid);
        response.setUsername(username);
        response.setUserId(userId);
        
        // Assert
        assertEquals(valid, response.isValid());
        assertEquals(username, response.getUsername());
        assertEquals(userId, response.getUserId());
    }
}