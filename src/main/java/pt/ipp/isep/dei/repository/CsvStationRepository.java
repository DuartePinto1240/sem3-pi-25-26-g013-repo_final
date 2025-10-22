package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for {@link Station} entities that loads data from a CSV file.
 * This class is responsible for reading station data from a specified CSV file
 * and providing access to that data.
 */
public class CsvStationRepository implements StationRepository {

    /**
     * A map to store the stations, with the station ID as the key.
     */
    private final Map<String, Station> stations = new HashMap<>();

    /**
     * Constructs a CsvStationRepository and initializes it by loading data
     * from the given CSV file path.
     *
     * @param filePath The path to the CSV file containing station data.
     */
    public CsvStationRepository(String filePath) {
        loadStations(filePath);
    }

    /**
     * Loads station data from a CSV file into the repository.
     * Each row in the CSV is expected to represent a station. The method includes
     * robustness logic to handle rows with incomplete data by creating a station
     * with default values.
     *
     * @param filePath The path to the CSV file.
     */
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

    /**
     * Finds a station by its unique identifier.
     *
     * @param id The unique ID of the station to find.
     * @return The {@link Station} object if found, otherwise {@code null}.
     */
    @Override
    public Station findById(String id) {
        return stations.get(id);
    }

    /**
     * Retrieves all stations stored in the repository.
     *
     * @return A {@link List} containing all {@link Station} objects.
     * The list will be empty if no stations are loaded.
     */
    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }
}