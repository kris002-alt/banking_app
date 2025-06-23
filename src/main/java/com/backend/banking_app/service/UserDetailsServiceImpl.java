package com.backend.banking_app.service;

import com.backend.banking_app.model.Account;
import com.backend.banking_app.repo.UserReposi;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserReposi userRepository;

    public UserDetailsServiceImpl(UserReposi userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                new ArrayList<>() // Add authorities if needed
        );
    }
}