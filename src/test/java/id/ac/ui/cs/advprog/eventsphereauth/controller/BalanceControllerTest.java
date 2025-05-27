package id.ac.ui.cs.advprog.eventsphereauth.controller;

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

    private UUID user1Id;
    private UUID user2Id;

    @BeforeEach
    void setUp() {
        user1Id = UUID.randomUUID();
        user2Id = UUID.randomUUID();
    }

    @Test
    void addBalanceSuccess() {
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", 50.0);

        BigDecimal amount = BigDecimal.valueOf(payload.get("amount"));

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
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", 50.0);

        BigDecimal amount = BigDecimal.valueOf(payload.get("amount"));

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService).addBalance(user1Id.toString(), amount);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> balanceController.addBalance(user1Id, payload));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(userService).addBalance(user1Id.toString(), amount);
    }

    @Test
    void deductBalanceSuccess() {
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", 30.0);

        BigDecimal amount = BigDecimal.valueOf(payload.get("amount"));

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
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", 200.0);

        BigDecimal amount = BigDecimal.valueOf(payload.get("amount"));

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(userService).deductBalance(user1Id.toString(), amount);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> balanceController.deductBalance(user1Id, payload));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userService).deductBalance(user1Id.toString(), amount);
    }

    @Test
    void deductBalanceSystemErrorReturns500() {
        Map<String, Double> payload = new HashMap<>();
        payload.put("amount", 50.0);

        BigDecimal amount = BigDecimal.valueOf(payload.get("amount"));

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
