package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Wagon;
import main.java.pt.ipp.isep.dei.domain.WagonModel;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.*;

/**
 * CSV-based implementation of {@link WagonRepository}.
 * <p>
 * Expected CSV format ("Wagons.csv"):
 * <pre>
 * Number,Model,Operator,Service Year
 * </pre>
 * The "Model" column must match the {@code modelCode} field of an existing {@link WagonModel}.
 * </p>
 */
public class CsvWagonRepository implements WagonRepository {

    private final Map<String, Wagon> byNumber = new HashMap<>();

    /**
     * Creates a new repository and loads all wagons from the given CSV file.
     *
     * @param filePath       path to "Wagons.csv"
     * @param wagonModelRepo repository used to resolve {@link WagonModel} by model code
     */
    public CsvWagonRepository(String filePath, WagonModelRepository wagonModelRepo) {
        loadFromCsv(filePath, wagonModelRepo);
    }

    private void loadFromCsv(String filePath, WagonModelRepository wagonModelRepo) {
        List<String[]> records = CsvReaderUtil.readCsvFile(filePath);

        for (String[] record : records) {
            if (record == null || record.length == 0) {
                continue;
            }

            String first = record[0].trim();
            // Skip header row
            if (first.equalsIgnoreCase("Number")) {
                continue;
            }

            if (record.length < 4) {
                continue;
            }

            String number = record[0].trim();
            String modelCode = record[1].trim();
            String operator = record[2].trim();
            int serviceYear = parseIntSafely(record[3].trim(), 0);

            WagonModel model = wagonModelRepo.findByModelCode(modelCode);
            if (model == null) {
                // Unknown model code; ignore this wagon entry
                continue;
            }

            Wagon wagon = new Wagon(number, model, operator, serviceYear);
            if (!number.isEmpty()) {
                byNumber.put(number, wagon);
            }
        }
    }

    private int parseIntSafely(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    @Override
    public Wagon findByNumber(String number) {
        return byNumber.get(number);
    }

    @Override
    public List<Wagon> findAll() {
        return new ArrayList<>(byNumber.values());
    }
}

