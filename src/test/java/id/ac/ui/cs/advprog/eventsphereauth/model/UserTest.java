package id.ac.ui.cs.advprog.eventsphereauth.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    }

    @Test
    void defaultRole_isUser_andBalanceZero() {
        assertEquals(Role.USER, user.getRole());
        assertEquals(null, user.getBalance());
    }

    @Test
    void allArgsConstructor_setsDefaultsCorrectly() {
        User newUser = new User(
                "anotheruser",
                "another@example.com",
                "0987654321",
                "anotherpassword"
        );
        assertEquals(Role.USER, newUser.getRole());
        assertEquals(null, newUser.getBalance());
    }

    @Test
    void attendee_canHoldPositiveBalance() {
        user.setRole(Role.USER);
        BigDecimal newAmount = BigDecimal.valueOf(120.75);

        assertDoesNotThrow(() -> user.setBalance(newAmount));
        assertEquals(newAmount, user.getBalance());
    }

    @Test
    void nonAttendee_settingNonZeroBalance_throwsException() {

        user.setRole(Role.ADMIN);
        assertThrows(IllegalStateException.class,
                () -> user.setBalance(BigDecimal.TEN));
    }

    @Test
    void negativeBalance_isNeverAllowed() {
        assertThrows(IllegalArgumentException.class,
                () -> user.setBalance(BigDecimal.valueOf(-1)));
    }

    @Test
    void authorities_password_username_flags_areCorrect() {
        Collection<?> authorities = user.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(
                new SimpleGrantedAuthority("ROLE_USER")
        ));
        assertEquals("password123", user.getPassword());
        assertEquals("testuser", user.getDisplayName());
        assertEquals("test@example.com", user.getUsername());

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}
