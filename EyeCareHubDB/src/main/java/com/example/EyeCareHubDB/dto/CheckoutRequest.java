package com.example.EyeCareHubDB.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CheckoutRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    private String notes;
}
