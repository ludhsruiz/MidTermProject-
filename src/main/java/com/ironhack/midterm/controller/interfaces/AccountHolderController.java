package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.models.LoginData.AccountHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

public interface AccountHolderController {
    List<AccountHolder> findAll();
    AccountHolder findById(Long id);
    AccountHolder store(AccountHolder accountHolder);

}
