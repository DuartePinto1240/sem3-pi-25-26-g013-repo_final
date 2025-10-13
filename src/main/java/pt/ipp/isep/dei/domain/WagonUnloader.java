package main.java.pt.ipp.isep.dei.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class WagonUnloader {
    private Warehouse warehouse;

    // Construtor
    public WagonUnloader(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    // Método para descarregar os vagões e alocar as caixas nos bays
    public void unload(List<Wagon> wagons) {
        for (Wagon wagon : wagons) {
            for (Box box : wagon.getBoxes()) {
                // Verifica se o produto é perecível (tem data de expiração)
                if (box.getExpiryDate() != null) {
                    // Ordena a caixa usando FEFO (First-Expired-First-Out) - produtos perecíveis
                    placeBoxInBay(box, true);  // FEFO
                } else {
                    // Ordena a caixa usando FIFO (First-In-First-Out) - produtos não perecíveis
                    placeBoxInBay(box, false); // FIFO
                }
            }
        }
    }

    // Método que realiza a alocação da caixa no armazém, respeitando FEFO ou FIFO
    private void placeBoxInBay(Box box, boolean isPerishable) {
        Map<String, Bay> bays = warehouse.getBays();

        Comparator<Box> comparator = null;
        if (isPerishable) {
            comparator = new Comparator<Box>() {
                @Override
                public int compare(Box b1, Box b2) {
                    if (b1.getExpiryDate() == null && b2.getExpiryDate() == null) return 0;
                    if (b1.getExpiryDate() == null) return 1; // Não perecível vai depois
                    if (b2.getExpiryDate() == null) return -1; // Não perecível vai depois
                    return b1.getExpiryDate().compareTo(b2.getExpiryDate());
                }
            };
        } else {
            comparator = new Comparator<Box>() {
                @Override
                public int compare(Box b1, Box b2) {
                    return b1.getReceivedAt().compareTo(b2.getReceivedAt());
                }
            };
        }

        for (Bay bay : bays.values()) {
            if (bay.canStoreBox(box)) {
                bay.addBox(box);

                // Ordenar APÓS adicionar, apenas neste bay
                Collections.sort(bay.getStoredBoxes(), comparator);

                System.out.println("Box " + box.getBoxId() + " alocada no Bay " + bay.getBay() + " do Warehouse " + bay.getWarehouseId());
                return;
            }
        }

        // Se não houver bay com capacidade suficiente
        System.out.println("Não há capacidade suficiente para armazenar a Box " + box.getBoxId());
    }




}


