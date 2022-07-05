package com.ironhack.midterm.repository.LoginDataRepositories;

import com.ironhack.midterm.models.LoginData.Role;
import com.ironhack.midterm.repository.RoleRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role("ACCOUNT_HOLDER");
        roleRepository.save(role);
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void findById_role() {
        Optional<Role> optionalRole = roleRepository.findById(role.getId());
        assertTrue(optionalRole.isPresent());
    }
}