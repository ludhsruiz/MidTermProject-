package com.ironhack.midterm.models.AccountData;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private BigDecimal balance = BigDecimal.valueOf(0);
    private final BigDecimal penaltyFee = BigDecimal.valueOf(40);

    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "primary_owner"))
    })
    @Embedded
    private Owner primaryOwner;

    //Optional: Secondary Owner
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "secondary_owner"))
    })
    @Embedded
    private Owner secondaryOwner;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate = LocalDateTime.now();

    public Account(BigDecimal balance, Owner primaryOwner, Owner secondaryOwner, LocalDateTime creationDate) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.creationDate = creationDate;
    }

}