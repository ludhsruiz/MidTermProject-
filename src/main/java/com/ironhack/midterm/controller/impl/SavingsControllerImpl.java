package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.dto.BalanceDTO;
import com.ironhack.midterm.controller.interfaces.SavingsController;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.models.AccountData.Savings;
import com.ironhack.midterm.repository.SavingsRepository;
import com.ironhack.midterm.service.interfaces.SavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class SavingsControllerImpl implements SavingsController {
    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    private SavingsService savingsService;

    @GetMapping("/savings")
    @ResponseStatus(HttpStatus.OK)
    public List<Savings> findAll() {
        return savingsRepository.findAll();
    }

    @GetMapping("/savings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Savings findById(@PathVariable Long id) {
        return savingsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings account not found."));
    }

    @PostMapping("/savings")
    @ResponseStatus(HttpStatus.CREATED)
    public Savings store(@RequestBody @Valid Savings savings) {
        return savingsService.store(savings);
    }

    @PatchMapping("/savings/interest/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addInterest(@PathVariable(value = "id") Long id) {
        savingsService.addInterest(id);
    }

    @PatchMapping("/modify/savings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable(name = "id") Long id, @RequestBody @Valid BalanceDTO balanceDTO) {
        savingsService.updateBalance(id, balanceDTO.getBalance());
    }

    @GetMapping(value = "/savings", params = {"id", "primaryOwner"})
    @ResponseStatus(HttpStatus.OK)
    public Optional<Savings> getSavingsByIdAndPrimaryOwner(@RequestParam long id, @RequestParam String primaryOwner) {
        Owner owner = new Owner(primaryOwner);
        return savingsRepository.findByIdAndPrimaryOwner(id, owner);
    }

    @GetMapping(value = "/savings", params = {"id", "secondaryOwner"})
    @ResponseStatus(HttpStatus.OK)
    public Optional<Savings> getSavingsByIdAndSecondaryOwner(@RequestParam long id, @RequestParam String secondaryOwner) {
        Owner owner = new Owner(secondaryOwner);
        return savingsRepository.findByIdAndSecondaryOwner(id, owner);
    }
}
