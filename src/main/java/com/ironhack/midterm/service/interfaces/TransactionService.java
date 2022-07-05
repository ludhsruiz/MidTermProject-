package com.ironhack.midterm.service.interfaces;

public interface TransactionService {
    void transferMoney(String accountType, String value, String owner, Long accountId);
    void transferMoneyThirdParty(String hashedKey, String value, Long accountId, String secretKey);
}
