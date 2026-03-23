package com.app.controller;

//import com.app.config.SecurityConfig;
//import com.app.controller.QuantityMeasurementController;
import com.app.dto.QuantityDTO;
import com.app.dto.QuantityInputDTO;
import com.app.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.QuantityMeasurementApp;
import com.app.service.IQuantityMeasurementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(                                          // ← replace @WebMvcTest
	    classes = QuantityMeasurementApp.class        // ← point to your main class
	)
@AutoConfigureMockMvc                                     // ← sets up MockMvc
@ContextConfiguration(
	    classes = QuantityMeasurementApp.class        // ← explicit context
)
class QuantityMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IQuantityMeasurementService service;

    // ── Shared fixtures ────────────────────────────────────────────────────

    private QuantityInputDTO lengthInput;   // 1 FEET vs 12 INCHES
    private QuantityInputDTO weightInput;   // 1 KILOGRAM vs 1000 GRAM

    @BeforeEach
    void setUp() {
        lengthInput = new QuantityInputDTO();
        lengthInput.setThisQuantityDTO(new QuantityDTO(1.0,    "FEET",     "LengthUnit"));
        lengthInput.setThatQuantityDTO(new QuantityDTO(12.0,   "INCHES",   "LengthUnit"));

        weightInput = new QuantityInputDTO();
        weightInput.setThisQuantityDTO(new QuantityDTO(1.0,    "KILOGRAM", "WeightUnit"));
        weightInput.setThatQuantityDTO(new QuantityDTO(1000.0, "GRAM",     "WeightUnit"));
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private QuantityMeasurementDTO dto(String op, String resultStr, Double resultVal,
                                       String unit, String measType) {
        QuantityMeasurementDTO d = new QuantityMeasurementDTO();
        d.setOperation(op);
        d.setResultString(resultStr);
        d.setResultValue(resultVal);
        d.setResultUnit(unit);
        d.setResultMeasurementType(measType);
        d.setError(false);
        return d;
    }

    // ── POST /compare ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /compare – equal lengths returns resultString=true")
    void compareLengths_equal() throws Exception {
        QuantityMeasurementDTO stubResult = dto("compare", "true", null, null, null);
        Mockito.when(service.compare(any(), any())).thenReturn(stubResult);

        mockMvc.perform(post("/api/v1/quantities/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lengthInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.operation").value("compare"))
            .andExpect(jsonPath("$.resultString").value("true"))
            .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    @DisplayName("POST /compare – different categories returns 400")
    void compareDifferentCategories_returns400() throws Exception {
        Mockito.when(service.compare(any(), any()))
            .thenThrow(new com.app.exception.QuantityMeasurementException(
                "compare Error: Cannot perform arithmetic between different measurement categories"));

        QuantityInputDTO mixedInput = new QuantityInputDTO();
        mixedInput.setThisQuantityDTO(new QuantityDTO(1.0, "FEET",     "LengthUnit"));
        mixedInput.setThatQuantityDTO(new QuantityDTO(1.0, "KILOGRAM", "WeightUnit"));

        mockMvc.perform(post("/api/v1/quantities/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mixedInput)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Quantity Measurement Error"));
    }

    // ── POST /convert ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /convert – 1 FEET → 12 INCHES")
    void convertFeetToInches() throws Exception {
        QuantityMeasurementDTO stubResult = dto("convert", null, 12.0, "INCHES", "LengthUnit");
        Mockito.when(service.convert(any(), any())).thenReturn(stubResult);

        mockMvc.perform(post("/api/v1/quantities/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lengthInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(12.0))
            .andExpect(jsonPath("$.resultUnit").value("INCHES"));
    }

    // ── POST /add ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /add – 1 FEET + 12 INCHES = 2 FEET")
    void addLengths() throws Exception {
        QuantityMeasurementDTO stubResult = dto("add", null, 2.0, "FEET", "LengthUnit");
        Mockito.when(service.add(any(), any(), any())).thenReturn(stubResult);

        mockMvc.perform(post("/api/v1/quantities/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lengthInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(2.0))
            .andExpect(jsonPath("$.resultUnit").value("FEET"));
    }

    @Test
    @DisplayName("POST /add – Temperature addition returns 400")
    void addTemperatures_returns400() throws Exception {
        Mockito.when(service.add(any(), any(), any()))
            .thenThrow(new com.app.exception.QuantityMeasurementException(
                "Unsupported operation: CELSIUS does not support addition"));

        QuantityInputDTO tempInput = new QuantityInputDTO();
        tempInput.setThisQuantityDTO(new QuantityDTO(100.0, "CELSIUS", "TemperatureUnit"));
        tempInput.setThatQuantityDTO(new QuantityDTO(50.0,  "CELSIUS", "TemperatureUnit"));

        mockMvc.perform(post("/api/v1/quantities/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tempInput)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("does not support addition")));
    }

    // ── POST /subtract ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /subtract – 10 KILOGRAM - 5000 GRAM = 5 KILOGRAM")
    void subtractWeights() throws Exception {
        QuantityMeasurementDTO stubResult = dto("subtract", null, 5.0, "KILOGRAM", "WeightUnit");
        Mockito.when(service.subtract(any(), any(), any())).thenReturn(stubResult);

        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(new QuantityDTO(10.0,   "KILOGRAM", "WeightUnit"));
        input.setThatQuantityDTO(new QuantityDTO(5000.0, "GRAM",     "WeightUnit"));

        mockMvc.perform(post("/api/v1/quantities/subtract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(5.0))
            .andExpect(jsonPath("$.resultUnit").value("KILOGRAM"));
    }

    // ── POST /divide ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /divide – 10 FEET / 2 FEET = 5.0")
    void divideLengths() throws Exception {
        QuantityMeasurementDTO stubResult = dto("divide", null, 5.0, "RATIO", null);
        Mockito.when(service.divide(any(), any())).thenReturn(stubResult);

        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(new QuantityDTO(10.0, "FEET", "LengthUnit"));
        input.setThatQuantityDTO(new QuantityDTO(2.0,  "FEET", "LengthUnit"));

        mockMvc.perform(post("/api/v1/quantities/divide")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(5.0));
    }

    @Test
    @DisplayName("POST /divide – divide by zero returns 400")
    void divideByZero_returns400() throws Exception {
        Mockito.when(service.divide(any(), any()))
            .thenThrow(new com.app.exception.QuantityMeasurementException(
                "Division by zero: divisor quantity has a base value of zero"));

        QuantityInputDTO input = new QuantityInputDTO();
        input.setThisQuantityDTO(new QuantityDTO(10.0, "FEET", "LengthUnit"));
        input.setThatQuantityDTO(new QuantityDTO(0.0,  "FEET", "LengthUnit"));

        mockMvc.perform(post("/api/v1/quantities/divide")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("zero")));
    }

    // ── GET /history/operation ────────────────────────────────────────────

//    @Test
//    @DisplayName("GET /history/operation/COMPARE – returns list")
//    void getOperationHistory_compare() throws Exception {
//        QuantityMeasurementDTO record = dto("compare", "true", null, null, null);
//        Mockito.when(service.getHistoryByOperation("COMPARE")).thenReturn(List.of(record));
//
//        mockMvc.perform(get("/api/v1/quantities/history/operation/COMPARE"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$", hasSize(1)))
//            .andExpect(jsonPath("$[0].operation").value("compare"));
//    }

    // ── GET /history/type ─────────────────────────────────────────────────

//    @Test
//    @DisplayName("GET /history/type/LengthUnit – returns list")
//    void getHistoryByType_length() throws Exception {
//        QuantityMeasurementDTO record = dto("add", null, 2.0, "FEET", "LengthUnit");
//        Mockito.when(service.getHistoryByMeasurementType("LengthUnit"))
//               .thenReturn(List.of(record));
//
//        mockMvc.perform(get("/api/v1/quantities/history/type/LengthUnit"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$", hasSize(1)))
//            .andExpect(jsonPath("$[0].resultUnit").value("FEET"));
//    }

    // ── GET /count ────────────────────────────────────────────────────────

//    @Test
//    @DisplayName("GET /count/COMPARE – returns long value")
//    void getOperationCount() throws Exception {
//        Mockito.when(service.getOperationCount("COMPARE")).thenReturn(3L);
//
//        mockMvc.perform(get("/api/v1/quantities/count/COMPARE"))
//            .andExpect(status().isOk())
//            .andExpect(content().string("3"));
//    }

    // ── GET /history/errored ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /history/errored – returns error records")
    void getErrorHistory() throws Exception {
        QuantityMeasurementDTO errorRecord = new QuantityMeasurementDTO();
        errorRecord.setOperation("add");
        errorRecord.setError(true);
        errorRecord.setErrorMessage("Cannot operate on different measurement categories");
        Mockito.when(service.getErrorHistory()).thenReturn(List.of(errorRecord));

        mockMvc.perform(get("/api/v1/quantities/history/errored"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].error").value(true))
            .andExpect(jsonPath("$[0].errorMessage", containsString("categories")));
    }

    // ── Validation ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /compare – invalid unit name returns 400 with validation message")
    void compareInvalidUnit_returns400() throws Exception {
        QuantityInputDTO bad = new QuantityInputDTO();
        bad.setThisQuantityDTO(new QuantityDTO(1.0, "FOOT",  "LengthUnit")); // invalid
        bad.setThatQuantityDTO(new QuantityDTO(1.0, "INCHES","LengthUnit"));

        mockMvc.perform(post("/api/v1/quantities/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bad)))
            .andExpect(status().isBadRequest());
    }
}