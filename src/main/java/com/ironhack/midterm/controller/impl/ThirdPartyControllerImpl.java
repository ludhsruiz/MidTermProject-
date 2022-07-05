package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.interfaces.ThirdPartyController;
import com.ironhack.midterm.models.LoginData.ThirdParty;
import com.ironhack.midterm.repository.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ThirdPartyControllerImpl implements ThirdPartyController {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @GetMapping("/third-party")
    @ResponseStatus(HttpStatus.OK)
    public List<ThirdParty> findAll() {
        return thirdPartyRepository.findAll();
    }

    @GetMapping("/third-party/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ThirdParty findById(@PathVariable Long id) {
        return thirdPartyRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party not found."));
    }

    @PostMapping("/third-party")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty store(@RequestBody @Valid ThirdParty thirdParty) {
        return thirdPartyRepository.save(thirdParty);
    }

}
