package com.ironhack.midterm.repository;


import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.models.AccountData.Savings;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Long> {
    Optional<Savings> findByIdAndPrimaryOwner(Long id, Owner primaryOwner);
    Optional<Savings> findByIdAndSecondaryOwner(Long id, Owner secondaryOwner);
    Optional<Savings> findByAccountHolder(AccountHolder accountHolder);

}