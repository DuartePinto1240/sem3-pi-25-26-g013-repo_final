package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a locomotive in the railway network.
 *
 * Fields supported by the current CSV:
 *  - id
 *  - model
 *  - maxSpeedKmH
 *  - weightTons
 *  - electric (true/false)
 *  - power (e.g. kW)
 *
 * Extra attributes in the CSV such as tractiveEffort / tractiveEffortStart
 * are currently ignored by the domain, but can be added later if needed.
 */
public class Locomotive {

    private final String id;
    private final String model;
    private final double maxSpeedKmH;
    private final double weightTons;
    private final boolean electric;

    /** Power of the locomotive (e.g. in kW). */
    private final double power;

    /**
     * Full constructor including power.
     */
    public Locomotive(String id,
                      String model,
                      double maxSpeedKmH,
                      double weightTons,
                      boolean electric,
                      double power) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Locomotive id must not be null or empty.");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Locomotive model must not be null or empty.");
        }
        if (maxSpeedKmH <= 0 || weightTons <= 0) {
            throw new IllegalArgumentException("Max speed and weight must be positive.");
        }

        this.id = id;
        this.model = model;
        this.maxSpeedKmH = maxSpeedKmH;
        this.weightTons = weightTons;
        this.electric = electric;
        this.power = power;
    }

    /**
     * Backwards-compatible constructor without power.
     * Power will default to 0.0.
     */
    public Locomotive(String id,
                      String model,
                      double maxSpeedKmH,
                      double weightTons,
                      boolean electric) {
        this(id, model, maxSpeedKmH, weightTons, electric, 0.0);
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public double getMaxSpeedKmH() {
        return maxSpeedKmH;
    }

    public double getWeightTons() {
        return weightTons;
    }

    public boolean isElectric() {
        return electric;
    }

    /**
     * Returns the locomotive power (e.g. in kW). May be 0.0 if not provided by the CSV.
     */
    public double getPower() {
        return power;
    }

    @Override
    public String toString() {
        // Mantemos a mesma apresentação usada na UI para não mudar nada visualmente.
        return String.format("%s (%s, %.0f km/h, %.0f tons, electric=%s)",
                model, id, maxSpeedKmH, weightTons, electric);
    }
}
