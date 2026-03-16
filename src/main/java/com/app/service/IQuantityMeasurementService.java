package com.app.service;

import com.app.entity.QuantityDTO;

public interface IQuantityMeasurementService {

    boolean    compare(QuantityDTO q1, QuantityDTO q2);

    QuantityDTO convert(QuantityDTO source, QuantityDTO targetUnit);

    QuantityDTO add(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit);

    QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit);

    double divide(QuantityDTO q1, QuantityDTO q2);
}
