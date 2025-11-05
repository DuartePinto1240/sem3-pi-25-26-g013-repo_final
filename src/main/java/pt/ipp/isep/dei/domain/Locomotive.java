package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a locomotive in the railway system.
 * <p>
 * A {@code Locomotive} has a unique identifier, model, maximum speed,
 * weight in tons, and a flag indicating whether it is electric.
 * </p>
 */
public class Locomotive {

    /** Unique identifier for the locomotive. */
    private final String id;

    /** Model name of the locomotive. */
    private final String model;

    /** Maximum speed in kilometers per hour. */
    private final double maxSpeedKmH;

    /** Weight of the locomotive in tons. */
    private final double weightTons;

    /** Whether the locomotive is electric. */
    private final boolean electric;

    /**
     * Constructs a new {@code Locomotive} with the specified details.
     *
     * @param id           the unique identifier of the locomotive (must not be null or empty)
     * @param model        the model name of the locomotive
     * @param maxSpeedKmH  maximum speed in km/h (must be positive)
     * @param weightTons   weight in tons (must be positive)
     * @param electric     true if the locomotive is electric, false otherwise
     * @throws IllegalArgumentException if {@code id} is null/empty, or if {@code maxSpeedKmH} or {@code weightTons} are non-positive
     */
    public Locomotive(String id, String model, double maxSpeedKmH, double weightTons, boolean electric) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Locomotive ID must not be empty.");
        }
        if (maxSpeedKmH <= 0) {
            throw new IllegalArgumentException("Max Speed must be a positive value.");
        }
        if (weightTons <= 0) {
            throw new IllegalArgumentException("Weight must be a positive value.");
        }

        this.id = id;
        this.model = model;
        this.maxSpeedKmH = maxSpeedKmH;
        this.weightTons = weightTons;
        this.electric = electric;
    }

    /** @return the unique identifier of the locomotive */
    public String getId() {
        return id;
    }

    /** @return the model name of the locomotive */
    public String getModel() {
        return model;
    }

    /** @return the maximum speed of the locomotive in km/h */
    public double getMaxSpeedKmH() {
        return maxSpeedKmH;
    }

    /** @return the weight of the locomotive in tons */
    public double getWeightTons() {
        return weightTons;
    }

    /** @return true if the locomotive is electric, false otherwise */
    public boolean isElectric() {
        return electric;
    }

    /**
     * Returns a string representation of the locomotive, including model, ID,
     * maximum speed, weight, and electric status.
     *
     * @return string representation of the locomotive
     */
    @Override
    public String toString() {
        return String.format("%s (%s, %.0f km/h, %.0f tons, electric=%s)",
                model, id, maxSpeedKmH, weightTons, electric);
    }
}
