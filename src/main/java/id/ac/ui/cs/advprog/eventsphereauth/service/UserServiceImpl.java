package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserUpdateRequest;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MetricsService metricsService;

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getAuthenticatedUserResponse() {
        log.info("Fetching authenticated user details");
        User currentUser = getCurrentUserEntity();
        return mapToUserResponse(currentUser);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest updateRequest) throws IllegalAccessException {
        log.info("Updating user: {}", id);
        User currentUser = getCurrentUserEntity();
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User to update not found with id: " + id));

        if (!currentUser.getId().equals(id) && currentUser.getRole() != Role.ADMIN) {
            log.warn("Unauthorized update attempt by user: {} for user: {}", currentUser.getEmail(), id);
            throw new IllegalAccessException("You are not authorized to update this user");
        }

        if (updateRequest.getUsername() != null) {
            userToUpdate.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getEmail() != null) {
            if (!userToUpdate.getEmail().equals(updateRequest.getEmail()) && userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new IllegalArgumentException("New email is already in use by another account.");
            }
            userToUpdate.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPhoneNumber() != null) {
            userToUpdate.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        User updatedUser = userRepository.save(userToUpdate);
        log.info("User updated successfully: {}", id);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) throws IllegalAccessException {
        log.info("Deleting user: {}", id);
        User currentUser = getCurrentUserEntity();

        if (currentUser.getRole() != Role.ADMIN) {
            log.warn("Unauthorized delete attempt by user: {}", currentUser.getEmail());
            throw new IllegalAccessException("Only admins can delete users");
        }

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User to delete not found with id: " + id));

        if (userToDelete.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Admin cannot delete their own account through this operation.");
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
    }

    public boolean isNotValidUUID(String input) {
        try {
            UUID.fromString(input);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    @Transactional
    public void addBalance(String userId, BigDecimal amount) {
        log.info("Adding balance: {} to user: {}", amount, userId);
        User user = validationBalance(userId, amount);
        try {
            BigDecimal newBalance = user.getBalance().add(amount);
            user.setBalance(newBalance);
            userRepository.save(user);
            
            metricsService.incrementBalanceOperations();
            log.info("Balance added successfully. New balance: {} for user: {}", newBalance, userId);
        } catch (RestClientException e) {
            log.error("Connection error while adding balance for user: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "CONNECTION_ERROR");
        } catch (Exception e) {
            log.error("System error while adding balance for user: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_ERROR");
        }
    }

    @Transactional
    public void deductBalance(String userId, BigDecimal amount) {
        log.info("Deducting balance: {} from user: {}", amount, userId);
        User user = validationBalance(userId, amount);
        if (user.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient balance for user: {}. Current: {}, Requested: {}", 
                    userId, user.getBalance(), amount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE");
        }
        try {
            BigDecimal newBalance = user.getBalance().subtract(amount);
            user.setBalance(newBalance);
            userRepository.save(user);
            
            metricsService.incrementBalanceOperations();
            log.info("Balance deducted successfully. New balance: {} for user: {}", newBalance, userId);
        } catch (RestClientException e) {
            log.error("Connection error while deducting balance for user: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "CONNECTION_ERROR");
        } catch (Exception e) {
            log.error("System error while deducting balance for user: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_ERROR");
        }
    }

    @Transactional(readOnly = true)
    protected User validationBalance(String userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (isNotValidUUID(userId)) {
            throw new IllegalArgumentException("Invalid user ID format: " + userId);
        }
        UUID uuid = UUID.fromString(userId);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (user.getRole() != Role.ATTENDEE) {
            throw new IllegalStateException("Only ATTENDEE can perform balance operations");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(String userId) {
        log.info("Getting balance for user: {}", userId);
        if (isNotValidUUID(userId)) {
            throw new IllegalArgumentException("Invalid user ID format: " + userId);
        }
        UUID uuid = UUID.fromString(userId);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (user.getRole() != Role.ATTENDEE) {
            throw new IllegalStateException("Only ATTENDEE can perform balance operations");
        }
        return user.getBalance();
    }

    private User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            throw new UsernameNotFoundException("No authenticated user found or user is anonymous.");
        }
        String currentUserEmail = authentication.getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found with email: " + currentUserEmail));
    }

    public UserResponse mapToUserResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getDisplayName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .balance(user.getBalance())
                .build();
    }
}