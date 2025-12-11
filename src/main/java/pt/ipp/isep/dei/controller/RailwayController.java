package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.service.Edge;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.service.MapGraph;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RailwayController {

    /**
     * USEI12: Calcula a Minimal Backbone Network (MST)
     * Utiliza o Algoritmo de Prim.
     * * @param network O grafo completo da rede ferroviária.
     * @return Um novo grafo contendo apenas a MST.
     */
    public Graph<Station, Track> getMinimalBackboneNetwork(Graph<Station, Track> network) {
        // Cria um grafo vazio para a MST (false = não direcionado)
        // Se a tua classe se chamar AdjacencyMapGraph, muda aqui:
        Graph<Station, Track> mst = new MapGraph<>(false);

        if (network == null || network.numVertices() == 0) return mst;

        // --- Algoritmo de Prim ---
        Set<Station> visited = new HashSet<>();

        // Fila de prioridade ordena arestas pela DISTÂNCIA (menor para maior)
        PriorityQueue<Edge<Station, Track>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(e -> e.getWeight().getDistance())
        );

        // 1. Começar pelo primeiro vértice que encontrarmos
        Station startVertex = network.vertices().iterator().next();
        visited.add(startVertex);
        mst.addVertex(startVertex);

        // Adicionar arestas iniciais à fila
        for (Edge<Station, Track> edge : network.outgoingEdges(startVertex)) {
            pq.add(edge);
        }

        // 2. Loop principal
        while (!pq.isEmpty() && visited.size() < network.numVertices()) {
            Edge<Station, Track> minEdge = pq.poll();
            Station vDest = minEdge.getVDest();

            if (!visited.contains(vDest)) {
                visited.add(vDest);

                // Adicionar à MST
                mst.addVertex(vDest);
                mst.addEdge(minEdge.getVOrig(), vDest, minEdge.getWeight());

                // Adicionar novas arestas vizinhas
                for (Edge<Station, Track> edge : network.outgoingEdges(vDest)) {
                    if (!visited.contains(edge.getVDest())) {
                        pq.add(edge);
                    }
                }
            }
        }
        return mst;
    }

    /**
     * USEI12: Exportar grafo para ficheiro DOT com coordenadas geográficas.
     */
    public boolean exportGraphToDOT(Graph<Station, Track> graph, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("graph backbone {\n");
            writer.write("  layout=neato;\n"); // Motor de layout geográfico
            writer.write("  node [shape=circle, style=filled, fillcolor=lightblue, width=15, height=15, fontsize=100];\n");
            writer.write("  edge [penwidth=10, fontsize=80];\n");

            // 1. Escrever Nós com a posição (pos="x,y!")
            for (Station s : graph.vertices()) {
                writer.write(String.format(Locale.US, "  \"%s\" [pos=\"%.4f,%.4f!\", label=\"%s\"];\n",
                        s.getId(), s.getX(), s.getY(), s.getName()));
            }

            // 2. Escrever Arestas (evitando duplicados A--B e B--A)
            Set<String> processedEdges = new HashSet<>();

            for (Edge<Station, Track> edge : graph.edges()) {
                String id1 = edge.getVOrig().getId();
                String id2 = edge.getVDest().getId();

                // Cria uma chave única ordenando os IDs
                String key = (id1.compareTo(id2) < 0) ? id1 + "-" + id2 : id2 + "-" + id1;

                if (!processedEdges.contains(key)) {
                    writer.write(String.format(Locale.US, "  \"%s\" -- \"%s\" [label=\"%.2f km\"];\n",
                            id1, id2, edge.getWeight().getDistance()));
                    processedEdges.add(key);
                }
            }

            writer.write("}\n");
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao escrever ficheiro DOT: " + e.getMessage());
            return false;
        }
    }
}