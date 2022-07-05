package com.ironhack.midterm.repository.AccountDataRepositories;

import com.ironhack.midterm.enums.CheckingType;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.models.AccountData.Checking;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.models.Address;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import com.ironhack.midterm.models.LoginData.Role;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.CheckingRepository;
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
class CheckingRepositoryTest {
    @Autowired
    CheckingRepository checkingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    private Checking checking;

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

        checking = new Checking(BigDecimal.valueOf(200), primaryOwnerOne, secondaryOwnerOne, creationDate, accountHolderOne, "123", CheckingType.STANDARD_CHECKING, Status.ACTIVE, BigDecimal.valueOf(250), BigDecimal.valueOf(12));
        checkingRepository.save(checking);
    }

    @AfterEach
    void tearDown() {
        checkingRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findByIdAndPrimaryOwner_checking() {
        Optional<Checking> checkingOptional = checkingRepository.findByIdAndPrimaryOwner(checking.getId(), checking.getPrimaryOwner());
        assertTrue(checkingOptional.isPresent());
    }

    @Test
    void findByIdAndSecondaryOwner_checking() {
        Optional<Checking> checkingOptional = checkingRepository.findByIdAndSecondaryOwner(checking.getId(), checking.getSecondaryOwner());
        assertTrue(checkingOptional.isPresent());
    }
}