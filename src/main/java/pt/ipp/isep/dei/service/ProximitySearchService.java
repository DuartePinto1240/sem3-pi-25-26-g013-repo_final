package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.TwoDTree;
import main.java.pt.ipp.isep.dei.domain.StationDistance;
import main.java.pt.ipp.isep.dei.dto.ProximitySearchResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
// Removed unused import for java.util.stream.Collectors

/**
 * Service class for USEI09.
 * Orchestrates the proximity search and packages the results, ensuring
 * the filter is handled correctly by the KD-Tree during the search.
 */
public class ProximitySearchService {

    /**
     * Finds the nearest N stations that match the filter (AC3).
     *
     * @param stationTree The 2D-Tree of stations (from USEI07)
     * @param lat Target latitude
     * @param lon Target longitude
     * @param N Number of nearest stations to find
     * @param timeZoneFilter Optional filter for timeZoneGroup (e.g., "CET")
     * @return A ProximitySearchResult DTO with results and metrics.
     */
    public ProximitySearchResult findNearestStations(
            TwoDTree stationTree, double lat, double lon, int N, String timeZoneFilter) {

        long startTime = System.currentTimeMillis();
        AtomicInteger visitedNodes = new AtomicInteger(0);

        // Prepare filter string (null if empty or null)
        String filterToApply = (timeZoneFilter == null || timeZoneFilter.trim().isEmpty()) ? null : timeZoneFilter.trim();

        // 1. Perform nearest-neighbour search in the KD-Tree.
        // The tree's nearestN method now internally applies the filter.
        List<StationDistance> finalResults = stationTree.nearestN(
                lat,
                lon,
                N,
                filterToApply, // Pass the filter for processing during search
                visitedNodes
        );

        long endTime = System.currentTimeMillis();
        long searchTimeMs = endTime - startTime;

        // 4. & 6. Package and return the final DTO (Section 4 & AC6)
        return new ProximitySearchResult(
                finalResults,
                searchTimeMs,
                visitedNodes.get(),
                "O(log n + N log N)", // From Temporal Analysis (Section 5)
                "O(n)",               // From Temporal Analysis (Section 5)
                filterToApply
        );
    }
}