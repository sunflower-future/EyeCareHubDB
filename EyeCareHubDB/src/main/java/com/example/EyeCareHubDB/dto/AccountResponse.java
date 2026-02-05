package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String email;
    private String role;
    private String token;
    private String refreshToken;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String message;
}
