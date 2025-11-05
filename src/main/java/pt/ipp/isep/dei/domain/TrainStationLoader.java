package main.java.pt.ipp.isep.dei.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader for TrainStation entities from CSV files.
 * Expected CSV format: country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
 */
public class TrainStationLoader {

    /**
     * Detects the delimiter used in a CSV file based on the first line (header).
     * If the header contains a semicolon (;), that is assumed to be the delimiter.
     * Otherwise, the comma delimiter (,) is used.
     *
     * @param headerLine the header line of the CSV file
     * @return the identified delimiter ("," or ";")
     */
    private String detectDelimiter(String headerLine) {
        if (headerLine.contains(";")) return ";";
        else return ",";
    }

    /**
     * Loads a list of TrainStations from a CSV file.
     * Expected format: country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
     *
     * @param filePath path to the CSV file
     * @return list of loaded TrainStation objects
     */
    public List<TrainStation> loadTrainStations(String filePath) {
        List<TrainStation> stations = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            if (header == null) return stations;

            String delimiter = detectDelimiter(header);
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] fields = line.split(delimiter);
                if (fields.length < 9) continue;

                try {
                    String country = fields[0].trim();
                    String timeZone = fields[1].trim();
                    String timeZoneGroup = fields[2].trim();
                    String station = fields[3].trim();
                    double latitude = Double.parseDouble(fields[4].trim());
                    double longitude = Double.parseDouble(fields[5].trim());
                    boolean isCity = Boolean.parseBoolean(fields[6].trim());
                    boolean isMainStation = Boolean.parseBoolean(fields[7].trim());
                    boolean isAirport = Boolean.parseBoolean(fields[8].trim());

                    TrainStation trainStation = new TrainStation(
                            country, timeZone, timeZoneGroup, station,
                            latitude, longitude, isCity, isMainStation, isAirport
                    );

                    stations.add(trainStation);

                } catch (NumberFormatException e) {
                    System.err.println("⚠️ Erro ao processar linha: " + line);
                    System.err.println("   Detalhe: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Erro a ler ficheiro de estações: " + e.getMessage());
        }

        return stations;
    }

    /**
     * Loads train stations filtered by country code.
     *
     * @param filePath path to the CSV file
     * @param countryCode country code to filter (e.g., "SE", "PT")
     * @return list of train stations from the specified country
     */
    public List<TrainStation> loadTrainStationsByCountry(String filePath, String countryCode) {
        List<TrainStation> allStations = loadTrainStations(filePath);
        List<TrainStation> filtered = new ArrayList<>();

        for (TrainStation station : allStations) {
            if (station.getCountry().equalsIgnoreCase(countryCode)) {
                filtered.add(station);
            }
        }

        return filtered;
    }
}