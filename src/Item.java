public class Item {
    private String sku;
    private String name;
    private String category;
    private String unit;
    private double volume;
    private double unitWeight;

    // Construtor
    public Item(String sku, String name, String category, String unit, double volume, double unitWeight) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.volume = volume;
        this.unitWeight = unitWeight;
    }

    // Getters
    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public double getVolume() {
        return volume;
    }

    public double getUnitWeight() {
        return unitWeight;
    }

    @Override
    public String toString() {
        return "Item{" +
                "sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", unit='" + unit + '\'' +
                ", volume=" + volume +
                ", unitWeight=" + unitWeight +
                '}';
    }
}


