package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents an item or product type identified by a unique SKU (Stock Keeping Unit).
 * <p>
 * Each {@code Item} defines static metadata such as name, category, unit of measure,
 * volume, and weight per unit. Actual stock quantities are represented separately
 * through {@link Box} objects that reference the same SKU.
 * </p>
 */
public class Item {

    /** Unique product identifier (Stock Keeping Unit). */
    private String sku;

    /** Human-readable name of the item. */
    private String name;

    /** Category or product family this item belongs to. */
    private String category;

    /** Unit of measure used for this item (e.g., "kg", "box", "litre"). */
    private String unit;

    /** Volume occupied by one unit of this item, in cubic meters. */
    private double volume;

    /** Weight of one unit of this item, in kilograms. */
    private double unitWeight;

    /**
     * Constructs a new {@link Item} with the given parameters.
     *
     * @param sku        the unique SKU identifier
     * @param name       the item name
     * @param category   the product category
     * @param unit       the unit of measure
     * @param volume     the volume of one unit (m³)
     * @param unitWeight the weight of one unit (kg)
     */
    public Item(String sku, String name, String category, String unit, double volume, double unitWeight) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.volume = volume;
        this.unitWeight = unitWeight;
    }

    /** @return the SKU code of the item */
    public String getSku() {
        return sku;
    }

    /** @return the human-readable name of the item */
    public String getName() {
        return name;
    }

    /** @return the product category */
    public String getCategory() {
        return category;
    }

    /** @return the unit of measure (e.g., "kg", "unit") */
    public String getUnit() {
        return unit;
    }

    /** @return the volume occupied by one unit (in cubic meters) */
    public double getVolume() {
        return volume;
    }

    /** @return the weight of one unit (in kilograms) */
    public double getUnitWeight() {
        return unitWeight;
    }

    /**
     * Returns a string representation of this item with all its attributes.
     *
     * @return a formatted string describing the item
     */
    @Override
    public String toString() {
        return "main.java.pt.ipp.isep.dei.domain.Item{" +
                "sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", unit='" + unit + '\'' +
                ", volume=" + volume +
                ", unitWeight=" + unitWeight +
                '}';
    }
}



