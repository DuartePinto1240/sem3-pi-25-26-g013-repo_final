package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
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
            if (record.length == 0 || record[0].trim().isEmpty()) continue; // Ignorar linhas vazias

            String id = record[0].trim();
            Station station;

            // --- LÓGICA DE ROBUSTEZ ---
            // Se o CSV tiver 4 ou mais colunas, lê todos os dados.
            if (record.length >= 4) {
                String name = record[1].trim();
                double latitude = Double.parseDouble(record[2].trim());
                double longitude = Double.parseDouble(record[3].trim());
                station = new Station(id, name, latitude, longitude);
            }
            // Se tiver menos colunas, usa valores padrão e assume que a primeira é o ID.
            else {
                String defaultName = "Estacao " + id;
                station = new Station(id, defaultName, 0.0, 0.0);
            }

            stations.put(id, station);
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