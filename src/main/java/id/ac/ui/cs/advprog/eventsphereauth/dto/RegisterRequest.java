package id.ac.ui.cs.advprog.eventsphereauth.dto;

import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private Role role;
}