package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class USEI10Controller {
    private final TrainStationLoader loader;

    public USEI10Controller() {
        this.loader = new TrainStationLoader();
    }

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

    public static class Result {
        public final AVLTree<DistanceNameKey, TrainStation> tree;
        public final Map<String, Long> countByCountry;
        public final Map<Boolean, Long> countByIsCity;
        public final int totalIndexedStations;
        public final int matchedCount;

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


    private static final double EARTH_RADIUS_KM = 6371.0088;

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
