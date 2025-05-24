package id.ac.ui.cs.advprog.eventsphereauth.repository;

import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("testuser1");
        user1.setEmail("test1@example.com");
        user1.setPhoneNumber("111");
        user1.setPassword("password");
        user1.setRole(Role.ATTENDEE);
        user1.setBalance(BigDecimal.ZERO);

        user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");
        user2.setPhoneNumber("222");
        user2.setPassword("password");
        user2.setRole(Role.ATTENDEE);
        user2.setBalance(BigDecimal.valueOf(50.0));

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
    }

    @Test
    void testFindByEmailFound() {
        Optional<User> foundUser = userRepository.findByEmail("test1@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(user1.getEmail(), foundUser.get().getEmail());
        assertEquals(user1.getDisplayName(), foundUser.get().getDisplayName());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testExistsByEmailTrue() {
        boolean exists = userRepository.existsByEmail("test1@example.com");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmailFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void testSaveUser() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPhoneNumber("333");
        newUser.setPassword("newpassword");
        newUser.setRole(Role.ATTENDEE);
        newUser.setBalance(BigDecimal.valueOf(10.0));

        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getDisplayName());
        assertEquals("new@example.com", savedUser.getEmail());

        Optional<User> foundSavedUser = userRepository.findById(savedUser.getId());
        assertTrue(foundSavedUser.isPresent());
        assertEquals("newuser", foundSavedUser.get().getDisplayName());
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(user1);

        Optional<User> foundUser = userRepository.findById(user1.getId());
        assertFalse(foundUser.isPresent());

        Optional<User> foundUser2 = userRepository.findById(user2.getId());
        assertTrue(foundUser2.isPresent());
    }
}
