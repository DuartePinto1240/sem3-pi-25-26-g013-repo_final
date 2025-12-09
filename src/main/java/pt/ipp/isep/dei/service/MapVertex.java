package main.java.pt.ipp.isep.dei.service;


import java.util.*;

/**
 * Represents a Vertex in a Map-based Graph.
 * Holds the element and a Map of outgoing edges.
 *
 * @param <V> Vertex value type
 * @param <E> Edge value type
 */
public class MapVertex<V, E> {

    final private V element;                            // Vertex information
    final private Map<V, Edge<V, E>> outVerts;    // Adjacent vertices (Key: Vertex, Value: Edge)

    public MapVertex(V vert) {
        if (vert == null) throw new RuntimeException("Vertex information cannot be null!");
        element = vert;
        outVerts = new LinkedHashMap<>();
    }

    public V getElement() {
        return element;
    }

    public void addAdjVert(V vAdj, Edge<V, E> edge) {
        outVerts.put(vAdj, edge);
    }

    public void remAdjVert(V vAdj) {
        outVerts.remove(vAdj);
    }

    public Edge<V, E> getEdge(V vAdj) {
        return outVerts.get(vAdj);
    }

    public int numAdjVerts() {
        return outVerts.size();
    }

    public Collection<V> getAllAdjVerts() {
        return new ArrayList<>(outVerts.keySet());
    }

    public Collection<Edge<V, E>> getAllOutEdges() {
        return new ArrayList<>(outVerts.values());
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder(element + ": \n");
        if (!outVerts.isEmpty()) {
            for (V vert : outVerts.keySet()) {
                st.append(outVerts.get(vert)).append("\n");
            }
        }
        return st.toString();
    }
}