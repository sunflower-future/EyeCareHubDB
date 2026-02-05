package com.example.EyeCareHubDB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressUpdateRequest {
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
    private String type;
    private Boolean isDefault;
}    

