package com.ironhack.midterm.service.impl;

import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.enums.CheckingType;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.models.AccountData.Checking;
import com.ironhack.midterm.models.AccountData.CreditCard;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.models.AccountData.Savings;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import com.ironhack.midterm.models.LoginData.ThirdParty;
import com.ironhack.midterm.models.LoginData.User;
import com.ironhack.midterm.models.Money;
import com.ironhack.midterm.models.Transaction;
import com.ironhack.midterm.repository.*;
import com.ironhack.midterm.service.interfaces.CheckingService;
import com.ironhack.midterm.service.interfaces.CreditCardService;
import com.ironhack.midterm.service.interfaces.SavingsService;
import com.ironhack.midterm.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    private CheckingService checkingService;

    @Autowired
    private CreditCardService creditCardService;

    @Autowired
    private SavingsService savingsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    public void transferMoney(String accountType, String value, String ownerString, Long accountId) {
        // Transform input values
        Owner owner = new Owner(ownerString);
        Money valueAsMoney = new Money(BigDecimal.valueOf(Long.parseLong(value)));
        BigDecimal amount = valueAsMoney.getAmount();

        // Get username from sender
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        Optional<User> optionalUser = userRepository.findByUsername(currentUser);
        Optional<AccountHolder> optionalAccountHolder = accountHolderRepository.findById(optionalUser.get().getId());

        // Get sender account and its new balance
        BigDecimal senderBalance = null;
        Long senderId = null;
        AccountType senderAccountType = null;

        if(recipientExists(ownerString, accountId)) {
            switch(accountType.toUpperCase()) {
                case "CHECKING":
                    Optional<Checking> optionalCheckingSender = checkingRepository.findByAccountHolder(optionalAccountHolder.get());
                    senderBalance = optionalCheckingSender.get().getBalance().subtract(amount);
                    senderAccountType = AccountType.CHECKING;
                    senderId = optionalCheckingSender.get().getId();
                    // Update balance of sender if enough money
                    if(hasEnoughMoney(amount, optionalCheckingSender.get().getBalance())) {
                        // Check if penalty must be deducted
                        if(optionalCheckingSender.get().getCheckingType() == CheckingType.STANDARD_CHECKING &&
                                belowMinimumBalance(senderBalance, optionalCheckingSender.get().getMinimumBalance())) {
                            senderBalance = senderBalance.subtract(optionalCheckingSender.get().getPenaltyFee());
                        }
                        savingsService.updateBalance(senderId, senderBalance);
                        checkingService.updateBalance(senderId, senderBalance);
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not enough money!");
                    }
                    break;
                case "CREDITCARD":
                    Optional<CreditCard> optionalCreditCardSender = creditCardRepository.findByAccountHolder(optionalAccountHolder.get());
                    senderBalance = optionalCreditCardSender.get().getBalance().subtract(amount);
                    senderAccountType = AccountType.CREDITCARD;
                    senderId = optionalCreditCardSender.get().getId();
                    // Update balance of sender if enough money
                    if(hasEnoughMoney(amount, optionalCreditCardSender.get().getBalance())) {
                        creditCardService.updateBalance(senderId, senderBalance);
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not enough money!");
                    }
                    break;
                case "SAVINGS":
                    Optional<Savings> optionalSavingsSender = savingsRepository.findByAccountHolder(optionalAccountHolder.get());
                    senderBalance = optionalSavingsSender.get().getBalance().subtract(amount);
                    senderAccountType = AccountType.SAVINGS;
                    senderId = optionalSavingsSender.get().getId();
                    // Update balance of sender if enough money
                    if(hasEnoughMoney(amount, optionalSavingsSender.get().getBalance())) {
                        // Check if penalty must be deducted
                        if(belowMinimumBalance(senderBalance, optionalSavingsSender.get().getMinimumBalance())) {
                            senderBalance = senderBalance.subtract(optionalSavingsSender.get().getPenaltyFee());
                        }
                        savingsService.updateBalance(senderId, senderBalance);
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not enough money!");
                    }
                    break;
            }
        }


        // Get recipient account by verifying all possibilities and its new balance
        BigDecimal recipientBalance;

        // Case if it's a checking account
        if(checkingRepository.findByIdAndPrimaryOwner(accountId, owner).isPresent() ||
                checkingRepository.findByIdAndSecondaryOwner(accountId, owner).isPresent()) {
            // Assign the checking optional to a variable
            Optional<Checking> checkingOptionalRecipient = null;
            checkingOptionalRecipient = checkingRepository.findByIdAndPrimaryOwner(accountId, owner);
            if(!checkingOptionalRecipient.isPresent()) {
                checkingOptionalRecipient = checkingRepository.findByIdAndSecondaryOwner(accountId, owner);
            }
            // Calculate new balance of the recipient and update it
            recipientBalance = checkingOptionalRecipient.get().getBalance().add(amount);
            checkingService.updateBalance(checkingOptionalRecipient.get().getId(), recipientBalance);
            // Add the transaction
            Transaction transaction = new Transaction(TransactionType.MONEY_TRANSFER, senderAccountType, senderId,
                    AccountType.CHECKING, checkingOptionalRecipient.get().getId(), valueAsMoney, LocalDateTime.now());
            transactionRepository.save(transaction);
            // Fraud detection
            fraudDetection(checkingOptionalRecipient.get().getId(), AccountType.CHECKING);
        }

        // Case if it's a credit card account
        else if(creditCardRepository.findByIdAndPrimaryOwner(accountId, owner).isPresent() ||
                creditCardRepository.findByIdAndSecondaryOwner(accountId, owner).isPresent()) {
            // Assign the credit card optional to a variable
            Optional<CreditCard> creditCardOptionalRecipient = null;
            creditCardOptionalRecipient = creditCardRepository.findByIdAndPrimaryOwner(accountId, owner);
            if(!creditCardOptionalRecipient.isPresent()) {
                creditCardOptionalRecipient = creditCardRepository.findByIdAndSecondaryOwner(accountId, owner);
            }
            // Calculate new balance of the recipient and update it
            recipientBalance = creditCardOptionalRecipient.get().getBalance().add(amount);
            creditCardService.updateBalance(creditCardOptionalRecipient.get().getId(), recipientBalance);
            // Add the transaction
            Transaction transaction = new Transaction(TransactionType.MONEY_TRANSFER, senderAccountType, senderId,
                    AccountType.CREDITCARD, creditCardOptionalRecipient.get().getId(), valueAsMoney, LocalDateTime.now());
            transactionRepository.save(transaction);
        }

        // Case if it's a savings account
        else if(savingsRepository.findByIdAndPrimaryOwner(accountId, owner).isPresent() ||
                savingsRepository.findByIdAndSecondaryOwner(accountId, owner).isPresent()) {
            // Assign the savings optional to a variable
            Optional<Savings> savingsOptionalRecipient;
            savingsOptionalRecipient = savingsRepository.findByIdAndPrimaryOwner(accountId, owner);
            if(!savingsOptionalRecipient.isPresent()) {
                savingsOptionalRecipient = savingsRepository.findByIdAndSecondaryOwner(accountId, owner);
            }
            // Calculate new balance of the recipient and update it
            recipientBalance = savingsOptionalRecipient.get().getBalance().add(amount);
            savingsService.updateBalance(savingsOptionalRecipient.get().getId(), recipientBalance);
            // Add the transaction
            Transaction transaction = new Transaction(TransactionType.MONEY_TRANSFER, senderAccountType, senderId,
                    AccountType.SAVINGS, savingsOptionalRecipient.get().getId(), valueAsMoney, LocalDateTime.now());
            transactionRepository.save(transaction);
            // Fraud detection
            fraudDetection(savingsOptionalRecipient.get().getId(), AccountType.SAVINGS);
        }

        // Case if it's a third-party account
        else if(thirdPartyRepository.findById(accountId).isPresent()) {
            ThirdParty thirdPartyOptionalRecipient = thirdPartyRepository.findById(accountId).get();
            // Add the transaction
            Transaction transaction = new Transaction(TransactionType.MONEY_TRANSFER, senderAccountType, senderId,
                    AccountType.THIRD_PARTY, thirdPartyOptionalRecipient.getId(), valueAsMoney, LocalDateTime.now());
            transactionRepository.save(transaction);

        }

        fraudDetection(senderId, senderAccountType);
    }

    // Check if the recipient account exists
    public boolean recipientExists(String ownerString, Long accountId) {
        Owner owner = new Owner(ownerString);
        boolean result = false;
        if(checkingRepository.findByIdAndPrimaryOwner(accountId, owner).isPresent() ||
                checkingRepository.findByIdAndSecondaryOwner(accountId, owner).isPresent() ||
                creditCardRepository.findByIdAndPrimaryOwner(accountId, owner).isPresent() ||
                creditCardRepository.findByIdAndSecondaryOwner(accountId, owner).isPresent() ||
                savingsRepository.findByIdAndPrimaryOwner(accountId, owner).isPresent() ||
                savingsRepository.findByIdAndSecondaryOwner(accountId, owner).isPresent() ||
                thirdPartyRepository.findById(accountId).isPresent()
        ) result = true;
        return result;
    }

    // Check if the account has enough money
    public boolean hasEnoughMoney(BigDecimal amount, BigDecimal currentBalance) {
        return amount.compareTo(currentBalance) > 0 ? false : true;
    }

    // Check if the balance is below the minimum balance
    public boolean belowMinimumBalance(BigDecimal balance, BigDecimal minimumBalance) {
        return balance.compareTo(minimumBalance) > 0 ? false : true;
    }

    // Method for the money transfer of third party accounts
    public void transferMoneyThirdParty(String hashedKey, String value, Long accountId, String secretKey) {
        // Transform input values
        Money valueAsMoney = new Money(BigDecimal.valueOf(Long.parseLong(value)));
        BigDecimal amount = valueAsMoney.getAmount();

        // Get username from sender
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        Optional<User> optionalUser = userRepository.findByUsername(currentUser);
        Optional<ThirdParty> optionalThirdPartyUser = thirdPartyRepository.findById(optionalUser.get().getId());

        // Check hashed key
        String correctHashedKey = optionalThirdPartyUser.get().getHashedKey();
        if(!correctHashedKey.equals(hashedKey)) return;

        // Determine recipient account and update it
        // Case if it's a checking account
        if(checkingRepository.findById(accountId).isPresent()) {
            Checking recipientChecking = checkingRepository.findById(accountId).get();
            String correctSecretKey = recipientChecking.getSecretKey();
            // Check if correct secret key was provided
            if(correctSecretKey.equals(secretKey)) {
                BigDecimal recipientBalance = recipientChecking.getBalance().add(amount);
                checkingService.updateBalance(recipientChecking.getId(), recipientBalance);
                // Add the transaction
                Transaction transaction = new Transaction(TransactionType.MONEY_TRANSFER, AccountType.THIRD_PARTY, optionalThirdPartyUser.get().getId(),
                        AccountType.CHECKING, recipientChecking.getId(), valueAsMoney, LocalDateTime.now());
                transactionRepository.save(transaction);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        // Case if it's a savings account
        if(savingsRepository.findById(accountId).isPresent()) {
            Savings recipientSavings = savingsRepository.findById(accountId).get();
            String correctSecretKey = recipientSavings.getSecretKey();
            // Check if correct secret key was provided
            if(correctSecretKey.equals(secretKey)) {
                BigDecimal recipientBalance = recipientSavings.getBalance().add(amount);
                savingsService.updateBalance(recipientSavings.getId(), recipientBalance);
                // Add the transaction
                Transaction transaction = new Transaction(TransactionType.MONEY_TRANSFER, AccountType.THIRD_PARTY, optionalThirdPartyUser.get().getId(),
                        AccountType.SAVINGS, recipientSavings.getId(), valueAsMoney, LocalDateTime.now());
                transactionRepository.save(transaction);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        // Case if it's a credit card account
        if(creditCardRepository.findById(accountId).isPresent()) {
            CreditCard recipientSavings = creditCardRepository.findById(accountId).get();
            // Update recipient account
            BigDecimal recipientBalance = recipientSavings.getBalance().add(amount);
            creditCardService.updateBalance(recipientSavings.getId(), recipientBalance);
            // Add the transaction
            Transaction transaction = new Transaction(TransactionType.MONEY_TRANSFER, AccountType.THIRD_PARTY, optionalThirdPartyUser.get().getId(),
                    AccountType.CREDITCARD, recipientSavings.getId(), valueAsMoney, LocalDateTime.now());
            transactionRepository.save(transaction);
        }

        // Case if it's a third party account
        if(thirdPartyRepository.findById(accountId).isPresent()) {
            ThirdParty recipientThirdParty = thirdPartyRepository.findById(accountId).get();

            // Add the transaction
            Transaction transaction = new Transaction(TransactionType.MONEY_TRANSFER, AccountType.THIRD_PARTY, optionalThirdPartyUser.get().getId(),
                    AccountType.THIRD_PARTY, recipientThirdParty.getId(), valueAsMoney, LocalDateTime.now());
            transactionRepository.save(transaction);
        }
    }

    // Method to detect fraud
    public void fraudDetection(Long accountId, AccountType accountType) {
        // Fraud, if transactions made in 24 hours total to more than 150% of the customers highest daily total transactions in any other 24 hour period.
        boolean higherTransaction = false;
        Optional<Double> highestDailyTotalOptional = transactionRepository.findMaxAmount(String.valueOf(accountId));
        if(highestDailyTotalOptional.isPresent()) {
            double transactionsCurrentDay = 0;
            double highestDailyTotal = highestDailyTotalOptional.get();
            Optional<Double> transactionsCurrentDayOptional = transactionRepository.findTotalToday(String.valueOf(accountId), LocalDateTime.now());
            if(transactionsCurrentDayOptional.isPresent()) transactionsCurrentDay = transactionsCurrentDayOptional.get();
            double limit = highestDailyTotal*1.5;
            if(transactionsCurrentDay > limit) higherTransaction = true;
        }

        // Fraud, if more than 2 transactions occurring on a single account within a 1 second period.
        List<Transaction> transactionList = transactionRepository.findAllTransactionsFromId(String.valueOf(accountId), LocalDateTime.now());
        boolean hasMoreThanTwoTransactionsPerSecond = hasMoreThanTwoTransactionsPerSecond(transactionList);

        // Freeze account if any of these occurs
        if(higherTransaction || hasMoreThanTwoTransactionsPerSecond) {
            switch(accountType) {
                case CHECKING:
                    Optional<Checking> checkingOptional = checkingRepository.findById(accountId);
                    if(checkingOptional.isPresent()) checkingOptional.get().setStatus(Status.FROZEN);
                    break;
                case SAVINGS:
                    Optional<Savings> savingsOptional = savingsRepository.findById(accountId);
                    if(savingsOptional.isPresent()) savingsOptional.get().setStatus(Status.FROZEN);
                    break;
            }
        }
    }

    // Check if there were more than two transactions per second
    public boolean hasMoreThanTwoTransactionsPerSecond(List<Transaction> transactionList) {
        for(Transaction transaction : transactionList) {
            LocalDateTime transactionDate = transaction.getTransactionDate();
            List<Transaction> findings = transactionRepository.findAllTransactionsFromTime(transactionDate);
            if(findings.size() > 1) return true;
        }
        return false;
    }

}
