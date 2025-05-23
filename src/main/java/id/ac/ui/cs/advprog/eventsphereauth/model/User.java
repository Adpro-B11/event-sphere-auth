package id.ac.ui.cs.advprog.eventsphereauth.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Generated
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    private void validateBalanceAndRole() {
        if (role != Role.ATTENDEE && balance != null && balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Only ATTENDEE can have a non-zero balance.");
        }
        if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Balance cannot be negative.");
        }
    }

    public void setBalance(BigDecimal newBalance) {
        if (newBalance == null) {
            throw new IllegalArgumentException("Balance cannot be null.");
        }
        if (role != Role.ATTENDEE && newBalance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Only ATTENDEE can have a non-zero balance.");
        }
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Balance cannot be negative.");
        }
        this.balance = newBalance;
    }

    public User() {
    }

    public User(String username, String email, String phoneNumber, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return this.username;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
