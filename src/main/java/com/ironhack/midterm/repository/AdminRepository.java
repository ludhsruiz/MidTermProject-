package com.ironhack.midterm.repository;

import com.ironhack.midterm.models.LoginData.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
