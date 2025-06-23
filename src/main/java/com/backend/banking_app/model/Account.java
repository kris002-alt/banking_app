package com.backend.banking_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountHolderName;
    private String username;
    private String password;

    private double balance;

    @Column(nullable = false)
    private String kycStatus = "NO_KYC";

    private double dailyLimit = 5000;
    private double monthlyLimit = 10000;

    private double dailyTransferred = 0;
    private double monthlyTransferred = 0;

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // No roles yet
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can link to account status if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Add lock logic later if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Could use password expiry policy in future
    }

    @Override
    public boolean isEnabled() {
        return true; // You could add an 'enabled' flag later
    }
}
