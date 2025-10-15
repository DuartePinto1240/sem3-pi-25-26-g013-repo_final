package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Order;
import main.java.pt.ipp.isep.dei.domain.OrderLine;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderCreationAndGetters() {
        OrderLine line1 = new OrderLine(1, "SKU001", 10);
        OrderLine line2 = new OrderLine(2, "SKU002", 20);
        Order order = new Order("ORD01", 2, LocalDate.of(2025, 10, 25), List.of(line1, line2));

        assertEquals("ORD01", order.getOrderId());
        assertEquals(2, order.getPriority());
        assertEquals(LocalDate.of(2025, 10, 25), order.getDueDate());
        assertEquals(2, order.getLines().size());
        assertEquals("SKU001", order.getLines().get(0).getSku());
    }

    @Test
    public void testOrderLineCreationAndGetters() {
        OrderLine line = new OrderLine(3, "SKU010", 50);
        assertEquals(3, line.getLineNo());
        assertEquals("SKU010", line.getSku());
        assertEquals(50, line.getQtyRequested());
    }
}

