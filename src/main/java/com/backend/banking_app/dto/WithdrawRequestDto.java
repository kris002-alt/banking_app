package com.backend.banking_app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawRequestDto {
    private Long userId;
    private double amount;
}
