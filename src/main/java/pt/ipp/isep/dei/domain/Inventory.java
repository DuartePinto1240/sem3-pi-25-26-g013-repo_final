package main.java.pt.ipp.isep.dei.domain;

import java.util.*;

public class Inventory {

    private final Map<String, List<Box>> stock = new HashMap<>();

    public void addBox(Box box) {
        stock.putIfAbsent(box.getSku(), new ArrayList<>());
        List<Box> boxes = stock.get(box.getSku());
        boxes.add(box);
        Collections.sort(boxes); // FEFO/FIFO ordering
    }

    public boolean containsSku(String sku) {
        return stock.containsKey(sku);
    }

    public List<Box> getBoxes(String sku) {
        return stock.getOrDefault(sku, Collections.emptyList());
    }

    public Map<String, List<Box>> getStock() {
        return Collections.unmodifiableMap(stock);
    }
}
