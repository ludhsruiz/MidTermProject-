package com.ironhack.midterm.controller.interfaces;

import com.ironhack.midterm.models.LoginData.ThirdParty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

public interface ThirdPartyController {
    List<ThirdParty> findAll();
    ThirdParty findById(Long id);
    ThirdParty store(ThirdParty thirdParty);

}
