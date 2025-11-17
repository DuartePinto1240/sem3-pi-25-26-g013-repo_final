package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.WagonModel;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.*;

/**
 * CSV-based implementation of {@link WagonModelRepository}.
 * <p>
 * Expected CSV format ("Wagon Model.csv"):
 * <pre>
 * ID,Model,Maker,Number Bogies,Bogies,Length,Width,Height,Weight (t),Max Speed,Payload (t),Volume (m3),Type,Bitola
 * </pre>
 * Only the first 14 columns are used. Additional columns, if any, are ignored.
 * </p>
 */
public class CsvWagonModelRepository implements WagonModelRepository {

    private final Map<String, WagonModel> byModelCode = new HashMap<>();

    /**
     * Creates a new repository and loads all wagon models from the given CSV file.
     *
     * @param filePath path to "Wagon Model.csv"
     */
    public CsvWagonModelRepository(String filePath) {
        loadFromCsv(filePath);
    }

    private void loadFromCsv(String filePath) {
        List<String[]> records = CsvReaderUtil.readCsvFile(filePath);

        for (String[] record : records) {
            if (record == null || record.length == 0) {
                continue;
            }

            String first = record[0].trim();
            // Skip header row
            if (first.equalsIgnoreCase("ID")) {
                continue;
            }

            // We expect at least 14 columns
            if (record.length < 14) {
                continue;
            }

            String id = record[0].trim();
            String modelCode = record[1].trim();
            String maker = record[2].trim();

            int numberBogies = parseIntSafely(record[3].trim(), 0);
            String bogiesType = record[4].trim();
            int lengthMm = parseIntSafely(record[5].trim(), 0);
            int widthMm = parseIntSafely(record[6].trim(), 0);
            double heightMm = parseDoubleSafely(record[7].trim(), 0.0);
            double tareWeightTons = parseDoubleSafely(record[8].trim(), 0.0);
            double maxSpeedKmH = parseDoubleSafely(record[9].trim(), 0.0);
            double payloadTons = parseDoubleSafely(record[10].trim(), 0.0);
            double volumeM3 = parseDoubleSafely(record[11].trim(), 0.0);
            String type = record[12].trim();
            int gaugeMm = parseIntSafely(record[13].trim(), 0);

            WagonModel model = new WagonModel(
                    id,
                    modelCode,
                    maker,
                    numberBogies,
                    bogiesType,
                    lengthMm,
                    widthMm,
                    heightMm,
                    tareWeightTons,
                    maxSpeedKmH,
                    payloadTons,
                    volumeM3,
                    type,
                    gaugeMm
            );

            // Use modelCode as key because Wagons.csv references this field
            if (!modelCode.isEmpty()) {
                byModelCode.put(modelCode, model);
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

    private double parseDoubleSafely(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    @Override
    public WagonModel findByModelCode(String modelCode) {
        return byModelCode.get(modelCode);
    }

    @Override
    public List<WagonModel> findAll() {
        return new ArrayList<>(byModelCode.values());
    }
}

