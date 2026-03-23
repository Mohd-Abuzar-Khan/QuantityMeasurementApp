package com.app.service;

import com.app.dto.QuantityDTO;
import com.app.dto.QuantityMeasurementDTO;

import java.util.List;

public interface IQuantityMeasurementService {

    QuantityMeasurementDTO compare(QuantityDTO q1, QuantityDTO q2);

    QuantityMeasurementDTO convert(QuantityDTO source, QuantityDTO targetUnit);

    QuantityMeasurementDTO add(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit);

    QuantityMeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit);

    QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2);


    List<QuantityMeasurementDTO> getHistoryByOperation(String operation);

    List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType);

    long getOperationCount(String operation);

    List<QuantityMeasurementDTO> getErrorHistory();
}