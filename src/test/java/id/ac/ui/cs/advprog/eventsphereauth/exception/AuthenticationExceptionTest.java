package id.ac.ui.cs.advprog.eventsphereauth.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    @Test
    void testAuthenticationExceptionMessage() {
        String errorMessage = "Invalid credentials";
        
        AuthenticationException exception = new AuthenticationException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
    }
    
    @Test
    void testAuthenticationExceptionIsRuntimeException() {
        AuthenticationException exception = new AuthenticationException("Error message");
        
        assertTrue(exception instanceof RuntimeException);
    }
}