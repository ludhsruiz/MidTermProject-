package com.ironhack.midterm.repository.LoginDataRepositories;

import com.ironhack.midterm.models.LoginData.Role;
import com.ironhack.midterm.models.LoginData.User;
import com.ironhack.midterm.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role("ACCOUNT_HOLDER");
        user = new User("bob", "123", role);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsername_user() {
        Optional<User> userOptional = userRepository.findById(user.getId());
        assertTrue(userOptional.isPresent());
    }
}