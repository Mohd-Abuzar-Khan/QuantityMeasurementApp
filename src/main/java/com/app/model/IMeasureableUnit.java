package com.app.model;

import com.app.unit.IMeasurable;

public interface IMeasureableUnit {
    String     getUnitName();
    String     getMeasurementType();
    IMeasurable getMeasurableUnit();
}
