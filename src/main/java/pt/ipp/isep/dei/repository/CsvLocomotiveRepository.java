package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.repository.LocomotiveRepository;
import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvLocomotiveRepository implements LocomotiveRepository {

    private final Map<String, Locomotive> locomotives = new HashMap<>();

    public CsvLocomotiveRepository(String filePath) {
        loadLocomotives(filePath);
    }

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

    @Override
    public Locomotive findById(String id) {
        return locomotives.get(id);
    }

    @Override
    public List<Locomotive> findAll() {
        return new ArrayList<>(locomotives.values());
    }
}