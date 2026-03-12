package com.app.dto;

import com.app.model.IMeasurable;

public interface IMeasureableUnit {
	public String getUnitName();
	public String getMeasurementType();
	IMeasurable getMeasurableUnit();
}
