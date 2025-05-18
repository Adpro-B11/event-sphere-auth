package id.ac.ui.cs.advprog.eventsphereauth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import id.ac.ui.cs.advprog.eventsphereauth.security.JwtAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class SecurityConfigTest {
    @MockBean JwtAuthenticationFilter jwtAuthFilter;
    @MockBean UserDetailsService userDetailsService;
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