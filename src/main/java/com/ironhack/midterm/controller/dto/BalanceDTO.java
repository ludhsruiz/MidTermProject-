package com.ironhack.midterm.controller.dto;

import com.ironhack.midterm.models.AccountData.Owner;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BalanceDTO {

    private BigDecimal balance;
    private Long id;
    private Owner primaryOwner;

    // money instead of big decimal and account number
}
