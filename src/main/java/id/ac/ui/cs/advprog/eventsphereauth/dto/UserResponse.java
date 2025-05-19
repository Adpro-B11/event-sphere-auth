package id.ac.ui.cs.advprog.eventsphereauth.dto;

import id.ac.ui.cs.advprog.eventsphereauth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String phoneNumber;
    private Role role;
    private BigDecimal balance;
}