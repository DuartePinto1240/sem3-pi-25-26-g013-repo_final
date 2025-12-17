package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvStationRepository implements StationRepository {

    private final Map<String, Station> stations = new HashMap<>();

    public CsvStationRepository(String filePath) {
        loadStations(filePath);
    }

    private void loadStations(String filePath) {
        List<String[]> records = CsvReaderUtil.readCsvFile(filePath);

        for (String[] record : records) {
            if (record == null || record.length == 0) continue;

            // Skip Header Rows (checks for both old and new header names)
            String firstCol = record[0].trim();
            if (firstCol.equalsIgnoreCase("Station id") || firstCol.equalsIgnoreCase("ID")) {
                continue;
            }

            try {
                String id = firstCol;
                String name = "";
                double lat = 0.0;
                double lon = 0.0;
                double x = 0.0;
                double y = 0.0;

                // LOGIC FOR NEW FILE (6 columns: Station id, Station, Lat, Lon, CoordX, CoordY)
                if (record.length >= 6) {
                    name = record[1].trim();
                    try {
                        lat = Double.parseDouble(record[2].trim());
                        lon = Double.parseDouble(record[3].trim());
                        x = Double.parseDouble(record[4].trim());
                        y = Double.parseDouble(record[5].trim());
                    } catch (NumberFormatException ignored) {
                        // Keep defaults if parsing fails
                    }
                }
                // LOGIC FOR OLD FILE (2 columns: ID, Name)
                else if (record.length >= 2) {
                    name = record[1].trim();
                }
                // Fallback
                else {
                    name = "Station " + id;
                }

                Station station = new Station(id, name, lat, lon, x, y);
                stations.put(id, station);

            } catch (Exception e) {
                System.err.println("Error parsing station row: " + String.join(",", record));
            }
        }
    }

    @Override
    public Station findById(String id) {
        return stations.get(id);
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }
}