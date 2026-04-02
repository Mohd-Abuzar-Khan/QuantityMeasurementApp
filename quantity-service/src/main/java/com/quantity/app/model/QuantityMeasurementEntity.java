package com.quantity.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA entity for measurement-service's own database.
 *
 * Stores a record for every operation performed (compare, convert, add, subtract, divide).
 * This table is PRIVATE to measurement-service.
 * auth-service never reads this table directly — it calls the REST API.
 */
@Entity
@Table(
    name = "QUANTITY_MEASUREMENTS",
    indexes = {
        @Index(name = "IDX_QM_OPERATION",      columnList = "operation"),
        @Index(name = "IDX_QM_MEASURE_TYPE",   columnList = "thisMeasurementType")
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

    // First operand
    @Column(length = 50) private String thisValue;
    @Column(length = 50) private String thisUnit;
    @Column(length = 50) private String thisMeasurementType;

    // Second operand
    @Column(length = 50) private String thatValue;
    @Column(length = 50) private String thatUnit;
    @Column(length = 50) private String thatMeasurementType;

    // Result
    @Column(length = 50)  private String resultValue;
    @Column(length = 50)  private String resultUnit;
    @Column(length = 50)  private String resultMeasurementType;
    @Column(length = 20)  private String resultString;  // "true"/"false" for COMPARE

    // Error
    @Column(nullable = false) private boolean isError = false;
    @Column(length = 500)     private String  errorMessage;

    // Timestamps
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column private LocalDateTime updatedAt;

    @PrePersist  protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate   protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
