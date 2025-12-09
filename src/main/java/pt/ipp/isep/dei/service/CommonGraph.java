package main.java.pt.ipp.isep.dei.service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class CommonGraph<V, E> implements Graph<V, E> {

    protected int numVerts;
    protected int numEdges;
    protected final boolean isDirected;
    protected ArrayList<V> vertices;

    public CommonGraph(boolean directed) {
        numVerts = 0;
        numEdges = 0;
        isDirected = directed;
        vertices = new ArrayList<>();
    }

    @Override
    public boolean isDirected() {
        return isDirected;
    }

    @Override
    public int numVertices() {
        return numVerts;
    }

    @Override
    public ArrayList<V> vertices() {
        return new ArrayList<>(vertices);
    }

    @Override
    public boolean validVertex(V vert) {
        return vertices.contains(vert);
    }

    @Override
    public int key(V vert) {
        return vertices.indexOf(vert);
    }

    @Override
    public V vertex(int key) {
        if (key < 0 || key >= vertices.size()) return null;
        return vertices.get(key);
    }

    @Override
    public V vertex(Predicate<V> p) {
        for (V v : vertices) {
            if (p.test(v)) return v;
        }
        return null;
    }

    @Override
    public int numEdges() {
        return numEdges;
    }

    // --- CORREÇÃO DO ERRO AQUI ---
    // Forçamos o método clone a ser público e abstrato para resolver o conflito com Object
    @Override
    public abstract Graph<V, E> clone();
    // -----------------------------

    protected void copy(Graph<V, E> from, Graph<V, E> to) {
        for (V v : from.vertices()) {
            to.addVertex(v);
        }
        for (Edge<V, E> e : from.edges()) {
            to.addEdge(e.getVOrig(), e.getVDest(), e.getWeight());
        }
    }

    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null || !(otherObj instanceof Graph<?, ?>)) return false;
        Graph<?, ?> otherGraph = (Graph<?, ?>) otherObj;
        if (numVerts != otherGraph.numVertices() || numEdges != otherGraph.numEdges() || isDirected() != otherGraph.isDirected())
            return false;

        ArrayList<V> otherVertices = (ArrayList<V>) otherGraph.vertices();
        return vertices.equals(otherVertices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numVerts, numEdges, isDirected);
    }
}