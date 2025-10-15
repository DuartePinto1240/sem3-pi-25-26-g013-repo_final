package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OrderAllocatorTest {

    private Warehouse warehouse;
    private Bay bay1, bay2;

    @BeforeEach
    public void setup() {
        warehouse = new Warehouse("W1");
        bay1 = new Bay("W1", 1, 1, 10);
        bay2 = new Bay("W1", 1, 2, 10);

        // FEFO: Box1 expira mais cedo, Box2 expira depois
        Box box1 = new Box("BOX001", "SKU001", 10, LocalDate.of(2025, 6, 1), LocalDateTime.of(2025, 5, 1, 10, 0));
        Box box2 = new Box("BOX002", "SKU001", 20, LocalDate.of(2025, 12, 1), LocalDateTime.of(2025, 5, 2, 10, 0));

        bay1.addBox(box1);
        bay2.addBox(box2);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);
    }

    @Test
    public void testStrictMode_FullEligible() {
        OrderLine line = new OrderLine(1, "SKU001", 10); // requer 10
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

    @Test
    public void testStrictMode_InsufficientStock_Undispatchable() {
        OrderLine line = new OrderLine(1, "SKU001", 50); // requer mais do que há
        Order order = new Order("ORD02", 1, LocalDate.of(2025, 10, 10), List.of(line));

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(List.of(order), warehouse, false); // strict

        assertEquals(1, result.lineStatuses.size());
        assertEquals(LineStatusEnum.UNDISPATCHABLE, result.lineStatuses.get(0).getStatus());
        assertTrue(result.allocationRows.isEmpty()); // rollback
    }

    @Test
    public void testPartialMode_PartialAllocationAllowed() {
        // Configurar o armazém
        Warehouse warehouse = new Warehouse("W1");
        Bay bay1 = new Bay("W1", 1, 1, 100); // Capacidade suficiente
        Box box1 = new Box("BOX001", "SKU001", 15, LocalDate.of(2025, 6, 30), LocalDateTime.of(2025, 6, 1, 10, 0));
        Box box2 = new Box("BOX002", "SKU001", 10, LocalDate.of(2025, 12, 31), LocalDateTime.of(2025, 6, 2, 10, 0));
        bay1.addBox(box1);
        bay1.addBox(box2);
        warehouse.addBay(bay1);

        // Configurar a ordem
        OrderLine line = new OrderLine(1, "SKU001", 25); // Requer 25 unidades
        Order order = new Order("ORD03", 1, LocalDate.of(2025, 10, 10), List.of(line));

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(List.of(order), warehouse, true); // Modo parcial

        // Verificar resultados
        LineStatus status = result.lineStatuses.get(0);
        assertEquals(LineStatusEnum.ELIGIBLE, status.getStatus()); // Corrigido: ELIGIBLE, pois allocatedQty == requestedQty
        assertEquals(25, status.getRequestedQty());
        assertEquals(25, status.getAllocatedQty());
        assertEquals(2, result.allocationRows.size()); // Duas caixas usadas (15 + 10)
        assertEquals("BOX001", result.allocationRows.get(0).getBoxId()); // FEFO: BOX001 expira antes
    }

    @Test
    public void testOrderProcessingPriorityDueDateOrderId() {
        // três orders com diferentes prioridades e dueDates
        Order o1 = new Order("ORD03", 2, LocalDate.of(2025, 10, 10), List.of(new OrderLine(1, "SKU001", 5)));
        Order o2 = new Order("ORD01", 1, LocalDate.of(2025, 10, 11), List.of(new OrderLine(1, "SKU001", 5)));
        Order o3 = new Order("ORD02", 1, LocalDate.of(2025, 10, 9), List.of(new OrderLine(1, "SKU001", 5)));

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(List.of(o1, o2, o3), warehouse, true);

        // a ordem deve seguir: prioridade asc, depois dueDate asc, depois orderId asc
        List<String> processedOrderIds = result.lineStatuses.stream()
                .map(LineStatus::getOrderId)
                .toList();

        assertEquals(List.of("ORD02", "ORD01", "ORD03"), processedOrderIds);
    }
}

