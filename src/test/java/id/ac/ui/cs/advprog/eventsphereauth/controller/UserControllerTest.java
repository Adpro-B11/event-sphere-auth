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
import org.springframework.web.server.ResponseStatusException;

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
                .balance(null)
                .build();

        userResponse2 = UserResponse.builder()
                .id(user2Id)
                .username("usertwo")
                .email("usertwo@example.com")
                .phoneNumber("222")
                .role(Role.USER)
                .balance(null)
                .build();
    }

    @Test
    void testGetAllUsersSuccess() {
        List<UserResponse> userResponses = Arrays.asList(userResponse1, userResponse2);
        when(userService.getAllUsers()).thenReturn(userResponses);

        ResponseEntity<List<UserResponse>> responseEntity = userController.getAllUsers();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponses, responseEntity.getBody());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetMeSuccess() {
        when(userService.getAuthenticatedUserResponse()).thenReturn(userResponse1);

        ResponseEntity<UserResponse> responseEntity = userController.getMe();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse1, responseEntity.getBody());

        verify(userService, times(1)).getAuthenticatedUserResponse();
    }

    @Test
    void testGetMeUserNotFound() {
        when(userService.getAuthenticatedUserResponse()).thenThrow(new UsernameNotFoundException("No authenticated user found"));

        assertThrows(UsernameNotFoundException.class, () -> {
            userController.getMe();
        });

        verify(userService, times(1)).getAuthenticatedUserResponse();
    }


    @Test
    void testGetUserByIdSuccess() {
        when(userService.getUserById(user1Id)).thenReturn(userResponse1);

        ResponseEntity<UserResponse> responseEntity = userController.getUserById(user1Id);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse1, responseEntity.getBody());

        verify(userService, times(1)).getUserById(user1Id);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userService.getUserById(user1Id)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> {
            userController.getUserById(user1Id);
        });

        verify(userService, times(1)).getUserById(user1Id);
    }

    @Test
    void testUpdateUserSuccess() throws IllegalAccessException {
        UserUpdateRequest updateRequest = UserUpdateRequest.builder().username("updateduser").build();
        UserResponse updatedUserResponse = UserResponse.builder()
                .id(user1Id)
                .username("updateduser")
                .email("userone@example.com")
                .phoneNumber("111")
                .role(Role.USER)
                .balance(null)
                .build();
        when(userService.updateUser(user1Id, updateRequest)).thenReturn(updatedUserResponse);

        ResponseEntity<UserResponse> responseEntity = userController.updateUser(user1Id, updateRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedUserResponse, responseEntity.getBody());

        verify(userService, times(1)).updateUser(user1Id, updateRequest);
    }

    @Test
    void testUpdateUserUnauthorized() throws IllegalAccessException {
        UserUpdateRequest updateRequest = UserUpdateRequest.builder().username("updateduser").build();
        when(userService.updateUser(user1Id, updateRequest)).thenThrow(new IllegalAccessException("Unauthorized"));

        assertThrows(IllegalAccessException.class, () -> {
            userController.updateUser(user1Id, updateRequest);
        });

        verify(userService, times(1)).updateUser(user1Id, updateRequest);
    }

    @Test
    void testUpdateUserNotFound() throws IllegalAccessException {
        UserUpdateRequest updateRequest = UserUpdateRequest.builder().username("updateduser").build();
        when(userService.updateUser(user1Id, updateRequest)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> {
            userController.updateUser(user1Id, updateRequest);
        });

        verify(userService, times(1)).updateUser(user1Id, updateRequest);
    }


    @Test
    void testDeleteUserSuccess() throws IllegalAccessException {
        doNothing().when(userService).deleteUser(user1Id);

        ResponseEntity<Void> responseEntity = userController.deleteUser(user1Id);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody()); // No content for 204

        verify(userService, times(1)).deleteUser(user1Id);
    }

    @Test
    void testDeleteUserUnauthorized() throws IllegalAccessException {
        doThrow(new IllegalAccessException("Unauthorized")).when(userService).deleteUser(user1Id);

        assertThrows(IllegalAccessException.class, () -> {
            userController.deleteUser(user1Id);
        });

        verify(userService, times(1)).deleteUser(user1Id);
    }

    @Test
    void testDeleteUserNotFound() throws IllegalAccessException {
        doThrow(new UsernameNotFoundException("User not found")).when(userService).deleteUser(user1Id);

        assertThrows(UsernameNotFoundException.class, () -> {
            userController.deleteUser(user1Id);
        });

        verify(userService, times(1)).deleteUser(user1Id);
    }

    @Test
    void addBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(50);
        Map<String, BigDecimal> payload = new HashMap<>();
        payload.put("amount", amount);

        doNothing().when(userService).addBalance(user1Id.toString(), amount);

        ResponseEntity<Void> response = userController.addBalance(user1Id, payload);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).addBalance(user1Id.toString(), amount);
    }

    @Test
    void addBalanceUserNotFoundReturns404() {
        BigDecimal amount = BigDecimal.valueOf(50);
        Map<String, BigDecimal> payload = new HashMap<>();
        payload.put("amount", amount);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService).addBalance(user1Id.toString(), amount);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userController.addBalance(user1Id, payload));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(userService).addBalance(user1Id.toString(), amount);
    }

    @Test
    void deductBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(30);
        Map<String, BigDecimal> payload = new HashMap<>();
        payload.put("amount", amount);

        doNothing().when(userService).deductBalance(user1Id.toString(), amount);

        ResponseEntity<Void> response = userController.deductBalance(user1Id, payload);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).deductBalance(user1Id.toString(), amount);
    }

    @Test
    void deductBalanceInsufficientFundsReturns400() {
        BigDecimal amount = BigDecimal.valueOf(200);
        Map<String, BigDecimal> payload = new HashMap<>();
        payload.put("amount", amount);

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(userService).deductBalance(user1Id.toString(), amount);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userController.deductBalance(user1Id, payload));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userService).deductBalance(user1Id.toString(), amount);
    }

    @Test
    void deductBalanceSystemErrorReturns500() {
        BigDecimal amount = BigDecimal.valueOf(50);
        Map<String, BigDecimal> payload = new HashMap<>();
        payload.put("amount", amount);

        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(userService).deductBalance(user1Id.toString(), amount);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userController.deductBalance(user1Id, payload));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        verify(userService).deductBalance(user1Id.toString(), amount);
    }

    @Test
    void getBalanceSuccess() {
        BigDecimal balance = BigDecimal.valueOf(123.45);
        when(userService.getBalance(user1Id.toString())).thenReturn(balance);

        ResponseEntity<BigDecimal> response = userController.getBalance(user1Id);

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
                () -> userController.getBalance(user1Id));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(userService).getBalance(user1Id.toString());
    }

    @Test
    void getBalanceNonAttendeeReturns400() {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(userService).getBalance(user1Id.toString());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userController.getBalance(user1Id));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(userService).getBalance(user1Id.toString());
    }
}