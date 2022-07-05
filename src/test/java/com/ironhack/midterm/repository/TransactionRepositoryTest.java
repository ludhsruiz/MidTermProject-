package com.ironhack.midterm.repository;

import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.models.Money;
import com.ironhack.midterm.models.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    private List<Transaction> transactionList;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private LocalDateTime transactionDate;

    @BeforeEach
    void setUp() {
        Money value1 = new Money(BigDecimal.valueOf(200));
        Money value2 = new Money(BigDecimal.valueOf(10));
        Money value3 = new Money(BigDecimal.valueOf(100));
        transactionDate = LocalDateTime.of(2019, Month.MARCH, 28, 14, 33, 48);

        transaction1 = new Transaction(TransactionType.MONEY_TRANSFER, AccountType.SAVINGS, 1L,
                AccountType.CHECKING, 2L, value1, transactionDate);
        transaction2 = new Transaction(TransactionType.INTEREST, AccountType.SAVINGS, 2L,
                null, null, value2, transactionDate);
        transaction3 = new Transaction(TransactionType.MONEY_TRANSFER, AccountType.SAVINGS, 1L,
                AccountType.CHECKING, 2L, value3, transactionDate);

        transactionList = transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3));
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
    }

    @Test
    void findByAccountOneIdAndAccountOneType_transaction() {
        AccountType accountType = transaction1.getAccountOneType();
        long id = transaction1.getAccountOneId();
        List<Transaction> transactionTestList = transactionRepository.findByAccountOneIdAndAccountOneType(id, accountType);
        assertTrue(transactionTestList.size() == 2);
    }

    @Test
    void findMaxAmount_transaction() {
        long id = transaction1.getAccountOneId();
        Optional<Double> optionalDouble = transactionRepository.findMaxAmount(String.valueOf(id));
        double resultAsDouble = optionalDouble.get();
        assertEquals(BigDecimal.valueOf(300).setScale(2, RoundingMode.CEILING), BigDecimal.valueOf(resultAsDouble).setScale(2, RoundingMode.CEILING));
    }

    @Test
    void findTotalToday_transaction() {
        long id = transaction1.getAccountOneId();
        Optional<Double> optionalDouble = transactionRepository.findTotalToday(String.valueOf(id), transactionDate);
        double resultAsDouble = optionalDouble.get();
        assertEquals(BigDecimal.valueOf(300).setScale(2, RoundingMode.CEILING), BigDecimal.valueOf(resultAsDouble).setScale(2, RoundingMode.CEILING));
    }

//    @Test
//    void findAllTransactionsFromId_transaction() {
//        long id = transaction1.getAccountOneId();
//        List<Transaction> transactionTestList = transactionRepository.findAllTransactionsFromId(String.valueOf(id), transactionDate);
//        assertEquals(transactionTestList.size(), transactionList.size());
//    }
//
//    @Test
//    void findAllTransactionsFromTime_transaction() {
//        List<Transaction> transactionTestList = transactionRepository.findAllTransactionsFromTime(transactionDate);
//        assertEquals(transactionTestList.size(), transactionList.size());
//    }
}