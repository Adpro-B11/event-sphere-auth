package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserUpdateRequest;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(UUID id);
    UserResponse getAuthenticatedUserResponse();
    UserResponse updateUser(UUID id, UserUpdateRequest updateRequest) throws IllegalAccessException;
    void deleteUser(UUID id) throws IllegalAccessException;
    UserResponse mapToUserResponse(id.ac.ui.cs.advprog.eventsphereauth.model.User user);
    void addBalance(String userId, BigDecimal amount);
    void deductBalance(String userId, BigDecimal amount);
    BigDecimal getBalance(String userId);
}