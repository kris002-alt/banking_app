package com.backend.banking_app.service;


import com.backend.banking_app.dto.*;
import com.backend.banking_app.model.Account;
import com.backend.banking_app.model.Transaction;
import com.backend.banking_app.repo.TransactionRepo;
import com.backend.banking_app.repo.UserReposi;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.banking_app.util.KycLimitValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {


    private UserReposi userReposi;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserResponseDto mapToUserResponseDto(Account account) {
        UserResponseDto response = new UserResponseDto();
        response.setId(account.getId());
        response.setAccountHolderName(account.getAccountHolderName());
        response.setBalance(account.getBalance());
        response.setUsername(account.getUsername());
        return response;
    }
    public UserResponseDto createAccount(UserRequestDto request) {
        Account account = new Account();
        account.setAccountHolderName(request.getAccountHolderName());
        account.setBalance(request.getBalance());
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        Account savedAccount = userReposi.save(account);
        return mapToUserResponseDto(savedAccount);
    }

    private UserResponseDto getUserResponseDto(UserRequestDto request, Account account) {
        account.setAccountHolderName(request.getAccountHolderName());
        account.setBalance(request.getBalance());

        Account savedAccount = userReposi.save(account);

        UserResponseDto response = new UserResponseDto();
        response.setId(savedAccount.getId());
        response.setAccountHolderName(savedAccount.getAccountHolderName());
        response.setBalance(savedAccount.getBalance());

        return response;
    }

    public List<UserResponseDto> getAllAccounts() {
        List<Account> accounts = userReposi.findAll();
        List<UserResponseDto> responses = new ArrayList<>();

        for (Account account : accounts) {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(account.getId());
            dto.setAccountHolderName(account.getAccountHolderName());
            dto.setBalance(account.getBalance());
            responses.add(dto);
        }


        return responses;
    }

    public UserResponseDto updateAccount(Long id, UserRequestDto requestDTO) {
        Account account = userReposi.findById(id).orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        return getUserResponseDto(requestDTO, account);
    }


    public UserResponseDto withdrawFromAccount(WithdrawRequestDto withdrawRequestDto) {
        Account account = userReposi.findById(withdrawRequestDto.getUserId()).orElseThrow(() -> new RuntimeException("Account not found with id: " + withdrawRequestDto.getUserId()));

        if (account.getBalance() < withdrawRequestDto.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - withdrawRequestDto.getAmount());
        Account updatedAccount = userReposi.save(account);

        UserResponseDto response = new UserResponseDto();
        response.setId(updatedAccount.getId());
        response.setAccountHolderName(updatedAccount.getAccountHolderName());
        response.setBalance(updatedAccount.getBalance());

        return response;
    }

    public UserResponseDto depositToAccount(DepositRequestDto depositRequestDto) {
        Account account = userReposi.findById(depositRequestDto.getUserId()).orElseThrow(() -> new RuntimeException("Account not found with id: " + depositRequestDto.getUserId()));

        double newBalance = account.getBalance() + depositRequestDto.getAmount();
        account.setBalance(newBalance);

        Account updatedAccount = userReposi.save(account);

        // ✅ Log transaction
        Transaction txn = new Transaction();
        txn.setAccountId(account.getId());
        txn.setType("deposit");
        txn.setAmount(depositRequestDto.getAmount());
        txn.setTimestamp(java.time.LocalDateTime.now());
        transactionRepo.save(txn);

        // ✅ Prepare response
        UserResponseDto response = new UserResponseDto();
        response.setId(updatedAccount.getId());
        response.setAccountHolderName(updatedAccount.getAccountHolderName());
        response.setBalance(updatedAccount.getBalance());

        return response;
    }

    @Transactional
    public String transferBalance(TransferRequestDto transferRequestDto) {
        Account sourceAccount = userReposi.findById(transferRequestDto.getFromUserId()).orElseThrow(() -> new RuntimeException("Source account not found"));

        Account destinationAccount = userReposi.findById(transferRequestDto.getToUserId()).orElseThrow(() -> new RuntimeException("Destination account not found"));

        double amount = transferRequestDto.getAmount();

        // ✅ KYC check
        KycLimitValidator.validateKycLimits(sourceAccount, amount);

        if (sourceAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

        sourceAccount.setDailyTransferred(sourceAccount.getDailyTransferred() + amount);
        sourceAccount.setMonthlyTransferred(sourceAccount.getMonthlyTransferred() + amount);

        userReposi.save(sourceAccount);
        userReposi.save(destinationAccount);

        createTransaction(sourceAccount.getId(), "transfer-out", amount);
        createTransaction(destinationAccount.getId(), "transfer-in", amount);

        return "Transfer successful";
    }


    public List<Transaction> getTransactionHistory(Long userId) {

        // first find accounts associated with the user
        return transactionRepo.findByAccountId(userId);
    }

    private void createTransaction(Long accountId, String type, double amount) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepo.save(transaction);
    }

    public UserService(UserReposi userReposi,
                       TransactionRepo transactionRepo,
                       PasswordEncoder passwordEncoder) {
        this.userReposi = userReposi;
        this.transactionRepo = transactionRepo;
        this.passwordEncoder = passwordEncoder;
    }
}
