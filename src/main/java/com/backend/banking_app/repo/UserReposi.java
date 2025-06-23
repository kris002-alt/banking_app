package com.backend.banking_app.repo;

import com.backend.banking_app.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserReposi extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

}