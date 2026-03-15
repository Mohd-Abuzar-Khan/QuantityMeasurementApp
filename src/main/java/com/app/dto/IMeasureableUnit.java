package com.app.dto;

import com.app.model.IMeasurable;

public interface IMeasureableUnit {
    String     getUnitName();
    String     getMeasurementType();
    IMeasurable getMeasurableUnit();
}
