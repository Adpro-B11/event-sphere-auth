package id.ac.ui.cs.advprog.eventsphereauth.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserAlreadyExistsExceptionTest {

    @Test
    void testUserAlreadyExistsExceptionMessage() {
        // Arrange
        String errorMessage = "Username already exists";
        
        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
    
    @Test
    void testUserAlreadyExistsExceptionIsRuntimeException() {
        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Error message");
        
        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}