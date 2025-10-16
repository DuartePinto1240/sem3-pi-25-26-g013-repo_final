package main.java.pt.ipp.isep.dei.repository.csv;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.*;

public class CsvRailwayLineRepository implements RailwayLineRepository {

    private final List<RailwayLine> railwayLines = new ArrayList<>();

    public CsvRailwayLineRepository(String linesFilePath, String segmentsFilePath, StationRepository stationRepo) {
        loadLinesAndSegments(linesFilePath, segmentsFilePath, stationRepo);
    }

    private void loadLinesAndSegments(String linesFilePath, String segmentsFilePath, StationRepository stationRepo) {
        Map<String, List<LineSegment>> segmentsByLine = new HashMap<>();
        List<String[]> segmentRecords = CsvReaderUtil.readCsvFile(segmentsFilePath);

        for (String[] record : segmentRecords) {
            // --- LÓGICA DE ROBUSTEZ ---
            // Ignora a linha se não tiver o número mínimo de colunas para ser válida
            if (record.length < 7) continue;

            String lineId = record[0].trim();
            Station origin = stationRepo.findById(record[2].trim());
            Station destination = stationRepo.findById(record[3].trim());

            // Apenas processa se as estações de origem e destino existirem
            if (origin != null && destination != null) {
                double distance = Double.parseDouble(record[4].trim());
                double maxSpeed = Double.parseDouble(record[5].trim());
                boolean isElectrified = record[6].trim().equals("1");
                LineSegment segment = new LineSegment(origin, destination, distance, maxSpeed, isElectrified);
                segmentsByLine.computeIfAbsent(lineId, k -> new ArrayList<>()).add(segment);
            }
        }

        List<String[]> lineRecords = CsvReaderUtil.readCsvFile(linesFilePath);
        for (String[] record : lineRecords) {
            // --- LÓGICA DE ROBUSTEZ ---
            if (record.length < 3) continue;

            String lineId = record[0].trim();
            List<LineSegment> segments = segmentsByLine.get(lineId);
            if (segments != null && !segments.isEmpty()) {
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