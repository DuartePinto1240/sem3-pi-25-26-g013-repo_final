package main.java.pt.ipp.isep.dei.domain;

import java.util.Objects;

public class Station {

    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;

    public Station(String id, String name) {
        this(id, name, 0.0, 0.0);
    }

    public Station(String id, String name, double latitude, double longitude) {
        // Validação de Integridade
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station ID and Name must not be empty.");
        }
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, id);
    }

    // Implementação crucial para que duas estações com o mesmo ID sejam consideradas o mesmo objeto.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id.equals(station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}