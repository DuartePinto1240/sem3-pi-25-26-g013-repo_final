package main.java.pt.ipp.isep.dei.domain;

import java.util.List;

public class PickingPlanner {
    private List<Allocation> allocations;
    private double trolleyCapacity;

    // Construtor


    public PickingPlanner(List<Allocation> allocations, double trolleyCapacity) {
        this.allocations = allocations;
        this.trolleyCapacity = trolleyCapacity;
    }

    // Métodos getters para acessar as propriedades privadas

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public double getTrolleyCapacity() {
        return trolleyCapacity;
    }

    public List<Trolley> planFirstFit() {
        // Implementar a heuristica aqui
        return List.of();
    }

    public List<Trolley> planFirstFitDecreasing() {
        // Implementar a heuristica aqui
        return List.of();
    }

    public List<Trolley> planBestFitDecreasing() {
        // Implementar a heuristica aqui
        return List.of();
    }

    @Override
    public String toString() {
        return "PickingPlanner{" +
                "allocations=" + allocations +
                ", trolleyCapacity=" + trolleyCapacity +
                '}';
    }
}