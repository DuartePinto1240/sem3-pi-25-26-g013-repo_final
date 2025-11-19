package main.java.pt.ipp.isep.dei.util;

/**
 * Utility class for geographical calculations.
 * Encapsulates the Haversine distance formula as required by USEI09.
 */
public class GeoUtils {

    /** Earth radius in kilometers, as specified in USEI09 requirements. */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the great-circle distance between two geographical points
     * using the Haversine formula.
     *
     * @param lat1 Latitude of the first point (in decimal degrees)
     * @param lon1 Longitude of the first point (in decimal degrees)
     * @param lat2 Latitude of the second point (in decimal degrees)
     * @param lon2 Longitude of the second point (in decimal degrees)
     * @return The distance in kilometers (km)
     */
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate deltas
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula (Section 2.2)
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}