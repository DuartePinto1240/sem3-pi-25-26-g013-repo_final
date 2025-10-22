package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.LineStatus;
import main.java.pt.ipp.isep.dei.domain.LineStatusEnum;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LineStatus} and {@link LineStatusEnum}.
 * <p>
 * These tests cover constructor correctness, getter methods, and enum values.
 * </p>
 */
public class LineStatusTest {

    /**
     * Verifies that {@link LineStatus} constructor initializes fields correctly
     * and that getters return expected values.
     */
    @Test
    public void testLineStatusValues() {
        LineStatus status = new LineStatus("ORD01", 1, "SKU001", 100, 100, LineStatusEnum.ELIGIBLE);

        assertEquals("ORD01", status.getOrderId());
        assertEquals(1, status.getLineNo());
        assertEquals("SKU001", status.getSku());
        assertEquals(100, status.getRequestedQty());
        assertEquals(100, status.getAllocatedQty());
        assertEquals(LineStatusEnum.ELIGIBLE, status.getStatus());
    }

    /**
     * Verifies the string representations of {@link LineStatusEnum} constants.
     */
    @Test
    public void testDifferentStatusEnums() {
        assertEquals("ELIGIBLE", LineStatusEnum.ELIGIBLE.name());
        assertEquals("PARTIAL", LineStatusEnum.PARTIAL.name());
        assertEquals("UNDISPATCHABLE", LineStatusEnum.UNDISPATCHABLE.name());
    }
}
