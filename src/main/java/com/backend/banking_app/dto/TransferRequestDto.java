package com.backend.banking_app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequestDto {
    private Long fromUserId;
    private Long toUserId;
    private double amount;
}
