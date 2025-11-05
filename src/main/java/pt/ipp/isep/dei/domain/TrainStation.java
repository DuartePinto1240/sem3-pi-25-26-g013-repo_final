package main.java.pt.ipp.isep.dei.domain;

import java.util.Objects;

/**
 * Represents a train station with geographical and operational information.
 * This entity is loaded from CSV files containing European train station data.
 */
public class TrainStation {

    private String country;
    private String timeZone;
    private String timeZoneGroup;
    private String station;
    private double latitude;
    private double longitude;
    private boolean isCity;
    private boolean isMainStation;
    private boolean isAirport;

    /**
     * Constructs a new TrainStation with all attributes.
     *
     * @param country the country code (e.g., "FR", "SE", "PT")
     * @param timeZone the timezone information
     * @param timeZoneGroup the timezone group (e.g., "CET")
     * @param station the station name
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @param isCity whether this location is a city
     * @param isMainStation whether this is a main station
     * @param isAirport whether this is an airport station
     */
    public TrainStation(String country, String timeZone, String timeZoneGroup,
                        String station, double latitude, double longitude,
                        boolean isCity, boolean isMainStation, boolean isAirport) {
        this.country = country;
        this.timeZone = timeZone;
        this.timeZoneGroup = timeZoneGroup;
        this.station = station;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
    }

    // Getters
    public String getCountry() {
        return country;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getTimeZoneGroup() {
        return timeZoneGroup;
    }

    public String getStation() {
        return station;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isCity() {
        return isCity;
    }

    public boolean isMainStation() {
        return isMainStation;
    }

    public boolean isAirport() {
        return isAirport;
    }

    // Setters
    public void setCountry(String country) {
        this.country = country;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setTimeZoneGroup(String timeZoneGroup) {
        this.timeZoneGroup = timeZoneGroup;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCity(boolean city) {
        isCity = city;
    }

    public void setMainStation(boolean mainStation) {
        isMainStation = mainStation;
    }

    public void setAirport(boolean airport) {
        isAirport = airport;
    }

    /**
     * Calculates the distance in kilometers to another train station using the Haversine formula.
     *
     * @param other the other train station
     * @return the distance in kilometers
     */
    public double distanceTo(TrainStation other) {
        final int EARTH_RADIUS_KM = 6371;

        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainStation that = (TrainStation) o;
        return Double.compare(latitude, that.latitude) == 0 &&
                Double.compare(longitude, that.longitude) == 0 &&
                Objects.equals(country, that.country) &&
                Objects.equals(station, that.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, station, latitude, longitude);
    }

    @Override
    public String toString() {
        return String.format("TrainStation{country='%s', station='%s', lat=%.5f, lon=%.5f, " +
                        "isCity=%s, isMainStation=%s, isAirport=%s}",
                country, station, latitude, longitude, isCity, isMainStation, isAirport);
    }
}
