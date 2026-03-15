package com.app.entity;

import java.io.Serializable;


public class QuantityMeasurementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Fields ────────────────────────────────────────────────────────────────
    private String operand1Value;
    private String operand1Unit;
    private String operand2Value;   // null for single-operand operations
    private String operand2Unit;    // null for single-operand operations
    private String operationType;   // COMPARE, CONVERT, ADD, SUBTRACT, DIVIDE
    private String resultValue;
    private String resultUnit;
    private boolean hasError;
    private String errorMessage;
    private long   timestamp;

    // Required for deserialization 
    protected QuantityMeasurementEntity() {}

    public QuantityMeasurementEntity(String operationType,
                                     double operand1Value, String operand1Unit,
                                     double resultValue,   String resultUnit) {
        this.operationType  = operationType;
        this.operand1Value  = String.valueOf(operand1Value);
        this.operand1Unit   = operand1Unit;
        this.operand2Value  = null;
        this.operand2Unit   = null;
        this.resultValue    = String.valueOf(resultValue);
        this.resultUnit     = resultUnit;
        this.hasError       = false;
        this.errorMessage   = null;
        this.timestamp      = System.currentTimeMillis();
    }

 
    public QuantityMeasurementEntity(String operationType,
                                     double operand1Value, String operand1Unit,
                                     double operand2Value, String operand2Unit,
                                     double resultValue,   String resultUnit) {
        this.operationType  = operationType;
        this.operand1Value  = String.valueOf(operand1Value);
        this.operand1Unit   = operand1Unit;
        this.operand2Value  = String.valueOf(operand2Value);
        this.operand2Unit   = operand2Unit;
        this.resultValue    = String.valueOf(resultValue);
        this.resultUnit     = resultUnit;
        this.hasError       = false;
        this.errorMessage   = null;
        this.timestamp      = System.currentTimeMillis();
    }


    public QuantityMeasurementEntity(String operationType,
                                     String operand1Value, String operand1Unit,
                                     String operand2Value, String operand2Unit,
                                     String errorMessage) {
        this.operationType  = operationType;
        this.operand1Value  = operand1Value;
        this.operand1Unit   = operand1Unit;
        this.operand2Value  = operand2Value;
        this.operand2Unit   = operand2Unit;
        this.resultValue    = null;
        this.resultUnit     = null;
        this.hasError       = true;
        this.errorMessage   = errorMessage;
        this.timestamp      = System.currentTimeMillis();
    }

    // Getters
    public String  getOperand1Value()  { return operand1Value; }
    public String  getOperand1Unit()   { return operand1Unit;  }
    public String  getOperand2Value()  { return operand2Value; }
    public String  getOperand2Unit()   { return operand2Unit;  }
    public String  getOperationType()  { return operationType; }
    public String  getResultValue()    { return resultValue;   }
    public String  getResultUnit()     { return resultUnit;    }
    public boolean hasError()          { return hasError;      }
    public String  getErrorMessage()   { return errorMessage;  }
    public long    getTimestamp()      { return timestamp;     }

    @Override
    public String toString() {
        if (hasError) {
            return String.format("[%s] ERROR: %s | Op1: %s %s | Op2: %s %s",
                operationType, errorMessage,
                operand1Value, operand1Unit,
                operand2Value != null ? operand2Value : "-",
                operand2Unit  != null ? operand2Unit  : "-");
        }
        if (operand2Value != null) {
            return String.format("[%s] %s %s %s %s = %s %s",
                operationType,
                operand1Value, operand1Unit,
                operand2Value, operand2Unit,
                resultValue,   resultUnit);
        }
        return String.format("[%s] %s %s => %s %s",
            operationType,
            operand1Value, operand1Unit,
            resultValue,   resultUnit);
    }
}
