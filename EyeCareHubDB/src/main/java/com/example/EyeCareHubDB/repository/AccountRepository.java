package com.example.EyeCareHubDB.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByEmail(String email);
    
    Optional<Account> findByEmailAndStatus(String email, Account.AccountStatus status);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT a FROM Account a WHERE a.role = :role")
    java.util.List<Account> findByRole(@Param("role") Account.AccountRole role);
}
