package com.example.EyeCareHubDB.service;

import com.example.EyeCareHubDB.dto.AddressDTO;
import com.example.EyeCareHubDB.dto.AddressCreateRequest;
import com.example.EyeCareHubDB.dto.AddressUpdateRequest;
import com.example.EyeCareHubDB.entity.Address;
import com.example.EyeCareHubDB.entity.Customer;
import com.example.EyeCareHubDB.repository.AddressRepository;
import com.example.EyeCareHubDB.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    
    public List<AddressDTO> getAddressesByCustomerId(Long customerId) {
        return addressRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public AddressDTO getAddressById(Long id) {
        return addressRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
    }
    
    public AddressDTO getDefaultAddress(Long customerId) {
        return addressRepository.findDefaultAddressByCustomerId(customerId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("No default address found for customer: " + customerId));
    }
    
    public AddressDTO createAddress(Long customerId, AddressCreateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        Address address = Address.builder()
                .customer(customer)
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .district(request.getDistrict())
                .ward(request.getWard())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null ? request.getCountry() : "Vietnam")
                .type(Address.AddressType.valueOf(request.getType() != null ? request.getType() : "HOME"))
                .isDefault(false)
                .build();
        
        Address saved = addressRepository.save(address);
        return toDTO(saved);
    }
    
    public AddressDTO updateAddress(Long id, AddressUpdateRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
        
        if (request.getRecipientName() != null) {
            address.setRecipientName(request.getRecipientName());
        }
        if (request.getPhoneNumber() != null) {
            address.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddressLine1() != null) {
            address.setAddressLine1(request.getAddressLine1());
        }
        if (request.getAddressLine2() != null) {
            address.setAddressLine2(request.getAddressLine2());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getDistrict() != null) {
            address.setDistrict(request.getDistrict());
        }
        if (request.getWard() != null) {
            address.setWard(request.getWard());
        }
        if (request.getProvince() != null) {
            address.setProvince(request.getProvince());
        }
        if (request.getPostalCode() != null) {
            address.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }
        if (request.getType() != null) {
            address.setType(Address.AddressType.valueOf(request.getType()));
        }
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }
        
        Address updated = addressRepository.save(address);
        return toDTO(updated);
    }
    
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found with id: " + id);
        }
        addressRepository.deleteById(id);
    }
    
    public void setDefaultAddress(Long customerId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
        
        if (!address.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Address does not belong to this customer");
        }
        
        addressRepository.findDefaultAddressByCustomerId(customerId).ifPresent(defaultAddr -> {
            defaultAddr.setIsDefault(false);
            addressRepository.save(defaultAddr);
        });
        
        address.setIsDefault(true);
        addressRepository.save(address);
    }
    
    private AddressDTO toDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .phoneNumber(address.getPhoneNumber())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .district(address.getDistrict())
                .ward(address.getWard())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .type(address.getType().name())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
