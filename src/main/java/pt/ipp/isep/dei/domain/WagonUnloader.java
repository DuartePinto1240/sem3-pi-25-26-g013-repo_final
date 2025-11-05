package main.java.pt.ipp.isep.dei.domain;

import java.util.*;
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
     * Unloads wagons by grouping boxes by SKU, sorting each group by FEFO/FIFO,
     * and allocating each SKU to its own bay(s).
     *
     * @param wagons list of {@link Wagon}s to unload
     */
    public void unload(List<Wagon> wagons) {
        // 1. Coletar todas as boxes de todos os wagons
        List<Box> allBoxes = new ArrayList<>();
        for (Wagon wagon : wagons) {
            allBoxes.addAll(wagon.getBoxes());
        }

        // 2. AGRUPAR POR SKU
        Map<String, List<Box>> boxesBySku = allBoxes.stream()
                .collect(Collectors.groupingBy(Box::getSku));

        // 3. Ordenar cada grupo de boxes por FEFO/FIFO
        for (List<Box> boxes : boxesBySku.values()) {
            Collections.sort(boxes);
        }

        // 4. Preparar lista de bays ordenada (por warehouse, aisle, bay)
        List<Bay> orderedBays = warehouse.getBays().values().stream()
                .sorted(Comparator
                        .comparing(Bay::getWarehouseId)
                        .thenComparingInt(Bay::getAisle)
                        .thenComparingInt(Bay::getBay))
                .collect(Collectors.toList());

        // 5. Alocar cada SKU aos bays
        int currentBayIndex = 0;

        // Ordenar SKUs para processamento consistente
        List<String> sortedSkus = new ArrayList<>(boxesBySku.keySet());
        Collections.sort(sortedSkus);

        for (String sku : sortedSkus) {
            List<Box> skuBoxes = boxesBySku.get(sku);
            int boxIndex = 0;

            // Verificar se há bays disponíveis antes de começar
            if (currentBayIndex >= orderedBays.size()) {
                System.out.println("⚠️ Erro: Não há mais bays disponíveis para o SKU " + sku);
                break;
            }

            // Alocar todas as boxes deste SKU
            while (boxIndex < skuBoxes.size()) {
                if (currentBayIndex >= orderedBays.size()) {
                    System.out.println("⚠️ Atenção: Capacidade do armazém esgotada. SKU " + sku +
                            " não foi totalmente alocado. Faltam " +
                            (skuBoxes.size() - boxIndex) + " boxes.");
                    break;
                }

                Bay currentBay = orderedBays.get(currentBayIndex);

                // Preencher o bay atual com boxes deste SKU
                while (boxIndex < skuBoxes.size() && currentBay.canStoreBox(skuBoxes.get(boxIndex))) {
                    Box boxToPlace = skuBoxes.get(boxIndex);
                    currentBay.addBox(boxToPlace);

                    // Output com Aisle incluído para clareza
                    System.out.println("Box " + boxToPlace.getBoxId() +
                            " alocada no Aisle " + currentBay.getAisle() +
                            ", Bay " + currentBay.getBay() +
                            " do Warehouse " + currentBay.getWarehouseId());
                    boxIndex++;
                }

                // Se ainda há boxes deste SKU, AVANÇA OBRIGATORIAMENTE para o próximo bay
                if (boxIndex < skuBoxes.size()) {
                    currentBayIndex++;
                }
            }

            // IMPORTANTE: Após terminar um SKU, SEMPRE avança para o próximo bay
            // (para garantir que cada SKU começa num bay limpo)
            currentBayIndex++;
        }

        // Output final obrigatório
        System.out.println("\n✅ Wagons descarregados: " + wagons.size());
        System.out.println("📦 Armazém processado: W1");

        // VERIFICAÇÃO DE INTEGRIDADE
        verifyAllocation(orderedBays);
    }

    /**
     * Verifies the allocation integrity by checking if each bay contains only one SKU.
     * Prints a detailed report of the allocation.
     *
     * @param orderedBays list of bays to verify
     */
    private void verifyAllocation(List<Bay> orderedBays) {
        System.out.println("\n=== VERIFICAÇÃO DE ALOCAÇÃO ===");

        int totalBoxesAllocated = 0;
        int baysUsed = 0;
        boolean hasErrors = false;

        for (Bay bay : orderedBays) {
            if (!bay.getStoredBoxes().isEmpty()) {
                baysUsed++;
                totalBoxesAllocated += bay.getStoredBoxes().size();

                // Obter todos os SKUs únicos neste bay
                Set<String> skusInBay = bay.getStoredBoxes().stream()
                        .map(Box::getSku)
                        .collect(Collectors.toSet());

                System.out.println("Aisle " + bay.getAisle() +
                        ", Bay " + bay.getBay() +
                        ": " + bay.getStoredBoxes().size() +
                        " boxes (capacidade: " + bay.getCapacityBoxes() +
                        ") | SKUs: " + skusInBay);

                // ALERTA se houver mais de 1 SKU no mesmo bay
                if (skusInBay.size() > 1) {
                    System.out.println("  ⚠️ ERRO: Bay contém múltiplos SKUs!");
                    hasErrors = true;
                }

                // Verificar ordem FEFO/FIFO dentro do bay
                if (!verifyFEFOOrder(bay.getStoredBoxes())) {
                    System.out.println("  ⚠️ AVISO: Ordem FEFO/FIFO não respeitada neste bay!");
                    hasErrors = true;
                }
            }
        }

        System.out.println("\n--- RESUMO ---");
        System.out.println("Total de boxes alocadas: " + totalBoxesAllocated);
        System.out.println("Bays utilizados: " + baysUsed);

        if (hasErrors) {
            System.out.println("❌ ALOCAÇÃO COM ERROS!");
        } else {
            System.out.println("✅ ALOCAÇÃO VÁLIDA - Todos os bays contêm apenas um SKU em ordem FEFO/FIFO!");
        }
    }

    /**
     * Verifies if boxes in a bay are correctly ordered according to FEFO/FIFO rules.
     *
     * @param boxes list of boxes to verify
     * @return true if correctly ordered, false otherwise
     */
    private boolean verifyFEFOOrder(List<Box> boxes) {
        if (boxes.size() <= 1) {
            return true;
        }

        for (int i = 0; i < boxes.size() - 1; i++) {
            Box current = boxes.get(i);
            Box next = boxes.get(i + 1);

            // Usar o compareTo() do Box para verificar a ordem
            if (current.compareTo(next) > 0) {
                return false; // Ordem incorreta
            }
        }

        return true;
    }
}
