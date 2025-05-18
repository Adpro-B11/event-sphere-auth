package id.ac.ui.cs.advprog.eventsphereauth.repository;

import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        
        // Create a test user
        String userId = UUID.randomUUID().toString();
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setPhone("1234567890");
        testUser.setBalance(100.0);
        
        userRepository.save(testUser);
    }

    @Test
    void testFindById() {
        // Act
        Optional<User> foundUser = userRepository.findById(testUser.getId());
        
        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }
    
    @Test
    void testFindByIdNonExistent() {
        // Act
        Optional<User> foundUser = userRepository.findById("non-existent-id");
        
        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername() {
        // Act
        Optional<User> foundUser = userRepository.findByUsername(testUser.getUsername());
        
        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }
    
    @Test
    void testFindByUsernameNonExistent() {
        // Act
        Optional<User> foundUser = userRepository.findByUsername("non-existent-username");
        
        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByEmail() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail(testUser.getEmail());
        
        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
    }
    
    @Test
    void testFindByEmailNonExistent() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("non-existent@example.com");
        
        // Assert
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    void testSave() {
        // Arrange
        User newUser = new User();
        String newUserId = UUID.randomUUID().toString();
        newUser.setId(newUserId);
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        
        // Act
        userRepository.save(newUser);
        Optional<User> foundUser = userRepository.findById(newUserId);
        
        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(newUserId, foundUser.get().getId());
        assertEquals("newuser", foundUser.get().getUsername());
    }
}