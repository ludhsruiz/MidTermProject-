package com.ironhack.midterm.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private AccountType accountOneType;

    private Long accountOneId;

    @Enumerated(EnumType.STRING)
    private AccountType accountTwoType;

    private Long accountTwoId;

    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    @Embedded
    private Money value;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;

    public Transaction(TransactionType transactionType, AccountType accountOneType, Long accountOneId, AccountType accountTwoType, Long accountTwoId, Money value, LocalDateTime transactionDate) {
        this.transactionType = transactionType;
        this.accountOneType = accountOneType;
        this.accountOneId = accountOneId;
        this.accountTwoType = accountTwoType;
        this.accountTwoId = accountTwoId;
        this.value = value;
        this.transactionDate = transactionDate;
    }
}
