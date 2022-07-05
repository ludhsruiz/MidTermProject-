package com.ironhack.midterm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midterm.controller.dto.BalanceDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CreditCardServiceTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<CreditCard> creditCardList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Role role = new Role("ACCOUNT_HOLDER");
        LocalDate dateOfBirth = LocalDate.parse("1995-01-01");
        Address primaryAddress = new Address("Street address", "12345", "Berlin");
        AccountHolder accountHolderOne = new AccountHolder("jane", "password123", role, dateOfBirth, primaryAddress,
                null);

        Role role2 = new Role("ACCOUNT_HOLDER");
        LocalDate dateOfBirth2 = LocalDate.parse("1996-02-02");
        Address primaryAddress2 = new Address("Street address2", "12346", "Paris");
        AccountHolder accountHolderTwo = new AccountHolder("john", "password123", role2, dateOfBirth2, primaryAddress2,
                null);

        List<AccountHolder> accountHolders = accountHolderRepository.saveAll(List.of(accountHolderOne, accountHolderTwo));

        Owner primaryOwnerOne = new Owner("Jane");
        Owner secondaryOwnerOne = new Owner("Jenny");
        Owner primaryOwnerTwo = new Owner("John");
        LocalDateTime creationDate = LocalDateTime.of(2019, Month.MARCH, 28, 14, 33, 48);

        creditCardList = creditCardRepository.saveAll(List.of(
                new CreditCard(BigDecimal.valueOf(100), primaryOwnerOne, secondaryOwnerOne, creationDate,
                        accountHolderOne, BigDecimal.valueOf(100), BigDecimal.valueOf(0.2)),
                new CreditCard(BigDecimal.valueOf(100), primaryOwnerTwo, null, creationDate,
                        accountHolderTwo, BigDecimal.valueOf(100), BigDecimal.valueOf(0.2))
        ));
    }

    @AfterEach
    void tearDown() {
        creditCardRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void store_newCreditCardAccount() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Role role3 = new Role("ACCOUNT_HOLDER");
        LocalDate dateOfBirth3 = LocalDate.parse("1994-01-01");
        Address primaryAddress3 = new Address("Street address3", "55555", "London");
        AccountHolder accountHolderTwo = new AccountHolder("bob", "password123", role3, dateOfBirth3, primaryAddress3,
                null);
        accountHolderRepository.save(accountHolderTwo);
        Owner primaryOwner3 = new Owner("Bob");
        LocalDateTime creationDate = LocalDateTime.of(2019, Month.MARCH, 28, 14, 33, 48);

        CreditCard newCreditCardAccount = new CreditCard(BigDecimal.valueOf(100), primaryOwner3, null, creationDate,
                accountHolderTwo, BigDecimal.valueOf(100), BigDecimal.valueOf(0.2));

        String body = objectMapper.writeValueAsString(newCreditCardAccount);
        MvcResult result = mockMvc.perform(post("/create/creditcard").content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Bob"));
    }

    @Test
    void addInterest_correct() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        CreditCard creditCard = creditCardList.get(0);
        long id = creditCard.getId();
        BigDecimal balance = creditCard.getBalance();
        BigDecimal interestRate = creditCard.getInterestRate();
        String body = objectMapper.writeValueAsString(creditCard);
        mockMvc.perform(patch("/creditcard/interest/" + id).content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent()).andReturn();
        assertEquals(creditCardRepository.findById(id).get().getBalance(), balance.multiply(BigDecimal.valueOf(1.1)).setScale(2, RoundingMode.CEILING));
    }

    @Test
    void interestAddedOneMonthOrLonger() {
    }

    @Test
    void hasNeverGottenAnyInterest() {
    }

    @Test
    void update_balance_correct() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setBalance(BigDecimal.valueOf(500));
        String body = objectMapper.writeValueAsString(balanceDTO);
        CreditCard creditCard = creditCardList.get(0);
        long id = creditCard.getId();
        mockMvc.perform(patch("/modify/creditcard/" + id).content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent()).andReturn();
        assertEquals(creditCardRepository.findById(id).get().getBalance(), BigDecimal.valueOf(500.00).setScale(2, RoundingMode.CEILING));
    }
}