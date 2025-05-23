package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserUpdateRequest;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private User adminUser;
    private UUID user1Id;
    private UUID user2Id;
    private UUID adminUserId;

    @BeforeEach
    void setUp() {
        user1Id = UUID.randomUUID();
        user2Id = UUID.randomUUID();
        adminUserId = UUID.randomUUID();

        user1 = new User();
        user1.setId(user1Id);
        user1.setUsername("userone");
        user1.setEmail("userone@example.com");
        user1.setPhoneNumber("111");
        user1.setRole(Role.ATTENDEE);
        user1.setBalance(BigDecimal.ZERO);

        user2 = new User();
        user2.setId(user2Id);
        user2.setUsername("usertwo");
        user2.setEmail("usertwo@example.com");
        user2.setPhoneNumber("222");
        user2.setRole(Role.ATTENDEE);
        user2.setBalance(BigDecimal.ZERO);

        adminUser = new User();
        adminUser.setId(adminUserId);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPhoneNumber("999");
        adminUser.setRole(Role.ATTENDEE);
        adminUser.setBalance(BigDecimal.ZERO);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testAddBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(100.25);
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));

        userService.addBalance(user1Id.toString(), amount);

        verify(userRepository).save(argThat(u ->
                u.getId().equals(user1Id) && u.getBalance().compareTo(amount) == 0
        ));
    }

    @Test
    void testAddBalanceInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.addBalance(user1Id.toString(), BigDecimal.ZERO)
        );
    }

    @Test
    void testAddBalanceInvalidUUID() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.addBalance("not-a-uuid", BigDecimal.valueOf(10))
        );
    }

    @Test
    void testAddBalanceUserNotFound() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.addBalance(user1Id.toString(), BigDecimal.valueOf(10))
        );
    }

    @Test
    void testDeductBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(50);
        user1.setBalance(BigDecimal.valueOf(100));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));

        userService.deductBalance(user1Id.toString(), amount);

        verify(userRepository).save(argThat(u ->
                u.getId().equals(user1Id) && u.getBalance().compareTo(BigDecimal.valueOf(50)) == 0
        ));
    }

    @Test
    void testDeductBalanceInsufficientFunds() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        assertThrows(IllegalArgumentException.class,
                () -> userService.deductBalance(user1Id.toString(), BigDecimal.valueOf(1))
        );
    }

    @Test
    void testDeductBalanceInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.deductBalance(user1Id.toString(), BigDecimal.ZERO)
        );
    }

    @Test
    void testDeductBalanceInvalidUUID() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.deductBalance("not-a-uuid", BigDecimal.valueOf(5))
        );
    }

    @Test
    void testDeductBalanceUserNotFound() {
        when(userRepository.findById(user2Id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.deductBalance(user2Id.toString(), BigDecimal.valueOf(10))
        );
    }

    @Test
    void testGetBalanceSuccess() {
        user1.setBalance(BigDecimal.valueOf(123.45));
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));

        BigDecimal balance = userService.getBalance(user1Id.toString());

        assertEquals(0, balance.compareTo(BigDecimal.valueOf(123.45)));
    }

    @Test
    void testGetBalanceInvalidUUID() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.getBalance("invalid")
        );
    }

    @Test
    void testGetBalanceUserNotFound() {
        when(userRepository.findById(user2Id)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> userService.getBalance(user2Id.toString())
        );
    }
}
