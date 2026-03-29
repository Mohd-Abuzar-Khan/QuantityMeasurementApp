package com.app.repository;

import com.app.model.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

    List<QuantityMeasurementEntity> findByOperation(String operation);

    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);

    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);

    List<QuantityMeasurementEntity> findByIsErrorTrue();

    @Query("SELECT e FROM QuantityMeasurementEntity e " + "WHERE UPPER(e.operation) = UPPER(:operation) AND e.isError = false " + "ORDER BY e.createdAt")
    List<QuantityMeasurementEntity> findSuccessfulByOperation(@Param("operation") String operation);

    long countByOperationAndIsErrorFalse(String operation);

    long countByOperationAndIsErrorTrue(String operation);
}