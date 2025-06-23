package com.backend.banking_app.util;

import com.backend.banking_app.model.Account;

public class KycLimitValidator {

    public static void validateKycLimits(Account account, double amount) {
        switch (account.getKycStatus()) {
            case "NO_KYC":
                checkLimits(account, amount, 5000, 10000);
                break;
            case "PARTIAL_KYC":
                checkLimits(account, amount, 20000, 50000);
                break;
            case "FULL_KYC":
                // No limit for FULL_KYC, or you can set very high values
                break;
            default:
                throw new RuntimeException("Invalid KYC status");
        }
    }

    private static void checkLimits(Account account, double amount, double dailyLimit, double monthlyLimit) {
        double dailyUsed = account.getDailyTransferred();
        double monthlyUsed = account.getMonthlyTransferred();

        if (dailyUsed + amount > dailyLimit) {
            throw new RuntimeException("Daily transaction limit exceeded. Allowed: " + dailyLimit);
        }

        if (monthlyUsed + amount > monthlyLimit) {
            throw new RuntimeException("Monthly transaction limit exceeded. Allowed: " + monthlyLimit);
        }
    }
}

