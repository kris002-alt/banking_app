package com.backend.banking_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    private String type; // "deposit", "withdraw", "transfer-in", "transfer-out"
    private double amount;

    private LocalDateTime timestamp;
}

