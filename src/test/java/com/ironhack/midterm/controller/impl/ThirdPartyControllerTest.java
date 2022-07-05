package com.ironhack.midterm.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.models.LoginData.Role;
import com.ironhack.midterm.models.LoginData.ThirdParty;
import com.ironhack.midterm.repository.ThirdPartyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class ThirdPartyControllerTest {


    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<ThirdParty> thirdPartyList;

    @BeforeEach
    void setUp() {

        Role thirdPartyRole1 = new Role("THIRD_PARTY");
        Role thirdPartyRole2 = new Role("THIRD_PARTY");
        thirdPartyList = thirdPartyRepository.saveAll(List.of(
                new ThirdParty("jane", "jane123", thirdPartyRole1, "Jane Doe", "123"),
                new ThirdParty("john", "john123", thirdPartyRole2, "John Doe", "1234")
        ));
    }

    @AfterEach
    void tearDown() {
        thirdPartyRepository.deleteAll();
    }

    @Test
    void findAll_listOfThirdParties() throws Exception {
        MvcResult result = mockMvc.perform(get("/third_party")).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("jane"));
        assertTrue(result.getResponse().getContentAsString().contains("john"));
    }

    @Test
    void findById_correct() throws Exception {
        ThirdParty thirdParty = thirdPartyList.get(0);
        long id = thirdParty.getId();
        MvcResult result = mockMvc.perform(get("/third_party/" + id)).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("jane"));
    }

    @Test
    void store_newThirdParty() throws Exception {
        Role role3 = new Role("THIRD_PARTY");
        ThirdParty newThirdParty = new ThirdParty("bob", "bob123", role3, "Bob Smith", "101");
        String body = objectMapper.writeValueAsString(newThirdParty);
        MvcResult result = mockMvc.perform(post("/create/third_party").content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("bob"));
    }
}