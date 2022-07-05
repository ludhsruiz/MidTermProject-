package com.ironhack.midterm.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midterm.controller.dto.BalanceDTO;
import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.models.AccountData.Savings;
import com.ironhack.midterm.models.Address;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import com.ironhack.midterm.models.LoginData.Role;
import com.ironhack.midterm.repository.AccountHolderRepository;
import com.ironhack.midterm.repository.SavingsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SavingsControllerTest {

    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Savings> savingsList;

    @BeforeEach
    void setUp() {

        Role role = new Role("ACCOUNT_HOLDER");
        LocalDate dateOfBirth = LocalDate.parse("1995-01-01");
        Address primaryAddress = new Address("Street address", "12345", "Berlin");
        AccountHolder accountHolderOne = new AccountHolder("jane", passwordEncoder.encode("123456"), role, dateOfBirth, primaryAddress,
                null);

        Role role2 = new Role("ACCOUNT_HOLDER");
        LocalDate dateOfBirth2 = LocalDate.parse("1996-02-02");
        Address primaryAddress2 = new Address("Street address2", "12346", "Paris");
        AccountHolder accountHolderTwo = new AccountHolder("john", passwordEncoder.encode("123456"), role2, dateOfBirth2, primaryAddress2,
                null);

        List<AccountHolder> accountHolders = accountHolderRepository.saveAll(List.of(accountHolderOne, accountHolderTwo));

        Owner primaryOwnerOne = new Owner("Jane");
        Owner secondaryOwnerOne = new Owner("Jenny");
        Owner primaryOwnerTwo = new Owner("John");
        LocalDateTime creationDate = LocalDateTime.of(2019, Month.MARCH, 28, 14, 33, 48);

        savingsList = savingsRepository.saveAll(List.of(
                new Savings(BigDecimal.valueOf(100), primaryOwnerOne, secondaryOwnerOne,
                creationDate, accountHolderOne, "secretkey123", Status.ACTIVE,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(0.1)),
                new Savings(BigDecimal.valueOf(200), primaryOwnerTwo, null,
                        creationDate, accountHolderTwo, "secretkey123", Status.ACTIVE,
                        BigDecimal.valueOf(1000), BigDecimal.valueOf(0.1))
        ));
    }

    @AfterEach
    void tearDown() {
        savingsRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findAll_listOfSavings() throws Exception {
        MvcResult result = mockMvc.perform(get("/savings")).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Jane"));
        assertTrue(result.getResponse().getContentAsString().contains("John"));
    }

    @Test
    void findByAccountId_correct() throws Exception {
        Savings savings = savingsList.get(0);
        long id = savings.getId();
        MvcResult result = mockMvc.perform(get("/savings/" + id)).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Jane"));
    }

    @Test
    void store_newSavingsAccount() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Role role3 = new Role("ACCOUNT_HOLDER");
        LocalDate dateOfBirth3 = LocalDate.parse("1994-01-01");
        Address primaryAddress3 = new Address("Street address3", "55555", "London");
        AccountHolder accountHolderTwo = new AccountHolder("bob", passwordEncoder.encode("123456"), role3, dateOfBirth3, primaryAddress3,
                null);
        accountHolderRepository.save(accountHolderTwo);
        Owner primaryOwner3 = new Owner("Bob");
        LocalDateTime creationDate = LocalDateTime.of(2019, Month.MARCH, 28, 14, 33, 48);

        Savings newSavingsAccount = new Savings(BigDecimal.valueOf(300), primaryOwner3, null,
                creationDate, accountHolderTwo, "secretkey123", Status.ACTIVE,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(0.0025));

        String body = objectMapper.writeValueAsString(newSavingsAccount);
        MvcResult result = mockMvc.perform(post("/create/savings").content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Bob"));
    }


    @Test
    void addInterest_correct() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Savings savings = savingsList.get(0);
        long id = savings.getId();
        BigDecimal balance = savings.getBalance();
        String body = objectMapper.writeValueAsString(savings);
        mockMvc.perform(patch("/savings/interest/" + id).content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent()).andReturn();
        assertEquals(savingsRepository.findById(id).get().getBalance(), balance.multiply(BigDecimal.valueOf(1.1)).setScale(2, RoundingMode.CEILING));
    }

    @Test
    void update_balance_correct() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setBalance(BigDecimal.valueOf(500));
        String body = objectMapper.writeValueAsString(balanceDTO);
        Savings savings = savingsList.get(0);
        long id = savings.getId();
        mockMvc.perform(patch("/modify/savings/" + id).content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent()).andReturn();
        assertEquals(savingsRepository.findById(id).get().getBalance(), BigDecimal.valueOf(500.00).setScale(2, RoundingMode.CEILING));
    }

    @Test
    void getSavingsByIdAndPrimaryOwner_correct() throws Exception {
        Savings savings = savingsList.get(0);
        long id = savings.getId();
        MvcResult result = mockMvc.perform(get("/savings?id=" + id + "&primaryOwner=Jane")).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Jane"));
    }

    @Test
    void getSavingsByIdAndSecondaryOwner_correct() throws Exception {
        Savings savings = savingsList.get(0);
        long id = savings.getId();
        MvcResult result = mockMvc.perform(get("/savings?id=" + id + "&secondaryOwner=Jenny")).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Jenny"));
    }
}