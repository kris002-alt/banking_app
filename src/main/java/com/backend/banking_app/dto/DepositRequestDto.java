package com.backend.banking_app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositRequestDto {
    private Long userId;
    private double amount;


}

