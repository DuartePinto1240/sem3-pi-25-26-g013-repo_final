package main.java.pt.ipp.isep.dei.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class responsible for unloading wagons and allocating the boxes
 * into the appropriate bays in the warehouse, following
 * FEFO (First-Expired-First-Out) or FIFO (First-In-First-Out) principles.
 */
public class WagonUnloader {
    private Warehouse warehouse;

    /**
     * Constructor.
     *
     * @param warehouse the warehouse where the boxes will be stored.
     */
    public WagonUnloader(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * Unloads the wagons, sorts all boxes globally (FEFO/FIFO/BoxId), and allocates
     * them sequentially into the warehouse bays (W1;1;1, W1;1;2, ...).
     *
     * @param wagons list of {@link Wagon}s to unload
     */
    public void unload(List<Wagon> wagons) {
        // 1. Coletar todas as caixas de todos os wagons numa única lista
        List<Box> allBoxes = new ArrayList<>();
        for (Wagon wagon : wagons) {
            allBoxes.addAll(wagon.getBoxes());
        }

        // 2. Ordenar a lista globalmente usando a lógica FEFO/FIFO/BoxId
        // Isto utiliza o Box.compareTo() (que deve ser a versão corrigida com o desempate por boxId).
        Collections.sort(allBoxes);

        // 3. Preparar a lista de baias ordenadas sequencialmente (W1;1;1, W1;1;2, etc.)
        List<Bay> orderedBays = warehouse.getBays().values().stream()
                .sorted(Comparator
                        .comparing(Bay::getWarehouseId)
                        .thenComparingInt(Bay::getAisle)
                        .thenComparingInt(Bay::getBay))
                .collect(Collectors.toList());

        // 4. Alocação Sequencial
        int boxIndex = 0;
        for (Bay bay : orderedBays) {
            // Enquanto houver caixas para alocar E a baia tiver capacidade
            while (boxIndex < allBoxes.size() && bay.canStoreBox(allBoxes.get(boxIndex))) {
                Box boxToPlace = allBoxes.get(boxIndex);

                // Adiciona a caixa à baia
                bay.addBox(boxToPlace);

                // Geração do output obrigatório
                System.out.println("Box " + boxToPlace.getBoxId() + " alocada no Bay " + bay.getBay() + " do Warehouse " + bay.getWarehouseId());

                boxIndex++;
            }

            // Se todas as caixas foram alocadas, termina o ciclo de baias.
            if (boxIndex >= allBoxes.size()) {
                break;
            }
        }

        // 5. Output de Conclusão e Erro (se necessário)
        if (boxIndex < allBoxes.size()) {
            System.out.println("Atenção: Nem todas as caixas foram alocadas. Faltam " + (allBoxes.size() - boxIndex) + " caixas.");
        }

        // Output final obrigatório da USEI01
        System.out.println("✅ Wagons descarregados: " + wagons.size());
        System.out.println("📦 Armazém processado: W1");
    }

    /* O método 'placeBoxInBay' original foi removido, pois a sua lógica (alocação
     * individual e triagem por baia) é incompatível com o requisito de ordenação
     * global seguida de alocação sequencial. */
}