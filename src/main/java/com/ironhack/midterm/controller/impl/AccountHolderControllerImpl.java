package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.AccountHolderController;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import com.ironhack.midterm.repository.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AccountHolderControllerImpl implements AccountHolderController {

    @Autowired
    private AccountHolderRepository accountHolderRepository;


    @GetMapping("/account-holders")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountHolder> findAll() {
        return accountHolderRepository.findAll();
    }

    @GetMapping("/account-holder/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountHolder findById(@PathVariable Long id) {
        return accountHolderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account holder not found."));
    }


    @PostMapping("/account-holders")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder store(@RequestBody @Valid AccountHolder accountHolder) {
        return accountHolderRepository.save(accountHolder);
    }

}
