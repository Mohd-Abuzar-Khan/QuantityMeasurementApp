package com.app.repository;

import com.app.entity.QuantityMeasurementEntity;

import java.util.List;


public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> findAll();

    QuantityMeasurementEntity findById(int id);

    List<QuantityMeasurementEntity> findByOperationType(String operationType);

    List<QuantityMeasurementEntity> findByMeasurementType(String measurementType);

    void clear();

    int count();

    default String getPoolStatistics() {
        return "PoolStatistics{not available for this repository implementation}";
    }


    default void releaseResources() {
    }
}
