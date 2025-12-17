package main.java.pt.ipp.isep.dei.dto;

import main.java.pt.ipp.isep.dei.domain.Station;
import java.util.List;

public class RiskPathFailureDTO implements RiskPathResult {
    private final List<Station> negativeCycle;
    private final long executionTimeMs;
    private final String complexityAnalysis;

    public RiskPathFailureDTO(List<Station> negativeCycle, long executionTimeMs, String complexityAnalysis) {
        this.negativeCycle = negativeCycle;
        this.executionTimeMs = executionTimeMs;
        this.complexityAnalysis = complexityAnalysis;
    }

    public List<Station> getNegativeCycle() { return negativeCycle; }

    @Override
    public boolean isSuccess() { return false; }

    @Override
    public String getMessage() {
        return "Calculation failed: Negative cost cycle detected.";
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