package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

public class Wagon {

    private String wagonId;
    private List<Box> boxes;

    // 🔹 Construtor simples (já existente)
    public Wagon(String wagonId) {
        this.wagonId = wagonId;
        this.boxes = new ArrayList<>();
    }

    // 🔹 Novo construtor com lista de caixas
    public Wagon(String wagonId, List<Box> boxes) {
        this.wagonId = wagonId;
        this.boxes = new ArrayList<>(boxes);
    }

    // 🔹 Adiciona uma caixa a este vagão
    public void addBox(Box box) {
        boxes.add(box);
    }

    public String getWagonId() {
        return wagonId;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId='" + wagonId + '\'' +
                ", boxes=" + boxes.size() +
                '}';
    }
}



