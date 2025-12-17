package main.java.pt.ipp.isep.dei.dto;

import main.java.pt.ipp.isep.dei.domain.Station;
import java.util.List;

public class RiskPathSuccessDTO implements RiskPathResult {
    private final List<Station> path;
    private final double totalCost;

    public RiskPathSuccessDTO(List<Station> path, double totalCost) {
        this.path = path;
        this.totalCost = totalCost;
    }

    public List<Station> getPath() { return path; }
    public double getTotalCost() { return totalCost; }

    @Override
    public boolean isSuccess() { return true; }

    @Override
    public String getMessage() {
        return String.format("Path found. Total Risk Cost: %.2f", totalCost);
    }
}