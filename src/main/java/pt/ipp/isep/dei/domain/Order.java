package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private int priority;
    private LocalDate dueDate;
    private List<OrderLine> lines = new ArrayList<>();

    public Order(String orderId, int priority, LocalDate dueDate, List<OrderLine> lines) {
        this.orderId = orderId;
        this.priority = priority;
        this.dueDate = dueDate;
        this.lines = lines;
    }

    public String getOrderId() { return orderId; }
    public int getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public List<OrderLine> getLines() { return lines; }
}

