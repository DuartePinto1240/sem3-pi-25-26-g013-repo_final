package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a wagon model (type of wagon) with its technical characteristics.
 * <p>
 * Instances of this class are typically loaded from the "Wagon Model.csv" file.
 * Each {@link Wagon} references one {@link WagonModel}.
 * </p>
 */
public class WagonModel {

    private final String id;
    private final String modelCode;
    private final String maker;
    private final int numberBogies;
    private final String bogiesType;
    private final int lengthMm;
    private final int widthMm;
    private final double heightMm;
    private final double tareWeightTons;
    private final double maxSpeedKmH;
    private final double payloadTons;
    private final double volumeM3;
    private final String type;
    private final int gaugeMm;

    /**
     * Creates a new wagon model with all relevant attributes loaded from CSV.
     *
     * @param id               internal numeric identifier of the model (column "ID")
     * @param modelCode        model code as printed on the wagon (column "Model")
     * @param maker            manufacturer name (column "Maker")
     * @param numberBogies     number of bogies (column "Number Bogies")
     * @param bogiesType       bogies type (column "Bogies")
     * @param lengthMm         wagon length in millimetres (column "Length")
     * @param widthMm          wagon width in millimetres (column "Width")
     * @param heightMm         wagon height in millimetres (column "Height")
     * @param tareWeightTons   empty weight of the wagon in tons (column "Weight (t)")
     * @param maxSpeedKmH      maximum allowed speed in km/h (column "Max Speed")
     * @param payloadTons      maximum payload in tons (column "Payload (t)")
     * @param volumeM3         internal volume in cubic meters (column "Volume (m3)")
     * @param type             wagon type description (column "Type")
     * @param gaugeMm          track gauge in millimetres (column "Bitola")
     */
    public WagonModel(String id,
                      String modelCode,
                      String maker,
                      int numberBogies,
                      String bogiesType,
                      int lengthMm,
                      int widthMm,
                      double heightMm,
                      double tareWeightTons,
                      double maxSpeedKmH,
                      double payloadTons,
                      double volumeM3,
                      String type,
                      int gaugeMm) {

        this.id = id;
        this.modelCode = modelCode;
        this.maker = maker;
        this.numberBogies = numberBogies;
        this.bogiesType = bogiesType;
        this.lengthMm = lengthMm;
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.tareWeightTons = tareWeightTons;
        this.maxSpeedKmH = maxSpeedKmH;
        this.payloadTons = payloadTons;
        this.volumeM3 = volumeM3;
        this.type = type;
        this.gaugeMm = gaugeMm;
    }

    public String getId() {
        return id;
    }

    /**
     * Returns the model code as printed on the wagon (e.g. "Regmms 32 94 356 3").
     */
    public String getModelCode() {
        return modelCode;
    }

    public String getMaker() {
        return maker;
    }

    public int getNumberBogies() {
        return numberBogies;
    }

    public String getBogiesType() {
        return bogiesType;
    }

    public int getLengthMm() {
        return lengthMm;
    }

    public int getWidthMm() {
        return widthMm;
    }

    public double getHeightMm() {
        return heightMm;
    }

    /**
     * Returns the tare (empty) weight of the wagon in tons.
     */
    public double getTareWeightTons() {
        return tareWeightTons;
    }

    /**
     * Returns the maximum allowed speed of the wagon in km/h.
     */
    public double getMaxSpeedKmH() {
        return maxSpeedKmH;
    }

    /**
     * Returns the maximum payload (cargo) in tons.
     */
    public double getPayloadTons() {
        return payloadTons;
    }

    public double getVolumeM3() {
        return volumeM3;
    }

    public String getType() {
        return type;
    }

    /**
     * Returns the track gauge in millimetres (e.g. 1668).
     */
    public int getGaugeMm() {
        return gaugeMm;
    }

    /**
     * Returns the total weight of the wagon when fully loaded (tare + payload).
     *
     * @return total weight in tons
     */
    public double getFullyLoadedWeightTons() {
        return tareWeightTons + payloadTons;
    }

    @Override
    public String toString() {
        return String.format("%s (maker=%s, tare=%.1f t, payload=%.1f t, gauge=%d mm)",
                modelCode, maker, tareWeightTons, payloadTons, gaugeMm);
    }
}
