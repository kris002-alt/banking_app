package com.backend.banking_app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String accountHolderName;
    private double balance;
    private String username;
    private String password;
}
