package main.java.pt.ipp.isep.dei.domain;

public class Allocation {
    private List<Allocatin> allocations;
    private double trolleyCapacity;

    // Construtor


    public Allocation(List<Allocatin> allocations, double trolleyCapacity) {
        this.allocations = allocations;
        this.trolleyCapacity = trolleyCapacity;
    }

    // Métodos getters para acessar as propriedades privadas

    public List<Allocatin> getAllocations() {
        return allocations;
    }

    public double getTrolleyCapacity() {
        return trolleyCapacity;
    }

    public List<Trolley> planFirstFit() {
        // Implementar a heuristica aqui
    }

    public List<Trolley> planFirstFitDecreasing() {
        // Implementar a heuristica aqui
    }

    public List<Trolley> planBestFitDecreasing() {
        // Implementar a heuristica aqui
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Allocation{" +
                "orderId='" + orderId + '\'' +
                ", lineNo=" + lineNo +
                ", sku='" + sku + '\'' +
                ", qty=" + qty +
                ", weight=" + weight +
                ", boxId='" + boxId + '\'' +
                ", aisle=" + aisle +
                ", bay=" + bay +
                '}';
    }
}



