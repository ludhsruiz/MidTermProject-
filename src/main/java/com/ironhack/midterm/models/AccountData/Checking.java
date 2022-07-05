package com.ironhack.midterm.models.AccountData;


import com.ironhack.midterm.enums.CheckingType;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Checking extends Account {

    @OneToOne(fetch = FetchType.EAGER, cascade= CascadeType.MERGE, optional = false)
    @JoinColumn(name = "account_holder_id")
    private AccountHolder accountHolder;

    private String secretKey;

    @Enumerated(EnumType.STRING)
    private CheckingType checkingType = CheckingType.STANDARD_CHECKING;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    // 250
    // I don't use the class Money because it is not an amount  but a defining property of the class
    private BigDecimal minimumBalance = BigDecimal.valueOf(250);

    // 12
    // I don't use the class Money because it is not an amount  but a defining property of the class
    private BigDecimal monthlyMaintenanceFee = BigDecimal.valueOf(12);

    public Checking(BigDecimal balance, Owner primaryOwner, Owner secondaryOwner, LocalDateTime creationDate, AccountHolder accountHolder, String secretKey, CheckingType checkingType, Status status, BigDecimal minimumBalance, BigDecimal monthlyMaintenanceFee) {
        super(balance, primaryOwner, secondaryOwner, creationDate);
        this.accountHolder = accountHolder;
        this.secretKey = secretKey;
        this.checkingType = checkingType;
        this.status = status;
        this.minimumBalance = minimumBalance;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }
}
