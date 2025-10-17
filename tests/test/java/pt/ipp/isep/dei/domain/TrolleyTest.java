package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Allocation;
import main.java.pt.ipp.isep.dei.domain.Trolley;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrolleyTest {

    private Trolley trolley;

    @BeforeEach
    void setUp() {
        trolley = new Trolley(1, 100.0);
    }

    @Test
    void testCanFit_TrueWhenUnderCapacity() {
        assertTrue(trolley.canFit(50.0));
    }

    @Test
    void testCanFit_TrueWhenExactlyFull() {
        assertTrue(trolley.canFit(100.0));
    }

    @Test
    void testCanFit_FalseWhenOverCapacity() {
        assertFalse(trolley.canFit(150.0));
    }

    @Test
    void testGetUtilization_WhenEmpty() {
        assertEquals(0.0, trolley.getUtilization(), 0.0001);
    }

}
