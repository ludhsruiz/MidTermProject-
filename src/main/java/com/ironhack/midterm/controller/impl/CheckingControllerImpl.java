package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.dto.BalanceDTO;
import com.ironhack.midterm.controller.interfaces.CheckingController;
import com.ironhack.midterm.models.AccountData.Checking;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.repository.CheckingRepository;
import com.ironhack.midterm.service.interfaces.CheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class CheckingControllerImpl implements CheckingController {

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private CheckingService checkingService;

    @GetMapping("/checkings")
    @ResponseStatus(HttpStatus.OK)
    public List<Checking> findAll() {
        return checkingRepository.findAll();
    }

    @GetMapping("/checking/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Checking findById(@PathVariable Long id) {
        return checkingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checking account not found."));
    }

    @PostMapping("/checkings")
    @ResponseStatus(HttpStatus.CREATED)
    public Checking store(@RequestBody @Valid Checking checking) {
        return checkingService.store(checking);
    }

    @PatchMapping("/modify/checking/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable(name = "id") Long id, @RequestBody @Valid BalanceDTO balanceDTO) {
        checkingService.updateBalance(id, balanceDTO.getBalance());
    }

    @GetMapping(value = "/checking", params = {"id", "primaryOwner"})
    @ResponseStatus(HttpStatus.OK)
    public Optional<Checking> getCheckingByIdAndPrimaryOwner(@RequestParam long id, @RequestParam String primaryOwner) {
        Owner owner = new Owner(primaryOwner);
        return checkingRepository.findByIdAndPrimaryOwner(id, owner);
    }

    @GetMapping(value = "/checking", params = {"id", "secondaryOwner"})
    @ResponseStatus(HttpStatus.OK)
    public Optional<Checking> getCheckingByIdAndSecondaryOwner(@RequestParam long id, @RequestParam String secondaryOwner) {
        Owner owner = new Owner(secondaryOwner);
        return checkingRepository.findByIdAndSecondaryOwner(id, owner);
    }
}
