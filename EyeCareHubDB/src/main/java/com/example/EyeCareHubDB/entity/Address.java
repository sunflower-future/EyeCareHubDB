package com.example.EyeCareHubDB.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 255, columnDefinition = "nvarchar(255)")
    private String addressLine1;

    @Column(length = 255, columnDefinition = "nvarchar(255)")
    private String addressLine2;

    @Column(nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String city;

    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String district;

    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String ward;

    @Column(nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String province;

    @Column(length = 20)
    private String postalCode;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String country = "Vietnam";

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AddressType type = AddressType.HOME;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
        if (updatedAt == null)
            updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AddressType {
        HOME, OFFICE, OTHER
    }
}
