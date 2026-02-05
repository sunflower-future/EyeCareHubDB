package com.example.EyeCareHubDB.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.EyeCareHubDB.dto.AccountResponse;
import com.example.EyeCareHubDB.dto.LoginRequest;
import com.example.EyeCareHubDB.dto.RegisterRequest;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.service.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and Authorization APIs")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authenticationService.register(request);
        return ResponseEntity.ok("Register successfully");
    }
    
    @PostMapping("/login")
    public ResponseEntity<AccountResponse> login(@RequestBody LoginRequest loginRequest) {
        AccountResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authenticationService.forgotPassword(email);
        return ResponseEntity.ok("Send email successfully");
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String password) {
        authenticationService.resetPassword(password);
        return ResponseEntity.ok("Reset password successfully");
    }
    
    @PostMapping("/admin/create-account")
    public ResponseEntity<String> createAccountByAdmin(@RequestBody RegisterRequest request) {
        Account newAccount = authenticationService.createAccountByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Admin created account successfully for user: " + newAccount.getEmail());
    }
}
