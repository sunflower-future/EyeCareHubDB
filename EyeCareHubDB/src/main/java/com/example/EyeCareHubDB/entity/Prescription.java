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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_item_id", nullable = false, unique = true)
    private OrderItem orderItem;

    @Column(nullable = false, length = 200, columnDefinition = "nvarchar(200)")
    private String customerName;

    @Column(length = 20)
    private String rightSphere;

    @Column(length = 20)
    private String rightCylinder;

    @Column(length = 20)
    private String rightAxis;

    @Column(length = 20)
    private String rightPD;

    @Column(length = 20)
    private String leftSphere;

    @Column(length = 20)
    private String leftCylinder;

    @Column(length = 20)
    private String leftAxis;

    @Column(length = 20)
    private String leftPD;

    @Column(length = 20)
    private String addPower;

    @Column(length = 1000, columnDefinition = "nvarchar(1000)")
    private String notes;

    @Column(length = 500)
    private String prescriptionImageUrl;

    @ManyToOne
    @JoinColumn(name = "verified_by_staff_id")
    private Account verifiedByStaff;

    private LocalDateTime verifiedAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrescriptionStatus status = PrescriptionStatus.PENDING;

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

    public enum PrescriptionStatus {
        PENDING, VERIFIED, REJECTED
    }
}
