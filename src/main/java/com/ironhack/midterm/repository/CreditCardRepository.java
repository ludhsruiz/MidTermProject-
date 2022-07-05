package com.ironhack.midterm.repository;


import com.ironhack.midterm.models.AccountData.CreditCard;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByIdAndPrimaryOwner(Long id, Owner primaryOwner);
    Optional<CreditCard> findByIdAndSecondaryOwner(Long id, Owner secondaryOwner);
    Optional<CreditCard> findByAccountHolder(AccountHolder accountHolder);

}