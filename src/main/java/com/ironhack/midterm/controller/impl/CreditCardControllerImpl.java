package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.dto.BalanceDTO;
import com.ironhack.midterm.controller.interfaces.CreditCardController;
import com.ironhack.midterm.models.AccountData.CreditCard;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.repository.CreditCardRepository;
import com.ironhack.midterm.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class CreditCardControllerImpl implements CreditCardController {
    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private CreditCardService creditCardService;

    @GetMapping("/creditcards")
    @ResponseStatus(HttpStatus.OK)
    public List<CreditCard> findAll() {
        return creditCardRepository.findAll();
    }

    @GetMapping("/creditcard/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CreditCard findById(@PathVariable Long id) {
        return creditCardRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit card account not found."));
    }

    @PostMapping("/creditcards")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCard store(@RequestBody @Valid CreditCard creditCard) {
        return creditCardService.store(creditCard);
    }

    @PatchMapping("/creditcard/interest/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addInterest(@PathVariable(value = "id") Long id) {
        creditCardService.addInterest(id);
    }

    @PatchMapping("/modify/creditcard/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable(name = "id") Long id, @RequestBody @Valid BalanceDTO balanceDTO) {
        creditCardService.updateBalance(id, balanceDTO.getBalance());
    }

    @GetMapping(value = "/creditcard", params = {"id", "primaryOwner"})
    @ResponseStatus(HttpStatus.OK)
    public Optional<CreditCard> getCreditCardByIdAndPrimaryOwner(@RequestParam long id, @RequestParam String primaryOwner) {
        Owner owner = new Owner(primaryOwner);
        return creditCardRepository.findByIdAndPrimaryOwner(id, owner);
    }

    @GetMapping(value = "/creditcard", params = {"id", "secondaryOwner"})
    @ResponseStatus(HttpStatus.OK)
    public Optional<CreditCard> getCreditCardByIdAndSecondaryOwner(@RequestParam long id, @RequestParam String secondaryOwner) {
        Owner owner = new Owner(secondaryOwner);
        return creditCardRepository.findByIdAndSecondaryOwner(id, owner);
    }

}
