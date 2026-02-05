package com.example.EyeCareHubDB.service;

import com.example.EyeCareHubDB.dto.CustomerDTO;
import com.example.EyeCareHubDB.dto.CustomerCreateRequest;
import com.example.EyeCareHubDB.dto.CustomerUpdateRequest;
import com.example.EyeCareHubDB.dto.AddressDTO;
import com.example.EyeCareHubDB.entity.Customer;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.repository.CustomerRepository;
import com.example.EyeCareHubDB.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }
    
    public CustomerDTO getCustomerByAccountId(Long accountId) {
        return customerRepository.findByAccountId(accountId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Customer not found for account id: " + accountId));
    }
    
    public CustomerDTO createCustomer(Long accountId, CustomerCreateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        
        if (customerRepository.existsByAccountId(accountId)) {
            throw new RuntimeException("Customer already exists for this account");
        }
        
        Customer customer = Customer.builder()
                .account(account)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender() != null ? Customer.Gender.valueOf(request.getGender()) : null)
                .dateOfBirth(request.getDateOfBirth())
                .build();
        
        Customer saved = customerRepository.save(customer);
        return toDTO(saved);
    }
    
    public CustomerDTO updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getGender() != null) {
            customer.setGender(Customer.Gender.valueOf(request.getGender()));
        }
        if (request.getDateOfBirth() != null) {
            customer.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAvatarUrl() != null) {
            customer.setAvatarUrl(request.getAvatarUrl());
        }
        
        Customer updated = customerRepository.save(customer);
        return toDTO(updated);
    }
    
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    public List<CustomerDTO> searchCustomersByName(String name) {
        return customerRepository.searchByName(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private CustomerDTO toDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .accountId(customer.getAccount().getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .gender(customer.getGender() != null ? customer.getGender().name() : null)
                .dateOfBirth(customer.getDateOfBirth())
                .avatarUrl(customer.getAvatarUrl())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
