package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order in the warehouse system.
 * <p>
 * An {@code Order} consists of a unique order ID, a priority level,
 * a due date, and a list of order lines.
 * </p>
 */
public class Order {

    /** Unique identifier for the order. */
    private String orderId;

    /** Priority level of the order (higher number = higher priority). */
    private int priority;

    /** Due date by which the order should be fulfilled. */
    private LocalDate dueDate;

    /** List of lines/items included in the order. */
    private List<OrderLine> lines = new ArrayList<>();

    /**
     * Constructs a new {@code Order} with the specified details.
     *
     * @param orderId  unique identifier of the order
     * @param priority priority level of the order
     * @param dueDate  due date of the order
     * @param lines    list of order lines associated with this order
     */
    public Order(String orderId, int priority, LocalDate dueDate, List<OrderLine> lines) {
        this.orderId = orderId;
        this.priority = priority;
        this.dueDate = dueDate;
        this.lines = lines;
    }

    /** @return the unique identifier of the order */
    public String getOrderId() {
        return orderId;
    }

    /** @return the priority level of the order */
    public int getPriority() {
        return priority;
    }

    /** @return the due date of the order */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /** @return the list of order lines in this order */
    public List<OrderLine> getLines() {
        return lines;
    }
}

