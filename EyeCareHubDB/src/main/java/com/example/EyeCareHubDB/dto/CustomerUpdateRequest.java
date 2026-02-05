package com.example.EyeCareHubDB.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerUpdateRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String avatarUrl;
}
