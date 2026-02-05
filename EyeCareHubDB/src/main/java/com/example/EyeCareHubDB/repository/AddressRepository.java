package com.example.EyeCareHubDB.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByCustomerId(Long customerId);
    
    List<Address> findByCustomerIdAndType(Long customerId, Address.AddressType type);
    
    Optional<Address> findByCustomerIdAndIsDefaultTrue(Long customerId);
    
    @Query("SELECT a FROM Address a WHERE a.customer.id = :customerId AND a.isDefault = true")
    Optional<Address> findDefaultAddressByCustomerId(@Param("customerId") Long customerId);
}
