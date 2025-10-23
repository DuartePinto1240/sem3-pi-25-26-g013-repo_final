package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.*;
import java.util.Comparator;

public class CsvRailwayLineRepository implements RailwayLineRepository {

    private final List<RailwayLine> railwayLines = new ArrayList<>();

    public CsvRailwayLineRepository(String linesFilePath, String segmentsFilePath, StationRepository stationRepo) {
        loadLinesAndSegments(linesFilePath, segmentsFilePath, stationRepo);
    }

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