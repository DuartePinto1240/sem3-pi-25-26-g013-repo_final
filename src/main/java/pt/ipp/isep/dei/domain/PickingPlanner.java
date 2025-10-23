package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Planner for allocating products (Allocations) into trolleys.
 * <p>
 * Supports three main strategies:
 * <ul>
 * <li>First Fit</li>
 * <li>First Fit Decreasing (FFD)</li>
 * <li>Best Fit Decreasing (BFD)</li>
 * </ul>
 * Each trolley has a maximum capacity, and allocations exceeding this capacity are ignored.
 * </p>
 */
public class PickingPlanner {

    /** List of allocations to be planned. */
    private List<Allocation> allocations;

    /** Maximum capacity of each trolley. */
    private double trolleyCapacity;

    /**
     * Constructor.
     *
     * @param allocations     list of allocations to be distributed into trolleys
     * @param trolleyCapacity maximum capacity of each trolley
     */
    public PickingPlanner(List<Allocation> allocations, double trolleyCapacity) {
        this.allocations = allocations;
        this.trolleyCapacity = trolleyCapacity;
    }

    /**
     * Returns the list of allocations.
     *
     * @return list of allocations
     */
    public List<Allocation> getAllocations() {
        return allocations;
    }

    /**
     * Returns the maximum capacity of each trolley.
     *
     * @return trolley capacity
     */
    public double getTrolleyCapacity() {
        return trolleyCapacity;
    }

    /**
     * Plans the allocations using the First Fit strategy.
     * <p>
     * Each allocation is added to the first trolley that has sufficient capacity.
     * If no trolley can accommodate the allocation, a new trolley is created.
     * </p>
     *
     * @return list of trolleys with distributed allocations
     */
    public List<Trolley> planFirstFit() {
        List<Trolley> trolleys = new ArrayList<>();
        int nextId = 1;

        for (Allocation alloc : allocations) {
            if (alloc.getWeight() > trolleyCapacity) {
                System.out.printf(
                        "⚠️ Allocation %s (%.1f kg) excede capacidade total do trolley (%.1f kg) — ignorada.%n",
                        alloc.getSku(), alloc.getWeight(), trolleyCapacity
                );
                continue;
            }

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

    /**
     * Plans the allocations using the First Fit Decreasing (FFD) strategy.
     * <p>
     * Allocations are sorted by descending weight before applying the First Fit strategy.
     * </p>
     *
     * @return list of trolleys with distributed allocations
     */
    public List<Trolley> planFirstFitDecreasing() {
        List<Allocation> sorted = new ArrayList<>(allocations);
        sorted.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));
        this.allocations = sorted;
        return planFirstFit();
    }

    /**
     * Plans the allocations using the Best Fit Decreasing (BFD) strategy.
     * <p>
     * Each allocation is placed in the trolley that results in the smallest possible remaining free space.
     * If no trolley can accommodate the allocation, a new trolley is created.
     * </p>
     *
     * @return list of trolleys with distributed allocations
     */
    public List<Trolley> planBestFitDecreasing() {
        List<Allocation> sorted = new ArrayList<>(allocations);
        sorted.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));

        List<Trolley> trolleys = new ArrayList<>();
        int nextId = 1;

        for (Allocation alloc : sorted) {
            if (alloc.getWeight() > trolleyCapacity) {
                System.out.printf(
                        "⚠️ Allocation %s (%.1f kg) excede capacidade total do trolley (%.1f kg) — ignorada.%n",
                        alloc.getSku(), alloc.getWeight(), trolleyCapacity
                );
                continue;
            }

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