package id.ac.ui.cs.advprog.eventsphereauth.service;

public interface UserService {
    String getUsernameById(String id);
    void addBalance(String userId, double amount);
    boolean deductBalance(String userId, double amount);
    double getBalance(String userId);
}