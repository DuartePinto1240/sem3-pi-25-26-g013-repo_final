package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Allocation;
import main.java.pt.ipp.isep.dei.domain.Trolley;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Trolley.
 */
public class TrolleyTest {

    private Trolley trolley;

    @BeforeEach
    public void setUp() {
        trolley = new Trolley(1, 100.0); // 100 kg de capacidade
    }

    @Test
    public void testConstructorAndGetters_ShouldReturnCorrectValues() {
        assertEquals(1, trolley.getId());
        assertEquals(100.0, trolley.getCapacity());
        assertEquals(0.0, trolley.getUsedWeight());
        assertTrue(trolley.getAllocations().isEmpty());
    }

    @Test
    public void testCanFit_ShouldReturnTrueIfWithinCapacity() {
        assertTrue(trolley.canFit(50.0));
        assertTrue(trolley.canFit(100.0)); // limite
    }

    @Test
    public void testCanFit_ShouldReturnFalseIfExceedsCapacity() {
        assertFalse(trolley.canFit(150.0));
    }

    @Test
    public void testAddAllocation_ShouldIncreaseUsedWeightAndStoreAllocation() {
        Allocation alloc = new Allocation("ORD001", 1, "SKU001", 5.0, 30.0, "BOX01", 1, 2);

        trolley.addAllocation(alloc);

        assertEquals(1, trolley.getAllocations().size());
        assertEquals(30.0, trolley.getUsedWeight());
        assertSame(alloc, trolley.getAllocations().get(0));
    }

    @Test
    public void testGetUtilization_ShouldReturnPercentage() {
        Allocation alloc1 = new Allocation("ORD001", 1, "SKU001", 5.0, 40.0, "BOX01", 1, 1);
        Allocation alloc2 = new Allocation("ORD002", 2, "SKU002", 3.0, 10.0, "BOX02", 1, 2);

        trolley.addAllocation(alloc1);
        trolley.addAllocation(alloc2);

        double utilization = trolley.getUtilization();
        assertEquals(50.0, utilization); // 50 kg usados de 100 = 50%
    }

    @Test
    public void testToString_ShouldContainAllocationInfo() {
        Allocation alloc = new Allocation("ORD003", 1, "SKU009", 2.0, 25.0, "BOX05", 1, 3);
        trolley.addAllocation(alloc);

        String output = trolley.toString();
        assertTrue(output.contains("id=1"));
        assertTrue(output.contains("capacity=100.0"));
        assertTrue(output.contains("usedWeight=25.0"));
        assertTrue(output.contains("BOX05"));
    }

    @Test
    public void testAddAllocation_ShouldNotExceedCapacity() {
        Allocation alloc1 = new Allocation("ORD001", 1, "SKU001", 5.0, 80.0, "BOX01", 1, 2);
        Allocation alloc2 = new Allocation("ORD002", 1, "SKU002", 5.0, 30.0, "BOX02", 1, 3);

        trolley.addAllocation(alloc1);

        assertTrue(trolley.canFit(20.0));
        assertFalse(trolley.canFit(30.0)); // 80 + 30 > 100
    }
}


