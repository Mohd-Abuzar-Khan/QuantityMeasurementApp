package com.app.repository;

import com.app.entity.QuantityMeasurementEntity;
import java.util.List;

public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> findAll();

    QuantityMeasurementEntity findById(int id);

    void clear();

    int count();
}
