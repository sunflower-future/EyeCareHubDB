package com.example.EyeCareHubDB.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EyeCareHubDB.dto.AccountCreateRequest;
import com.example.EyeCareHubDB.dto.AccountDTO;
import com.example.EyeCareHubDB.dto.AccountUpdateRequest;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.service.AccountService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account Management APIs")
public class AccountController {
    
    private final AccountService accountService;
    
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<AccountDTO> getAccountByEmail(@PathVariable String email) {
        return ResponseEntity.ok(accountService.getAccountByEmail(email));
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<AccountDTO>> getAccountsByRole(@PathVariable String role) {
        return ResponseEntity.ok(accountService.getAccountsByRole(Account.AccountRole.valueOf(role)));
    }
    
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id,
            @RequestBody AccountUpdateRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
