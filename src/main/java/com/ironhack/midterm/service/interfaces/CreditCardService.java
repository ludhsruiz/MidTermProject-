package com.ironhack.midterm.service.interfaces;

import com.ironhack.midterm.models.AccountData.CreditCard;

import java.math.BigDecimal;

public interface CreditCardService {

    void addInterest(Long id);
    void updateBalance(Long id, BigDecimal balance);
    CreditCard store(CreditCard creditCard);
}
