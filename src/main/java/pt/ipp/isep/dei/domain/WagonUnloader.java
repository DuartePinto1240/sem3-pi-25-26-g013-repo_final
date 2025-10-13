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
        // Obter os bays do warehouse através do getter
        Map<String, Bay> bays = warehouse.getBays();

        // Ordenar os bays do armazém
        List<Bay> bayList = new ArrayList<>(bays.values());

        // Ordenação: FEFO (para perecíveis) ou FIFO (para não perecíveis)
        if (isPerishable) {
            // Ordena por data de expiração (mais próxima primeiro) ou por data de recebimento
            Collections.sort(bayList, new Comparator<Bay>() {
                @Override
                public int compare(Bay bay1, Bay bay2) {
                    // Ordena as caixas em cada bay por data de expiração (mais próxima primeiro)
                    Box box1 = bay1.getStoredBoxes().get(0); // Pega a primeira caixa (ou a mais antiga)
                    Box box2 = bay2.getStoredBoxes().get(0); // Pega a primeira caixa (ou a mais antiga)
                    if (box1.getExpiryDate() == null) return 1;
                    if (box2.getExpiryDate() == null) return -1;
                    return box1.getExpiryDate().compareTo(box2.getExpiryDate());
                }
            });
        } else {
            // FIFO: ordena pelas caixas recebidas primeiro
            Collections.sort(bayList, new Comparator<Bay>() {
                @Override
                public int compare(Bay bay1, Bay bay2) {
                    // Ordena as caixas pela data de recebimento (primeiro recebido, primeiro armazenado)
                    Box box1 = bay1.getStoredBoxes().get(0); // Pega a primeira caixa (ou a mais antiga)
                    Box box2 = bay2.getStoredBoxes().get(0); // Pega a primeira caixa (ou a mais antiga)
                    return box1.getReceivedAt().compareTo(box2.getReceivedAt());
                }
            });
        }

        // Buscar o primeiro bay com capacidade para armazenar a caixa
        for (Bay bay : bayList) {
            if (bay.canStoreBox(box)) {
                bay.addBox(box);
                System.out.println("main.java.pt.ipp.isep.dei.domain.Box " + box.getBoxId() + " alocada no main.java.pt.ipp.isep.dei.domain.Bay " + bay.getBay() + " do main.java.pt.ipp.isep.dei.domain.Warehouse " + bay.getWarehouseId());
                return;
            }
        }

        // Se não houver bay com capacidade suficiente
        System.out.println("Não há capacidade suficiente para armazenar a main.java.pt.ipp.isep.dei.domain.Box " + box.getBoxId());
    }
}


