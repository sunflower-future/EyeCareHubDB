package com.example.EyeCareHubDB.service;

import com.example.EyeCareHubDB.dto.AccountResponse;
import com.example.EyeCareHubDB.dto.LoginRequest;
import com.example.EyeCareHubDB.dto.RegisterRequest;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.Customer;
import com.example.EyeCareHubDB.repository.AccountRepository;
import com.example.EyeCareHubDB.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Account register(RegisterRequest request) {
        // Check if email already exists
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Create new account
        Account account = Account.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Account.AccountRole.CUSTOMER)
                .status(Account.AccountStatus.ACTIVE)
                .build();

        Account savedAccount = accountRepository.save(account);

        // Create customer profile
        if (request.getFirstName() != null || request.getLastName() != null) {
            Customer customer = Customer.builder()
                    .account(savedAccount)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .build();
            customerRepository.save(customer);
        }

        return savedAccount;
    }

    public AccountResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found: " + request.getEmail()));

        // Update last login time
        account.setLastLoginAt(LocalDateTime.now());
        accountRepository.save(account);

        String token = jwtService.generateToken(account);
        String refreshToken = jwtService.generateRefreshToken(account);

        // Get customer info if exists
        Customer customer = customerRepository.findByAccountId(account.getId()).orElse(null);

        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole().name())
                .phoneNumber(account.getPhoneNumber())
                .firstName(customer != null ? customer.getFirstName() : null)
                .lastName(customer != null ? customer.getLastName() : null)
                .token(token)
                .refreshToken(refreshToken)
                .message("Login successfully")
                .build();
    }

    public void forgotPassword(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found: " + email));

        // TODO: Generate password reset token and send email
        // For now, just a placeholder
        String resetToken = UUID.randomUUID().toString();
        // Store token in cache or database with expiration
    }

    public void resetPassword(String password) {
        // TODO: Verify token and update password
        // This should be called after validating the reset token from SecurityContext
    }

    public Account createAccountByAdmin(RegisterRequest request) {
        return register(request);
    }
}
