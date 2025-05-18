package id.ac.ui.cs.advprog.eventsphereauth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoderBeanCreation() {
        // Assert
        assertNotNull(passwordEncoder);
    }
    
    @Test
    void testPasswordEncoderEncoding() {
        // Arrange
        String rawPassword = "password123";
        
        // Act
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // Assert
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}