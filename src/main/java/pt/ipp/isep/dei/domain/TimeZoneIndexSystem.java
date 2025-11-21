package main.java.pt.ipp.isep.dei.domain;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * TimeZoneIndexSystem refatorado com AVL thread-safe para paralelismo.
 */
public class TimeZoneIndexSystem {

    // ========== WRAPPERS ==========

    public static class CoordinateEntry implements Comparable<CoordinateEntry> {
        final double coordinate;
        final List<TrainStation> stations = new ArrayList<>();

        public CoordinateEntry(double coordinate) {
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

    public static class TimeZoneEntry implements Comparable<TimeZoneEntry> {
        final String timeZoneGroup;
        final Map<String, List<TrainStation>> countryMap = new HashMap<>();

        public TimeZoneEntry(String timeZoneGroup) {
            this.timeZoneGroup = timeZoneGroup;
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

    // ========== AVL EXTENSIONS ==========

    /**
     * AVL para coordenadas (latitude/longitude) thread-safe
     */
    public static class CoordinateAVL extends AVL<CoordinateEntry> {
        public CoordinateAVL() { super(); }

        public CoordinateEntry getOrCreate(double coord) {
            CoordinateEntry probe = new CoordinateEntry(coord);

            // Tentativa rápida sem lock
            Node<CoordinateEntry> found = find(root(), probe);
            if (found != null) return found.getElement();

            // Inserção segura
            synchronized (this) {
                found = find(root(), probe);
                if (found != null) return found.getElement();

                insert(probe);
                found = find(root(), probe);
                if (found == null) throw new IllegalStateException("Failed to insert CoordinateEntry: " + coord);
            }

            return found.getElement();
        }

        public List<CoordinateEntry> inOrderEntries() {
            List<CoordinateEntry> list = new ArrayList<>();
            for (CoordinateEntry e : inOrder()) list.add(e);
            return list;
        }

        public List<TrainStation> range(double min, double max) {
            List<TrainStation> result = new ArrayList<>();
            for (CoordinateEntry e : inOrderEntries()) {
                if (e.coordinate >= min && e.coordinate <= max) result.addAll(e.stations);
            }
            return result;
        }
    }

    /**
     * AVL para time zones thread-safe
     */
    public static class TimeZoneAVL extends AVL<TimeZoneEntry> {
        public TimeZoneAVL() { super(); }

        public TimeZoneEntry getOrCreate(String tzGroup) {
            TimeZoneEntry probe = new TimeZoneEntry(tzGroup);

            Node<TimeZoneEntry> found = find(root(), probe);
            if (found != null) return found.getElement();

            synchronized (this) {
                found = find(root(), probe);
                if (found != null) return found.getElement();

                insert(probe);
                found = find(root(), probe);
                if (found == null) throw new IllegalStateException("Failed to insert TimeZoneEntry: " + tzGroup);
            }

            return found.getElement();
        }

        public List<TimeZoneEntry> inOrderEntries() {
            List<TimeZoneEntry> list = new ArrayList<>();
            for (TimeZoneEntry e : inOrder()) list.add(e);
            return list;
        }
    }

    // ========== INDICES ==========

    private final CoordinateAVL latitudeIndex = new CoordinateAVL();
    private final CoordinateAVL longitudeIndex = new CoordinateAVL();
    private final TimeZoneAVL timeZoneIndex = new TimeZoneAVL();

    private final int validStations;
    private final int rejectedStations;

    private static final Comparator<TrainStation> BY_NAME = Comparator.comparing(TrainStation::getStation);

    public TimeZoneIndexSystem(List<TrainStation> stations) {
        long[] counts = buildIndicesParallel(stations);
        this.validStations = (int) counts[0];
        this.rejectedStations = (int) counts[1];

        sortAllListsParallel();

        System.out.println("✅ Indexed " + validStations + " valid stations");
        System.out.println("📊 Unique latitudes: " + getUniqueLatitudes());
        System.out.println("📊 Unique longitudes: " + getUniqueLongitudes());
        System.out.println("📊 Time zones: " + getTimeZoneCount());
    }

    private long[] buildIndicesParallel(List<TrainStation> stations) {
        Map<Boolean, List<TrainStation>> partitioned = stations.parallelStream()
                .collect(Collectors.partitioningBy(this::isValidStation));

        List<TrainStation> valid = partitioned.get(true);
        int rejected = partitioned.get(false).size();

        valid.parallelStream().forEach(this::indexStationFast);

        return new long[]{valid.size(), rejected};
    }

    private boolean isValidStation(TrainStation s) {
        return s.getStation() != null && !s.getStation().trim().isEmpty()
                && s.getCountry() != null && !s.getCountry().trim().isEmpty()
                && s.getTimeZoneGroup() != null && !s.getTimeZoneGroup().trim().isEmpty()
                && !Double.isNaN(s.getLatitude()) && !Double.isNaN(s.getLongitude())
                && !Double.isInfinite(s.getLatitude()) && !Double.isInfinite(s.getLongitude())
                && s.getLatitude() >= -90 && s.getLatitude() <= 90
                && s.getLongitude() >= -180 && s.getLongitude() <= 180;
    }

    private void indexStationFast(TrainStation station) {
        // Latitude
        CoordinateEntry latEntry = latitudeIndex.getOrCreate(station.getLatitude());
        synchronized (latEntry) { latEntry.stations.add(station); }

        // Longitude
        CoordinateEntry lonEntry = longitudeIndex.getOrCreate(station.getLongitude());
        synchronized (lonEntry) { lonEntry.stations.add(station); }

        // TimeZone
        TimeZoneEntry tzEntry = timeZoneIndex.getOrCreate(station.getTimeZoneGroup());
        synchronized (tzEntry) {
            List<TrainStation> list = tzEntry.countryMap.computeIfAbsent(
                    station.getCountry(), k -> new CopyOnWriteArrayList<>());
            list.add(station);
        }
    }

    private void sortAllListsParallel() {
        List<List<TrainStation>> allLists = new ArrayList<>();

        for (CoordinateEntry ce : latitudeIndex.inOrderEntries()) allLists.add(ce.stations);
        for (CoordinateEntry ce : longitudeIndex.inOrderEntries()) allLists.add(ce.stations);
        for (TimeZoneEntry tze : timeZoneIndex.inOrderEntries()) allLists.addAll(tze.countryMap.values());

        allLists.parallelStream()
                .filter(list -> list.size() > 1)
                .forEach(list -> list.sort(BY_NAME));
    }

    // ========== QUERY METHODS ==========

    public List<TrainStation> findByLatitude(double latitude) {
        CoordinateEntry e = latitudeIndex.getOrCreate(latitude);
        synchronized (e) { return new ArrayList<>(e.stations); }
    }

    public List<TrainStation> findByLongitude(double longitude) {
        CoordinateEntry e = longitudeIndex.getOrCreate(longitude);
        synchronized (e) { return new ArrayList<>(e.stations); }
    }

    public Map<String, List<TrainStation>> findByTimeZoneGroup(String timeZoneGroup) {
        TimeZoneEntry tz = timeZoneIndex.getOrCreate(timeZoneGroup);
        Map<String, List<TrainStation>> result = new TreeMap<>();
        synchronized (tz) {
            tz.countryMap.forEach((country, stations) -> result.put(country, new ArrayList<>(stations)));
        }
        return result;
    }

    public List<TrainStation> findByTimeZoneAndCountry(String timeZoneGroup, String country) {
        TimeZoneEntry tz = timeZoneIndex.getOrCreate(timeZoneGroup);
        synchronized (tz) {
            List<TrainStation> result = tz.countryMap.get(country);
            return result != null ? new ArrayList<>(result) : Collections.emptyList();
        }
    }

    public Map<String, Map<String, List<TrainStation>>> windowedQuery(String... timeZoneGroups) {
        Map<String, Map<String, List<TrainStation>>> result = new LinkedHashMap<>();
        for (String tz : timeZoneGroups) {
            Map<String, List<TrainStation>> byCountry = findByTimeZoneGroup(tz);
            if (!byCountry.isEmpty()) result.put(tz, byCountry);
        }
        return result;
    }

    public List<TrainStation> findByLatitudeRange(double minLat, double maxLat) {
        return latitudeIndex.range(minLat, maxLat);
    }

    // ========== STATS ==========

    public int getValidStationsCount() { return validStations; }
    public int getRejectedStationsCount() { return rejectedStations; }
    public int getTimeZoneIndexHeight() { return timeZoneIndex.height(); }
    public int getUniqueLatitudes() { return latitudeIndex.size(); }
    public int getUniqueLongitudes() { return longitudeIndex.size(); }
    public int getTimeZoneCount() { return timeZoneIndex.size(); }
    public void printComplexityAnalysis() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║              TEMPORAL COMPLEXITY ANALYSIS (REFATORADO p/ AVL DA PROFESSORA)  ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Operation                     │ Best Case    │ Average Case │ Worst Case     ║");
        System.out.println("╟───────────────────────────────┼──────────────┼──────────────┼────────────────╢");
        System.out.println("║ Validation                    │ O(1)         │ O(1)         │ O(1)           ║");
        System.out.println("║ Build Index (sequential)      │ O(n log n)   │ O(n log n)   │ O(n log n)     ║");
        System.out.println("║ Build Index (parallel, p)     │ O(n log n / p)│ O(n log n / p)│ O(n log n / p) ║");
        System.out.println("║ Find by latitude/longitude    │ O(log u + k) │ O(log u + k) │ O(log u + k)   ║");
        System.out.println("║ Find by time zone group       │ O(log t + m) │ O(log t + m) │ O(log t + m)   ║");
        System.out.println("║ Find by TZ + country          │ O(log t + 1 + k) │ O(log t + k)│ O(log t + k)   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\nWhere:");
        System.out.println("  n = total stations (" + validStations + ")");
        System.out.println("  p = CPU cores (" + Runtime.getRuntime().availableProcessors() + ")");
        System.out.println("  u = unique coordinates");
        System.out.println("  t = time zone groups");
        System.out.println("  k = matches returned");

        System.out.println("\nIndex Statistics:");
        System.out.println("  Unique latitudes: " + getUniqueLatitudes());
        System.out.println("  Unique longitudes: " + getUniqueLongitudes());
        System.out.println("  Time zones: " + getTimeZoneCount());
        System.out.println("  AVL time zone height: " + getTimeZoneIndexHeight());
    }

}
