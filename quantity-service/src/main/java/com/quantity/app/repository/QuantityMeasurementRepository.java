package com.quantity.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quantity.app.model.QuantityMeasurementEntity;

import java.util.List;

/**
 * Spring Data JPA repository for measurement-service's own database.
 * All query methods are auto-implemented from method name conventions.
 */
@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

    List<QuantityMeasurementEntity> findByOperation(String operation);

    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);

    List<QuantityMeasurementEntity> findByIsErrorTrue();

    @Query("SELECT e FROM QuantityMeasurementEntity e " +
           "WHERE UPPER(e.operation) = UPPER(:operation) AND e.isError = false " +
           "ORDER BY e.createdAt")
    List<QuantityMeasurementEntity> findSuccessfulByOperation(@Param("operation") String operation);

    long countByOperationAndIsErrorFalse(String operation);

    long countByOperationAndIsErrorTrue(String operation);
}
