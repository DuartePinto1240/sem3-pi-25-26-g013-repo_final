package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Planner para alocação de produtos (Allocations) em trolleys.
 * <p>
 * Suporta três estratégias principais:
 * <ul>
 *     <li>First Fit</li>
 *     <li>First Fit Decreasing (FFD)</li>
 *     <li>Best Fit Decreasing (BFD)</li>
 * </ul>
 * Cada trolley possui capacidade máxima, e as alocações excedendo essa capacidade são ignoradas.
 * </p>
 */
public class PickingPlanner {

    /** Lista de alocações a serem planejadas. */
    private List<Allocation> allocations;

    /** Capacidade máxima de cada trolley. */
    private double trolleyCapacity;

    /**
     * Construtor.
     *
     * @param allocations     lista de alocações a serem distribuídas nos trolleys
     * @param trolleyCapacity capacidade máxima de cada trolley
     */
    public PickingPlanner(List<Allocation> allocations, double trolleyCapacity) {
        this.allocations = allocations;
        this.trolleyCapacity = trolleyCapacity;
    }

    /**
     * Retorna a lista de alocações.
     *
     * @return lista de alocações
     */
    public List<Allocation> getAllocations() {
        return allocations;
    }

    /**
     * Retorna a capacidade máxima de cada trolley.
     *
     * @return capacidade do trolley
     */
    public double getTrolleyCapacity() {
        return trolleyCapacity;
    }

    /**
     * Planeia as alocações usando a estratégia First Fit.
     * <p>
     * Cada allocation é adicionada ao primeiro trolley que tiver capacidade suficiente.
     * Se nenhum trolley puder acomodar a allocation, é criado um novo trolley.
     * </p>
     *
     * @return lista de trolleys com alocações distribuídas
     */
    public List<Trolley> planFirstFit() {
        List<Trolley> trolleys = new ArrayList<>();
        int nextId = 1;

        for (Allocation alloc : allocations) {
            if (alloc.getWeight() > trolleyCapacity) {
                System.out.printf(
                        "⚠️ Allocation %s (%.1f kg) excede capacidade total do trolley (%.1f kg) — ignorada.%n",
                        alloc.getSku(), alloc.getWeight(), trolleyCapacity
                );
                continue;
            }

            boolean placed = false;
            for (Trolley t : trolleys) {
                if (t.canFit(alloc.getWeight())) {
                    t.addAllocation(alloc);
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                Trolley newT = new Trolley(nextId++, trolleyCapacity);
                newT.addAllocation(alloc);
                trolleys.add(newT);
            }
        }
        return trolleys;
    }

    /**
     * Planeia as alocações usando a estratégia First Fit Decreasing (FFD).
     * <p>
     * As alocações são ordenadas por peso decrescente antes de aplicar a estratégia First Fit.
     * </p>
     *
     * @return lista de trolleys com alocações distribuídas
     */
    public List<Trolley> planFirstFitDecreasing() {
        List<Allocation> sorted = new ArrayList<>(allocations);
        sorted.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));
        this.allocations = sorted;
        return planFirstFit();
    }

    /**
     * Planeia as alocações usando a estratégia Best Fit Decreasing (BFD).
     * <p>
     * Cada allocation é alocada no trolley que deixa o menor espaço livre remanescente possível.
     * Se nenhum trolley puder acomodar a allocation, é criado um novo trolley.
     * </p>
     *
     * @return lista de trolleys com alocações distribuídas
     */
    public List<Trolley> planBestFitDecreasing() {
        List<Allocation> sorted = new ArrayList<>(allocations);
        sorted.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));

        List<Trolley> trolleys = new ArrayList<>();
        int nextId = 1;

        for (Allocation alloc : sorted) {
            if (alloc.getWeight() > trolleyCapacity) {
                System.out.printf(
                        "⚠️ Allocation %s (%.1f kg) excede capacidade total do trolley (%.1f kg) — ignorada.%n",
                        alloc.getSku(), alloc.getWeight(), trolleyCapacity
                );
                continue;
            }

            Trolley bestFit = null;
            double minRemaining = Double.MAX_VALUE;
            for (Trolley t : trolleys) {
                double remaining = t.getCapacity() - t.getUsedWeight();
                if (t.canFit(alloc.getWeight()) && remaining - alloc.getWeight() < minRemaining) {
                    bestFit = t;
                    minRemaining = remaining - alloc.getWeight();
                }
            }
            if (bestFit == null) {
                bestFit = new Trolley(nextId++, trolleyCapacity);
                trolleys.add(bestFit);
            }
            bestFit.addAllocation(alloc);
        }
        return trolleys;
    }
}
