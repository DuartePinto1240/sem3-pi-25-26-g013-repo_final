package main.java.pt.ipp.isep.dei.domain;

public class Locomotive {

    private final String id;
    private final String model;
    private final double maxSpeedKmH;
    private final double weightTons;
    private final boolean electric;

    public Locomotive(String id, String model, double maxSpeedKmH, double weightTons, boolean electric) {
        // Validação de Integridade
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

    @Override
    public String toString() {
        return String.format("%s (%s, %.0f km/h, %.0f tons, electric=%s)",
                model, id, maxSpeedKmH, weightTons, electric);
    }
}