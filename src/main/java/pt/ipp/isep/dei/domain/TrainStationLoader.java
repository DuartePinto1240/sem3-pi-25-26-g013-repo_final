package main.java.pt.ipp.isep.dei.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrainStationLoader {

    public List<TrainStation> load(String csvPath) {
        List<TrainStation> stations = new ArrayList<>();
        int rejected = 0;
        long lineNum = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                lineNum++;

                List<String> cols = safeSplit(line);

                if (cols.size() < 9) {
                    logReject(lineNum, line, "Expected 9 columns, got " + cols.size());
                    rejected++;
                    continue;
                }

                String country = clean(cols.get(0));
                String timeZone = clean(cols.get(1));
                String timeZoneGroup = clean(cols.get(2));
                String station = clean(cols.get(3));

                if (country.isEmpty() || timeZoneGroup.isEmpty() || station.isEmpty()) {
                    logReject(lineNum, line, "Missing required fields");
                    rejected++;
                    continue;
                }

                try {
                    double lat = Double.parseDouble(clean(cols.get(4)));
                    double lon = Double.parseDouble(clean(cols.get(5)));

                    if (!validLat(lat) || !validLon(lon)) {
                        logReject(lineNum, line, "Invalid coordinates");
                        rejected++;
                        continue;
                    }

                    boolean isCity = Boolean.parseBoolean(clean(cols.get(6)));
                    boolean isMainStation = Boolean.parseBoolean(clean(cols.get(7)));
                    boolean isAirport = Boolean.parseBoolean(clean(cols.get(8)));

                    stations.add(new TrainStation(
                            country, timeZone, timeZoneGroup, station,
                            lat, lon, isCity, isMainStation, isAirport
                    ));

                } catch (NumberFormatException ex) {
                    logReject(lineNum, line, "Number parse failed: " + ex.getMessage());
                    rejected++;
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Error reading: " + e.getMessage());
        }

        System.out.printf("✅ Loaded %d stations | ❌ Rejected %d%n", stations.size(), rejected);
        return stations;
    }

    private static List<String> safeSplit(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') insideQuotes = !insideQuotes;
            else if (c == ',' && !insideQuotes) {
                parts.add(current.toString().trim());
                current.setLength(0);
            } else current.append(c);
        }
        parts.add(current.toString().trim());
        return parts;
    }

    private boolean validLat(double lat) { return lat >= -90 && lat <= 90; }
    private boolean validLon(double lon) { return lon >= -180 && lon <= 180; }

    private String clean(String s) {
        return s == null ? "" : s.replace("\"", "").trim();
    }

    private void logReject(long lineNum, String line, String reason) {
        System.err.printf(
                "⚠️ Rejected line %d:\n   %s\n   Reason: %s\n\n",
                lineNum + 1, line, reason
        );
    }
}
