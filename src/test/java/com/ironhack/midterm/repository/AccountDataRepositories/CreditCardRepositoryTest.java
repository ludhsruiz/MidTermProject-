package com.ironhack.midterm.repository.AccountDataRepositories;

import com.ironhack.midterm.models.AccountData.CreditCard;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.models.Address;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import com.ironhack.midterm.models.LoginData.Role;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.CreditCardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CreditCardRepositoryTest {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    private CreditCard creditCard;

    @BeforeEach
    void setUp() {
        Role role = new Role("ACCOUNT_HOLDER");
        LocalDate dateOfBirth = LocalDate.parse("1995-01-01");
        Address primaryAddress = new Address("Street address", "12345", "Berlin");
        AccountHolder accountHolderOne = new AccountHolder("jane", "password123", role, dateOfBirth, primaryAddress,
                null);
        accountHolderRepository.save(accountHolderOne);

        Owner primaryOwnerOne = new Owner("Jane");
        Owner secondaryOwnerOne = new Owner("Jenny");
        LocalDateTime creationDate = LocalDateTime.of(2019, Month.MARCH, 28, 14, 33, 48);

        creditCard = new CreditCard(BigDecimal.valueOf(100), primaryOwnerOne, secondaryOwnerOne, creationDate,
                        accountHolderOne, BigDecimal.valueOf(100), BigDecimal.valueOf(0.2));
        creditCardRepository.save(creditCard);
    }

    @AfterEach
    void tearDown() {
        creditCardRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findByIdAndPrimaryOwner_creditCard() {
        Optional<CreditCard> creditCardOptional = creditCardRepository.findByIdAndPrimaryOwner(creditCard.getId(), creditCard.getPrimaryOwner());
        assertTrue(creditCardOptional.isPresent());
    }

    @Test
    void findByIdAndSecondaryOwner_creditCard() {
        Optional<CreditCard> creditCardOptional = creditCardRepository.findByIdAndSecondaryOwner(creditCard.getId(), creditCard.getSecondaryOwner());
        assertTrue(creditCardOptional.isPresent());
    }

    @Test
    void findByAccountHolder_creditCard() {
        Optional<CreditCard> creditCardOptional = creditCardRepository.findByAccountHolder(creditCard.getAccountHolder());
        assertTrue(creditCardOptional.isPresent());
    }
}