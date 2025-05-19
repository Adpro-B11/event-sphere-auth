package id.ac.ui.cs.advprog.eventsphereauth.service;

import id.ac.ui.cs.advprog.eventsphereauth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.eventsphereauth.dto.UserResponse;

public interface AdminService {
    UserResponse createAdminOrOrganizer(RegisterRequest request) throws IllegalAccessException;
}
