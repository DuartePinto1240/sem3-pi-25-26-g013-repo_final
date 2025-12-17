package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.Track;

import java.util.*;

public class EdmondsKarpMaxFlow<V> {

    public double maxFlow(Graph<V, Track> graph, V source, V sink) {

        if (!graph.validVertex(source) || !graph.validVertex(sink))
            throw new IllegalArgumentException("Invalid source or sink");

        Map<V, Map<V, Double>> residual = new HashMap<>();

        for (V v : graph.vertices())
            residual.put(v, new HashMap<>());

        for (Edge<V, Track> e : graph.edges()) {
            double cap = e.getWeight().getCapacity();
            residual.get(e.getVOrig()).put(e.getVDest(), cap);
            residual.get(e.getVDest()).putIfAbsent(e.getVOrig(), 0.0);
        }

        double maxFlow = 0.0;
        Map<V, V> parent = new HashMap<>();

        while (bfs(residual, source, sink, parent)) {

            double pathFlow = Double.MAX_VALUE;
            V v = sink;

            while (!v.equals(source)) {
                V u = parent.get(v);
                pathFlow = Math.min(pathFlow, residual.get(u).get(v));
                v = u;
            }

            v = sink;
            while (!v.equals(source)) {
                V u = parent.get(v);
                residual.get(u).put(v, residual.get(u).get(v) - pathFlow);
                residual.get(v).put(u, residual.get(v).get(u) + pathFlow);
                v = u;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private boolean bfs(Map<V, Map<V, Double>> residual,
                        V source,
                        V sink,
                        Map<V, V> parent) {

        parent.clear();
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();

        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            V u = queue.poll();

            for (Map.Entry<V, Double> entry : residual.get(u).entrySet()) {
                V v = entry.getKey();
                double cap = entry.getValue();

                if (!visited.contains(v) && cap > 0) {
                    parent.put(v, u);
                    visited.add(v);
                    queue.add(v);

                    if (v.equals(sink))
                        return true;
                }
            }
        }
        return false;
    }
}