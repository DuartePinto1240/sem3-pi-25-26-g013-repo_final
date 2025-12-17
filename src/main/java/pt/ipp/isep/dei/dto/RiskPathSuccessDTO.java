package main.java.pt.ipp.isep.dei.dto;

import main.java.pt.ipp.isep.dei.domain.Station;
import java.util.List;

public class RiskPathSuccessDTO implements RiskPathResult {
    private final List<Station> path;
    private final double totalCost;
    private final long executionTimeMs;
    private final String complexityAnalysis;

    public RiskPathSuccessDTO(List<Station> path, double totalCost, long executionTimeMs, String complexityAnalysis) {
        this.path = path;
        this.totalCost = totalCost;
        this.executionTimeMs = executionTimeMs;
        this.complexityAnalysis = complexityAnalysis;
    }

    public List<Station> getPath() { return path; }
    public double getTotalCost() { return totalCost; }

    @Override
    public boolean isSuccess() { return true; }

    @Override
    public String getMessage() {
        return String.format("Path found. Total Risk Cost: %.2f", totalCost);
    }

    @Override
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public String getComplexityAnalysis() {
        return complexityAnalysis;
    }
}