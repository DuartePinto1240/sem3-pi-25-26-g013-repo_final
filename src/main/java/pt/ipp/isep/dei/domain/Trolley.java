package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trolley used to transport allocations ({@link Allocation})
 * during the picking process in the warehouse.
 * <p>
 * Each trolley has a maximum weight capacity and stores a set of allocations
 * whose total weight cannot exceed that capacity.
 * </p>
 */
public class Trolley {

    /** Unique identifier of the trolley. */
    private final int id;

    /** Maximum capacity of the trolley, in kilograms. */
    private final double capacity;

    /** Total weight currently utilized in the trolley. */
    private double usedWeight;

    /** List of allocations contained in this trolley. */
    private final List<Allocation> allocations;

    /**
     * Constructs a new {@code Trolley} with the specified capacity.
     *
     * @param id unique identifier of the trolley
     * @param capacity maximum capacity of the trolley (in kilograms)
     */
    public Trolley(int id, double capacity) {
        this.id = id;
        this.capacity = capacity;
        this.usedWeight = 0;
        this.allocations = new ArrayList<>();
    }

    /**
     * Gets the identifier of the trolley.
     *
     * @return the trolley ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the maximum capacity of the trolley.
     *
     * @return the total capacity, in kilograms
     */
    public double getCapacity() {
        return capacity;
    }

    /**
     * Gets the total weight currently utilized in the trolley.
     *
     * @return the total occupied weight, in kilograms
     */
    public double getUsedWeight() {
        return usedWeight;
    }

    /**
     * Returns the list of allocations currently stored in the trolley.
     *
     * @return list of {@link Allocation} objects
     */
    public List<Allocation> getAllocations() {
        return allocations;
    }

    /**
     * Checks if the trolley has enough capacity to add
     * a new allocation with the specified weight.
     *
     * @param weight weight of the new allocation
     * @return {@code true} if the trolley can support the additional weight, {@code false} otherwise
     */
    public boolean canFit(double weight) {
        return usedWeight + weight <= capacity;
    }

    /**
     * Adds a new allocation to the trolley and updates the total weight utilized.
     *
     * @param alloc the allocation to be added
     */
    public void addAllocation(Allocation alloc) {
        allocations.add(alloc);
        usedWeight += alloc.getWeight();
    }

    /**
     * Calculates the utilization rate of the trolley based on the total capacity.
     *
     * @return the percentage utilization of the trolley (0–100%)
     */
    public double getUtilization() {
        return (usedWeight / capacity) * 100;
    }
}