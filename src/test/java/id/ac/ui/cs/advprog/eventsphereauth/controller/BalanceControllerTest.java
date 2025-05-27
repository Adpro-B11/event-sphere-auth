package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private BalanceController balanceController;

    private UserResponse userResponse1;
    private UserResponse userResponse2;
    private UUID user1Id;
    private UUID user2Id;

    @BeforeEach
    void setUp() {
        user1Id = UUID.randomUUID();
        user2Id = UUID.randomUUID();

        userResponse1 = UserResponse.builder()
                .id(user1Id)
                .username("userone")
                .email("userone@example.com")
                .phoneNumber("111")
                .role(Role.USER)
                .balance(BigDecimal.ZERO)
                .build();

        userResponse2 = UserResponse.builder()
                .id(user2Id)
                .username("usertwo")
                .email("usertwo@example.com")
                .phoneNumber("222")
                .role(Role.USER)
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void addBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(50);
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", amount.doubleValue());

        doNothing().when(userService).addBalance(user1Id.toString(), amount);
        when(userService.getBalance(user1Id.toString())).thenReturn(amount);

        ResponseEntity<Void> response = balanceController.addBalance(user1Id, payload);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).addBalance(user1Id.toString(), amount);
        verify(userService, times(1)).getBalance(user1Id.toString());
    }

    @Test
    void addBalanceUserNotFoundReturns404() {
        BigDecimal amount = BigDecimal.valueOf(50);
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", amount.doubleValue());

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService).addBalance(user1Id.toString(), amount);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> balanceController.addBalance(user1Id, payload));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(userService).addBalance(user1Id.toString(), amount);
    }

    @Test
    void deductBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(30);
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", amount.doubleValue());

        doNothing().when(userService).deductBalance(user1Id.toString(), amount);
        when(userService.getBalance(user1Id.toString())).thenReturn(BigDecimal.ZERO);

        ResponseEntity<Void> response = balanceController.deductBalance(user1Id, payload);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).deductBalance(user1Id.toString(), amount);
        verify(userService).getBalance(user1Id.toString());
    }

    @Test
    void deductBalanceInsufficientFundsReturns400() {
        BigDecimal amount = BigDecimal.valueOf(200);
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", amount.doubleValue());

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(userService).deductBalance(user1Id.toString(), amount);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> balanceController.deductBalance(user1Id, payload));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userService).deductBalance(user1Id.toString(), amount);
    }

    @Test
    void deductBalanceSystemErrorReturns500() {
        BigDecimal amount = BigDecimal.valueOf(50);
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", amount.doubleValue());

        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(userService).deductBalance(user1Id.toString(), amount);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> balanceController.deductBalance(user1Id, payload));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        verify(userService).deductBalance(user1Id.toString(), amount);
    }

    @Test
    void getBalanceSuccess() {
        BigDecimal balance = BigDecimal.valueOf(123.45);
        when(userService.getBalance(user1Id.toString())).thenReturn(balance);

        ResponseEntity<BigDecimal> response = balanceController.getBalance(user1Id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, balance.compareTo(response.getBody()));
        verify(userService).getBalance(user1Id.toString());
    }

    @Test
    void getBalanceInvalidUserReturns404() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService).getBalance(user1Id.toString());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> balanceController.getBalance(user1Id));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(userService).getBalance(user1Id.toString());
    }

    @Test
    void getBalanceNonAttendeeReturns400() {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(userService).getBalance(user1Id.toString());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> balanceController.getBalance(user1Id));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userService).getBalance(user1Id.toString());
    }
}
