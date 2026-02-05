package com.example.EyeCareHubDB.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String ward;
    private String province;
    private String postalCode;
    private String country;
    private Boolean isDefault;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



