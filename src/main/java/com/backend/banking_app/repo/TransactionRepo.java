package com.backend.banking_app.repo;

import com.backend.banking_app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
}
