package com.ironhack.midterm.models.LoginData;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.midterm.models.AccountData.Checking;
import com.ironhack.midterm.models.AccountData.CreditCard;
import com.ironhack.midterm.models.AccountData.Savings;
import com.ironhack.midterm.models.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class AccountHolder extends User {

    @DateTimeFormat(pattern = "yyy-MM-dd")
    private LocalDate dateOfBirth;

    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "primary_street")),
            @AttributeOverride(name = "city", column = @Column(name = "primary_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "primary_postal_code"))
    })
    @Embedded
    private Address primaryAddress;

    // Optional
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "mailing_street")),
            @AttributeOverride(name = "city", column = @Column(name = "mailing_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "mailing_postal_code"))
    })
    @Embedded
    private Address mailingAddress;

    @OneToOne(mappedBy = "accountHolder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Savings savings;

    @OneToOne(mappedBy = "accountHolder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private CreditCard creditCard;

    @OneToOne(mappedBy = "accountHolder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Checking checking;

    public AccountHolder(String username, String password, Role role, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress) {
        super(username, password, role);
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;
    }
}