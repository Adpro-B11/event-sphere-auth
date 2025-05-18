package id.ac.ui.cs.advprog.eventsphereauth.repository;

import id.ac.ui.cs.advprog.eventsphereauth.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
    void save(User user);
}