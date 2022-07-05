package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.models.Transaction;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface TransactionController {
    List<Transaction> findAll();
    Transaction findById(Long id);
    List<Transaction> findByAccountOneId(Long accountOneId);
    void transferMoney(String accountType, String value, String owner, Long accountId);
    void transferMoneyThirdParty(String hashedKey, String value, Long accountId, String secretKey);
}
