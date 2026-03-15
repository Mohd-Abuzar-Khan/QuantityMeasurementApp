package com.app.service;

import com.app.dto.QuantityDTO;
import com.app.model.IMeasurable;
import com.app.model.QuantityModel;

public interface IQuantityMeasurementService<U extends IMeasurable> {
	
	boolean compare(QuantityDTO q1, QuantityDTO q2);
	
	QuantityDTO convert(QuantityDTO source, QuantityDTO target);

    QuantityDTO add(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit);

    QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2, QuantityDTO targetUnit);

    double divide(QuantityDTO q1, QuantityDTO q2);

}