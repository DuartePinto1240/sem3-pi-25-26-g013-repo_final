package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.*;

public class CsvRailwayLineRepository implements RailwayLineRepository {

    private static class LineAggregate {
        double totalLengthMeters = 0.0;
        boolean allElectrified = true;
        int minTracks = Integer.MAX_VALUE;
    }

    private final List<RailwayLine> railwayLines = new ArrayList<>();

    /**
     * OLD CONSTRUCTOR: Preserves compatibility with existing code.
     * Loads data from 'railway_lines.csv' and 'line_segments.csv'.
     */
    public CsvRailwayLineRepository(String linesFilePath, String segmentsFilePath, StationRepository stationRepo) {
        loadLinesAndSegments(linesFilePath, segmentsFilePath, stationRepo);
    }

    /**
     * NEW CONSTRUCTOR: For USEI15 using only 'lines.csv'.
     */
    public CsvRailwayLineRepository(String singleLinesFilePath, StationRepository stationRepo) {
        loadLinesSimple(singleLinesFilePath, stationRepo);
    }

    // --- OLD LOADING LOGIC (Preserved) ---
    private void loadLinesAndSegments(String linesFilePath, String segmentsFilePath, StationRepository stationRepo) {
        Map<String, LineAggregate> aggregateByLine = new HashMap<>();
        List<String[]> segmentRecords = CsvReaderUtil.readCsvFile(segmentsFilePath);

        for (String[] record : segmentRecords) {
            if (record == null || record.length < 7 || record[0].trim().equalsIgnoreCase("ID")) continue;

            String lineId = record[1].trim();
            String electrifiedStr = record[3].trim();
            String lengthStr = record[5].trim();
            String tracksStr = record[6].trim();

            LineAggregate agg = aggregateByLine.computeIfAbsent(lineId, k -> new LineAggregate());

            double lengthMeters = 0.0;
            if (!lengthStr.isEmpty()) {
                try { lengthMeters = Double.parseDouble(lengthStr); } catch (NumberFormatException ignored) {}
            }
            agg.totalLengthMeters += lengthMeters;

            if (!electrifiedStr.equalsIgnoreCase("Yes")) {
                agg.allElectrified = false;
            }

            int tracks = 1;
            if (!tracksStr.isEmpty()) {
                try { tracks = Integer.parseInt(tracksStr); } catch (NumberFormatException ignored) {}
            }
            if (tracks < agg.minTracks) {
                agg.minTracks = tracks;
            }
        }

        List<String[]> lineRecords = CsvReaderUtil.readCsvFile(linesFilePath);
        for (String[] record : lineRecords) {
            if (record == null || record.length < 3 || record[0].trim().equalsIgnoreCase("lineId")) continue;

            String lineId = record[0].trim();
            String startId = record[1].trim();
            String endId   = record[2].trim();

            Station start = stationRepo.findById(startId);
            Station end   = stationRepo.findById(endId);

            if (start == null || end == null) continue;

            LineAggregate agg = aggregateByLine.get(lineId);
            double distanceKm = (agg != null) ? agg.totalLengthMeters / 1000.0 : 0.0;
            boolean electrified = (agg != null) && agg.allElectrified;
            int numberTracks = (agg != null && agg.minTracks != Integer.MAX_VALUE) ? agg.minTracks : 1;
            double maxSpeedKmH = 120.0;

            LineSegment segment = new LineSegment(1, start, end, distanceKm, maxSpeedKmH, electrified, numberTracks);
            railwayLines.add(new RailwayLine(Collections.singletonList(segment)));
        }
    }

    // --- NEW LOADING LOGIC (For lines.csv) ---
    private void loadLinesSimple(String linesFilePath, StationRepository stationRepo) {
        List<String[]> records = CsvReaderUtil.readCsvFile(linesFilePath);

        for (String[] record : records) {
            // Check for header: departure_stid,arrival_stid,...
            if (record == null || record.length < 3 || record[0].trim().equalsIgnoreCase("departure_stid")) {
                continue;
            }

            try {
                String startId = record[0].trim();
                String endId = record[1].trim();
                double distance = Double.parseDouble(record[2].trim());
                // capacity [3], cost [4] - ignored for generic LineSegment

                Station start = stationRepo.findById(startId);
                Station end = stationRepo.findById(endId);

                if (start != null && end != null) {
                    // Defaults for data not present in simple lines.csv
                    LineSegment segment = new LineSegment(
                            1, start, end, distance, 120.0, true, 1
                    );
                    railwayLines.add(new RailwayLine(Collections.singletonList(segment)));
                }
            } catch (NumberFormatException ignored) {
                // Skip invalid lines
            }
        }
    }

    @Override
    public RailwayLine findDirectLine(Station start, Station end) {
        for (RailwayLine line : railwayLines) {
            if (line.getStartStation().equals(start) && line.getEndStation().equals(end)) {
                return line;
            }
        }
        return null;
    }

    @Override
    public List<RailwayLine> findAll() {
        return new ArrayList<>(railwayLines);
    }

}