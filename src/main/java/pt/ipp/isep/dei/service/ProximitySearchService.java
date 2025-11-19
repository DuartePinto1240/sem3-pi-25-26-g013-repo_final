package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.TwoDTree;
import main.java.pt.ipp.isep.dei.domain.StationDistance;
import main.java.pt.ipp.isep.dei.dto.ProximitySearchResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service class for USEI09.
 * Orchestrates the proximity search, applies filtering, and packages
 * the results, including performance metrics.
 */
public class ProximitySearchService {

    /**
     * Finds the nearest N stations, with optional filtering.
     * Follows the algorithm in Section 2.3 of the requirements.
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

        // 1. & 2. Perform nearest-neighbour search in the KD-Tree (AC1)
        // This gets the N-nearest *unfiltered* candidates.
        List<StationDistance> allCandidates = stationTree.nearestN(lat, lon, N, visitedNodes);

        // 3. & 5. Apply filter (if any) and final sort (AC3, AC4)
        // This follows the pseudocode logic: search first, then filter.
        List<StationDistance> finalResults;

        String filterToApply = (timeZoneFilter == null || timeZoneFilter.trim().isEmpty()) ? null : timeZoneFilter.trim();

        if (filterToApply != null) {
            finalResults = allCandidates.stream()
                    .filter(sd -> {
                        // **ASSUMPTION**: TrainStation class has getTimeZoneGroup()
                        String tz = sd.getStation().getTimeZoneGroup();
                        return tz != null && tz.equalsIgnoreCase(filterToApply);
                    })
                    .collect(Collectors.toList());
            // List is already sorted by compareTo, filter preserves order.
        } else {
            finalResults = allCandidates;
        }

        // Note: AC4 says "Return exactly N". Section 8 clarifies:
        // "When fewer than N stations satisfy the filter, return all that match".
        // This implementation follows Section 8.

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