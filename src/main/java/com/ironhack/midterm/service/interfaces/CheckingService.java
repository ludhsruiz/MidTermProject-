package com.ironhack.midterm.service.interfaces;

import com.ironhack.midterm.models.AccountData.Checking;

import java.math.BigDecimal;

public interface CheckingService {

    Checking store(Checking checking);
    void updateBalance(Long id, BigDecimal balance);
}
