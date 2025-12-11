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
            String name; // Declarar a variável para o nome

            // --- LÓGICA CORRIGIDA ---
            // Se a linha do CSV tiver 2 ou mais colunas, usa a segunda coluna para o nome.
            if (record.length >= 2) {
                name = record[1].trim();
            }
            // Se tiver apenas 1 coluna (o ID), cria um nome padrão para não dar erro.
            else {
                name = "Estacao " + id;
            }

            // Cria o objeto Station com o nome correto. Latitude e longitude são 0.0.
            Station station = new Station(id, name, 0.0, 0.0,0.0,0.0);
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