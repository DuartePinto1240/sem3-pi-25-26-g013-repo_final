package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*;

public class USEI04UI implements Runnable {

    @Override
    public void run() {
        System.out.println("\n=== USEI04 — Pick Path Sequencing ===");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");
        List<Order> orders = (List<Order>) data.get("orders");
        List<Item> items = (List<Item>) data.get("items");

        if (bays.isEmpty()) {
            System.out.println("⚠️ Nenhuma bay encontrada no CSV.");
            return;
        }

        // Criar warehouse com base no ID real das bays
        String warehouseId = bays.get(0).getWarehouseId();
        Warehouse warehouse = new Warehouse(warehouseId);

        // Adicionar apenas bays consistentes
        for (Bay b : bays) {
            if (!b.getWarehouseId().equals(warehouseId)) {
                System.out.printf("⚠️ Ignorada bay de outro warehouse: %s%n", b.getWarehouseId());
                continue;
            }
            warehouse.addBay(b);
        }

        // Descarregar os vagões
        new WagonUnloader(warehouse).unload(wagons);

        Scanner sc = new Scanner(System.in);

        // Escolher modo de alocação (US02)
        System.out.println("\nDeseja usar qual conjunto de alocações para o planeamento?");
        System.out.println("1 - STRICT");
        System.out.println("2 - PARTIAL");
        System.out.print("Opção: ");
        int modeChoice = sc.nextInt();

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, modeChoice == 2);

        // Obter peso por item
        Map<String, Double> weights = new HashMap<>();
        for (Item item : items)
            weights.put(item.getSku(), item.getUnitWeight());

        // Converter AllocationsRows em Allocations com peso
        List<Allocation> allocations = new ArrayList<>();
        for (AllocationRow row : result.allocationRows) {
            double weight = weights.getOrDefault(row.getSku(), 0.0) * row.getQty();
            allocations.add(new Allocation(
                    row.getOrderId(),
                    row.getLineNo(),
                    row.getSku(),
                    row.getQty(),
                    weight,
                    row.getBoxId(),
                    row.getAisle(),
                    row.getBay()
            ));
        }

        // Pedir capacidade máxima (peso) do trolley (US03)
        System.out.print("\nIndique a capacidade máxima (peso) de cada trolley: ");
        double trolleyCapacity = sc.nextDouble();

        // Escolher heurística de planeamento (US03)
        System.out.println("\nEscolha a heurística de distribuição:");
        System.out.println("1 - First Fit (FF)");
        System.out.println("2 - First Fit Decreasing (FFD)");
        System.out.println("3 - Best Fit Decreasing (BFD)");
        System.out.print("Opção: ");
        int option = sc.nextInt();

        // Executa a heurística selecionada
        PickingPlanner planner = new PickingPlanner(allocations, trolleyCapacity);
        List<Trolley> trolleys;
        switch (option) {
            case 1:
                trolleys = planner.planFirstFit();
                break;
            case 2:
                trolleys = planner.planFirstFitDecreasing();
                break;
            case 3:
            default:
                trolleys = planner.planBestFitDecreasing();
                break;
        }

        // Recolhe todas as allocations dos trolleys (para o percurso)
        List<Allocation> allAllocations = new ArrayList<>();
        for (Trolley t : trolleys) {
            allAllocations.addAll(t.getAllocations());
        }

        // Funde baias duplicadas (somar quantidades)
        Map<String, PickStop> mergedMap = new HashMap<>();
        for (Allocation alloc : allAllocations) {
            String key = alloc.getAisle() + ":" + alloc.getBay();
            Coordinate coord = new Coordinate(alloc.getAisle(), alloc.getBay());

            mergedMap.merge(key, new PickStop(coord, alloc),
                    (oldVal, newVal) -> {
                        oldVal.getAllocation().setQty(
                                oldVal.getAllocation().getQty() + alloc.getQty());
                        return oldVal;
                    });
        }

        List<PickStop> mergedStops = new ArrayList<>(mergedMap.values());

        // Gerar trajetos (US04)
        PickPathPlanner pickPathPlanner = new PickPathPlanner();
        PickPath deterministic = pickPathPlanner.planDeterministicSweep(mergedStops);
        PickPath nearest = pickPathPlanner.planNearestNeighbour(mergedStops);

        // Mostrar resultados detalhados
        System.out.println("\n✅ Warehouse processado: " + warehouseId);
        printPath("A — Deterministic Sweep", deterministic);
        printPath("B — Nearest Neighbour", nearest);
    }

    // Impressão do percurso
    private void printPath(String name, PickPath path) {
        System.out.println("\nEstratégia " + name);
        System.out.print("Caminho: (0,0)");
        for (PickStop s : path.getSequence()) {
            System.out.print(" -> (" + s.getCoordinate().getAisle() + "," + s.getCoordinate().getBay() + ")");
        }
        System.out.printf("\nDistância total: %.2f\n", path.getTotalDistance());
    }
}
