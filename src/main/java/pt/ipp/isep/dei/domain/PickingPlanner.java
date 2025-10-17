package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
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
        List<Trolley> trolleys = new ArrayList<>();
        int nextId = 1;

        for (Allocation alloc : allocations) {
            boolean placed = false;
            for (Trolley t : trolleys) {
                if (t.canFit(alloc.getWeight())) {
                    t.addAllocation(alloc);
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                Trolley newT = new Trolley(nextId++, trolleyCapacity);
                newT.addAllocation(alloc);
                trolleys.add(newT);
            }
        }
        return trolleys;
    }

    public List<Trolley> planFirstFitDecreasing() {
        List<Allocation> sorted = new ArrayList<>(allocations);
        sorted.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));
        this.allocations = sorted;
        return planFirstFit();
    }

    public List<Trolley> planBestFitDecreasing() {
        List<Allocation> sorted = new ArrayList<>(allocations);
        sorted.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));

        List<Trolley> trolleys = new ArrayList<>();
        int nextId = 1;

        for (Allocation alloc : sorted) {
            Trolley bestFit = null;
            double minRemaining = Double.MAX_VALUE;
            for (Trolley t : trolleys) {
                double remaining = t.getCapacity() - t.getUsedWeight();
                if (t.canFit(alloc.getWeight()) && remaining - alloc.getWeight() < minRemaining) {
                    bestFit = t;
                    minRemaining = remaining - alloc.getWeight();
                }
            }
            if (bestFit == null) {
                bestFit = new Trolley(nextId++, trolleyCapacity);
                trolleys.add(bestFit);
            }
            bestFit.addAllocation(alloc);
        }
        return trolleys;
    }
}