package com.example.EyeCareHubDB.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200, columnDefinition = "nvarchar(200)")
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(length = 100)
    private String sku;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String brand;

    @Column(length = 2000, columnDefinition = "nvarchar(2000)")
    private String shortDescription;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String fullDescription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductMedia> media = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isFeatured = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer viewCount = 0;

    @Builder.Default
    private Integer soldCount = 0;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String metaTitle;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String metaDescription;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String metaKeywords;

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
