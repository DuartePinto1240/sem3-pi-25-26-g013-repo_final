package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*; // tem ArrayList, HashMap, LinkedHashMap, List, Map, Scanner

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

        // US01 — Construir warehouse e descarregar vagões
        String warehouseId = bays.get(0).getWarehouseId();
        Warehouse warehouse = new Warehouse(warehouseId);

        for (Bay b : bays) {
            if (!b.getWarehouseId().equals(warehouseId)) {
                System.out.printf("⚠️ Ignorada bay de outro warehouse: %s%n", b.getWarehouseId());
                continue;
            }
            warehouse.addBay(b);
        }

        new WagonUnloader(warehouse).unload(wagons);

        Scanner sc = new Scanner(System.in);

        // US02 — Modo de alocação
        System.out.println("\nDeseja usar qual conjunto de alocações para o planeamento?");
        System.out.println("1 - STRICT");
        System.out.println("2 - PARTIAL");
        System.out.print("Opção: ");
        int modeChoice = sc.nextInt();

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, modeChoice == 2);

        // Mapear pesos por SKU
        Map<String, Double> weights = new HashMap<>();
        for (Item item : items) {
            weights.put(item.getSku(), item.getUnitWeight());
        }

        // Converter AllocationRows -> Allocation (com peso)
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

        // US03 — Planeamento dos trolleys
        System.out.print("\nIndique a capacidade máxima (peso) de cada trolley: ");
        double trolleyCapacity = sc.nextDouble();

        System.out.println("\nEscolha a heurística de distribuição:");
        System.out.println("1 - First Fit (FF)");
        System.out.println("2 - First Fit Decreasing (FFD)");
        System.out.println("3 - Best Fit Decreasing (BFD)");
        System.out.print("Opção: ");
        int option = sc.nextInt();

        PickingPlanner planner = new PickingPlanner(allocations, trolleyCapacity);
        List<Trolley> trolleys;
        switch (option) {
            case 1:  trolleys = planner.planFirstFit();            break;
            case 2:  trolleys = planner.planFirstFitDecreasing();  break;
            case 3:
            default: trolleys = planner.planBestFitDecreasing();   break;
        }

        // ==== USEI04 — Sequenciação por TROLLEY ====

        if (trolleys == null || trolleys.isEmpty()) {
            System.out.println("Não há trolleys para sequenciar (USEI03 não gerou plano).");
            return;
        }

        // Resumo dos trolleys
        System.out.println("\n--- TROLLEYS GERADOS (USEI03) ---");
        for (int i = 0; i < trolleys.size(); i++) {
            Trolley t = trolleys.get(i);
            // Se o teu Trolley não tiver getUsedWeight()/getCapacity(), remove estes dois campos
            System.out.printf("  %d) items=%d  peso=%.2f/%.2f%n",
                    i + 1,
                    t.getAllocations().size(),
                    t.getUsedWeight(),
                    t.getCapacity());
        }

        // Escolher qual trolley sequenciar
        int choice = -1;
        do {
            System.out.printf("Escolha o trolley para sequenciar [1..%d]: ", trolleys.size());
            try {
                // usar o mesmo Scanner 'sc'
                sc.nextLine(); // limpar newline pendente do último nextInt()
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception ignore) {}
        } while (choice < 1 || choice > trolleys.size());

        Trolley selected = trolleys.get(choice - 1);

        // Agregar por (aisle,bay): 1 PickStop por bay
        Map<String, PickStop> byCoord = new LinkedHashMap<>();
        for (Allocation a : selected.getAllocations()) {
            int aisle = a.getAisle();
            int bay   = a.getBay();
            String key = aisle + ":" + bay;
            byCoord.putIfAbsent(key, new PickStop(new Coordinate(aisle, bay), a));
        }
        List<PickStop> stops = new ArrayList<>(byCoord.values());

        // Planear percurso (A e B)
        PickPathPlanner pickPathPlanner = new PickPathPlanner();
        PickPath sweep  = pickPathPlanner.planDeterministicSweep(stops);
        PickPath nn     = pickPathPlanner.planNearestNeighbour(stops);

        // Output
        printPath("A — Deterministic Sweep", sweep);
        printPath("B — Nearest Neighbour", nn);
    }

    // Impressão do percurso
    private void printPath(String name, PickPath path) {
        System.out.println("\nEstratégia " + name);
        System.out.print("Caminho: (0,0)");
        for (PickStop s : path.getSequence()) {
            System.out.print(" -> (" + s.getCoordinate().getAisle() + "," + s.getCoordinate().getBay() + ")");
        }
        System.out.printf("\nDistância total: %.2f%n", path.getTotalDistance());
    }
}
