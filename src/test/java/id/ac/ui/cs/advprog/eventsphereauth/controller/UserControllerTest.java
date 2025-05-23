package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserUpdateRequest;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private UUID userId;
    private UUID otherId;
    private UUID adminId;

    private UserResponse respUser;
    private UserResponse respOther;
    private UserResponse respAdmin;

    @BeforeEach
    void setUp() {
        userId  = UUID.randomUUID();
        otherId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        respUser = UserResponse.builder()
                .id(userId)
                .username("attendeeOne")
                .email("one@example.com")
                .phoneNumber("111")
                .role(Role.ATTENDEE)
                .balance(BigDecimal.ZERO)
                .build();

        respOther = UserResponse.builder()
                .id(otherId)
                .username("attendeeTwo")
                .email("two@example.com")
                .phoneNumber("222")
                .role(Role.ATTENDEE)
                .balance(BigDecimal.ZERO)
                .build();

        respAdmin = UserResponse.builder()
                .id(adminId)
                .username("adminGuy")
                .email("admin@example.com")
                .phoneNumber("999")
                .role(Role.ADMIN)
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void addBalance_success_attendee() {
        BigDecimal amount = BigDecimal.valueOf(50);
        BigDecimal newBal = BigDecimal.valueOf(150);

        doNothing().when(userService)
                .addBalance(userId.toString(), amount);
        when(userService.getBalance(userId.toString()))
                .thenReturn(newBal);

        ResponseEntity<BigDecimal> resp =
                userController.addBalance(userId, Map.of("amount", amount));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(newBal, resp.getBody());
        verify(userService).addBalance(userId.toString(), amount);
        verify(userService).getBalance(userId.toString());
    }

    @Test
    void addBalance_nonAttendee_shouldThrow() {
        BigDecimal amount = BigDecimal.TEN;

        doThrow(new IllegalStateException("Only ATTENDEE can have a non-zero balance"))
                .when(userService).addBalance(adminId.toString(), amount);

        assertThrows(IllegalStateException.class,
                () -> userController.addBalance(adminId, Map.of("amount", amount)));

        verify(userService).addBalance(adminId.toString(), amount);
    }

    @Test
    void deductBalance_success_attendee() {
        BigDecimal amount     = BigDecimal.valueOf(20);
        BigDecimal newBalance = BigDecimal.valueOf(30);

        doNothing().when(userService)
                .deductBalance(userId.toString(), amount);
        when(userService.getBalance(userId.toString()))
                .thenReturn(newBalance);

        ResponseEntity<BigDecimal> resp =
                userController.deductBalance(userId, Map.of("amount", amount));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(newBalance, resp.getBody());
        verify(userService).deductBalance(userId.toString(), amount);
        verify(userService).getBalance(userId.toString());
    }

    @Test
    void deductBalance_insufficient_shouldThrow() {
        BigDecimal amount = BigDecimal.valueOf(200);

        doThrow(new IllegalArgumentException("Insufficient funds"))
                .when(userService).deductBalance(userId.toString(), amount);

        assertThrows(IllegalArgumentException.class,
                () -> userController.deductBalance(userId, Map.of("amount", amount)));

        verify(userService).deductBalance(userId.toString(), amount);
    }

    @Test
    void getAllUsers_success() {
        when(userService.getAllUsers()).thenReturn(List.of(respUser, respOther));

        ResponseEntity<List<UserResponse>> resp = userController.getAllUsers();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(2, Objects.requireNonNull(resp.getBody()).size());
        verify(userService).getAllUsers();
    }

    @Test
    void getMe_success() {
        when(userService.getAuthenticatedUserResponse()).thenReturn(respUser);

        ResponseEntity<UserResponse> resp = userController.getMe();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(respUser, resp.getBody());
        verify(userService).getAuthenticatedUserResponse();
    }

    @Test
    void getMe_notFound() {
        when(userService.getAuthenticatedUserResponse())
                .thenThrow(new UsernameNotFoundException("No authenticated user"));

        assertThrows(UsernameNotFoundException.class, () -> userController.getMe());
    }

    @Test
    void updateUser_success() throws Exception {
        UserUpdateRequest req = UserUpdateRequest.builder().username("upd").build();
        when(userService.updateUser(userId, req)).thenReturn(respUser);

        ResponseEntity<UserResponse> resp = userController.updateUser(userId, req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(userService).updateUser(userId, req);
    }

    @Test
    void deleteUser_success() throws Exception {
        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<Void> resp = userController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(userService).deleteUser(userId);
    }
}
