package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.controller.dto.BalanceDTO;
import com.ironhack.midterm.models.AccountData.Savings;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface SavingsController {

    List<Savings> findAll();
    Savings findById(Long id);
    Savings store(Savings savings);
    void addInterest(Long id);
    void update(Long id, BalanceDTO balanceDTO);

    Optional<Savings> getSavingsByIdAndPrimaryOwner(long id, String primaryOwner);
    Optional<Savings> getSavingsByIdAndSecondaryOwner(long id, String secondaryOwner);
}