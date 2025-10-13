import java.util.List;

public class Wagon {
    private String wagonId;
    private List<Box> boxes;

    // Construtor
    public Wagon(String wagonId, List<Box> boxes) {
        this.wagonId = wagonId;
        this.boxes = boxes;
    }

    // Métodos
    public List<Box> getBoxes() {
        return boxes;
    }

    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId='" + wagonId + '\'' +
                ", boxes=" + boxes +
                '}';
    }
}

