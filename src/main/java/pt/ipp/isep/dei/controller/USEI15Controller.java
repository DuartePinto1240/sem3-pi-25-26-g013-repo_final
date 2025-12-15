package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.dto.RiskPathResult;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.Repositories;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.service.MapGraph;
import main.java.pt.ipp.isep.dei.service.RiskAwarePathService;

import java.util.List;

public class USEI15Controller {

    private final RiskAwarePathService service;
    private final StationRepository stationRepo;
    private final RailwayLineRepository lineRepo;

    public USEI15Controller() {
        this.service = new RiskAwarePathService();
        this.stationRepo = Repositories.getInstance().getStationRepository();
        this.lineRepo = Repositories.getInstance().getRailwayLineRepository();
    }

    public RiskPathResult calculateRiskPath(String sourceId, String targetId) {
        Station source = stationRepo.findById(sourceId);
        Station target = stationRepo.findById(targetId);

        if (source == null || target == null) {
            throw new IllegalArgumentException("Station(s) not found.");
        }

        Graph<Station, Double> riskGraph = buildGraph();

        return service.computeShortestPath(riskGraph, source, target);
    }

    private Graph<Station, Double> buildGraph() {
        MapGraph<Station, Double> graph = new MapGraph<>(true); // Directed graph

        for (Station s : stationRepo.findAll()) {
            graph.addVertex(s);
        }

        List<RailwayLine> lines = lineRepo.findAll();

        for (RailwayLine line : lines) {
            for (LineSegment seg : line.getSegments()) {
                Station u = seg.getOrigin();
                Station v = seg.getDestination();

                // COST CALCULATION

                double cost = seg.getDistanceKm();

                // Add edge (A -> B)
                graph.addEdge(u, v, cost);

                graph.addEdge(v, u, cost);
            }
        }
        return graph;
    }
}