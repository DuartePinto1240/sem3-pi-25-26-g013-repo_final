package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

public class Allocation {
    private final int id;
    private final double capacity;
    private double usedWeight;
    private List <Allocation> allocations;

    // Construtor

    public Allocation(int id, double capacity) {
        this.id = id;
        this.capacity = capacity;
        this.usedWeight = 0;
        this.allocations = new ArrayList<>();
    }

    // Métodos getters para acessar as propriedades privadas

    public int getId() {
        return id;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getUsedWeight() {
        return usedWeight;
    }

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public boolean canFit (double weight) {
        return usedWeight + weight <= capacity;
    }

    public void addAllocation (Allocation alloc) {
        allocations.add(alloc);
        usedWeight += alloc.getUsedWeight();
    }

    public double getUtilization() {
        return (usedWeight / capacity) * 100;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Allocation{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", usedWeight=" + usedWeight +
                ", allocations=" + allocations +
                '}';
    }
}



