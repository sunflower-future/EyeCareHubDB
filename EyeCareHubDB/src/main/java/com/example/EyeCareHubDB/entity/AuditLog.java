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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String oldValue;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String newValue;

    @ManyToOne
    @JoinColumn(name = "changed_by_id")
    private Account changedBy;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(length = 50)
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        if (changedAt == null)
            changedAt = LocalDateTime.now();
    }

    public enum AuditAction {
        CREATE, UPDATE, DELETE, STATUS_CHANGE
    }
}
