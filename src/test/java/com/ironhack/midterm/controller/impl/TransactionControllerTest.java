package com.ironhack.midterm.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.enums.TransactionType;
import com.ironhack.midterm.models.Money;
import com.ironhack.midterm.models.Transaction;
import com.ironhack.midterm.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Transaction> transactionList;

    @BeforeEach
    void setUp() {
        Money value1 = new Money(BigDecimal.valueOf(200));
        Money value2 = new Money(BigDecimal.valueOf(10));
        LocalDateTime transactionDate = LocalDateTime.now();

        transactionList = transactionRepository.saveAll(List.of(
                new Transaction(TransactionType.MONEY_TRANSFER, AccountType.SAVINGS, 1L,
                        AccountType.CHECKING, 2L, value1, transactionDate),
                new Transaction(TransactionType.INTEREST, AccountType.SAVINGS, 2L,
                        null, null, value2, transactionDate)
        ));
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
    }

    @Test
    void findAll_listOfTransactions() throws Exception {
        MvcResult result = mockMvc.perform(get("/transactions")).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(String.valueOf(TransactionType.MONEY_TRANSFER)));
        assertTrue(result.getResponse().getContentAsString().contains(String.valueOf(TransactionType.INTEREST)));
    }

    @Test
    void findByTransactionId_correct() throws Exception {
        Transaction transaction = transactionList.get(0);
        long id = transaction.getId();
        MvcResult result = mockMvc.perform(get("/transactions/" + id)).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(String.valueOf(TransactionType.MONEY_TRANSFER)));
    }

    @Test
    void findByAccountOneId_correct() throws Exception {
        Transaction transaction = transactionList.get(0);
        long id = transaction.getAccountOneId();
        String transactionType = transaction.getTransactionType().toString();
        MvcResult result = mockMvc.perform(get("/transactions/savings/" + id)).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(transactionType));
    }

    @Test
    void transferMoney(){

    }


    @Test
    void transferMoneyThirdParty() {
    }
}