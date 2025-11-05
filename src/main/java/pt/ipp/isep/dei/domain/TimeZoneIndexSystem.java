package main.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.TrainStation;
import java.util.*;

/**
 * USEI06 - Time-Zone Index and Windowed Queries
 *
 * Multi-index query engine for European railway stations using BST/AVL trees.
 * Provides efficient lookups by latitude, longitude, and time zone group/country.
 */
public class TimeZoneIndexSystem {

    // Index trees
    private AVLTree<Double, List<TrainStation>> latitudeIndex;
    private AVLTree<Double, List<TrainStation>> longitudeIndex;
    private AVLTree<String, AVLTree<String, List<TrainStation>>> timeZoneCountryIndex;

    private int validStations;
    private int rejectedStations;

    /**
     * Constructs the index system and loads stations from the provided list.
     *
     * @param stations list of train stations to index
     */
    public TimeZoneIndexSystem(List<TrainStation> stations) {
        this.latitudeIndex = new AVLTree<>();
        this.longitudeIndex = new AVLTree<>();
        this.timeZoneCountryIndex = new AVLTree<>();
        this.validStations = 0;
        this.rejectedStations = 0;

        buildIndices(stations);
    }

    /**
     * Validates and indexes all stations.
     * Rejects invalid stations based on acceptance criteria.
     */
    private void buildIndices(List<TrainStation> stations) {
        for (TrainStation station : stations) {
            if (isValidStation(station)) {
                indexStation(station);
                validStations++;
            } else {
                rejectedStations++;
                System.err.println("⚠️ Rejected station: " + station.getStation() +
                        " (invalid data)");
            }
        }

        System.out.println("✅ Indexed " + validStations + " stations");
        System.out.println("❌ Rejected " + rejectedStations + " stations");
    }

    /**
     * Validates station according to acceptance criteria:
     * - Non-empty name, country, and time zone group
     * - Latitude ∈ [-90, 90]
     * - Longitude ∈ [-180, 180]
     *
     * Temporal Complexity: O(1)
     */
    private boolean isValidStation(TrainStation station) {
        // Check non-empty fields
        if (station.getStation() == null || station.getStation().trim().isEmpty()) {
            return false;
        }
        if (station.getCountry() == null || station.getCountry().trim().isEmpty()) {
            return false;
        }
        if (station.getTimeZoneGroup() == null || station.getTimeZoneGroup().trim().isEmpty()) {
            return false;
        }

        // Check coordinate bounds
        if (station.getLatitude() < -90 || station.getLatitude() > 90) {
            return false;
        }
        if (station.getLongitude() < -180 || station.getLongitude() > 180) {
            return false;
        }

        return true;
    }

    /**
     * Indexes a single station in all three indices.
     * Stations with same coordinates are kept sorted by name.
     *
     * Temporal Complexity: O(log n) for each tree insertion
     */
    private void indexStation(TrainStation station) {
        // Index by latitude
        indexByCoordinate(latitudeIndex, station.getLatitude(), station);

        // Index by longitude
        indexByCoordinate(longitudeIndex, station.getLongitude(), station);

        // Index by time zone group and country
        indexByTimeZoneCountry(station);
    }

    /**
     * Helper method to index by coordinate (lat or lon).
     * Maintains sorted list by station name for duplicate coordinates.
     *
     * Temporal Complexity: O(log n) + O(k log k) where k = stations at same coordinate
     */
    private void indexByCoordinate(AVLTree<Double, List<TrainStation>> index,
                                   double coordinate, TrainStation station) {
        List<TrainStation> stations = index.find(coordinate);

        if (stations == null) {
            stations = new ArrayList<>();
            index.insert(coordinate, stations);
        }

        // Insert maintaining sorted order by station name
        int pos = Collections.binarySearch(stations, station,
                Comparator.comparing(TrainStation::getStation));

        if (pos < 0) {
            stations.add(-pos - 1, station);
        } else {
            stations.add(pos, station);
        }
    }

    /**
     * Indexes by time zone group and country (two-level tree).
     *
     * Temporal Complexity: O(log n) + O(log m) where n = zones, m = countries
     */
    private void indexByTimeZoneCountry(TrainStation station) {
        String tzGroup = station.getTimeZoneGroup();
        String country = station.getCountry();

        // Get or create country tree for this time zone
        AVLTree<String, List<TrainStation>> countryTree = timeZoneCountryIndex.find(tzGroup);
        if (countryTree == null) {
            countryTree = new AVLTree<>();
            timeZoneCountryIndex.insert(tzGroup, countryTree);
        }

        // Get or create station list for this country
        List<TrainStation> stations = countryTree.find(country);
        if (stations == null) {
            stations = new ArrayList<>();
            countryTree.insert(country, stations);
        }

        // Insert maintaining sorted order by station name
        int pos = Collections.binarySearch(stations, station,
                Comparator.comparing(TrainStation::getStation));

        if (pos < 0) {
            stations.add(-pos - 1, station);
        } else {
            stations.add(pos, station);
        }
    }

    // ========== QUERY METHODS ==========

