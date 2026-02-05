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
@Table(name = "product_media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaType type = MediaType.IMAGE;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 200, columnDefinition = "nvarchar(200)")
    private String altText;

    @Column(length = 500, columnDefinition = "nvarchar(500)")
    private String title;

    @Builder.Default
    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isPrimary = false;

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

    public enum MediaType {
        IMAGE, VIDEO, DOCUMENT
    }
}
