package com.ironhack.midterm.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midterm.controller.dto.BalanceDTO;
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
class CheckingControllerTest {


    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Checking> checkingList;

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

        checkingList = checkingRepository.saveAll(List.of(
                new Checking(BigDecimal.valueOf(200), primaryOwnerOne, secondaryOwnerOne, creationDate, accountHolderOne, "123", CheckingType.STANDARD_CHECKING, Status.ACTIVE, BigDecimal.valueOf(250), BigDecimal.valueOf(12)),
                new Checking(BigDecimal.valueOf(300), primaryOwnerTwo, null, creationDate, accountHolderTwo, "321", CheckingType.STUDENT_CHECKING, Status.ACTIVE, null, null)
        ));
    }

    @AfterEach
    void tearDown() {
        checkingRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findAll_listOfCheckingAccounts() throws Exception {
        MvcResult result = mockMvc.perform(get("/checking")).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Jane"));
        assertTrue(result.getResponse().getContentAsString().contains("John"));
    }


    @Test
    void findByAccountId_correct() throws Exception {
        Checking checking = checkingList.get(0);
        long id = checking.getId();
        MvcResult result = mockMvc.perform(get("/checking/" + id)).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Jane"));
    }

    @Test
    void store_newCheckingAccount() throws Exception {
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

        Checking newCheckingAccount = new Checking(BigDecimal.valueOf(300), primaryOwner3, null, creationDate, accountHolderTwo, "321", CheckingType.STUDENT_CHECKING, Status.ACTIVE, null, null);
        String body = objectMapper.writeValueAsString(newCheckingAccount);
        MvcResult result = mockMvc.perform(post("/create/checking").content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Bob"));
    }


    @Test
    void update_balance_correct() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setBalance(BigDecimal.valueOf(500));
        String body = objectMapper.writeValueAsString(balanceDTO);
        Checking checking = checkingList.get(0);
        long id = checking.getId();
        mockMvc.perform(patch("/modify/checking/" + id).content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent()).andReturn();
        assertEquals(checkingRepository.findById(id).get().getBalance(), BigDecimal.valueOf(500.00).setScale(2, RoundingMode.CEILING));
    }

    @Test
    void getCheckingByIdAndPrimaryOwner_correct() throws Exception {
        Checking checking = checkingList.get(0);
        long id = checking.getId();
        MvcResult result = mockMvc.perform(get("/checking?id=" + id + "&primaryOwner=Jane")).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Jane"));
    }

    @Test
    void getCheckingByIdAndSecondaryOwner_correct() throws Exception {
        Checking checking = checkingList.get(0);
        long id = checking.getId();
        MvcResult result = mockMvc.perform(get("/checking?id=" + id + "&secondaryOwner=Jenny")).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Jenny"));
    }
}