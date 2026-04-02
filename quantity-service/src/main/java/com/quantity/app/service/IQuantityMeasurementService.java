package com.quantity.app.service;


import java.util.List;

import com.quantity.app.dto.QuantityDTO;
import com.quantity.app.dto.QuantityMeasurementDTO;



/**
 * Contract for all quantity measurement operations.
 * Implemented by QuantityMeasurementServiceImpl.
 */
public interface IQuantityMeasurementService {

    QuantityMeasurementDTO compare(QuantityDTO q1, QuantityDTO q2);

    QuantityMeasurementDTO convert(QuantityDTO source, QuantityDTO targetUnit);

    QuantityMeasurementDTO add(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit);

    QuantityMeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit);

    QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2);

    // History / analytics
    List<QuantityMeasurementDTO> getHistoryByOperation(String operation);

    List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType);

    long getOperationCount(String operation);

    List<QuantityMeasurementDTO> getErrorHistory();
}
