package com.ironhack.midterm.service.impl;

import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.models.AccountData.Savings;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import com.ironhack.midterm.models.Money;
import com.ironhack.midterm.models.Transaction;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.SavingsRepository;
import com.ironhack.midterm.repository.TransactionRepository;
import com.ironhack.midterm.service.interfaces.SavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class SavingsServiceImpl implements SavingsService {
    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Savings store(Savings savings) {
        Long accountHolderId = savings.getAccountHolder().getId();
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(accountHolderId);
        // Check if the account holder exists
        if(accountHolder.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The account holder does not exist.");
        }
        // Check if the account holder already has a savings account
        Optional<Savings> existentSavingsAccount = savingsRepository.findByAccountHolder(accountHolder.get());
        if(!existentSavingsAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The account holder already has a savings account.");
        }
        savings.setAccountHolder(accountHolder.get());
        return savingsRepository.save(savings);
    }

    public void addInterest(Long id) {
        Optional<Savings> savingsAccount = savingsRepository.findById(id);

        if(!savingsAccount.isPresent()) {
            return;
        }
        Savings savings = savingsAccount.get();

        // Add interest rate if it has been 1 year or longer since interest was added
        boolean interestAddedOneYearOrLonger = interestAddedOneYearOrLonger(id);

        // Add interest rate if it has been 1 year or longer since Account creation and if the account has never gotten any interest rate
        int timeDifference = calculateTimeDifference(savings.getCreationDate(), LocalDateTime.now());
        boolean hasNeverGottenAnyInterest = hasNeverGottenAnyInterest(id);

        // If one of these two conditions applies, add interest
        if(interestAddedOneYearOrLonger || (timeDifference > 365 && hasNeverGottenAnyInterest)) {
            BigDecimal currentBalance = savings.getBalance();
            BigDecimal interest = savings.getInterestRate();
            BigDecimal interestValue = currentBalance.multiply(interest);
            BigDecimal newBalance = currentBalance.add(interestValue);

            savings.setBalance(newBalance);
            savingsRepository.save(savings);

            Money newBalanceMoney = new Money(interestValue);
            Transaction transaction = new Transaction(TransactionType.INTEREST, AccountType.SAVINGS, savings.getId(), null, null, newBalanceMoney, LocalDateTime.now());
            transactionRepository.save(transaction);
        }

    }

    // Check if it has been 1 year or longer since interest was added
    public boolean interestAddedOneYearOrLonger(Long id) {
        List<Transaction> transactionList = transactionRepository.findByAccountOneIdAndAccountOneType(id, AccountType.SAVINGS);
        for(Transaction transaction : transactionList) {
            if(transaction.getTransactionType() == TransactionType.INTEREST && transaction.getAccountOneType() == AccountType.SAVINGS) {
                int timeDifference = calculateTimeDifference(transaction.getTransactionDate(), LocalDateTime.now());
                if(timeDifference > 365) return true;
            }
        }
        return false;
    }

    // Helper method to calculate the time difference
    public static int calculateTimeDifference(LocalDateTime date, LocalDateTime currentDate) {
        if(date != null && currentDate != null) {
            return (int) DAYS.between(date, currentDate);
        } else {
            return 0;
        }
    }

    // Check if the account has every gotten any itnerest
    public boolean hasNeverGottenAnyInterest(Long id) {
        List<Transaction> transactionList = transactionRepository.findByAccountOneIdAndAccountOneType(id, AccountType.SAVINGS);
        if(transactionList.isEmpty()) return true;
        return false;
    }

    // Method to update the account's balance
    public void updateBalance(Long id, BigDecimal balance) {
        Optional<Savings> optionalSavings = savingsRepository.findById(id);
        if(optionalSavings.isPresent()) {
            optionalSavings.get().setBalance(balance);
            savingsRepository.save(optionalSavings.get());
        }
    }
}
