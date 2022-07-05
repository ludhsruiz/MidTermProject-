package com.ironhack.midterm.repository;

import com.ironhack.midterm.models.LoginData.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
