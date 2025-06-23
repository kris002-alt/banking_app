package com.backend.banking_app.controller;

import com.backend.banking_app.dto.*;
import com.backend.banking_app.model.Account;
import com.backend.banking_app.model.Transaction;
import com.backend.banking_app.repo.UserReposi;
import com.backend.banking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserReposi userReposi;

    @PostMapping
    public UserResponseDto createAccount(@RequestBody UserRequestDto userRequestDto) {
        return userService.createAccount(userRequestDto);
    }

    @PutMapping("/deposit")
    public UserResponseDto depositToAccount(@RequestBody DepositRequestDto depositRequestDto) {
        return userService.depositToAccount(depositRequestDto);
    }

    @GetMapping
    public List<UserResponseDto> getAllAccounts() {
        return userService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account = userReposi.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateAccount(@PathVariable Long id, @RequestBody UserRequestDto requestDTO) {
        return userService.updateAccount(id, requestDTO);
    }

    @PutMapping("/withdraw")
    public UserResponseDto withdrawFromAccount(@RequestBody WithdrawRequestDto withdrawRequestDto) {
        return userService.withdrawFromAccount(withdrawRequestDto);
    }

    @PutMapping("/transfer")
    public String transferBetweenAccounts(@RequestBody TransferRequestDto transferRequestDto) {
        return userService.transferBalance(transferRequestDto);
    }

    @GetMapping("/{userId}/transactions")
    public List<Transaction> getTransactionHistory(@PathVariable Long userId) {
        return userService.getTransactionHistory(userId);
    }

    // ✅ NEW: Admin-only API to update KYC status
    @PutMapping("/admin/kyc/{id}")
    public ResponseEntity<String> updateKycStatus(@PathVariable Long id, @RequestParam String status) {
        Account account = userReposi.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        switch (status.toUpperCase()) {
            case "NO_KYC":
                account.setKycStatus("NO_KYC");
                account.setDailyLimit(5000);
                account.setMonthlyLimit(10000);
                break;
            case "PARTIAL_KYC":
                account.setKycStatus("PARTIAL_KYC");
                account.setDailyLimit(20000);
                account.setMonthlyLimit(50000);
                break;
            case "FULL_KYC":
                account.setKycStatus("FULL_KYC");
                account.setDailyLimit(1000000);
                account.setMonthlyLimit(10000000);
                break;
            default:
                throw new RuntimeException("Invalid KYC status: " + status);
        }

        userReposi.save(account);
        return ResponseEntity.ok("KYC updated to " + status + " for user ID " + id);
    }

    @GetMapping("/{id}/kyc-status")
    public ResponseEntity<Map<String, String>> getKycStatusJson(@PathVariable Long id) {
        Account account = userReposi.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        Map<String, String> response = new HashMap<>();
        response.put("userId", account.getId().toString());
        response.put("kycStatus", account.getKycStatus());
        return ResponseEntity.ok(response);
    }

}


