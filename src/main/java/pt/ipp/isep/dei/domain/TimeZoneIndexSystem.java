package main.java.pt.ipp.isep.dei.domain;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * USEI06 - Time-Zone Index and Windowed Queries (ULTRA-OPTIMIZED)
 *
 * Extreme performance optimizations:
 * - Parallel processing for indexing
 * - Lazy AVL tree construction
 * - Pre-sized collections
 * - Minimal object allocation
 * - Optimized sorting with parallel streams
 *
 * Target: <1 second for 64k stations
 */
public class TimeZoneIndexSystem {

    // ========== LIGHTWEIGHT WRAPPERS ==========

    private static class CoordinateEntry implements Comparable<CoordinateEntry> {
        final double coordinate;
        List<TrainStation> stations; // Not final for lazy init

        CoordinateEntry(double coordinate) {
            this.coordinate = coordinate;
        }

        @Override
        public int compareTo(CoordinateEntry other) {
            return Double.compare(this.coordinate, other.coordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CoordinateEntry &&
                    Double.compare(this.coordinate, ((CoordinateEntry) obj).coordinate) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(coordinate);
        }
    }

    private static class TimeZoneEntry implements Comparable<TimeZoneEntry> {
        final String timeZoneGroup;
        final ConcurrentHashMap<String, List<TrainStation>> countryMap;

        TimeZoneEntry(String timeZoneGroup) {
            this.timeZoneGroup = timeZoneGroup;
            this.countryMap = new ConcurrentHashMap<>();
        }

        @Override
        public int compareTo(TimeZoneEntry other) {
            return this.timeZoneGroup.compareTo(other.timeZoneGroup);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TimeZoneEntry &&
                    this.timeZoneGroup.equals(((TimeZoneEntry) obj).timeZoneGroup);
        }

        @Override
        public int hashCode() {
            return timeZoneGroup.hashCode();
        }
    }

    // ========== INDEX STRUCTURES ==========

    private final ConcurrentHashMap<Double, List<TrainStation>> latMap;
    private final ConcurrentHashMap<Double, List<TrainStation>> lonMap;
    private final ConcurrentHashMap<String, TimeZoneEntry> tzMap;

    // Lazy-loaded AVL (only built when needed)
    private volatile AVL<CoordinateEntry> latitudeIndex;
    private volatile AVL<TimeZoneEntry> timeZoneIndex;

    private final int validStations;
    private final int rejectedStations;

    // Comparator reused (avoid creating multiple times)
    private static final Comparator<TrainStation> BY_NAME =
            Comparator.comparing(TrainStation::getStation);

    /**
     * Ultra-fast constructor with parallel processing.
     */
    public TimeZoneIndexSystem(List<TrainStation> stations) {
        int estimatedCoords = (int)(stations.size() * 0.25);
        int estimatedTZ = 30;

        this.latMap = new ConcurrentHashMap<>(estimatedCoords);
        this.lonMap = new ConcurrentHashMap<>(estimatedCoords);
        this.tzMap = new ConcurrentHashMap<>(estimatedTZ);

        // Use parallel stream for validation and indexing
        long[] counts = buildIndicesParallel(stations);
        this.validStations = (int) counts[0];
        this.rejectedStations = (int) counts[1];

        // Sort in parallel after all insertions
        sortAllListsParallel();

        System.out.println("✅ Indexed " + validStations + " valid stations");
        System.out.println("📊 Unique latitudes: " + latMap.size());
        System.out.println("📊 Unique longitudes: " + lonMap.size());
        System.out.println("📊 Time zones: " + tzMap.size());
    }

    /**
     * PARALLEL indexing using streams - MUCH faster on multi-core CPUs.
     * Returns [validCount, rejectedCount]
     */
    private long[] buildIndicesParallel(List<TrainStation> stations) {
        // Partition into valid and invalid in parallel
        Map<Boolean, List<TrainStation>> partitioned = stations.parallelStream()
                .collect(Collectors.partitioningBy(this::isValidStation));

        List<TrainStation> valid = partitioned.get(true);
        int rejected = partitioned.get(false).size();

        // Index valid stations in parallel
        valid.parallelStream().forEach(this::indexStationFast);

        return new long[] { valid.size(), rejected };
    }

