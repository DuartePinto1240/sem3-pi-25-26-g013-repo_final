package test.java.pt.ipp.isep.dei.domain.USEI02;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for Use Case USEI02 — Order Eligibility & Allocation.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class USEI02Test {

    private Warehouse warehouse;
    private OrderAllocator allocator;
    private List<main.java.pt.ipp.isep.dei.domain.Order> orders;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse("W1");
        allocator = new OrderAllocator();
        orders = new ArrayList<>();

        // Setup warehouse with bays
        for (int aisle = 1; aisle <= 2; aisle++) {
            for (int bay = 1; bay <= 5; bay++) {
                warehouse.addBay(new Bay("W1", aisle, bay, 10));
            }
        }
    }

    // ========================================================
    // Concrete subclass of Order for testing
    // ========================================================
    private static class TestOrder extends main.java.pt.ipp.isep.dei.domain.Order {
        public TestOrder(String orderId, int priority, LocalDate dueDate, List<OrderLine> lines) {
            super(orderId, priority, dueDate, lines);
        }
    }

    // ========================================================
    // Helper Methods
    // ========================================================
    private void addBoxToBay(int aisle, int bay, String boxId, String sku, int qty,
                             LocalDate expiryDate, LocalDateTime receivedAt) {
        Bay targetBay = warehouse.getBay("W1", aisle, bay);
        if (targetBay == null) {
            throw new IllegalArgumentException("Bay not found: aisle " + aisle + ", bay " + bay);
        }
        targetBay.addBox(new Box(boxId, sku, qty, expiryDate, receivedAt));
    }

    // ========================================================
    // Order Sequence Tests
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Order Sequence: Orders should be processed by priority ASC")
    void testOrderSequence_PriorityAscending() {
        addBoxToBay(1, 1, "BOX001", "SKU001", 100, null, LocalDateTime.of(2025, 9, 1, 10, 0));

        orders.addAll(List.of(
                new TestOrder("ORD001", 3, LocalDate.of(2025, 10, 1),
                        List.of(new OrderLine(1, "SKU001", 10))),
                new TestOrder("ORD002", 1, LocalDate.of(2025, 10, 1),
                        List.of(new OrderLine(1, "SKU001", 20))),
                new TestOrder("ORD003", 2, LocalDate.of(2025, 10, 1),
                        List.of(new OrderLine(1, "SKU001", 30)))
        ));

        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, false);

        List<String> processedOrderIds = result.lineStatuses.stream()
                .map(LineStatus::getOrderId)
                .distinct()
                .collect(Collectors.toList());

        assertEquals("ORD002", processedOrderIds.get(0));
        assertEquals("ORD003", processedOrderIds.get(1));
        assertEquals("ORD001", processedOrderIds.get(2));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Order Sequence: Same priority orders should be processed by dueDate ASC")
    void testOrderSequence_DueDateAscending() {
        addBoxToBay(1, 1, "BOX001", "SKU001", 100, null, LocalDateTime.of(2025, 9, 1, 10, 0));

        orders.addAll(List.of(
                new TestOrder("ORD001", 1, LocalDate.of(2025, 10, 15),
                        List.of(new OrderLine(1, "SKU001", 10))),
                new TestOrder("ORD002", 1, LocalDate.of(2025, 10, 1),
                        List.of(new OrderLine(1, "SKU001", 20))),
                new TestOrder("ORD003", 1, LocalDate.of(2025, 10, 10),
                        List.of(new OrderLine(1, "SKU001", 30)))
        ));

        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, false);

        List<String> processedOrderIds = result.lineStatuses.stream()
                .map(LineStatus::getOrderId)
                .distinct()
                .collect(Collectors.toList());

        assertEquals("ORD002", processedOrderIds.get(0));
        assertEquals("ORD003", processedOrderIds.get(1));
        assertEquals("ORD001", processedOrderIds.get(2));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Order Sequence: Same priority and dueDate should be processed by orderId ASC")
    void testOrderSequence_OrderIdAscending() {
        addBoxToBay(1, 1, "BOX001", "SKU001", 100, null, LocalDateTime.of(2025, 9, 1, 10, 0));

        LocalDate sameDate = LocalDate.of(2025, 10, 1);
        orders.addAll(List.of(
                new TestOrder("ORD003", 1, sameDate, List.of(new OrderLine(1, "SKU001", 10))),
                new TestOrder("ORD001", 1, sameDate, List.of(new OrderLine(1, "SKU001", 20))),
                new TestOrder("ORD002", 1, sameDate, List.of(new OrderLine(1, "SKU001", 30)))
        ));

        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, false);

        List<String> processedOrderIds = result.lineStatuses.stream()
                .map(LineStatus::getOrderId)
                .distinct()
                .collect(Collectors.toList());

        assertEquals("ORD001", processedOrderIds.get(0));
        assertEquals("ORD002", processedOrderIds.get(1));
        assertEquals("ORD003", processedOrderIds.get(2));
    }

    // ========================================================
    // Strict Mode Test Example
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("Strict Mode: Fully allocated order should be ELIGIBLE")
    void testStrictMode_FullyAllocated_Eligible() {
        addBoxToBay(1, 1, "BOX001", "SKU001", 50, null, LocalDateTime.of(2025, 9, 1, 10, 0));
        addBoxToBay(1, 2, "BOX002", "SKU002", 30, null, LocalDateTime.of(2025, 9, 1, 10, 0));

        orders.add(new TestOrder("ORD001", 1, LocalDate.of(2025, 10, 1), List.of(
                new OrderLine(1, "SKU001", 20),
                new OrderLine(2, "SKU002", 15)
        )));

        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, true);

        assertEquals(2, result.lineStatuses.size());
        assertEquals(LineStatusEnum.ELIGIBLE, result.lineStatuses.get(0).getStatus());
        assertEquals(LineStatusEnum.ELIGIBLE, result.lineStatuses.get(1).getStatus());
        assertEquals(20, result.lineStatuses.get(0).getAllocatedQty());
        assertEquals(15, result.lineStatuses.get(1).getAllocatedQty());
    }

    @AfterEach
    void tearDown() {
        warehouse = null;
        allocator = null;
        orders = null;
    }
}
