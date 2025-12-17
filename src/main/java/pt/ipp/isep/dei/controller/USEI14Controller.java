package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.service.EdmondsKarpMaxFlow;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.service.MapGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class USEI14Controller {

    private final EdmondsKarpMaxFlow<Station> algorithm = new EdmondsKarpMaxFlow<>();
    private Map<String, Station> stations;

    public double maximumThroughput(String stationsFile,
                                    String linesFile,
                                    String sourceId,
                                    String sinkId) {

        Graph<Station, Track> graph = buildGraph(stationsFile, linesFile);

        Station source = stations.get(sourceId);
        Station sink   = stations.get(sinkId);

        if (source == null || sink == null) {
            throw new IllegalArgumentException(
                    "Source or destination station ID does not exist."
            );
        }

        return algorithm.maxFlow(graph, source, sink);
    }

    private Graph<Station, Track> buildGraph(String stationsFile, String linesFile) {

        Graph<Station, Track> graph = new MapGraph<>(true);
        stations = new HashMap<>();


        try (BufferedReader br = new BufferedReader(new FileReader(stationsFile))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",");
                Station s = new Station(
                        f[0],           // id
                        f[1],           // name
                        Double.parseDouble(f[2]),
                        Double.parseDouble(f[3]),
                        Double.parseDouble(f[4]),
                        Double.parseDouble(f[5])
                );
                stations.put(s.getId(), s);
                graph.addVertex(s);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading stations", e);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(linesFile))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",");

                Station a = stations.get(f[0]);
                Station b = stations.get(f[1]);

                double distance = Double.parseDouble(f[2]);
                double capacity = Double.parseDouble(f[3]);
                double cost = Double.parseDouble(f[4]);

                Track track = new Track(distance, capacity, cost);
                graph.addEdge(a, b, track);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading lines", e);
        }

        return graph;
    }
}
