package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*;

public class USEI03UI implements Runnable {

    @Override
    public void run() {
        System.out.println("\n=== USEI03 — Picking Plans ===");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");
        List<Order> orders = (List<Order>) data.get("orders");
        List<Item> items = (List<Item>) data.get("items");

        if (bays.isEmpty()) {
            System.out.println("⚠️ Nenhuma bay encontrada.");
            return;
        }

        // Determinar ID do warehouse dinamicamente
        String warehouseId = bays.get(0).getWarehouseId();
        Warehouse warehouse = new Warehouse(warehouseId);

        // Validar e adicionar apenas bays do mesmo warehouse
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

        System.out.println("\nDeseja usar qual conjunto de alocações para o planeamento?");
        System.out.println("1 - STRICT");
        System.out.println("2 - PARTIAL");
        System.out.print("Opção: ");
        int modeChoice = sc.nextInt();

        // Alocar encomendas
        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, modeChoice == 2);

        // Calcular pesos por item
        Map<String, Double> itemWeights = new HashMap<>();
        for (Item i : items) {
            itemWeights.put(i.getSku(), i.getUnitWeight());
        }

        List<Allocation> allocations = new ArrayList<>();
        for (AllocationRow row : result.allocationRows) {
            double w = itemWeights.getOrDefault(row.getSku(), 0.0) * row.getQty();
            allocations.add(new Allocation(
                    row.getOrderId(),
                    row.getLineNo(),
                    row.getSku(),
                    row.getQty(),
                    w,
                    row.getBoxId(),
                    row.getAisle(),
                    row.getBay()
            ));
        }

        // Capacidade do trolley
        System.out.print("Indique capacidade máxima (peso) do trolley: ");
        double capacity = sc.nextDouble();

        // Escolher heurística de planeamento
        System.out.println("\nEscolha a heurística de distribuição:");
        System.out.println("1 - First Fit (FF)");
        System.out.println("2 - First Fit Decreasing (FFD)");
        System.out.println("3 - Best Fit Decreasing (BFD)");
        System.out.print("Opção: ");
        int option = sc.nextInt();

        // Executar a heurística selecionada
        PickingPlanner planner = new PickingPlanner(allocations, capacity);
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

        // Mostrar resultados detalhados
        printPickingPlans(trolleys);
    }

    // Imprimir resultados formatados
    private void printPickingPlans(List<Trolley> trolleys) {
        System.out.println("Total de trolleys necessários: " + trolleys.size());
        System.out.println("------------------------------------");

        for (Trolley t : trolleys) {
            System.out.printf("Trolley %d — Utilização: %.1f%% (%.1f / %.1f)\n",
                    t.getId(), t.getUtilization(), t.getUsedWeight(), t.getCapacity());
            for (Allocation a : t.getAllocations()) {
                System.out.printf("   Order=%s | SKU=%s | Qty=%d | Box=%s | Aisle=%d | Bay=%d | Weight=%.1f\n",
                        a.getOrderId(), a.getSku(), a.getQty(), a.getBoxId(), a.getAisle(), a.getBay(), a.getWeight());
            }
            System.out.println();
        }
    }
}
