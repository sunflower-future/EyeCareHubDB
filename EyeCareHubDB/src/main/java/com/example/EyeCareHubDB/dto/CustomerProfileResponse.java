package com.example.EyeCareHubDB.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private Integer totalOrders;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
