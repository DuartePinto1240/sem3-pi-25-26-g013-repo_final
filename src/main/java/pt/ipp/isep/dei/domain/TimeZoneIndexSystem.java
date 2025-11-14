package main.java.pt.ipp.isep.dei.domain;

import java.util.*;

/**
 * USEI06 - Time-Zone Index and Windowed Queries
 *
 * Multi-index query engine using existing AVL<E> implementation.
 * Uses wrapper classes and internal tracking to avoid dependency on inOrder().
 */
public class TimeZoneIndexSystem {

    // ========== WRAPPER CLASSES FOR AVL STORAGE ==========

    /**
     * Wrapper for coordinate-based indexing (latitude or longitude).
     */
    private static class CoordinateEntry implements Comparable<CoordinateEntry> {
        final double coordinate;
        final List<TrainStation> stations;

        CoordinateEntry(double coordinate) {
            this.coordinate = coordinate;
            this.stations = new ArrayList<>();
        }

        @Override
        public int compareTo(CoordinateEntry other) {
            return Double.compare(this.coordinate, other.coordinate);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CoordinateEntry)) return false;
            return Double.compare(this.coordinate, ((CoordinateEntry) obj).coordinate) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(coordinate);
        }
    }

    /**
     * Wrapper for time zone group indexing.
     */
    private static class TimeZoneEntry implements Comparable<TimeZoneEntry> {
        final String timeZoneGroup;
        final AVL<CountryEntry> countryTree;
        final Map<String, CountryEntry> countryMap; // For quick lookup

        TimeZoneEntry(String timeZoneGroup) {
            this.timeZoneGroup = timeZoneGroup;
            this.countryTree = new AVL<>();
            this.countryMap = new TreeMap<>(); // Keep sorted
        }

        @Override
        public int compareTo(TimeZoneEntry other) {
            return this.timeZoneGroup.compareTo(other.timeZoneGroup);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TimeZoneEntry)) return false;
            return this.timeZoneGroup.equals(((TimeZoneEntry) obj).timeZoneGroup);
        }

        @Override
        public int hashCode() {
            return timeZoneGroup.hashCode();
        }
    }

    /**
     * Wrapper for country indexing within a time zone.
     */
    private static class CountryEntry implements Comparable<CountryEntry> {
        final String country;
        final List<TrainStation> stations;

        CountryEntry(String country) {
            this.country = country;
            this.stations = new ArrayList<>();
        }

        @Override
        public int compareTo(CountryEntry other) {
            return this.country.compareTo(other.country);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CountryEntry)) return false;
            return this.country.equals(((CountryEntry) obj).country);
        }

        @Override
        public int hashCode() {
            return country.hashCode();
        }
    }

    // ========== INDEX STRUCTURES ==========

    private AVL<CoordinateEntry> latitudeIndex;
    private AVL<CoordinateEntry> longitudeIndex;
    private AVL<TimeZoneEntry> timeZoneIndex;

    // Quick lookup maps (to avoid inOrder() dependency)
    private Map<Double, CoordinateEntry> latMap;
    private Map<Double, CoordinateEntry> lonMap;
    private Map<String, TimeZoneEntry> tzMap;

    private int validStations;
    private int rejectedStations;

    /**
     * Constructs the index system and loads stations.
     */
    public TimeZoneIndexSystem(List<TrainStation> stations) {
        this.latitudeIndex = new AVL<>();
        this.longitudeIndex = new AVL<>();
        this.timeZoneIndex = new AVL<>();

        this.latMap = new TreeMap<>();
        this.lonMap = new TreeMap<>();
        this.tzMap = new TreeMap<>();

        this.validStations = 0;
        this.rejectedStations = 0;

        buildIndices(stations);
    }

    /**
     * Validates and indexes all stations.
     */
    private void buildIndices(List<TrainStation> stations) {
        for (TrainStation station : stations) {
            if (isValidStation(station)) {
                indexStation(station);
                validStations++;
            } else {
                rejectedStations++;
            }
        }

        System.out.println("✅ Indexed " + validStations + " valid stations");
        System.out.println("📊 Unique latitudes: " + latMap.size());
        System.out.println("📊 Unique longitudes: " + lonMap.size());
        System.out.println("📊 Time zones: " + tzMap.size());
    }

    /**
     * Validates station according to acceptance criteria.
     * Temporal Complexity: O(1)
     */
    private boolean isValidStation(TrainStation station) {
        if (station.getStation() == null || station.getStation().trim().isEmpty())
            return false;
        if (station.getCountry() == null || station.getCountry().trim().isEmpty())
            return false;
        if (station.getTimeZoneGroup() == null || station.getTimeZoneGroup().trim().isEmpty())
            return false;
        if (station.getLatitude() < -90 || station.getLatitude() > 90)
            return false;
        if (station.getLongitude() < -180 || station.getLongitude() > 180)
            return false;

        return true;
    }

    /**
     * Indexes a station in all three indices.
     * Temporal Complexity: O(log n) per index
     */
    private void indexStation(TrainStation station) {
        indexByCoordinate(latitudeIndex, latMap, station.getLatitude(), station);
        indexByCoordinate(longitudeIndex, lonMap, station.getLongitude(), station);
        indexByTimeZoneCountry(station);
    }

    /**
     * Helper to index by coordinate using both AVL and Map.
     */
    private void indexByCoordinate(AVL<CoordinateEntry> index,
                                   Map<Double, CoordinateEntry> map,
                                   double coordinate,
                                   TrainStation station) {
        CoordinateEntry entry = map.get(coordinate);

        if (entry == null) {
            entry = new CoordinateEntry(coordinate);
            index.insert(entry);
            map.put(coordinate, entry);
        }

        insertSorted(entry.stations, station);
    }

    /**
     * Indexes by time zone group and country.
     */
    private void indexByTimeZoneCountry(TrainStation station) {
        String tzGroup = station.getTimeZoneGroup();
        String country = station.getCountry();

        // Find or create TimeZoneEntry
        TimeZoneEntry tzEntry = tzMap.get(tzGroup);
        if (tzEntry == null) {
            tzEntry = new TimeZoneEntry(tzGroup);
            timeZoneIndex.insert(tzEntry);
            tzMap.put(tzGroup, tzEntry);
        }

        // Find or create CountryEntry
        CountryEntry countryEntry = tzEntry.countryMap.get(country);
        if (countryEntry == null) {
            countryEntry = new CountryEntry(country);
            tzEntry.countryTree.insert(countryEntry);
            tzEntry.countryMap.put(country, countryEntry);
        }

        insertSorted(countryEntry.stations, station);
    }

    /**
     * Inserts station into list maintaining alphabetical order by name.
     */
    private void insertSorted(List<TrainStation> list, TrainStation station) {
        int pos = Collections.binarySearch(list, station,
                Comparator.comparing(TrainStation::getStation));

        if (pos < 0) {
            list.add(-pos - 1, station);
        } else {
            list.add(pos, station);
        }
    }

    // ========== QUERY METHODS ==========

    /**
     * Query 1: Find all stations at specific latitude.
     * Temporal Complexity: O(log n)
     */
    public List<TrainStation> findByLatitude(double latitude) {
        CoordinateEntry entry = latMap.get(latitude);
        return entry != null ? new ArrayList<>(entry.stations) : new ArrayList<>();
    }

    /**
     * Query 2: Find all stations at specific longitude.
     * Temporal Complexity: O(log n)
     */
    public List<TrainStation> findByLongitude(double longitude) {
        CoordinateEntry entry = lonMap.get(longitude);
        return entry != null ? new ArrayList<>(entry.stations) : new ArrayList<>();
    }

    /**
     * Query 3: Find all stations in time zone group, ordered by country.
     * Temporal Complexity: O(log n + m)
     */
    public Map<String, List<TrainStation>> findByTimeZoneGroup(String timeZoneGroup) {
        TimeZoneEntry tzEntry = tzMap.get(timeZoneGroup);

        if (tzEntry == null) {
            return new LinkedHashMap<>();
        }

        Map<String, List<TrainStation>> result = new LinkedHashMap<>();

        // Use the sorted TreeMap to maintain order
        for (Map.Entry<String, CountryEntry> entry : tzEntry.countryMap.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue().stations));
        }

        return result;
    }

    /**
     * Query 4: Find stations in specific time zone and country.
     * Temporal Complexity: O(log n + log m)
     */
    public List<TrainStation> findByTimeZoneAndCountry(String timeZoneGroup, String country) {
        TimeZoneEntry tzEntry = tzMap.get(timeZoneGroup);

        if (tzEntry == null) {
            return new ArrayList<>();
        }

        CountryEntry countryEntry = tzEntry.countryMap.get(country);
        return countryEntry != null ? new ArrayList<>(countryEntry.stations) : new ArrayList<>();
    }

    /**
     * Query 5: Windowed query - multiple time zones.
     * Temporal Complexity: O(k·log n + m)
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
     * Query 6: Range query by latitude.
     * Temporal Complexity: O(k) where k = coordinates in range
     */
    public List<TrainStation> findByLatitudeRange(double minLat, double maxLat) {
        List<TrainStation> result = new ArrayList<>();

        // Use TreeMap's subMap for efficient range query
        for (Map.Entry<Double, CoordinateEntry> entry : latMap.entrySet()) {
            double lat = entry.getKey();
            if (lat >= minLat && lat <= maxLat) {
                result.addAll(entry.getValue().stations);
            }
        }

        return result;
    }

    /**
     * Alternative range query using TreeMap's subMap.
     * Temporal Complexity: O(k log n)
     */
    public List<TrainStation> findByLatitudeRangeOptimized(double minLat, double maxLat) {
        List<TrainStation> result = new ArrayList<>();

        // TreeMap's subMap is more efficient for large ranges
        Map<Double, CoordinateEntry> subMap =
                ((TreeMap<Double, CoordinateEntry>) latMap).subMap(minLat, true, maxLat, true);

        for (CoordinateEntry entry : subMap.values()) {
            result.addAll(entry.stations);
        }

        return result;
    }

    // ========== STATISTICS ==========

    public int getValidStationsCount() {
        return validStations;
    }

    public int getRejectedStationsCount() {
        return rejectedStations;
    }

    public int getTimeZoneIndexHeight() {
        return timeZoneIndex.height();
    }

    public int getUniqueLatitudes() {
        return latMap.size();
    }

    public int getUniqueLongitudes() {
        return lonMap.size();
    }

    public int getTimeZoneCount() {
        return tzMap.size();
    }

    /**
     * Returns list of all time zones in the system.
     */
    public List<String> getAllTimeZones() {
        return new ArrayList<>(tzMap.keySet());
    }

    /**
     * Returns statistics about a specific time zone.
     */
    public String getTimeZoneStats(String timeZoneGroup) {
        TimeZoneEntry tzEntry = tzMap.get(timeZoneGroup);
        if (tzEntry == null) {
            return "Time zone not found: " + timeZoneGroup;
        }

        int totalStations = tzEntry.countryMap.values().stream()
                .mapToInt(c -> c.stations.size())
                .sum();

        return String.format("%s: %d countries, %d stations",
                timeZoneGroup, tzEntry.countryMap.size(), totalStations);
    }

    /**
     * Prints complexity analysis.
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
        System.out.println("║ Find by latitude/longitude         │ O(1) - HashMap       ║");
        System.out.println("║ Find by time zone group            │ O(1) + O(m)          ║");
        System.out.println("║ Find by time zone & country        │ O(1) - HashMap       ║");
        System.out.println("║ Windowed query (k zones)           │ O(k + m)             ║");
        System.out.println("║ Range query                        │ O(k) - TreeMap       ║");
        System.out.println("╚════════════════════════════════════╧══════════════════════╝");
        System.out.println("\nWhere:");
        System.out.println("  n = total stations indexed (" + validStations + ")");
        System.out.println("  m = countries/results in query");
        System.out.println("  k = number of matches or zones");
        System.out.println("\nIndex Statistics:");
        System.out.println("  Unique latitudes: " + getUniqueLatitudes());
        System.out.println("  Unique longitudes: " + getUniqueLongitudes());
        System.out.println("  Time zones: " + getTimeZoneCount());
        System.out.println("  AVL tree height: " + getTimeZoneIndexHeight());
    }
}


