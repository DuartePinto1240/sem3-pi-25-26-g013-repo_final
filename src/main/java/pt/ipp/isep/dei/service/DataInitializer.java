package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.domain.Station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CLASSE PROVISÓRIA: Simula o carregamento de dados do sistema (que virão do CSV).
 * Deve ser substituída pela lógica de leitura de CSV.
 */
public class DataInitializer {

    // Simulação de todas as entidades
    private final Map<String, Station> stations = new HashMap<>();
    private final List<LineSegment> segments;
    private final List<Locomotive> locomotives;

    public DataInitializer() {
        // Inicialização das estações (Simulando leitura de Station CSV)
        Station lisbon = new Station("ST01", "Lisbon");
        Station porto = new Station("ST02", "Porto");
        Station coimbra = new Station("ST03", "Coimbra");
        stations.put(lisbon.getId(), lisbon);
        stations.put(porto.getId(), porto);
        stations.put(coimbra.getId(), coimbra);

        // Inicialização dos segmentos (Simulando leitura de Rotas CSV)
        this.segments = List.of(
                new LineSegment(lisbon, porto, 310.0, 160.0, true),
                new LineSegment(lisbon, coimbra, 200.0, 140.0, true),
                new LineSegment(coimbra, porto, 120.0, 100.0, false) // Note: limite 100 km/h
        );

        // Inicialização das locomotivas (Simulando leitura de Locomotiva CSV)
        this.locomotives = List.of(
                new Locomotive("L01", "Siemens Vectron", 200.0, 84.0, true), // Elétrica, 200 km/h
                new Locomotive("L02", "EMD SD40", 130.0, 170.0, false)       // Diesel, 130 km/h
        );
    }

    public Map<String, Station> getAllStations() {
        return stations;
    }

    public List<LineSegment> getAllSegments() {
        return segments;
    }

    public List<Locomotive> getAllLocomotives() {
        return locomotives;
    }
}