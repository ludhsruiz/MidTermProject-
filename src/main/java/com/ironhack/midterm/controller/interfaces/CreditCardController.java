package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.controller.dto.BalanceDTO;
import com.ironhack.midterm.models.AccountData.CreditCard;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface CreditCardController {

    List<CreditCard> findAll();
    CreditCard findById(Long id);
    CreditCard store(CreditCard creditCard);

    void addInterest(Long id);
    void update(Long id, BalanceDTO balanceDTO);

    Optional<CreditCard> getCreditCardByIdAndPrimaryOwner(long id, String primaryOwner);
    Optional<CreditCard> getCreditCardByIdAndSecondaryOwner(long id, String secondaryOwner);
}
