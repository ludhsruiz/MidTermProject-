package com.ironhack.midterm.service.interfaces;

import com.ironhack.midterm.models.AccountData.Savings;

import java.math.BigDecimal;

public interface SavingsService {

        void addInterest(Long id);
        void updateBalance(Long id, BigDecimal balance);
        Savings store(Savings savings);

}
