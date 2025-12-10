package main.java.pt.ipp.isep.dei.util;


import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.service.MapGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RailwayLoader {

    /**
     * Lê os ficheiros CSV e constrói o grafo da rede ferroviária.
     * * @param stationsFile Caminho para o ficheiro de estações.
     * @param linesFile Caminho para o ficheiro de linhas/arestas.
     * @return Um grafo não direcionado carregado com os dados.
     */
    public static Graph<Station, Track> loadGraph(String stationsFile, String linesFile) {
        // false = Grafo Não Direcionado (ideal para MST/Prim)
        Graph<Station, Track> graph = new MapGraph<>(false);

        // Cache temporária para encontrar Estações pelo ID rapidamente (O(1))
        Map<String, Station> stationMap = new HashMap<>();

        // --- 1. LER ESTAÇÕES ---
        // Formato CSV: Station id,Station,Lat,Lon,CoordX,CoordY
        try (BufferedReader br = new BufferedReader(new FileReader(stationsFile))) {
            String line = br.readLine(); // Saltar cabeçalho

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                // Garante que a linha tem colunas suficientes
                if (p.length >= 6) {
                    try {
                        String id = p[0].trim();
                        String name = p[1].trim();
                        double lat = Double.parseDouble(p[2].trim());
                        double lon = Double.parseDouble(p[3].trim());
                        double x = Double.parseDouble(p[4].trim()); // CoordX para Graphviz
                        double y = Double.parseDouble(p[5].trim()); // CoordY para Graphviz

                        // Cria a Estação e adiciona ao Grafo
                        Station station = new Station(id, name, lat, lon, x, y);

                        graph.addVertex(station);
                        stationMap.put(id, station); // Guarda no mapa para uso posterior

                    } catch (NumberFormatException e) {
                        // Ignora linhas com formatação numérica errada
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler ficheiro de estações: " + e.getMessage());
            return null;
        }

        // --- 2. LER LINHAS (TRACKS) ---
        // Formato CSV: departure_stid,arrival_stid,dist,capacity,cost
        try (BufferedReader br = new BufferedReader(new FileReader(linesFile))) {
            String line = br.readLine(); // Saltar cabeçalho

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 5) {
                    try {
                        String idOrig = p[0].trim();
                        String idDest = p[1].trim();
                        double dist = Double.parseDouble(p[2].trim());
                        double cap = Double.parseDouble(p[3].trim());
                        double cost = Double.parseDouble(p[4].trim());

                        // Recupera os objetos Station reais através do ID
                        Station sOrig = stationMap.get(idOrig);
                        Station sDest = stationMap.get(idDest);

                        // Só adiciona a linha se ambas as estações existirem
                        if (sOrig != null && sDest != null) {
                            Track track = new Track(dist, cap, cost);
                            graph.addEdge(sOrig, sDest, track);
                        }

                    } catch (NumberFormatException e) {
                        // Ignora linhas mal formatadas
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler ficheiro de linhas: " + e.getMessage());
            return null;
        }

        return graph;
    }
}