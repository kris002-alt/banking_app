package com.backend.banking_app.service;

import com.backend.banking_app.model.Account;
import com.backend.banking_app.repo.UserReposi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KycResetScheduler {

    @Autowired
    private UserReposi userReposi;

    // Reset daily transfers at midnight
    @Scheduled(cron = "0 0 0 * * ?") // every day at 00:00
    public void resetDailyTransfers() {
        List<Account> accounts = userReposi.findAll();
        for (Account account : accounts) {
            account.setDailyTransferred(0);
        }
        userReposi.saveAll(accounts);
        System.out.println("Daily transfer limits reset");
    }

    // Reset monthly transfers on 1st of every month
    @Scheduled(cron = "0 0 0 1 * ?") // 1st of every month at 00:00
    public void resetMonthlyTransfers() {
        List<Account> accounts = userReposi.findAll();
        for (Account account : accounts) {
            account.setMonthlyTransferred(0);
        }
        userReposi.saveAll(accounts);
        System.out.println("Monthly transfer limits reset");
    }
}
