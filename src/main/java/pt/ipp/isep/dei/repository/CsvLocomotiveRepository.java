package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.repository.LocomotiveRepository;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for {@link Locomotive} entities that loads data from a CSV file.
 * This class is responsible for reading locomotive data from a specified CSV file
 * and providing access to that data.
 */
public class CsvLocomotiveRepository implements LocomotiveRepository {

    /**
     * A map to store the locomotives, with the locomotive ID as the key.
     */
    private final Map<String, Locomotive> locomotives = new HashMap<>();

    /**
     * Constructs a CsvLocomotiveRepository and initializes it by loading data
     * from the given CSV file path.
     *
     * @param filePath The path to the CSV file containing locomotive data.
     */
    public CsvLocomotiveRepository(String filePath) {
        loadLocomotives(filePath);
    }

    /**
     * Loads locomotive data from a CSV file into the repository.
     * Each row in the CSV is expected to represent a locomotive.
     * The method handles rows with incomplete data by creating a locomotive
     * with default values.
     *
     * @param filePath The path to the CSV file.
     */
    private void loadLocomotives(String filePath) {
        List<String[]> records = CsvReaderUtil.readCsvFile(filePath);
        for (String[] record : records) {
            if (record.length == 0 || record[0].trim().isEmpty()) continue;

            String id = record[0].trim();
            Locomotive locomotive;

            // --- LÓGICA DE ROBUSTEZ ---
            if (record.length >= 5) {
                String model = record[1].trim();
                double maxSpeed = Double.parseDouble(record[2].trim());
                double weight = Double.parseDouble(record[3].trim());
                boolean isElectric = record[4].trim().equals("1");
                locomotive = new Locomotive(id, model, maxSpeed, weight, isElectric);
            } else {
                // Valores padrão para uma locomotiva com dados incompletos
                locomotive = new Locomotive(id, "Modelo Desconhecido", 120, 50, false);
            }

            locomotives.put(id, locomotive);
        }
    }

    /**
     * Finds a locomotive by its unique identifier.
     *
     * @param id The unique ID of the locomotive to find.
     * @return The {@link Locomotive} object if found, otherwise {@code null}.
     */
    @Override
    public Locomotive findById(String id) {
        return locomotives.get(id);
    }

    /**
     * Retrieves all locomotives stored in the repository.
     *
     * @return A {@link List} containing all {@link Locomotive} objects.
     * The list will be empty if no locomotives are loaded.
     */
    @Override
    public List<Locomotive> findAll() {
        return new ArrayList<>(locomotives.values());
    }
}