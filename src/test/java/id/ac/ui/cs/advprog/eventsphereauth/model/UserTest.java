package id.ac.ui.cs.advprog.eventsphereauth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setPassword("password123");
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.ZERO);
    }

    @Test
    void testUserCreationWithNoArgsConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
        // Default values should be set
        assertEquals(Role.USER, newUser.getRole());
        assertEquals(BigDecimal.ZERO, newUser.getBalance());
    }

    @Test
    void testUserCreationWithAllArgsConstructor() {
        User newUser = new User("anotheruser", "another@example.com", "0987654321", "anotherpassword");
        assertNotNull(newUser);
        assertEquals("anotheruser", newUser.getDisplayName());
        assertEquals("another@example.com", newUser.getEmail());
        assertEquals("0987654321", newUser.getPhoneNumber());
        assertEquals("anotherpassword", newUser.getPassword());
        assertEquals(Role.USER, newUser.getRole());
        assertEquals(BigDecimal.ZERO, newUser.getBalance());
    }


    @Test
    void testGettersAndSetters() {
        assertEquals(userId, user.getId());
        assertEquals("testuser", user.getDisplayName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.USER, user.getRole());
        assertEquals(BigDecimal.ZERO, user.getBalance());

        UUID newId = UUID.randomUUID();
        user.setId(newId);
        user.setUsername("updateduser");
        user.setEmail("updated@example.com");
        user.setPhoneNumber("0000000000");
        user.setPassword("newpassword");
        user.setRole(Role.ADMIN);
        user.setBalance(BigDecimal.valueOf(100.50));

        assertEquals(newId, user.getId());
        assertEquals("updateduser", user.getDisplayName());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("0000000000", user.getPhoneNumber());
        assertEquals("newpassword", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals(BigDecimal.valueOf(100.50), user.getBalance());
    }

    @Test
    void testGetAuthorities() {
        Collection<?> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")));

        user.setRole(Role.ADMIN);
        authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testGetDisplayName() {
        assertEquals("testuser", user.getDisplayName());
    }

    @Test
    void testGetUsernameFromUserDetails() {
        // UserDetails.getUsername() should return email
        assertEquals("test@example.com", user.getUsername());
    }

    @Test
    void testAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testEnabled() {
        assertTrue(user.isEnabled());
    }
}
