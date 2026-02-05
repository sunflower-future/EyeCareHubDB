package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreateRequest {
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private String role;
}