    /**
     * Fast validation with early returns.
     */
    private boolean isValidStation(TrainStation s) {
        return s.getStation() != null && !s.getStation().trim().isEmpty()
                && s.getCountry() != null && !s.getCountry().trim().isEmpty()
                && s.getTimeZoneGroup() != null && !s.getTimeZoneGroup().trim().isEmpty()
                && s.getLatitude() >= -90 && s.getLatitude() <= 90
                && s.getLongitude() >= -180 && s.getLongitude() <= 180;
    }

    /**
     * Thread-safe fast indexing using ConcurrentHashMap.
     */
    private void indexStationFast(TrainStation station) {
        // Latitude index
        latMap.computeIfAbsent(station.getLatitude(), k -> new CopyOnWriteArrayList<>())
                .add(station);

        // Longitude index
        lonMap.computeIfAbsent(station.getLongitude(), k -> new CopyOnWriteArrayList<>())
                .add(station);

        // TimeZone/Country index
        tzMap.computeIfAbsent(station.getTimeZoneGroup(), TimeZoneEntry::new)
                .countryMap.computeIfAbsent(station.getCountry(), k -> new CopyOnWriteArrayList<>())
                .add(station);
    }

    /**
     * PARALLEL sorting of all lists - uses all CPU cores.
     */
    private void sortAllListsParallel() {
        // Collect all lists to sort
        List<List<TrainStation>> allLists = new ArrayList<>();

        allLists.addAll(latMap.values());
        allLists.addAll(lonMap.values());

        for (TimeZoneEntry tz : tzMap.values()) {
            allLists.addAll(tz.countryMap.values());
        }

        // Sort in parallel - HUGE speedup on multi-core
        allLists.parallelStream()
                .filter(list -> list.size() > 1)
                .forEach(list -> list.sort(BY_NAME));
    }

    /**
     * Lazy AVL construction - only build when height() is called.
     */
    private void ensureLatitudeIndexBuilt() {
        if (latitudeIndex == null) {
            synchronized (this) {
                if (latitudeIndex == null) {
                    AVL<CoordinateEntry> temp = new AVL<>();
                    latMap.entrySet().parallelStream()
                            .map(e -> {
                                CoordinateEntry entry = new CoordinateEntry(e.getKey());
                                entry.stations = e.getValue();
                                return entry;
                            })
                            .forEach(temp::insert);
                    latitudeIndex = temp;
                }
            }
        }
    }

    private void ensureTimeZoneIndexBuilt() {
        if (timeZoneIndex == null) {
            synchronized (this) {
                if (timeZoneIndex == null) {
                    AVL<TimeZoneEntry> temp = new AVL<>();
                    tzMap.values().forEach(temp::insert);
                    timeZoneIndex = temp;
                }
            }
        }
    }

    // ========== QUERY METHODS (ALL OPTIMIZED) ==========

    public List<TrainStation> findByLatitude(double latitude) {
        List<TrainStation> result = latMap.get(latitude);
        return result != null ? new ArrayList<>(result) : Collections.emptyList();
    }

    public List<TrainStation> findByLongitude(double longitude) {
        List<TrainStation> result = lonMap.get(longitude);
        return result != null ? new ArrayList<>(result) : Collections.emptyList();
    }

    public Map<String, List<TrainStation>> findByTimeZoneGroup(String timeZoneGroup) {
        TimeZoneEntry tzEntry = tzMap.get(timeZoneGroup);

        if (tzEntry == null) {
            return Collections.emptyMap();
        }

        // TreeMap auto-sorts by country
        Map<String, List<TrainStation>> result = new TreeMap<>();
        tzEntry.countryMap.forEach((country, stations) ->
                result.put(country, new ArrayList<>(stations))
        );

        return result;
    }

