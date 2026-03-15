package com.app.quantitymeasurement;

import com.app.controller.QuantityMeasurementController;
import com.app.dto.QuantityDTO;
import com.app.dto.QuantityDTO.LengthUnit;
import com.app.dto.QuantityDTO.WeightUnit;
import com.app.dto.QuantityDTO.VolumeUnit;
import com.app.dto.QuantityDTO.TemperatureUnit;
import com.app.entity.QuantityMeasurementEntity;
import com.app.exception.QuantityMeasurementException;
import com.app.repository.IQuantityMeasurementRepository;
import com.app.repository.QuantityMeasurementH2Repository;
import com.app.service.IQuantityMeasurementService;
import com.app.service.QuantityMeasurementServiceImpl;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UC15 test suite — 40 test cases.
 * Uses H2 in-memory database via QuantityMeasurementH2Repository.
 * Updated to work with the simplified QuantityMeasurementException
 * (no ErrorType enum, no MSG_* constants, no factory methods).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementAppTest {

    private IQuantityMeasurementRepository repository;
    private IQuantityMeasurementService    service;
    private QuantityMeasurementController  controller;

    private static QuantityDTO q(double value, com.app.dto.IMeasureableUnit unit) {
        return new QuantityDTO(value, unit);
    }

    @BeforeEach
    void setUp() {
        QuantityMeasurementH2Repository.resetInstance();
        repository = QuantityMeasurementH2Repository.getInstance();
        service    = new QuantityMeasurementServiceImpl(repository);
        controller = new QuantityMeasurementController(service);
    }

    // ── 1. Entity Layer ───────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("TC01 - Entity single-operand construction")
    void testEntity_SingleOperandConstruction() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity(
            "CONVERT", 1.0, "FEET", 12.0, "INCHES");
        assertAll(
            () -> assertEquals("CONVERT", e.getOperationType()),
            () -> assertEquals("1.0",     e.getOperand1Value()),
            () -> assertEquals("FEET",    e.getOperand1Unit()),
            () -> assertNull(e.getOperand2Value()),
            () -> assertEquals("12.0",    e.getResultValue()),
            () -> assertEquals("INCHES",  e.getResultUnit()),
            () -> assertFalse(e.hasError())
        );
    }

    @Test @Order(2)
    @DisplayName("TC02 - Entity binary-operand construction")
    void testEntity_BinaryOperandConstruction() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity(
            "ADD", 1.0, "FEET", 12.0, "INCHES", 2.0, "FEET");
        assertAll(
            () -> assertEquals("ADD",  e.getOperationType()),
            () -> assertEquals("1.0",  e.getOperand1Value()),
            () -> assertEquals("12.0", e.getOperand2Value()),
            () -> assertEquals("2.0",  e.getResultValue()),
            () -> assertFalse(e.hasError())
        );
    }

    @Test @Order(3)
    @DisplayName("TC03 - Entity error construction")
    void testEntity_ErrorConstruction() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity(
            "ADD", "100.0", "CELSIUS", "50.0", "CELSIUS", "does not support addition");
        assertAll(
            () -> assertTrue(e.hasError()),
            () -> assertNotNull(e.getErrorMessage()),
            () -> assertNull(e.getResultValue())
        );
    }

    @Test @Order(4)
    @DisplayName("TC04 - Entity toString success format")
    void testEntity_ToString_Success() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity(
            "CONVERT", 1.0, "FEET", 12.0, "INCHES");
        String s = e.toString();
        assertTrue(s.contains("CONVERT") && s.contains("FEET") && s.contains("INCHES"));
    }

    @Test @Order(5)
    @DisplayName("TC05 - Entity toString error format")
    void testEntity_ToString_Error() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity(
            "DIVIDE", "10.0", "FEET", "0.0", "FEET", "Division by zero");
        String s = e.toString();
        assertTrue(s.contains("ERROR") && s.contains("Division by zero"));
    }

    // ── 2. Exception — simplified class ──────────────────────────────────────

    @Test @Order(6)
    @DisplayName("TC06 - Exception is unchecked (extends RuntimeException)")
    void testException_IsUnchecked() {
        assertTrue(
            new QuantityMeasurementException("test") instanceof RuntimeException);
    }

    @Test @Order(7)
    @DisplayName("TC07 - Exception message constructor stores message")
    void testException_MessageConstructor() {
        String msg = "null value not allowed";
        QuantityMeasurementException ex = new QuantityMeasurementException(msg);
        assertEquals(msg, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test @Order(8)
    @DisplayName("TC08 - Exception message+cause constructor stores both")
    void testException_MessageAndCauseConstructor() {
        Throwable cause = new IllegalArgumentException("root");
        QuantityMeasurementException ex =
            new QuantityMeasurementException("wrapper", cause);
        assertEquals("wrapper", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test @Order(9)
    @DisplayName("TC09 - Exception thrown by service contains meaningful message")
    void testException_ServiceThrowsWithMessage() {
        QuantityMeasurementException ex = assertThrows(
            QuantityMeasurementException.class,
            () -> service.compare(null, q(1.0, LengthUnit.FEET)));
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isBlank());
    }

    @Test @Order(10)
    @DisplayName("TC10 - Exception thrown by controller constructor contains message")
    void testException_ControllerNullServiceMessage() {
        QuantityMeasurementException ex = assertThrows(
            QuantityMeasurementException.class,
            () -> new QuantityMeasurementController(null));
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().toLowerCase().contains("service"));
    }

    // ── 3. H2 Repository Layer ────────────────────────────────────────────────

    @Test @Order(11)
    @DisplayName("TC11 - H2 repository save and count")
    void testH2Repository_SaveAndCount() {
        repository.save(new QuantityMeasurementEntity(
            "CONVERT", 1.0, "FEET", 12.0, "INCHES"));
        assertEquals(1, repository.count());
    }

    @Test @Order(12)
    @DisplayName("TC12 - H2 repository findAll returns all saved entities")
    void testH2Repository_FindAll() {
        repository.save(new QuantityMeasurementEntity("ADD", 1.0, "FEET", 2.0, "FEET", 3.0, "FEET"));
        repository.save(new QuantityMeasurementEntity("COMPARE", 1.0, "KG", 1.0, "KG", 1.0, "BOOLEAN"));
        assertEquals(2, repository.findAll().size());
    }

    @Test @Order(13)
    @DisplayName("TC13 - H2 repository findById returns correct entity")
    void testH2Repository_FindById() {
        repository.save(new QuantityMeasurementEntity("CONVERT", 1.0, "FEET", 12.0, "INCHES"));
        QuantityMeasurementEntity found = repository.findById(0);
        assertEquals("CONVERT", found.getOperationType());
    }

    @Test @Order(14)
    @DisplayName("TC14 - H2 repository findById out-of-range throws exception with message")
    void testH2Repository_FindById_OutOfRange() {
        QuantityMeasurementException ex = assertThrows(
            QuantityMeasurementException.class, () -> repository.findById(99));
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test @Order(15)
    @DisplayName("TC15 - H2 repository clear wipes all rows")
    void testH2Repository_Clear() {
        repository.save(new QuantityMeasurementEntity("CONVERT", 1.0, "FEET", 12.0, "INCHES"));
        repository.clear();
        assertEquals(0, repository.count());
    }

    @Test @Order(16)
    @DisplayName("TC16 - H2 repository persists error entities")
    void testH2Repository_SaveErrorEntity() {
        repository.save(new QuantityMeasurementEntity(
            "ADD", "100.0", "CELSIUS", "50.0", "CELSIUS", "Unsupported operation"));
        QuantityMeasurementEntity saved = repository.findById(0);
        assertTrue(saved.hasError());
        assertNotNull(saved.getErrorMessage());
    }

    // ── 4. Service Layer ──────────────────────────────────────────────────────

    @Test @Order(17)
    @DisplayName("TC17 - Service compare same unit")
    void testService_CompareEquality_SameUnit() {
        assertTrue(service.compare(q(5.0, LengthUnit.FEET), q(5.0, LengthUnit.FEET)));
    }

    @Test @Order(18)
    @DisplayName("TC18 - Service compare different unit")
    void testService_CompareEquality_DifferentUnit() {
        assertTrue(service.compare(q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES)));
    }

    @Test @Order(19)
    @DisplayName("TC19 - Service compare cross-category throws with descriptive message")
    void testService_CompareEquality_CrossCategory_Error() {
        QuantityMeasurementException ex = assertThrows(
            QuantityMeasurementException.class,
            () -> service.compare(q(1.0, LengthUnit.FEET), q(1.0, WeightUnit.KILOGRAMS)));
        // Message must name both categories
        assertTrue(ex.getMessage().contains("LengthUnit"));
        assertTrue(ex.getMessage().contains("WeightUnit"));
    }

    @Test @Order(20)
    @DisplayName("TC20 - Service convert success and persists to H2")
    void testService_Convert_Success() {
        QuantityDTO result = service.convert(q(1.0, LengthUnit.FEET), q(0.0, LengthUnit.INCHES));
        assertEquals(12.0, result.getValue(), 0.01);
        assertEquals(1, repository.count());
    }

    @Test @Order(21)
    @DisplayName("TC21 - Service add success and persists to H2")
    void testService_Add_Success() {
        QuantityDTO result = service.add(
            q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        assertEquals(2.0, result.getValue(), 0.01);
        assertEquals(1, repository.count());
    }

    @Test @Order(22)
    @DisplayName("TC22 - Service add temperature throws unsupported operation")
    void testService_Add_UnsupportedOperation_Error() {
        QuantityMeasurementException ex = assertThrows(
            QuantityMeasurementException.class,
            () -> service.add(
                q(100.0, TemperatureUnit.CELSIUS), q(50.0, TemperatureUnit.CELSIUS),
                q(0.0, TemperatureUnit.CELSIUS)));
        assertTrue(ex.getMessage().toLowerCase().contains("unsupported") ||
                   ex.getMessage().toLowerCase().contains("addition"));
        // Error entity must also be saved to H2
        assertEquals(1, repository.count());
        assertTrue(repository.findById(0).hasError());
    }

    @Test @Order(23)
    @DisplayName("TC23 - Service subtract success")
    void testService_Subtract_Success() {
        QuantityDTO result = service.subtract(
            q(2.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES), q(0.0, LengthUnit.FEET));
        assertEquals(1.0, result.getValue(), 0.01);
    }

    @Test @Order(24)
    @DisplayName("TC24 - Service divide success returns QuantityDTO")
    void testService_Divide_Success() {
        double result = service.divide(q(10.0, LengthUnit.FEET), q(2.0, LengthUnit.FEET));
        assertEquals(5.0, result, 0.01);
    }

    @Test @Order(25)
    @DisplayName("TC25 - Service divide by zero throws with descriptive message")
    void testService_Divide_ByZero_Error() {
        QuantityMeasurementException ex = assertThrows(
            QuantityMeasurementException.class,
            () -> service.divide(q(10.0, LengthUnit.FEET), q(0.0, LengthUnit.FEET)));
        assertTrue(ex.getMessage().toLowerCase().contains("zero") ||
                   ex.getMessage().toLowerCase().contains("division"));
    }

    // ── 5. Controller Layer ───────────────────────────────────────────────────

    @Test @Order(26)
    @DisplayName("TC26 - Controller comparison success")
    void testController_DemonstrateEquality_Success() {
        assertTrue(controller.performComparison(
            q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES)));
    }

    @Test @Order(27)
    @DisplayName("TC27 - Controller conversion success")
    void testController_DemonstrateConversion_Success() {
        QuantityDTO result = controller.performConversion(
            q(1.0, LengthUnit.FEET), q(0.0, LengthUnit.INCHES));
        assertEquals(12.0, result.getValue(), 0.01);
    }

    @Test @Order(28)
    @DisplayName("TC28 - Controller addition success")
    void testController_DemonstrateAddition_Success() {
        QuantityDTO result = controller.performAddition(
            q(1.0, WeightUnit.KILOGRAMS), q(1000.0, WeightUnit.GRAMS), q(0.0, WeightUnit.KILOGRAMS));
        assertEquals(2.0, result.getValue(), 0.01);
    }

    @Test @Order(29)
    @DisplayName("TC29 - Controller addition error returns NaN DTO — does not throw")
    void testController_DemonstrateAddition_Error() {
        QuantityDTO result = controller.performAddition(
            q(100.0, TemperatureUnit.CELSIUS), q(50.0, TemperatureUnit.CELSIUS),
            q(0.0, TemperatureUnit.CELSIUS));
        assertTrue(Double.isNaN(result.getValue()));
    }

    @Test @Order(30)
    @DisplayName("TC30 - Controller requires non-null service")
    void testController_RequiresNonNullService() {
        QuantityMeasurementException ex = assertThrows(
            QuantityMeasurementException.class,
            () -> new QuantityMeasurementController(null));
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isBlank());
    }

    @Test @Order(31)
    @DisplayName("TC31 - Controller null input returns false gracefully")
    void testController_NullInput_GracefulDegradation() {
        boolean result = controller.performComparison(null, q(1.0, LengthUnit.FEET));
        assertFalse(result);
    }

    // ── 6. Layer Separation ───────────────────────────────────────────────────

    @Test @Order(32)
    @DisplayName("TC32 - Service can be tested independently without controller")
    void testLayerSeparation_ServiceIndependence() {
        IQuantityMeasurementService standalone =
            new QuantityMeasurementServiceImpl(QuantityMeasurementH2Repository.getInstance());
        assertTrue(standalone.compare(q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES)));
    }

    @Test @Order(33)
    @DisplayName("TC33 - Data flows correctly controller → service → H2")
    void testDataFlow_FullStack() {
        QuantityDTO result = controller.performAddition(
            q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES), q(0.0, LengthUnit.INCHES));
        assertEquals(24.0, result.getValue(), 0.01);
        assertEquals(1, repository.count());
        assertEquals("ADD", repository.findById(0).getOperationType());
    }

    @Test @Order(34)
    @DisplayName("TC34 - Changing service impl does not affect controller")
    void testLayerDecoupling_ServiceChange() {
        IQuantityMeasurementService freshService =
            new QuantityMeasurementServiceImpl(repository);
        QuantityMeasurementController freshController =
            new QuantityMeasurementController(freshService);
        assertTrue(freshController.performComparison(
            q(1.0, LengthUnit.YARDS), q(3.0, LengthUnit.FEET)));
    }

    // ── 7. Backward Compatibility UC1–UC14 ───────────────────────────────────

    @Test @Order(35)
    @DisplayName("TC35 - UC1: 1 foot == 1 foot")
    void testBackwardCompat_UC1() {
        assertTrue(service.compare(q(1.0, LengthUnit.FEET), q(1.0, LengthUnit.FEET)));
    }

    @Test @Order(36)
    @DisplayName("TC36 - UC3: 1 foot == 12 inches")
    void testBackwardCompat_UC3() {
        assertTrue(service.compare(q(1.0, LengthUnit.FEET), q(12.0, LengthUnit.INCHES)));
    }

    @Test @Order(37)
    @DisplayName("TC37 - UC9: 1 kg == 1000 grams")
    void testBackwardCompat_UC9() {
        assertTrue(service.compare(q(1.0, WeightUnit.KILOGRAMS), q(1000.0, WeightUnit.GRAMS)));
    }

    @Test @Order(38)
    @DisplayName("TC38 - UC11: 1 litre == 1000 millilitres")
    void testBackwardCompat_UC11() {
        assertTrue(service.compare(q(1.0, VolumeUnit.LITERS), q(1000.0, VolumeUnit.MILLILITERS)));
    }

    @Test @Order(39)
    @DisplayName("TC39 - UC14: 0°C == 32°F (temperature cross-unit compare)")
    void testBackwardCompat_UC14_TemperatureEquality() {
        assertTrue(service.compare(
            q(0.0, TemperatureUnit.CELSIUS), q(32.0, TemperatureUnit.FAHRENHEIT)));
    }

    @Test @Order(40)
    @DisplayName("TC40 - UC14: 100°C converts to 212°F")
    void testBackwardCompat_UC14_TemperatureConversion() {
        QuantityDTO result = service.convert(
            q(100.0, TemperatureUnit.CELSIUS), q(0.0, TemperatureUnit.FAHRENHEIT));
        assertEquals(212.0, result.getValue(), 0.01);
    }
}
