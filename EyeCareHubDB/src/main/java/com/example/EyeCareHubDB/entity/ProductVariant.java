package com.example.EyeCareHubDB.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String color;

    @Column(length = 50)
    private String size;

    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String material;

    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String lensType;

    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String frameMaterial;

    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String frameShape;

    @Column(precision = 10, scale = 2)
    private BigDecimal additionalPrice;

    @Builder.Default
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    @Column(length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    private Integer displayOrder = 0;

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
}
