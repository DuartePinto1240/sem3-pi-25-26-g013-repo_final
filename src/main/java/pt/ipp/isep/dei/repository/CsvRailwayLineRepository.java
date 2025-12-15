package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.*;

/**
 * Repository implementation for managing {@link RailwayLine} data, loading it from CSV files.
 *
 * CSVs atuais:
 *
 * railway_lines.csv
 *   lineId,startStationId,endStationId
 *
 * line_segments.csv
 *   ID,Line,Order,Electrified,Max Weight (kg/m),Length (m),NumberTracks,Siding Position,Siding Length
 *
 * Nesta implementação, cada linha (lineId) é tratada como uma ligação direta
 * entre duas estações, com:
 *  - origem = startStationId
 *  - destino = endStationId
 *  - distância = soma dos comprimentos (Length) de todos os segmentos dessa linha
 *  - electrified = true se todos os segmentos dessa linha forem "Yes"
 *  - numberTracks = mínimo dos NumberTracks dessa linha
 */
public class CsvRailwayLineRepository implements RailwayLineRepository {

    private static class LineAggregate {
        double totalLengthMeters = 0.0;
        boolean allElectrified = true;
        int minTracks = Integer.MAX_VALUE;
    }

    /** Internal list storing all loaded railway lines. */
    private final List<RailwayLine> railwayLines = new ArrayList<>();

    public CsvRailwayLineRepository(String linesFilePath,
                                    String segmentsFilePath,
                                    StationRepository stationRepo) {
        loadLinesAndSegments(linesFilePath, segmentsFilePath, stationRepo);
    }

    private void loadLinesAndSegments(String linesFilePath,
                                      String segmentsFilePath,
                                      StationRepository stationRepo) {

        // 1) Agregar info de line_segments por cada Line
        Map<String, LineAggregate> aggregateByLine = new HashMap<>();
        List<String[]> segmentRecords = CsvReaderUtil.readCsvFile(segmentsFilePath);

        for (String[] record : segmentRecords) {
            if (record == null || record.length == 0) continue;

            // Header: ID, Line, Order, Electrified, Max Weight (kg/m), Length (m), NumberTracks, Siding Position, Siding Length
            // Ignorar header
            String first = record[0].trim();
            if (first.equalsIgnoreCase("ID")) {
                continue;
            }
            if (record.length < 7) {
                continue;
            }

            String lineId = record[1].trim();          // coluna "Line"
            String electrifiedStr = record[3].trim();  // "Electrified"
            String lengthStr = record[5].trim();       // "Length (m)"
            String tracksStr = record[6].trim();       // "NumberTracks"

            LineAggregate agg = aggregateByLine.computeIfAbsent(lineId, k -> new LineAggregate());

            // comprimento
            double lengthMeters = 0.0;
            if (!lengthStr.isEmpty()) {
                try {
                    lengthMeters = Double.parseDouble(lengthStr);
                } catch (NumberFormatException e) {
                    // ignore, fica 0
                }
            }
            agg.totalLengthMeters += lengthMeters;

            // electrificada: se algum for "No", marcamos false
            boolean isElectrified = electrifiedStr.equalsIgnoreCase("Yes");
            if (!isElectrified) {
                agg.allElectrified = false;
            }

            // número de vias: guardamos o mínimo
            int tracks = 1;
            if (!tracksStr.isEmpty()) {
                try {
                    tracks = Integer.parseInt(tracksStr);
                } catch (NumberFormatException e) {
                    tracks = 1;
                }
            }
            if (tracks < agg.minTracks) {
                agg.minTracks = tracks;
            }
        }

        // 2) Construir RailwayLine para cada linha em railway_lines.csv
        List<String[]> lineRecords = CsvReaderUtil.readCsvFile(linesFilePath);

        for (String[] record : lineRecords) {
            if (record == null || record.length == 0) continue;

            // header: lineId,startStationId,endStationId
            String first = record[0].trim();
            if (first.equalsIgnoreCase("lineId")) {
                continue;
            }
            if (record.length < 3) {
                continue;
            }

            String lineId = record[0].trim();
            String startId = record[1].trim();
            String endId   = record[2].trim();

            Station start = stationRepo.findById(startId);
            Station end   = stationRepo.findById(endId);

            if (start == null || end == null) {
                // Linha inválida se não encontrarmos as estações
                continue;
            }

            LineAggregate agg = aggregateByLine.get(lineId);

            double distanceKm = 0.0;
            boolean electrified = true;
            int numberTracks = 1;

            if (agg != null) {
                distanceKm = agg.totalLengthMeters / 1000.0;
                electrified = agg.allElectrified;
                numberTracks = (agg.minTracks == Integer.MAX_VALUE) ? 1 : agg.minTracks;
            }

            // Não temos velocidade máx da infraestrutura -> usamos um default razoável (ex: 120 km/h)
            double maxSpeedKmH = 120.0;

            LineSegment segment = new LineSegment(
                    1,           // sequenceNumber
                    start,
                    end,
                    distanceKm,
                    maxSpeedKmH,
                    electrified,
                    numberTracks
            );

            RailwayLine line = new RailwayLine(Collections.singletonList(segment));
            railwayLines.add(line);
        }
    }

    /**
     * Finds a {@link RailwayLine} that directly runs between the specified start and end stations.
     * A "direct line" is one whose first segment starts at {@code start}
     * and whose last segment ends at {@code end}.
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

    @Override
    public List<RailwayLine> findAll() {
        return List.of();
    }
}