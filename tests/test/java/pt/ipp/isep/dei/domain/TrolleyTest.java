package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Allocation;
import main.java.pt.ipp.isep.dei.domain.Trolley;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrolleyTest {

    private Trolley trolley;

    @BeforeEach
    void setUp() {
        trolley = new Trolley(1, 100.0); // capacidade 100 kg
    }

    private Allocation createAllocation(String sku, double weight) {
        return new Allocation(
                "ORDER001",   // orderId
                1,            // lineNo
                sku,          // sku
                10,           // qty (agora int!)
                weight,       // weight
                "BOX01",      // boxId
                1,            // aisle
                1             // bay
        );
    }


    @Test
    void testInitialState() {
        assertEquals(1, trolley.getId());
        assertEquals(100.0, trolley.getCapacity());
        assertEquals(0.0, trolley.getUsedWeight());
        assertTrue(trolley.getAllocations().isEmpty());
    }

    @Test
    void testCanFitWhenEnoughCapacity() {
        assertTrue(trolley.canFit(50.0), "Deve caber se peso for menor que a capacidade.");
    }

    @Test
    void testCanFitWhenAtLimit() {
        trolley.addAllocation(createAllocation("SKU001", 60.0));
        assertTrue(trolley.canFit(40.0), "Deve caber exatamente até o limite.");
    }

    @Test
    void testCanFitWhenExceedsCapacity() {
        trolley.addAllocation(createAllocation("SKU001", 90.0));
        assertFalse(trolley.canFit(20.0), "Não deve caber se exceder a capacidade.");
    }

    @Test
    void testAddAllocationIncreasesUsedWeight() {
        Allocation a1 = createAllocation("SKU001", 30.0);
        Allocation a2 = createAllocation("SKU002", 20.0);

        trolley.addAllocation(a1);
        trolley.addAllocation(a2);

        assertEquals(50.0, trolley.getUsedWeight(), 0.0001);
        assertEquals(2, trolley.getAllocations().size());
        assertTrue(trolley.getAllocations().contains(a1));
        assertTrue(trolley.getAllocations().contains(a2));
    }

    @Test
    void testGetUtilization() {
        trolley.addAllocation(createAllocation("SKU001", 25.0)); // 25/100 = 25%
        trolley.addAllocation(createAllocation("SKU002", 25.0)); // 50/100 = 50%

        assertEquals(50.0, trolley.getUtilization(), 0.0001, "A utilização deve ser 50%.");
    }

    @Test
    void testGetUtilizationAtFullCapacity() {
        trolley.addAllocation(createAllocation("SKU001", 100.0));
        assertEquals(100.0, trolley.getUtilization(), 0.0001, "Deve ser 100% quando cheio.");
    }

    @Test
    void testGetUtilizationWhenEmpty() {
        assertEquals(0.0, trolley.getUtilization(), 0.0001, "Deve ser 0% quando vazio.");
    }
}



