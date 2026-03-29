package com.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA entity for persisting quantity measurement operation records.
 * Replaces the UC16 hand-written JDBC entity with Spring Data JPA mapping.
 */
@Entity
@Table(
    name = "QUANTITY_MEASUREMENTS",
    indexes = {
        @Index(name = "IDX_QM_OPERATION",       columnList = "operation"),
        @Index(name = "IDX_QM_MEASUREMENT_TYPE", columnList = "thisMeasurementType")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String operation;

    // ── First operand ──────────────────────────────────────────────────────
    @Column(length = 50)
    private String thisValue;

    @Column(length = 50)
    private String thisUnit;

    @Column(length = 50)
    private String thisMeasurementType;

    // ── Second operand ─────────────────────────────────────────────────────
    @Column(length = 50)
    private String thatValue;

    @Column(length = 50)
    private String thatUnit;

    @Column(length = 50)
    private String thatMeasurementType;

    // ── Result ─────────────────────────────────────────────────────────────
    @Column(length = 50)
    private String resultValue;

    @Column(length = 50)
    private String resultUnit;

    @Column(length = 50)
    private String resultMeasurementType;

    /** Non-null result string used for COMPARE (true/false). */
    @Column(length = 20)
    private String resultString;

    // ── Error ──────────────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean isError = false;

    @Column(length = 500)
    private String errorMessage;

    // ── Timestamps ─────────────────────────────────────────────────────────
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}