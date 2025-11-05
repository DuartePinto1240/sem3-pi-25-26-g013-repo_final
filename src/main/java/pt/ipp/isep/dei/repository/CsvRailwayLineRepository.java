package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository implementation for managing {@link RailwayLine} data, loading it from CSV files.
 * <p>
 * This repository is responsible for reading railway line definitions and their
 * constituent segments from separate CSV files and constructing the domain objects.
 * </p>
 */
public class CsvRailwayLineRepository implements RailwayLineRepository { // Assuming it implements an interface

    /** Internal list storing all loaded railway lines. */
    private final List<RailwayLine> railwayLines = new ArrayList<>();

    /**
     * Constructs the repository and loads all railway lines and segments from CSV files.
     *
     * @param linesFilePath the path to the CSV file containing the main railway line definitions
     * @param segmentsFilePath the path to the CSV file containing the line segment definitions
     * @param stationRepo the {@link StationRepository} used to look up {@link Station} objects by ID
     */
    public CsvRailwayLineRepository(String linesFilePath, String segmentsFilePath, StationRepository stationRepo) {
        loadLinesAndSegments(linesFilePath, segmentsFilePath, stationRepo);
    }

    /**
     * Loads the line segments, groups them by line ID, and then constructs the
     * final {@link RailwayLine} objects by sequencing the segments.
     *
     * @param linesFilePath the path to the CSV file with line records
     * @param segmentsFilePath the path to the CSV file with segment records
     * @param stationRepo the repository to resolve Station IDs
     */
    private void loadLinesAndSegments(String linesFilePath, String segmentsFilePath, StationRepository stationRepo) {
        Map<String, List<LineSegment>> segmentsByLine = new HashMap<>();
        List<String[]> segmentRecords = CsvReaderUtil.readCsvFile(segmentsFilePath);

        for (String[] record : segmentRecords) {
            if (record.length < 7) continue;

            String lineId = record[0].trim();
            int sequenceNumber = Integer.parseInt(record[1].trim());
            Station origin = stationRepo.findById(record[2].trim());
            Station destination = stationRepo.findById(record[3].trim());

            if (origin != null && destination != null) {
                double distance = Double.parseDouble(record[4].trim());
                double maxSpeed = Double.parseDouble(record[5].trim());
                boolean isElectrified = record[6].trim().equals("1");
                LineSegment segment = new LineSegment(sequenceNumber, origin, destination, distance, maxSpeed, isElectrified);
                segmentsByLine.computeIfAbsent(lineId, k -> new ArrayList<>()).add(segment);
            }
        }

        List<String[]> lineRecords = CsvReaderUtil.readCsvFile(linesFilePath);
        for (String[] record : lineRecords) {
            if (record.length < 3) continue;

            String lineId = record[0].trim();
            List<LineSegment> segments = segmentsByLine.get(lineId);

            if (segments != null && !segments.isEmpty()) {
                segments.sort(Comparator.comparingInt(LineSegment::getSequenceNumber));
                railwayLines.add(new RailwayLine(segments));
            }
        }
    }

    /**
     * Finds a {@link RailwayLine} that directly runs between the specified start and end stations.
     * <p>
     * A "direct line" is one whose first segment starts at {@code start} and whose
     * last segment ends at {@code end}.
     * </p>
     *
     * @param start the starting {@link Station}
     * @param end the ending {@link Station}
     * @return the matching {@link RailwayLine}, or {@code null} if no such line exists
     */
    @Override
    public RailwayLine findDirectLine(Station start, Station end) {
        for (RailwayLine line : railwayLines) {
            if (line.getStartStation().equals(start) && line.getEndStation().equals(end)) {
                return line;
            }
        }
        return null;
    }
}