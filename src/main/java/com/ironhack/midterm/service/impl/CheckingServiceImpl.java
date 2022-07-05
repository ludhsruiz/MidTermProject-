package com.ironhack.midterm.service.impl;

import com.ironhack.midterm.enums.CheckingType;
import com.ironhack.midterm.models.AccountData.Checking;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.CheckingRepository;
import com.ironhack.midterm.service.interfaces.CheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;


@Service
public class CheckingServiceImpl implements CheckingService {
    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Transactional
    public Checking store(Checking checking) {
        Long accountHolderId = checking.getAccountHolder().getId();
        Optional<AccountHolder> accountHolder = accountHolderRepository.findById(accountHolderId);
        // Check if the account holder exists
        if(accountHolder.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The account holder does not exist.");
        }
        // Check if the account holder already has a checking account
        Optional<Checking> existentCheckingAccount = checkingRepository.findByAccountHolder(accountHolder.get());
        if(!existentCheckingAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The account holder already has a checking account.");
        }

        // Create a student checking account if the account holder is younger than 24
        int age = calculateAge(checking.getAccountHolder().getDateOfBirth(), LocalDate.now());

        if(age < 24) {
            checking.setCheckingType(CheckingType.STUDENT_CHECKING);
            checking.setMinimumBalance(null);
            checking.setMonthlyMaintenanceFee(null);
        }
        checking.setAccountHolder(accountHolder.get());
        return checkingRepository.save(checking);
    }

    // Helper method to calculate the age of the account holder
    public static int calculateAge(LocalDate dateOfBirth, LocalDate currentDate) {
        if(dateOfBirth != null && currentDate != null) {
            return Period.between(dateOfBirth, currentDate).getYears();
        } else {
            return 0;
        }
    }

    // Method to update the account's balance
    public void updateBalance(Long id, BigDecimal balance) {
        Optional<Checking> optionalChecking = checkingRepository.findById(id);
        if(optionalChecking.isPresent()) {
            optionalChecking.get().setBalance(balance);
            checkingRepository.save(optionalChecking.get());
        }
    }
}