    public List<TrainStation> findByTimeZoneAndCountry(String timeZoneGroup, String country) {
        TimeZoneEntry tzEntry = tzMap.get(timeZoneGroup);

        if (tzEntry == null) {
            return Collections.emptyList();
        }

        List<TrainStation> result = tzEntry.countryMap.get(country);
        return result != null ? new ArrayList<>(result) : Collections.emptyList();
    }

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
     * OPTIMIZED range query using parallel stream.
     */
    public List<TrainStation> findByLatitudeRange(double minLat, double maxLat) {
        return latMap.entrySet().parallelStream()
                .filter(e -> e.getKey() >= minLat && e.getKey() <= maxLat)
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
    }

    // ========== STATISTICS ==========

    public int getValidStationsCount() {
        return validStations;
    }

    public int getRejectedStationsCount() {
        return rejectedStations;
    }

    public int getTimeZoneIndexHeight() {
        ensureTimeZoneIndexBuilt();
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

    public List<String> getAllTimeZones() {
        return new ArrayList<>(tzMap.keySet());
    }

    public String getTimeZoneStats(String timeZoneGroup) {
        TimeZoneEntry tzEntry = tzMap.get(timeZoneGroup);
        if (tzEntry == null) {
            return "Time zone not found: " + timeZoneGroup;
        }

        int totalStations = tzEntry.countryMap.values().parallelStream()
                .mapToInt(List::size)
                .sum();

        return String.format("%s: %d countries, %d stations",
                timeZoneGroup, tzEntry.countryMap.size(), totalStations);
    }

    public void printComplexityAnalysis() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║              TEMPORAL COMPLEXITY ANALYSIS (ULTRA-OPTIMIZED)                  ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Operation                     │ Best Case    │ Average Case │ Worst Case     ║");
        System.out.println("╟───────────────────────────────┼──────────────┼──────────────┼────────────────╢");
        System.out.println("║ Validation                    │ O(1)         │ O(1)         │ O(1)           ║");
        System.out.println("║ Build Index (sequential)      │ O(n)         │ O(n log n)   │ O(n log n)     ║");
        System.out.println("║ Build Index (parallel, p)     │ O(n/p)       │ O(n log n/p) │ O(n log n/p)   ║");
        System.out.println("║ Find by latitude/longitude    │ O(1)         │ O(1+k)       │ O(1+k)         ║");
        System.out.println("║ Find by time zone group       │ O(1)         │ O(m log m)   │ O(m log m+n)   ║");
        System.out.println("║ Find by TZ + country          │ O(1)         │ O(1+k)       │ O(1+k)         ║");
        System.out.println("║ Windowed query (w zones)      │ O(w)         │ O(w·m log m) │ O(w·(m log m+n))║");
        System.out.println("║ Range query (sequential)      │ O(c)         │ O(c+r·k)     │ O(c+n)         ║");
        System.out.println("║ Range query (parallel, p)     │ O(c/p)       │ O((c+r·k)/p) │ O((c+n)/p)     ║");
        System.out.println("║ Get AVL height (cached)       │ O(1)         │ O(1)         │ O(1)           ║");
        System.out.println("║ Get AVL height (1st call)     │ O(t log t)   │ O(t log t)   │ O(t log t)     ║");
        System.out.println("╚═══════════════════════════════╧══════════════╧══════════════╧════════════════╝");
        System.out.println("\nWhere:");
        System.out.println("  n = total stations (" + validStations + ")");
        System.out.println("  p = CPU cores (" + Runtime.getRuntime().availableProcessors() + ")");
        System.out.println("  m = countries/results");
        System.out.println("  k = matches or zones");
        System.out.println("\nIndex Statistics:");
        System.out.println("  Unique latitudes: " + getUniqueLatitudes());
        System.out.println("  Unique longitudes: " + getUniqueLongitudes());
        System.out.println("  Time zones: " + getTimeZoneCount());
        System.out.println("  AVL tree height: " + getTimeZoneIndexHeight() + " (lazy-loaded)");
    }
}