package main.java.pt.ipp.isep.dei.domain;

import java.util.Objects;

/**
 * Represents a railway station in the system.
 * <p>
 * Each {@code Station} is uniquely identified by an {@code id} and contains
 * basic information such as the name, latitude, and longitude. These coordinates
 * can be used for geographical calculations, visualizations, or map identification.
 * </p>
 *
 * <p>
 * Two stations are considered equal if they have the same identifier
 * ({@code id}), regardless of other attributes.
 * </p>
 */
public class Station {

    /** Unique identifier of the station (e.g., "ST001"). */
    private final String id;

    /** Human-readable name of the station (e.g., "Lisboa Oriente"). */
    private final String name;

    /** Geographical latitude of the station (may be 0.0 if unknown). */
    private final double latitude;

    /** Geographical longitude of the station (may be 0.0 if unknown). */
    private final double longitude;

    /**
     * Constructs a station without geographical coordinates.
     *
     * @param id unique identifier of the station, cannot be empty
     * @param name name of the station, cannot be empty
     * @throws IllegalArgumentException if {@code id} or {@code name} are null or empty
     */
    public Station(String id, String name) {
        this(id, name, 0.0, 0.0);
    }

    /**
     * Constructs a station with all details.
     *
     * @param id unique identifier of the station, cannot be null or empty
     * @param name name of the station, cannot be null or empty
     * @param latitude station latitude (decimal degrees)
     * @param longitude station longitude (decimal degrees)
     * @throws IllegalArgumentException if {@code id} or {@code name} are invalid
     */
    public Station(String id, String name, double latitude, double longitude) {
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station ID and Name must not be empty.");
        }
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** @return the unique identifier of the station */
    public String getId() {
        return id;
    }

    /** @return the name of the station */
    public String getName() {
        return name;
    }

    /** @return the geographical latitude of the station */
    public double getLatitude() {
        return latitude;
    }

    /** @return the geographical longitude of the station */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Textual representation of the station, containing the name and the identifier.
     *
     * @return string in the format "Name (ID)"
     */
    @Override
    public String toString() {
        return String.format("%s (%s)", name, id);
    }

    /**
     * Two stations are considered equal if they have the same identifier.
     *
     * @param o the other object to compare
     * @return true if the stations have the same {@code id}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id.equals(station.id);
    }

    /**
     * Calculates the station's hash code based on the {@code id}.
     *
     * @return hash value consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}