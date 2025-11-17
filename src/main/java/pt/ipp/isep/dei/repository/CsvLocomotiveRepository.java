package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV-based implementation of {@link LocomotiveRepository}.
 *
 * Expected CSV header (locomotives.csv):
 *
 *   id,model,maxSpeedKmH,weightTons,isElectric,power,tractiveEffort,tractiveEffortStart
 *
 * Only the first 6 columns are used at the moment. The last two are currently ignored
 * but can be mapped in the domain later if needed.
 */
public class CsvLocomotiveRepository implements LocomotiveRepository {

    private final Map<String, Locomotive> locomotivesById = new HashMap<>();

    public CsvLocomotiveRepository(String filePath) {
        loadFromCsv(filePath);
    }

    private void loadFromCsv(String filePath) {
        List<String[]> records = CsvReaderUtil.readCsvFile(filePath);

        for (String[] record : records) {
            // Linha muito curta → ignora
            if (record.length < 5) {
                continue;
            }

            String id = record[0].trim();
            String model = record[1] != null ? record[1].trim() : "";

            // Ignorar header ou linhas sem modelo
            if ("id".equalsIgnoreCase(id) || "model".equalsIgnoreCase(model) || model.isBlank()) {
                continue;
            }

            double maxSpeed = parseDoubleSafely(record[2].trim(), 120.0);
            double weight = parseDoubleSafely(record[3].trim(), 50.0);

            String electricStr = record[4].trim();
            boolean isElectric =
                    "1".equals(electricStr) ||
                            "true".equalsIgnoreCase(electricStr);

            // power é opcional (6ª coluna). Se não existir, fica 0.0
            double power = 0.0;
            if (record.length >= 6) {
                String powerStr = record[5].trim();
                if (!powerStr.isEmpty()) {
                    power = parseDoubleSafely(powerStr, 0.0);
                }
            }

            Locomotive locomotive = new Locomotive(
                    id,
                    model,
                    maxSpeed,
                    weight,
                    isElectric,
                    power
            );

            locomotivesById.put(id, locomotive);
        }
    }

    private double parseDoubleSafely(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    @Override
    public List<Locomotive> findAll() {
        return new ArrayList<>(locomotivesById.values());
    }

    @Override
    public Locomotive findById(String id) {
        return locomotivesById.get(id);
    }
}