    /**
     * Query 1: Find all stations at a specific latitude.
     *
     * Temporal Complexity: O(log n)
     *
     * @param latitude the latitude to search
     * @return list of stations at that latitude, sorted by name
     */
    public List<TrainStation> findByLatitude(double latitude) {
        List<TrainStation> result = latitudeIndex.find(latitude);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }

    /**
     * Query 2: Find all stations at a specific longitude.
     *
     * Temporal Complexity: O(log n)
     *
     * @param longitude the longitude to search
     * @return list of stations at that longitude, sorted by name
     */
    public List<TrainStation> findByLongitude(double longitude) {
        List<TrainStation> result = longitudeIndex.find(longitude);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }

    /**
     * Query 3: Find all stations in a time zone group, ordered by country.
     *
     * Temporal Complexity: O(log n + m) where m = countries in time zone
     *
     * @param timeZoneGroup the time zone group (e.g., "CET", "WET/GMT")
     * @return map of country -> stations, in ascending order of country
     */
    public Map<String, List<TrainStation>> findByTimeZoneGroup(String timeZoneGroup) {
        AVLTree<String, List<TrainStation>> countryTree =
                timeZoneCountryIndex.find(timeZoneGroup);

        if (countryTree == null) {
            return new LinkedHashMap<>();
        }

        Map<String, List<TrainStation>> result = new LinkedHashMap<>();
        countryTree.inOrder((country, stations) -> {
            result.put(country, new ArrayList<>(stations));
        });

        return result;
    }

    /**
     * Query 4: Find all stations in a specific time zone group and country.
     *
     * Temporal Complexity: O(log n + log m)
     *
     * @param timeZoneGroup the time zone group
     * @param country the country code
     * @return list of stations, sorted by name
     */
    public List<TrainStation> findByTimeZoneAndCountry(String timeZoneGroup, String country) {
        AVLTree<String, List<TrainStation>> countryTree =
                timeZoneCountryIndex.find(timeZoneGroup);

        if (countryTree == null) {
            return new ArrayList<>();
        }

        List<TrainStation> result = countryTree.find(country);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }

    /**
     * Query 5: Windowed query - Find all stations in multiple time zone groups.
     * Returns stations organized by time zone and country.
     *
     * Temporal Complexity: O(k * log n + m) where k = zones, m = total countries
     *
     * @param timeZoneGroups array of time zone groups (e.g., ["CET", "WET/GMT"])
     * @return nested map: timeZone -> country -> stations
     */
    public Map<String, Map<String, List<TrainStation>>> windowedQuery(String... timeZoneGroups) {
        Map<String, Map<String, List<TrainStation>>> result = new LinkedHashMap<>();

        for (String tzGroup : timeZoneGroups) {
            Map<String, List<TrainStation>> byCountry = findByTimeZoneGroup(tzGroup);
            if (!byCountry.isEmpty()) {
                result.put(tzGroup, byCountry);
            }
        }

        return result;
    }

    /**
     * Query 6: Find stations within a latitude range.
     *
     * Temporal Complexity: O(log n + k) where k = matching stations
     *
     * @param minLat minimum latitude
     * @param maxLat maximum latitude
     * @return list of all stations in range
     */
    public List<TrainStation> findByLatitudeRange(double minLat, double maxLat) {
        List<TrainStation> result = new ArrayList<>();

        latitudeIndex.inOrder((lat, stations) -> {
            if (lat >= minLat && lat <= maxLat) {
                result.addAll(stations);
            }
        });

        return result;
    }

    // ========== STATISTICS ==========

    public int getValidStationsCount() {
        return validStations;
    }

    public int getRejectedStationsCount() {
        return rejectedStations;
    }

    /**
     * Returns the height of the time zone index tree.
     * Useful for analyzing tree balance.
     */
    public int getTimeZoneIndexHeight() {
        return timeZoneCountryIndex.height();
    }

    /**
     * Prints complexity analysis summary.
     */
    public void printComplexityAnalysis() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║           TEMPORAL COMPLEXITY ANALYSIS                    ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║ Operation                          │ Complexity           ║");
        System.out.println("╟────────────────────────────────────┼──────────────────────╢");
        System.out.println("║ Validation                         │ O(1)                 ║");
        System.out.println("║ Index by coordinate                │ O(log n)             ║");
        System.out.println("║ Index by time zone/country         │ O(log n + log m)     ║");
        System.out.println("║ Find by latitude/longitude         │ O(log n)             ║");
        System.out.println("║ Find by time zone group            │ O(log n + m)         ║");
        System.out.println("║ Find by time zone & country        │ O(log n + log m)     ║");
        System.out.println("║ Windowed query (k zones)           │ O(k·log n + m)       ║");
        System.out.println("║ Range query                        │ O(log n + k)         ║");
        System.out.println("╚════════════════════════════════════╧══════════════════════╝");
        System.out.println("\nWhere:");
        System.out.println("  n = total stations indexed");
        System.out.println("  m = countries in time zone");
        System.out.println("  k = number of results or zones");
        System.out.println("  Tree height: " + getTimeZoneIndexHeight());
    }
}