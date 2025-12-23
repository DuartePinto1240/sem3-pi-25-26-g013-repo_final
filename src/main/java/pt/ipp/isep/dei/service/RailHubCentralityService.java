package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.dto.HubCentralityResultDTO;

import java.util.*;

/**
 * USEI13 - Rail Hub Centrality Analysis.
 *
 * Computes:
 *  - degree / strength
 *  - betweenness (Brandes, weighted with Dijkstra using Track.distance)
 *  - harmonic closeness (sum(1/dist) using Track.distance)
 * Then normalises each component to [0,1] and builds HubScore.
 */
public class RailHubCentralityService {

    /** Fixed weights from the statement. */
    private static final double W_BETW = 0.35;
    private static final double W_HC = 0.35;
    private static final double W_STRENGTH = 0.30;

    /**
     * Computes the hub ranking for the whole network.
     *
     * @param g railway network graph
     * @return list of per-station results sorted by hubScore descending
     */
    public List<HubCentralityResultDTO> computeHubRanking(Graph<Station, Track> g) {
        if (g == null || g.numVertices() == 0) return Collections.emptyList();

        List<Station> vertices = g.vertices();

        Map<Station, Integer> degree = computeDegree(g, vertices);
        Map<Station, Double> strength = computeStrengthByCapacity(g, vertices);
        Map<Station, Double> betw = computeBetweennessBrandesWeighted(g, vertices);
        Map<Station, Double> hc = computeHarmonicCloseness(g, vertices);

        Map<Station, Double> betwNorm = normalize(betw, vertices);
        Map<Station, Double> hcNorm = normalize(hc, vertices);
        Map<Station, Double> strengthNorm = normalize(strength, vertices);

        List<HubCentralityResultDTO> out = new ArrayList<>(vertices.size());
        for (Station s : vertices) {
            double hubScore = hubScore(betwNorm.get(s), hcNorm.get(s), strengthNorm.get(s));

            out.add(new HubCentralityResultDTO(
                    s.getId(),
                    s.getName(),
                    degree.get(s),
                    strength.get(s),
                    betw.get(s),
                    hc.get(s),
                    hubScore,
                    strengthNorm.get(s),
                    betwNorm.get(s),
                    hcNorm.get(s)
            ));
        }

        out.sort((a, b) -> Double.compare(b.hubScore, a.hubScore));
        return out;
    }

    /**
     * HubScore calculation based on already normalised components.
     */
    public static double hubScore(double betwNorm, double harmonicClosenessNorm, double strengthNorm) {
        return W_BETW * betwNorm + W_HC * harmonicClosenessNorm + W_STRENGTH * strengthNorm;
    }

    private Map<Station, Integer> computeDegree(Graph<Station, Track> g, List<Station> vs) {
        Map<Station, Integer> res = new HashMap<>();
        for (Station v : vs) res.put(v, g.outDegree(v));
        return res;
    }

    private Map<Station, Double> computeStrengthByCapacity(Graph<Station, Track> g, List<Station> vs) {
        Map<Station, Double> res = new HashMap<>();
        for (Station v : vs) {
            double sum = 0.0;
            for (Edge<Station, Track> e : g.outgoingEdges(v)) {
                sum += e.getWeight().getCapacity();
            }
            res.put(v, sum);
        }
        return res;
    }

    private Map<Station, Double> computeHarmonicCloseness(Graph<Station, Track> g, List<Station> vs) {
        Map<Station, Double> res = new HashMap<>();
        for (Station s : vs) {
            Map<Station, Double> dist = dijkstraDistanceByTrackDistance(g, vs, s);

            double sumInv = 0.0;
            for (Station t : vs) {
                if (t.equals(s)) continue;
                double d = dist.get(t);
                if (Double.isInfinite(d) || d <= 0.0) continue;
                sumInv += 1.0 / d;
            }
            res.put(s, sumInv);
        }
        return res;
    }

