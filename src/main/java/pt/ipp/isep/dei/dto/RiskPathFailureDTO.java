package main.java.pt.ipp.isep.dei.dto;

import main.java.pt.ipp.isep.dei.domain.Station;
import java.util.List;

public class RiskPathFailureDTO implements RiskPathResult {
    private final List<Station> negativeCycle;

    public RiskPathFailureDTO(List<Station> negativeCycle) {
        this.negativeCycle = negativeCycle;
    }

    public List<Station> getNegativeCycle() { return negativeCycle; }

    @Override
    public boolean isSuccess() { return false; }

    @Override
    public String getMessage() {
        return "Calculation failed: Negative cost cycle detected.";
    }
}