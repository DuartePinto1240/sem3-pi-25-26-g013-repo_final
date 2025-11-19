package main.java.pt.ipp.isep.dei.dto;

import main.java.pt.ipp.isep.dei.domain.StationDistance;
import java.util.List;

/**
 * DTO to hold the complete result of a USEI09 proximity search,
 * including metrics as specified in Section 4 of the requirements.
 */
public class ProximitySearchResult {

    private final List<StationDistance> nearestStations;
    private final long searchTimeMs;
    private final int visitedNodes;
    private final String averageComplexity;
    private final String worstCaseComplexity;
    private final String filterApplied;

    public ProximitySearchResult(List<StationDistance> nearestStations,
                                 long searchTimeMs, int visitedNodes,
                                 String averageComplexity, String worstCaseComplexity,
                                 String filterApplied) {
        this.nearestStations = nearestStations;
        this.searchTimeMs = searchTimeMs;
        this.visitedNodes = visitedNodes;
        this.averageComplexity = averageComplexity;
        this.worstCaseComplexity = worstCaseComplexity;
        this.filterApplied = filterApplied != null ? filterApplied : "None";
    }

    // --- Getters ---

    public List<StationDistance> getNearestStations() {
        return nearestStations;
    }

    public long getSearchTimeMs() {
        return searchTimeMs;
    }

    public int getVisitedNodes() {
        return visitedNodes;
    }

    public String getAverageComplexity() {
        return averageComplexity;
    }

    public String getWorstCaseComplexity() {
        return worstCaseComplexity;
    }

    public String getFilterApplied() {
        return filterApplied;
    }
}