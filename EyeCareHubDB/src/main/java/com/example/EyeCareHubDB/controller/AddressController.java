package com.example.EyeCareHubDB.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EyeCareHubDB.dto.AddressCreateRequest;
import com.example.EyeCareHubDB.dto.AddressDTO;
import com.example.EyeCareHubDB.dto.AddressUpdateRequest;
import com.example.EyeCareHubDB.service.AddressService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "Address Management APIs")
public class AddressController {
    
    private final AddressService addressService;
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AddressDTO>> getAddressesByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(addressService.getAddressesByCustomerId(customerId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }
    
    @GetMapping("/customer/{customerId}/default")
    public ResponseEntity<AddressDTO> getDefaultAddress(@PathVariable Long customerId) {
        return ResponseEntity.ok(addressService.getDefaultAddress(customerId));
    }
    
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<AddressDTO> createAddress(
            @PathVariable Long customerId,
            @RequestBody AddressCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.createAddress(customerId, request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressUpdateRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
