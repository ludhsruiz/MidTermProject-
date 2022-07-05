package com.ironhack.midterm.repository;


import com.ironhack.midterm.models.AccountData.Checking;
import com.ironhack.midterm.models.AccountData.Owner;
import com.ironhack.midterm.models.LoginData.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckingRepository extends JpaRepository<Checking, Long> {
    Optional<Checking> findByIdAndPrimaryOwner(Long id, Owner primaryOwner);
    Optional<Checking> findByIdAndSecondaryOwner(Long id, Owner secondaryOwner);
    Optional<Checking> findByAccountHolder(AccountHolder accountHolder);
}