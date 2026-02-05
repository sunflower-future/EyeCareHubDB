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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.EyeCareHubDB.dto.CustomerCreateRequest;
import com.example.EyeCareHubDB.dto.CustomerDTO;
import com.example.EyeCareHubDB.dto.CustomerUpdateRequest;
import com.example.EyeCareHubDB.service.CustomerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer Management APIs")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<CustomerDTO> getCustomerByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(customerService.getCustomerByAccountId(accountId));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomersByName(@RequestParam String name) {
        return ResponseEntity.ok(customerService.searchCustomersByName(name));
    }
    
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request.getAccountId(), request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerUpdateRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
