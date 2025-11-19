package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for the USEI10 use case:
 * <strong>Search train stations within a radius from a target coordinate,
 * order results, and compute summaries.</strong>
 *
 * <p>This controller loads all stations, performs a bounding-box preliminary filter,
 * computes accurate Haversine distances, sorts candidates, inserts them into
 * an {@link AVLTree}, and generates summaries by country and by city flag.</p>
 */
public class USEI10Controller {

    /** Loader responsible for reading the CSV dataset of train stations. */
    private final TrainStationLoader loader;

    /** Earth radius in kilometers, used by the Haversine formula. */
    private static final double EARTH_RADIUS_KM = 6371.0088;

    /**
     * Constructs a controller using a default {@link TrainStationLoader}.
     */
    public USEI10Controller() {
        this.loader = new TrainStationLoader();
    }

    /**
     * Performs a radius search around a target latitude/longitude.
     *
     * <p>Algorithm:</p>
     * <ol>
     *   <li>Load all stations from CSV</li>
     *   <li>Compute latitude/longitude bounding box</li>
     *   <li>Filter stations inside bounding box</li>
     *   <li>Compute Haversine distance for candidates</li>
     *   <li>Keep only stations ≤ radius</li>
     *   <li>Sort by distance ascending, name descending</li>
     *   <li>Insert into AVL tree using {@link DistanceNameKey}</li>
     *   <li>Compute summaries (country, city)</li>
     * </ol>
     *
     * @param csvPath   path to the CSV file
     * @param lat       target latitude
     * @param lon       target longitude
     * @param radiusKm  search radius in kilometers
     * @return a {@link Result} object containing the AVL tree and summaries
     */
    public Result radiusSearch(String csvPath, double lat, double lon, double radiusKm) {
        List<TrainStation> allStations = loader.load(csvPath);

        double latDegDiff = Math.toDegrees(radiusKm / EARTH_RADIUS_KM);
        double lonDegDiff = Math.toDegrees(radiusKm / (EARTH_RADIUS_KM * Math.cos(Math.toRadians(lat))));

        double latMin = lat - latDegDiff;
        double latMax = lat + latDegDiff;
        double lonMin = lon - lonDegDiff;
        double lonMax = lon + lonDegDiff;

        List<StationDistance> candidates = new ArrayList<>();

        for (TrainStation s : allStations) {
            double sLat = s.getLatitude();
            double sLon = s.getLongitude();

            if (sLat < latMin || sLat > latMax) continue;
            if (sLon < lonMin || sLon > lonMax) continue;

            double d = haversineKm(lat, lon, sLat, sLon);
            if (d <= radiusKm) {
                candidates.add(new StationDistance(s, d));
            }
        }

        candidates.sort((a, b) -> {
            int cmp = Double.compare(a.getDistanceKm(), b.getDistanceKm());
            if (cmp != 0) return cmp;
            return b.getStation().getStation().compareTo(a.getStation().getStation());
        });

        AVLTree<DistanceNameKey, TrainStation> tree = new AVLTree<>();
        long seq = 0;
        for (StationDistance sd : candidates) {
            DistanceNameKey key = new DistanceNameKey(sd.getDistanceKm(), sd.getStation().getStation(), seq++);
            tree.insert(key, sd.getStation());
        }

        Map<String, Long> byCountry = candidates.stream()
                .collect(Collectors.groupingBy(sd -> sd.getStation().getCountry(), Collectors.counting()));

        Map<Boolean, Long> byIsCity = candidates.stream()
                .collect(Collectors.groupingBy(sd -> sd.getStation().isCity(), Collectors.counting()));

        return new Result(tree, byCountry, byIsCity, allStations.size(), candidates.size());
    }

    /**
     * Container class returned by {@link USEI10Controller#radiusSearch}.
     * Holds the AVL tree of results and the computed summary statistics.
     */
    public static class Result {

        /** AVL tree containing matched stations, keyed by sorted composite distance key. */
        public final AVLTree<DistanceNameKey, TrainStation> tree;

        /** Summary: count stations by country. */
        public final Map<String, Long> countByCountry;

        /** Summary: count stations by whether they belong to a city. */
        public final Map<Boolean, Long> countByIsCity;

        /** Total number of stations loaded from the dataset. */
        public final int totalIndexedStations;

        /** Number of stations that matched the radius filter. */
        public final int matchedCount;

        /**
         * Constructs a result container.
         */
        public Result(AVLTree<DistanceNameKey, TrainStation> tree,
                      Map<String, Long> countByCountry,
                      Map<Boolean, Long> countByIsCity,
                      int totalIndexedStations,
                      int matchedCount) {
            this.tree = tree;
            this.countByCountry = countByCountry;
            this.countByIsCity = countByIsCity;
            this.totalIndexedStations = totalIndexedStations;
            this.matchedCount = matchedCount;
        }
    }

    /**
     * Computes geographic distance between two coordinates using the Haversine formula.
     *
     * @param lat1 latitude of point 1
     * @param lon1 longitude of point 1
     * @param lat2 latitude of point 2
     * @param lon2 longitude of point 2
     * @return distance in kilometers
     */
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double r = EARTH_RADIUS_KM;
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }
}
