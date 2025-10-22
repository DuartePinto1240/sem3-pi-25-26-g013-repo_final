package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.*;

/**
 * A repository for {@link RailwayLine} entities that loads data from CSV files.
 * This implementation is responsible for reading railway line and line segment data
 * from specified CSV files, constructing {@link RailwayLine} objects, and providing
 * methods to query them.
 */
public class CsvRailwayLineRepository implements RailwayLineRepository {

    /**
     * A list to store all the RailwayLine objects loaded from the CSV files.
     */
    private final List<RailwayLine> railwayLines = new ArrayList<>();

    /**
     * Constructs a CsvRailwayLineRepository and initializes it by loading data
     * from the given CSV files for lines and segments.
     *
     * @param linesFilePath    The file path for the CSV containing railway line definitions.
     * @param segmentsFilePath The file path for the CSV containing line segment details.
     * @param stationRepo      A {@link StationRepository} instance to find station objects by their IDs.
     */
    public CsvRailwayLineRepository(String linesFilePath, String segmentsFilePath, StationRepository stationRepo) {
        loadLinesAndSegments(linesFilePath, segmentsFilePath, stationRepo);
    }

    /**
     * Loads railway lines and their corresponding segments from two separate CSV files.
     * It first reads all segments and groups them by line ID. Then, it reads the lines
     * and assembles them using the previously loaded segments.
     *
     * @param linesFilePath    The path to the CSV file defining the railway lines.
     * @param segmentsFilePath The path to the CSV file defining the segments of each line.
     * @param stationRepo      The repository used to look up {@link Station} objects.
     */
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

    /**
     * Finds a direct railway line that connects a specified start and end station.
     * A direct line is one where the line's first station matches the start station
     * and its last station matches the end station.
     *
     * @param start The starting {@link Station}.
     * @param end   The ending {@link Station}.
     * @return The {@link RailwayLine} object if a direct connection is found; otherwise, {@code null}.
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