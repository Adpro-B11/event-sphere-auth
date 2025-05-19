package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserUpdateRequest;
import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Consistent exception type
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Recommended

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse) // Using your existing mapper name
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getAuthenticatedUserResponse() {
        User currentUser = getCurrentUserEntity();
        return mapToUserResponse(currentUser);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest updateRequest) throws IllegalAccessException {
        User currentUser = getCurrentUserEntity();
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User to update not found with id: " + id));

        if (!currentUser.getId().equals(id) && currentUser.getRole() != Role.ADMIN) {
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
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) throws IllegalAccessException {
        User currentUser = getCurrentUserEntity();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new IllegalAccessException("Only admins can delete users");
        }

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User to delete not found with id: " + id));

        if (userToDelete.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Admin cannot delete their own account through this operation.");
        }

        userRepository.deleteById(id);
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
