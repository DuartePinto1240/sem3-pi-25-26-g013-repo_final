package main.java.pt.ipp.isep.dei.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Validator {
    public boolean validateItems(List<Item> items) {
        boolean valid = true;
        Set<String> seenSkus = new HashSet<>();

        for (Item i : items) {
            if (i.getSku() == null || i.getSku().isBlank()) {
                System.err.println("Item com SKU vazio.");
                valid = false;
            }
            if (i.getUnitWeight() < 0 || i.getVolume() < 0) {
                System.err.println("Item " + i.getSku() + " com valores inválidos (negativos).");
                valid = false;
            }
            if (!seenSkus.add(i.getSku())) {
                System.err.println("SKU duplicado: " + i.getSku());
                valid = false;
            }
        }
        return valid;
    }

    public boolean validateBays(List<Bay> bays) {
        boolean valid = true;
        Set<String> seen = new HashSet<>();

        for (Bay b : bays) {
            if (b.getAisle() <= 0 || b.getBay() <= 0 || b.getCapacityBoxes() <= 0) {
                System.err.printf("Bay inválida (Aisle=%d, Bay=%d, Cap=%d)%n",
                        b.getAisle(), b.getBay(), b.getCapacityBoxes());
                valid = false;
            }
            String key = b.getAisle() + ":" + b.getBay();
            if (!seen.add(key)) {
                System.err.printf("⚠️ Bay duplicada no corredor %d com número %d%n",
                        b.getAisle(), b.getBay());
                valid = false;
            }
        }
        return valid;
    }

    public boolean validateWagons(List<Wagon> wagons, List<Item> validItems) {
        boolean valid = true;

        Set<String> validSkus = new HashSet<>();
        for (Item i : validItems) validSkus.add(i.getSku());

        Set<String> seenBoxIds = new HashSet<>();

        for (Wagon w : wagons) {
            if (w.getBoxes().isEmpty()) {
                System.err.printf("Wagon %s não contém nenhuma caixa.%n", w.getWagonId());
                valid = false;
            }

            for (Box b : w.getBoxes()) {
                if (!validSkus.contains(b.getSku())) {
                    System.err.printf("Box %s tem SKU desconhecido (%s) no wagon %s.%n",
                            b.getBoxId(), b.getSku(), w.getWagonId());
                    valid = false;
                }

                if (b.getQuantity() <= 0) {
                    System.err.printf(" Box %s tem quantidade inválida (%d).%n",
                            b.getBoxId(), b.getQuantity());
                    valid = false;
                }

                if (b.getReceivedAt() == null) {
                    System.err.printf("Box %s não tem 'receivedAt' definido.%n", b.getBoxId());
                    valid = false;
                }

                if (!seenBoxIds.add(b.getBoxId())) {
                    System.err.printf("BoxId duplicado encontrado: %s.%n", b.getBoxId());
                    valid = false;
                }
            }
        }
        return valid;
    }

    public boolean validateOrders(List<Order> orders, List<Item> validItems) {
        boolean valid = true;
        Set<String> validSkus = new HashSet<>();
        for (Item i : validItems) validSkus.add(i.getSku());

        for (Order o : orders) {
            if (o.getDueDate() == null) {
                System.err.println("Order " + o.getOrderId() + " sem data de entrega.");
                valid = false;
            }
            for (OrderLine line : o.getLines()) {
                if (!validSkus.contains(line.getSku())) {
                    System.err.println("Order " + o.getOrderId() + " contém SKU inexistente: " + line.getSku());
                    valid = false;
                }
                if (line.getQtyRequested() <= 0) {
                    System.err.println("Quantidade inválida em " + o.getOrderId() + " SKU " + line.getSku());
                    valid = false;
                }
            }
        }
        return valid;
    }

    public boolean validateReturns(List<ReturnItem> returns, List<Item> validItems) {
        boolean valid = true;
        Set<String> validSkus = new HashSet<>();
        for (Item i : validItems) validSkus.add(i.getSku());

        for (ReturnItem r : returns) {
            if (!validSkus.contains(r.getSku())) {
                System.err.println("⚠️ Return " + r.getReturnId() + " tem SKU inválido: " + r.getSku());
                valid = false;
            }
            if (r.getQuantity() <= 0) {
                System.err.println("⚠️ Return " + r.getReturnId() + " com quantidade inválida: " + r.getQuantity());
                valid = false;
            }
        }
        return valid;
    }
}


