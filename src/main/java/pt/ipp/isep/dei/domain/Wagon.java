package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a wagon used in two contexts:
 * <ul>
 *     <li><strong>Warehouse domain (USEI01, etc.)</strong> – a wagon that carries {@link Box} instances.</li>
 *     <li><strong>Railway planning domain (USLP07)</strong> – a physical railway wagon associated with a
 *     {@link WagonModel}, an operator and a service year.</li>
 * </ul>
 *
 * In the warehouse context, instances are typically created with {@link #Wagon(String)} and boxes are added
 * using {@link #addBox(Box)}. In the railway context, instances are created with
 * {@link #Wagon(String, WagonModel, String, int)} and weight/speed information is taken from the {@link WagonModel}.
 */
public class Wagon {

    /**
     * Identifier / number of the wagon. In the warehouse context this is the wagon id; in the railway context,
     * it is the wagon number from the CSV ("Number").
     */
    private final String id;

    /**
     * Boxes loaded in this wagon (warehouse domain).
     */
    private final List<Box> boxes = new ArrayList<>();

    /**
     * Wagon model (railway domain).
     */
    private WagonModel model;

    /**
     * Operator of the wagon (railway domain).
     */
    private String operator;

    /**
     * Year the wagon entered service (railway domain).
     */
    private int serviceYear;

    /**
     * Creates a wagon for the warehouse domain, identified only by its id.
     * Boxes can be associated later via {@link #addBox(Box)}.
     *
     * @param id wagon identifier
     */
    public Wagon(String id) {
        this.id = id;
    }

    /**
     * Creates a wagon for the railway planning domain, with a model, operator and service year.
     *
     * @param id          wagon number / identifier (column "Number" in Wagons.csv)
     * @param model       wagon model referenced by this wagon
     * @param operator    operator name (column "Operator")
     * @param serviceYear year the wagon entered service (column "Service Year")
     */
    public Wagon(String id, WagonModel model, String operator, int serviceYear) {
        this.id = id;
        this.model = model;
        this.operator = operator;
        this.serviceYear = serviceYear;
    }

    /**
     * Returns the wagon identifier used in the warehouse domain.
     * This is equivalent to {@link #getNumber()}.
     *
     * @return wagon id
     */
    public String getWagonId() {
        return id;
    }

    /**
     * Returns the wagon number as used in the railway domain.
     * This is equivalent to {@link #getWagonId()}.
     *
     * @return wagon number / id
     */
    public String getNumber() {
        return id;
    }

    /**
     * Returns the wagon model associated with this wagon (railway domain).
     *
     * @return wagon model, or {@code null} if not set (warehouse-only usage)
     */
    public WagonModel getModel() {
        return model;
    }

    /**
     * Returns the operator owning/using this wagon (railway domain).
     *
     * @return operator name, or {@code null} if not set
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Returns the year the wagon entered service (railway domain).
     *
     * @return service year, or 0 if not set
     */
    public int getServiceYear() {
        return serviceYear;
    }

    /**
     * Adds a box to this wagon (warehouse domain).
     *
     * @param box box to add; if {@code null} the call is ignored
     */
    public void addBox(Box box) {
        if (box != null) {
            boxes.add(box);
        }
    }

    /**
     * Returns the list of boxes currently loaded in this wagon (warehouse domain).
     *
     * @return unmodifiable list of boxes
     */
    public List<Box> getBoxes() {
        return Collections.unmodifiableList(boxes);
    }

    /**
     * Returns the empty weight of the wagon in tons (railway domain).
     * If no model is associated, returns 0.0.
     *
     * @return tare weight in tons
     */
    public double getTareWeightTons() {
        return model != null ? model.getTareWeightTons() : 0.0;
    }

    /**
     * Returns the fully loaded weight of the wagon in tons (tare + payload) (railway domain).
     * If no model is associated, returns 0.0.
     *
     * @return fully loaded weight in tons
     */
    public double getFullyLoadedWeightTons() {
        return model != null ? model.getFullyLoadedWeightTons() : 0.0;
    }

    @Override
    public String toString() {
        if (model != null) {
            return String.format("%s (%s, operator=%s, year=%d)",
                    id, model.getModelCode(), operator, serviceYear);
        }
        return "Wagon{id='" + id + "', boxes=" + boxes.size() + "}";
    }
}