    /**
     * Brandes betweenness centrality for weighted graphs.
     * Uses Dijkstra for shortest paths with edge weight = Track.distance.
     */
    private Map<Station, Double> computeBetweennessBrandesWeighted(Graph<Station, Track> g, List<Station> vs) {
        Map<Station, Double> CB = new HashMap<>();
        for (Station v : vs) CB.put(v, 0.0);

        for (Station s : vs) {
            Deque<Station> S = new ArrayDeque<>();

            Map<Station, List<Station>> P = new HashMap<>();
            Map<Station, Double> sigma = new HashMap<>();
            Map<Station, Double> dist = new HashMap<>();

            for (Station v : vs) {
                P.put(v, new ArrayList<>());
                sigma.put(v, 0.0);
                dist.put(v, Double.POSITIVE_INFINITY);
            }

            sigma.put(s, 1.0);
            dist.put(s, 0.0);

            PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a.dist));
            pq.add(new NodeDist(s, 0.0));

            while (!pq.isEmpty()) {
                NodeDist nd = pq.poll();
                Station v = nd.node;

                if (nd.dist > dist.get(v)) continue;
                S.push(v);

                for (Edge<Station, Track> edge : g.outgoingEdges(v)) {
                    Station w = edge.getVDest();
                    double weight = edge.getWeight().getDistance();
                    double vwDist = dist.get(v) + weight;

                    if (vwDist < dist.get(w)) {
                        dist.put(w, vwDist);
                        pq.add(new NodeDist(w, vwDist));
                        sigma.put(w, sigma.get(v));
                        P.get(w).clear();
                        P.get(w).add(v);
                    } else if (vwDist == dist.get(w)) {
                        sigma.put(w, sigma.get(w) + sigma.get(v));
                        P.get(w).add(v);
                    }
                }
            }

            Map<Station, Double> delta = new HashMap<>();
            for (Station v : vs) delta.put(v, 0.0);

            while (!S.isEmpty()) {
                Station w = S.pop();
                for (Station v : P.get(w)) {
                    double sigW = sigma.get(w);
                    if (sigW == 0.0) continue; // unreachable
                    double c = (sigma.get(v) / sigW) * (1.0 + delta.get(w));
                    delta.put(v, delta.get(v) + c);
                }
                if (!w.equals(s)) {
                    CB.put(w, CB.get(w) + delta.get(w));
                }
            }
        }

        // Brandes correction for undirected graphs
        if (!g.isDirected()) {
            for (Station v : vs) CB.put(v, CB.get(v) / 2.0);
        }
        return CB;
    }

    private Map<Station, Double> dijkstraDistanceByTrackDistance(Graph<Station, Track> g, List<Station> vs, Station source) {
        Map<Station, Double> dist = new HashMap<>();
        for (Station v : vs) dist.put(v, Double.POSITIVE_INFINITY);
        dist.put(source, 0.0);

        PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a.dist));
        pq.add(new NodeDist(source, 0.0));

        while (!pq.isEmpty()) {
            NodeDist cur = pq.poll();
            if (cur.dist > dist.get(cur.node)) continue;

            for (Edge<Station, Track> e : g.outgoingEdges(cur.node)) {
                Station nxt = e.getVDest();
                double w = e.getWeight().getDistance();
                double nd = cur.dist + w;

                if (nd < dist.get(nxt)) {
                    dist.put(nxt, nd);
                    pq.add(new NodeDist(nxt, nd));
                }
            }
        }
        return dist;
    }

    /**
     * Min-max normalisation to [0,1].
     * Package-private so tests can call it.
     */
    public static Map<Station, Double> normalize(Map<Station, Double> values, List<Station> vs) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (Station v : vs) {
            double x = values.get(v);
            if (x < min) min = x;
            if (x > max) max = x;
        }

        Map<Station, Double> out = new HashMap<>();
        if (Double.compare(max, min) == 0) {
            for (Station v : vs) out.put(v, 0.0);
            return out;
        }

        for (Station v : vs) {
            double x = values.get(v);
            out.put(v, (x - min) / (max - min));
        }
        return out;
    }

    private static class NodeDist {
        final Station node;
        final double dist;
        NodeDist(Station node, double dist) { this.node = node; this.dist = dist; }
    }
}
