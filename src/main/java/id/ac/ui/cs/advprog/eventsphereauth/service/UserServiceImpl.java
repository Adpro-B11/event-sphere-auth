package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.model.User;
import id.ac.ui.cs.advprog.eventsphereauth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getUsernameById(String id) {
        return userRepository.findById(id)
                .map(User::getUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    private boolean isValidUUID(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void addBalance(String userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (!isValidUUID(userId)) {
            throw new IllegalArgumentException("Invalid user ID format: " + userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        double newBalance = user.getBalance() + amount;
        user.setBalance(newBalance);
        userRepository.save(user);
    }


    @Override
    public boolean deductBalance(String userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (!isValidUUID(userId)) {
            throw new IllegalArgumentException("Invalid user ID format: " + userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (user.getBalance() < amount) {
            return false;
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);
        return true;
    }


    @Override
    public double getBalance(String userId) {
        if (!isValidUUID(userId)) {
            throw new IllegalArgumentException("Invalid user ID format: " + userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return user.getBalance();
    }



}