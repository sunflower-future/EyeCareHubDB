package com.example.EyeCareHubDB.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.AccountCreateRequest;
import com.example.EyeCareHubDB.dto.AccountDTO;
import com.example.EyeCareHubDB.dto.AccountUpdateRequest;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public AccountDTO getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }
    
    public AccountDTO getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + email));
    }
    
    public AccountDTO createAccount(AccountCreateRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        Account account = Account.builder()
                .email(request.getEmail())
                .passwordHash(request.getPasswordHash())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole() != null ? Account.AccountRole.valueOf(request.getRole()) : Account.AccountRole.CUSTOMER)
                .status(Account.AccountStatus.ACTIVE)
                .build();
        
        Account saved = accountRepository.save(account);
        return toDTO(saved);
    }
    
    public AccountDTO updateAccount(Long id, AccountUpdateRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        
        if (request.getPhoneNumber() != null) {
            account.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getRole() != null) {
            account.setRole(Account.AccountRole.valueOf(request.getRole()));
        }
        if (request.getStatus() != null) {
            account.setStatus(Account.AccountStatus.valueOf(request.getStatus()));
        }
        
        Account updated = accountRepository.save(account);
        return toDTO(updated);
    }
    
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        account.setStatus(Account.AccountStatus.DELETED);
        accountRepository.save(account);
    }
    
    public List<AccountDTO> getAccountsByRole(Account.AccountRole role) {
        return accountRepository.findByRole(role).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private AccountDTO toDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .role(account.getRole().name())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .lastLoginAt(account.getLastLoginAt())
                .build();
    }
}
