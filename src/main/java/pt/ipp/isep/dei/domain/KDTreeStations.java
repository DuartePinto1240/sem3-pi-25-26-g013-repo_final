package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A 2-dimensional KD-tree specialized for TrainStation objects.
 * <p>
 * This structure supports:
 * <ul>
 *     <li>Incremental construction by inserting stations one by one</li>
 *     <li>Recursive range search in latitude/longitude bounding boxes</li>
 *     <li>Optional filtering by isCity, isMainStation and country</li>
 * </ul>
 * The tree alternates splitting dimension at each level:
 * <ul>
 *     <li>Level 0,2,4,... split by longitude</li>
 *     <li>Level 1,3,5,... split by latitude</li>
 * </ul>
 * allowing efficient pruning of entire subtrees during spatial queries.
 */
public class KDTreeStations {

    /**
     * Node of the KD-tree, storing a TrainStation and references
     * to left and right subtrees.
     */
    private static class Node {
        TrainStation value;
        Node left;
        Node right;

        /**
         * Creates a new KD-tree node holding a TrainStation.
         *
         * @param value station to store in this node
         */
        Node(TrainStation value) {
            this.value = value;
        }
    }

    /** Root of the KD-tree. */
    private Node root;

    /**
     * Builds the KD-tree by inserting all stations from a given list.
     * <p>
     * The tree is constructed incrementally, inserting each station
     * according to alternating comparisons (lon → lat → lon → ...).
     * Existing content is discarded, and the tree is rebuilt from scratch.
     *
     * @param stations list of stations to insert
     */
    public void buildFromStations(List<TrainStation> stations) {
        root = null;
        if (stations == null) return;

        for (TrainStation s : stations) {
            if (s != null) {
                root = insert(root, s, true);
            }
        }
    }

    /**
     * Inserts a station into the KD-tree.
     *
     * @param current current node in recursion
     * @param station station to insert
     * @param divByLon true if this level splits by longitude, false for latitude
     * @return the updated subtree root
     */
    private Node insert(Node current, TrainStation station, boolean divByLon) {
        if (current == null) {
            return new Node(station);
        }

        double currLon = current.value.getLongitude();
        double currLat = current.value.getLatitude();
        double newLon  = station.getLongitude();
        double newLat  = station.getLatitude();

        int cmp;
        if (divByLon) {
            cmp = Double.compare(newLon, currLon);
        } else {
            cmp = Double.compare(newLat, currLat);
        }

        if (cmp == 0) {
            cmp = station.getStation().compareToIgnoreCase(current.value.getStation());
        }

        if (cmp < 0) {
            current.left = insert(current.left, station, !divByLon);
        } else {
            current.right = insert(current.right, station, !divByLon);
        }

        return current;
    }

    /**
     * Performs a range search on the KD-tree.
     * <p>
     * The algorithm returns all stations whose latitude and longitude fall
     * inside the rectangle:
     * <pre>
     *     lat ∈ [latMin, latMax]
     *     lon ∈ [lonMin, lonMax]
     * </pre>
     * and match the optional filters (isCity, isMainStation, country).
     *
     * @param latMin minimum latitude
     * @param latMax maximum latitude
     * @param lonMin minimum longitude
     * @param lonMax maximum longitude
     * @param isCity optional filter, or null for no filter
     * @param isMainStation optional filter, or null for no filter
     * @param country optional filter ("PT", "ES"), or null for ALL
     * @return list of matching TrainStation objects
     */
    public List<TrainStation> rangeSearch(double latMin, double latMax,
                                          double lonMin, double lonMax,
                                          Boolean isCity,
                                          Boolean isMainStation,
                                          String country) {

        List<TrainStation> result = new ArrayList<>();
        rangeSearch(root, latMin, latMax, lonMin, lonMax,
                isCity, isMainStation, country,
                true, result);
        return result;
    }

    /**
     * Recursive range-search helper with pruning.
     * <p>
     * Depending on the splitting dimension (longitude or latitude),
     * the method decides whether the left or right subtree can be skipped.
     *
     * @param node current tree node
     * @param latMin minimum latitude
     * @param latMax maximum latitude
     * @param lonMin minimum longitude
     * @param lonMax maximum longitude
     * @param isCity optional city filter
     * @param isMainStation optional main-station filter
     * @param country optional country filter
     * @param divByLon true if splitting by longitude at this level
     * @param result output list of all valid stations
     */
    private void rangeSearch(Node node,
                             double latMin, double latMax,
                             double lonMin, double lonMax,
                             Boolean isCity,
                             Boolean isMainStation,
                             String country,
                             boolean divByLon,
                             List<TrainStation> result) {

        if (node == null) {
            return;
        }

        TrainStation s = node.value;
        double lat = s.getLatitude();
        double lon = s.getLongitude();

        boolean insideRect =
                lat >= latMin && lat <= latMax &&
                        lon >= lonMin && lon <= lonMax;

        if (insideRect && matchesFilters(s, isCity, isMainStation, country)) {
            result.add(s);
        }

        if (divByLon) {
            if (lonMin <= lon) {
                rangeSearch(node.left, latMin, latMax, lonMin, lonMax,
                        isCity, isMainStation, country,
                        !divByLon, result);
            }
            if (lonMax >= lon) {
                rangeSearch(node.right, latMin, latMax, lonMin, lonMax,
                        isCity, isMainStation, country,
                        !divByLon, result);
            }
        } else {
            if (latMin <= lat) {
                rangeSearch(node.left, latMin, latMax, lonMin, lonMax,
                        isCity, isMainStation, country,
                        !divByLon, result);
            }
            if (latMax >= lat) {
                rangeSearch(node.right, latMin, latMax, lonMin, lonMax,
                        isCity, isMainStation, country,
                        !divByLon, result);
            }
        }
    }

    /**
     * Checks whether a given station satisfies the optional filters.
     *
     * @param s station being evaluated
     * @param isCity optional city filter
     * @param isMainStation optional main-station filter
     * @param country optional country filter
     * @return true if the station matches all active filters
     */
    private boolean matchesFilters(TrainStation s,
                                   Boolean isCity,
                                   Boolean isMainStation,
                                   String country) {

        if (isCity != null && s.isCity() != isCity) {
            return false;
        }
        if (isMainStation != null && s.isMainStation() != isMainStation) {
            return false;
        }
        if (country != null && !country.equalsIgnoreCase(s.getCountry())) {
            return false;
        }

        return true;
    }
}
