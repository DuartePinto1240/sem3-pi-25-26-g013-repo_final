package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OrderAllocator}.
 * <p>
 * These tests cover different allocation scenarios including:
 * <ul>
 *     <li>Strict mode: full allocation required, otherwise order is undispatchable.</li>
 *     <li>Partial mode: allows partial allocation if full quantity is not available.</li>
 *     <li>FEFO (First-Expire-First-Out) box selection.</li>
 *     <li>Order processing priority based on due date, priority, and order ID.</li>
 * </ul>
 * </p>
 */
public class OrderAllocatorTest {

    private Warehouse warehouse;
    private Bay bay1, bay2;

    /**
     * Sets up a warehouse with two bays and boxes before each test.
     * Boxes are arranged to test FEFO allocation.
     */
    @BeforeEach
    public void setup() {
        warehouse = new Warehouse("W1");
        bay1 = new Bay("W1", 1, 1, 10);
        bay2 = new Bay("W1", 1, 2, 10);

        // FEFO: Box1 expires earlier than Box2
        Box box1 = new Box("BOX001", "SKU001", 10, LocalDate.of(2025, 6, 1), LocalDateTime.of(2025, 5, 1, 10, 0));
        Box box2 = new Box("BOX002", "SKU001", 20, LocalDate.of(2025, 12, 1), LocalDateTime.of(2025, 5, 2, 10, 0));

        bay1.addBox(box1);
        bay2.addBox(box2);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);
    }

    /**
     * Tests strict allocation mode when enough stock is available.
     */
    @Test
    public void testStrictMode_FullEligible() {
        OrderLine line = new OrderLine(1, "SKU001", 10); // requires 10
        Order order = new Order("ORD01", 1, LocalDate.of(2025, 10, 10), List.of(line));

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(List.of(order), warehouse, false); // strict

        assertEquals(1, result.lineStatuses.size());
        LineStatus status = result.lineStatuses.get(0);
        assertEquals(LineStatusEnum.ELIGIBLE, status.getStatus());
        assertEquals(10, status.getAllocatedQty());
        assertEquals(10, result.allocationRows.get(0).getQty());
        assertEquals("BOX001", result.allocationRows.get(0).getBoxId());
    }

    /**
     * Tests strict allocation mode when there is insufficient stock.
     * The order should be marked as undispatchable.
     */
    @Test
    public void testStrictMode_InsufficientStock_Undispatchable() {
        OrderLine line = new OrderLine(1, "SKU001", 50); // requires more than available
        Order order = new Order("ORD02", 1, LocalDate.of(2025, 10, 10), List.of(line));

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(List.of(order), warehouse, false); // strict

        assertEquals(1, result.lineStatuses.size());
        assertEquals(LineStatusEnum.UNDISPATCHABLE, result.lineStatuses.get(0).getStatus());
        assertTrue(result.allocationRows.isEmpty()); // rollback
    }

    /**
     * Tests partial allocation mode where partial quantities can be allocated
     * and FEFO box selection is respected.
     */
    @Test
    public void testPartialMode_PartialAllocationAllowed() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay1 = new Bay("W1", 1, 1, 100);
        Box box1 = new Box("BOX001", "SKU001", 15, LocalDate.of(2025, 6, 30), LocalDateTime.of(2025, 6, 1, 10, 0));
        Box box2 = new Box("BOX002", "SKU001", 10, LocalDate.of(2025, 12, 31), LocalDateTime.of(2025, 6, 2, 10, 0));
        bay1.addBox(box1);
        bay1.addBox(box2);
        warehouse.addBay(bay1);

        OrderLine line = new OrderLine(1, "SKU001", 25);
        Order order = new Order("ORD03", 1, LocalDate.of(2025, 10, 10), List.of(line));

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(List.of(order), warehouse, true); // partial mode

        LineStatus status = result.lineStatuses.get(0);
        assertEquals(LineStatusEnum.ELIGIBLE, status.getStatus());
        assertEquals(25, status.getRequestedQty());
        assertEquals(25, status.getAllocatedQty());
        assertEquals(2, result.allocationRows.size()); // two boxes used
        assertEquals("BOX001", result.allocationRows.get(0).getBoxId()); // FEFO
    }

    /**
     * Tests that orders are processed in correct priority:
     * ascending due date, then priority, then order ID.
     */
    @Test
    public void testOrderProcessingPriorityDueDateOrderId() {
        Order o1 = new Order("ORD03", 2, LocalDate.of(2025, 10, 10), List.of(new OrderLine(1, "SKU001", 5)));
        Order o2 = new Order("ORD01", 1, LocalDate.of(2025, 10, 11), List.of(new OrderLine(1, "SKU001", 5)));
        Order o3 = new Order("ORD02", 1, LocalDate.of(2025, 10, 9), List.of(new OrderLine(1, "SKU001", 5)));

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(List.of(o1, o2, o3), warehouse, true);

        List<String> processedOrderIds = result.lineStatuses.stream()
                .map(LineStatus::getOrderId)
                .toList();

        assertEquals(List.of("ORD02", "ORD01", "ORD03"), processedOrderIds);
    }
}
