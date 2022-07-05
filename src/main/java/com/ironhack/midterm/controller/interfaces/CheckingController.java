package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.controller.dto.BalanceDTO;
import com.ironhack.midterm.models.AccountData.Checking;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface CheckingController {

    Optional<Checking> getCheckingByIdAndPrimaryOwner(long id, String primaryOwner);
    Optional<Checking> getCheckingByIdAndSecondaryOwner(long id, String secondaryOwner);
    void update(Long id, BalanceDTO balanceDTO);
    Checking store(Checking checking);
    Checking findById(Long id);
    List<Checking> findAll();
}

