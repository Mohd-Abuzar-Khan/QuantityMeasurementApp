	package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import com.app.model.QuantityMeasurementEntity;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementDTO {

    // ── First operand ──────────────────────────────────────────────────────
    private Double  thisValue;
    private String  thisUnit;
    private String  thisMeasurementType;

    // ── Second operand ─────────────────────────────────────────────────────
    private Double  thatValue;
    private String  thatUnit;
    private String  thatMeasurementType;

    // ── Operation ──────────────────────────────────────────────────────────
    private String  operation;

    // ── Result ─────────────────────────────────────────────────────────────
    /** Non-null for COMPARE operations (the string "true" / "false"). */
    private String  resultString;
    private Double  resultValue;
    private String  resultUnit;
    private String  resultMeasurementType;

    // ── Error ──────────────────────────────────────────────────────────────
    private String  errorMessage;
    private boolean error;

    // ── Static factory: entity → DTO ──────────────────────────────────────

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

    /** Convert this DTO back to a new entity. */
    public QuantityMeasurementEntity toEntity() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity();
        e.setOperation           (operation);
        e.setThisValue           (thisValue  != null ? String.valueOf(thisValue)   : null);
        e.setThisUnit            (thisUnit);
        e.setThisMeasurementType (thisMeasurementType);
        e.setThatValue           (thatValue  != null ? String.valueOf(thatValue)   : null);
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

    public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> entities) {
        return entities.stream()
            .map(QuantityMeasurementDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public static List<QuantityMeasurementEntity> toEntityList(List<QuantityMeasurementDTO> dtos) {
        return dtos.stream()
            .map(QuantityMeasurementDTO::toEntity)
            .collect(Collectors.toList());
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private static Double parseDouble(String s) {
        if (s == null) return null;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }
}