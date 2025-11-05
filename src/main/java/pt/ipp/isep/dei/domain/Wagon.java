package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wagon that transports multiple boxes ({@link Box}).
 * <p>
 * Each {@code Wagon} has a unique identifier and maintains a list of boxes.
 * </p>
 */
public class Wagon {

    /** Unique identifier of the wagon. */
    private String wagonId;

    /** List of boxes contained in this wagon. */
    private List<Box> boxes;

    /**
     * Constructs an empty {@code Wagon} with a specific identifier.
     *
     * @param wagonId unique identifier of the wagon
     */
    public Wagon(String wagonId) {
        this.wagonId = wagonId;
        this.boxes = new ArrayList<>();
    }

    /**
     * Constructs a {@code Wagon} with an identifier and an initial list of boxes.
     *
     * @param wagonId unique identifier of the wagon
     * @param boxes list of boxes to be initially added to the wagon
     */
    public Wagon(String wagonId, List<Box> boxes) {
        this.wagonId = wagonId;
        this.boxes = new ArrayList<>(boxes);
    }

    /**
     * Adds a new box to the wagon.
     *
     * @param box the box to be added
     */
    public void addBox(Box box) {
        boxes.add(box);
    }

    /**
     * Gets the identifier of the wagon.
     *
     * @return the wagon ID
     */
    public String getWagonId() {
        return wagonId;
    }

    /**
     * Returns the list of boxes currently contained in the wagon.
     *
     * @return list of {@link Box} objects
     */
    public List<Box> getBoxes() {
        return boxes;
    }

    /**
     * Textual representation of the wagon, including its ID and the number of boxes.
     *
     * @return string in the format {@code Wagon{wagonId='ID', boxes=N}}
     */
    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId='" + wagonId + '\'' +
                ", boxes=" + boxes.size() +
                '}';
    }
}