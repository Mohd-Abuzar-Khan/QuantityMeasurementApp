package com.quantity.app.dto;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

import com.quantity.app.model.QuantityMeasurementEntity;


/**
 * DTO returned by every REST endpoint.
 * Maps to/from QuantityMeasurementEntity, keeping API decoupled from DB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementDTO {

    // First operand
    private Double thisValue;
    private String thisUnit;
    private String thisMeasurementType;

    // Second operand
    private Double thatValue;
    private String thatUnit;
    private String thatMeasurementType;

    // Operation performed
    private String operation;

    // Result
    private String  resultString;   // non-null for COMPARE ("true"/"false")
    private Double  resultValue;    // non-null for arithmetic/conversion
    private String  resultUnit;
    private String  resultMeasurementType;

    // Error info
    private String  errorMessage;
    private boolean error;

    // ── Static factories ──────────────────────────────────────────────────

    public static QuantityMeasurementDTO fromEntity(QuantityMeasurementEntity e) {
        if (e == null) return null;
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue           (parseDouble(e.getThisValue()));
        dto.setThisUnit            (e.getThisUnit());
        dto.setThisMeasurementType (e.getThisMeasurementType());
        dto.setThatValue           (parseDouble(e.getThatValue()));
        dto.setThatUnit            (e.getThatUnit());
        dto.setThatMeasurementType (e.getThatMeasurementType());
        dto.setOperation           (e.getOperation());
        dto.setResultString        (e.getResultString());
        dto.setResultValue         (parseDouble(e.getResultValue()));
        dto.setResultUnit          (e.getResultUnit());
        dto.setResultMeasurementType(e.getResultMeasurementType());
        dto.setError               (e.isError());
        dto.setErrorMessage        (e.getErrorMessage());
        return dto;
    }

    public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> list) {
        return list.stream().map(QuantityMeasurementDTO::fromEntity).collect(Collectors.toList());
    }

    public QuantityMeasurementEntity toEntity() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity();
        e.setOperation           (operation);
        e.setThisValue           (thisValue  != null ? String.valueOf(thisValue)  : null);
        e.setThisUnit            (thisUnit);
        e.setThisMeasurementType (thisMeasurementType);
        e.setThatValue           (thatValue  != null ? String.valueOf(thatValue)  : null);
        e.setThatUnit            (thatUnit);
        e.setThatMeasurementType (thatMeasurementType);
        e.setResultValue         (resultValue != null ? String.valueOf(resultValue) : null);
        e.setResultUnit          (resultUnit);
        e.setResultMeasurementType(resultMeasurementType);
        e.setResultString        (resultString);
        e.setError               (error);
        e.setErrorMessage        (errorMessage);
        return e;
    }

    private static Double parseDouble(String s) {
        if (s == null) return null;
        try { return Double.parseDouble(s); } catch (NumberFormatException ex) { return null; }
    }
}
