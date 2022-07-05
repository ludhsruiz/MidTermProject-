package com.ironhack.midterm.service.impl;

import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.models.AccountData.CreditCard;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import com.ironhack.midterm.models.Money;
import com.ironhack.midterm.models.Transaction;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.CreditCardRepository;
import com.ironhack.midterm.repository.TransactionRepository;
import com.ironhack.midterm.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//// ?????????????
import static com.ironhack.midterm.service.impl.SavingsServiceImpl.calculateTimeDifference;


@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional
    public CreditCard store(CreditCard creditCard) {
        Long accountHolderId = creditCard.getAccountHolder().getId();
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(accountHolderId);
        // Check if the account holder exists
        if(accountHolder.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The account holder does not exist.");
        }
        // Check if the account holder already has a credit card account
        Optional<CreditCard> existentCreditCardAccount = creditCardRepository.findByAccountHolder(accountHolder.get());
        if(!existentCreditCardAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The account holder already has a credit card account.");
        }
        creditCard.setAccountHolder(accountHolder.get());
        return creditCardRepository.save(creditCard);
    }


    public void addInterest(Long id) {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);

        if(!optionalCreditCard.isPresent()) return;
        CreditCard creditCard = optionalCreditCard.get();

        // Add interest rate if it has been 1 month or longer since interest was added
        boolean interestAddedOneMonthOrLonger = interestAddedOneMonthOrLonger(id);

        // Add interest rate if it has been 1 month or longer since Account creation and if the account has never gotten any interest rate
        int timeDifference = calculateTimeDifference(creditCard.getCreationDate(), LocalDateTime.now());
        boolean hasNeverGottenAnyInterest = hasNeverGottenAnyInterest(id);

        // If one of these two conditions applies, add interest
        if(interestAddedOneMonthOrLonger || (timeDifference > 30 && hasNeverGottenAnyInterest)) {
            BigDecimal currentBalance = creditCard.getBalance();
            BigDecimal interest = creditCard.getInterestRate();
            BigDecimal interestPerMonth = interest.divide(BigDecimal.valueOf(2));
            BigDecimal interestValue = currentBalance.multiply(interestPerMonth);
            BigDecimal newBalance = currentBalance.add(interestValue);

            creditCard.setBalance(newBalance);
            creditCardRepository.save(creditCard);

            Money newBalanceMoney = new Money(interestValue);
            Transaction transaction = new Transaction(TransactionType.INTEREST, AccountType.CREDITCARD, creditCard.getId(), null, null, newBalanceMoney, LocalDateTime.now());
            transactionRepository.save(transaction);
        }

    }

    // Check if it has been 1 month or longer since interest was added
    public boolean interestAddedOneMonthOrLonger(Long id) {
        List<Transaction> transactionList = transactionRepository.findByAccountOneIdAndAccountOneType(id, AccountType.CREDITCARD);
        for(Transaction transaction : transactionList) {
            if(transaction.getTransactionType() == TransactionType.INTEREST && transaction.getAccountOneType() == AccountType.CREDITCARD) {
                int timeDifference = calculateTimeDifference(transaction.getTransactionDate(), LocalDateTime.now());
                if(timeDifference > 30) return true;
            }
        }
        return false;
    }

    // Check if the account has ever gotten any interest
    public boolean hasNeverGottenAnyInterest(Long id) {
        List<Transaction> transactionList = transactionRepository.findByAccountOneIdAndAccountOneType(id, AccountType.CREDITCARD);
        if(transactionList.isEmpty()) return true;
        return false;
    }

    // Update the account's balance
    public void updateBalance(Long id, BigDecimal balance) {
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);
        if(optionalCreditCard.isPresent()) {
            optionalCreditCard.get().setBalance(balance);
            creditCardRepository.save(optionalCreditCard.get());
        }
    }
}
