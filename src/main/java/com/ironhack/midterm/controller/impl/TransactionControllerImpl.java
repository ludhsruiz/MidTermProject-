package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.TransactionController;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.models.Transaction;
import com.ironhack.midterm.repository.TransactionRepository;
import com.ironhack.midterm.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class TransactionControllerImpl implements TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transactions")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @GetMapping("/transactions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Transaction findById(@PathVariable Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found."));
    }

    @GetMapping("/transactions/savings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> findByAccountOneId(@PathVariable(value="id")  Long accountOneId) {
        return transactionRepository.findByAccountOneIdAndAccountOneType(accountOneId, AccountType.SAVINGS);
    }

    @PutMapping("/transfer/{accountType}/{value}/{owner}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void transferMoney(@PathVariable String accountType, @PathVariable String value, @PathVariable String owner,
                              @PathVariable(value = "id") Long accountId) {
        transactionService.transferMoney(accountType, value, owner, accountId);
    }

    @PutMapping(value = "/transfer/third-party", params = {"value", "id", "secretKey"})
    @ResponseStatus(HttpStatus.OK)
    public void transferMoneyThirdParty(@RequestHeader String hashedKey, @RequestParam String value, @RequestParam(value = "id") Long accountId, @RequestParam String secretKey) {
        transactionService.transferMoneyThirdParty(hashedKey, value, accountId, secretKey);
    }

}
