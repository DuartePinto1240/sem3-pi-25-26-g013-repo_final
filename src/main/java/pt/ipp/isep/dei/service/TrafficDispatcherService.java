package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.repository.LocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta classe contém a lógica de negócio principal da aplicação.
 * Ela orquestra as operações, utilizando os repositórios para aceder aos dados
 * e executando os cálculos necessários para cumprir os casos de uso.
 */
public class TrafficDispatcherService {

    private final StationRepository stationRepo;
    private final LocomotiveRepository locomotiveRepo;
    private final RailwayLineRepository lineRepo;

    /**
     * Construtor que recebe os repositórios como dependências.
     * Isto permite que o serviço seja independente da forma como os dados são armazenados (CSV, base de dados, etc.).
     * @param stationRepo O repositório para aceder aos dados das estações.
     * @param locomotiveRepo O repositório para aceder aos dados das locomotivas.
     * @param lineRepo O repositório para aceder aos dados das linhas férreas.
     */
    public TrafficDispatcherService(StationRepository stationRepo, LocomotiveRepository locomotiveRepo, RailwayLineRepository lineRepo) {
        this.stationRepo = stationRepo;
        this.locomotiveRepo = locomotiveRepo;
        this.lineRepo = lineRepo;
    }

    /**
     * Devolve uma lista de todas as estações disponíveis no sistema.
     * @return A lista de objetos Station.
     */
    public List<Station> getAvailableStations() {
        return stationRepo.findAll();
    }

    /**
     * Devolve uma lista de todas as locomotivas disponíveis no sistema.
     * @return A lista de objetos Locomotive.
     */
    public List<Locomotive> getAvailableLocomotives() {
        return locomotiveRepo.findAll();
    }

    /**
     * Calcula o tempo de viagem estimado entre duas estações com uma locomotiva selecionada.
     * [cite_start]A aceleração é considerada instantânea, conforme os requisitos. [cite: 509]
     * @param startStationId O ID da estação de partida.
     * @param endStationId O ID da estação de destino.
     * @param locomotiveId O ID da locomotiva a ser utilizada.
     * @return Um objeto TravelTimeResult contendo os detalhes do cálculo.
     * @throws IllegalArgumentException se algum dos IDs for inválido.
     * @throws IllegalStateException se não for encontrada uma linha direta entre as estações.
     */
    public TravelTimeResult calculateTravelTime(String startStationId, String endStationId, String locomotiveId) {
        // 1. Obter os objetos de domínio a partir dos seus IDs, usando os repositórios.
        Station start = stationRepo.findById(startStationId);
        Station end = stationRepo.findById(endStationId);
        Locomotive loco = locomotiveRepo.findById(locomotiveId);

        // Validar se todos os objetos foram encontrados.
        if (start == null || end == null || loco == null) {
            throw new IllegalArgumentException("IDs de estação ou locomotiva inválidos.");
        }

        // 2. Encontrar a linha férrea que liga diretamente as duas estações.
        RailwayLine line = lineRepo.findDirectLine(start, end);
        if (line == null) {
            throw new IllegalStateException("Não existe uma linha direta entre as estações selecionadas.");
        }

        // 3. Executar a lógica de cálculo.
        List<TravelSection> sections = new ArrayList<>();
        double totalTimeMinutes = 0;

        // Iterar sobre cada segmento da linha para calcular o tempo individualmente.
        for (LineSegment segment : line.getSegments()) {
            // A velocidade efetiva é o valor mínimo entre o limite de velocidade do segmento e a velocidade máxima da locomotiva.
            double effectiveSpeed = Math.min(segment.getMaxSpeedKmH(), loco.getMaxSpeedKmH());

            // Fórmula: Tempo (em horas) = Distância / Velocidade
            double timeHours = segment.getDistanceKm() / effectiveSpeed;
            double timeMinutes = timeHours * 60;

            // Criar um objeto para guardar os detalhes desta secção da viagem.
            sections.add(new TravelSection(segment, effectiveSpeed, timeMinutes));

            // Acumular o tempo total.
            totalTimeMinutes += timeMinutes;
        }

        // 4. Construir e retornar o objeto de resultado final.
        return new TravelTimeResult(sections, totalTimeMinutes, loco, line);
    }
}