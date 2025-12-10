package main.java.pt.ipp.isep.dei.service;



import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public interface Graph<V, E> extends Cloneable {
    // Informação do Grafo
    boolean isDirected();
    int numVertices();
    ArrayList<V> vertices();

    // Validar e obter vértices
    boolean validVertex(V vert);
    int key(V vert);
    V vertex(int key);
    V vertex(Predicate<V> p);

    // Obter adjacências e arestas
    Collection<V> adjVertices(V vert);
    int numEdges();
    Collection<Edge<V, E>> edges();

    Edge<V, E> edge(V vOrig, V vDest);
    Edge<V, E> edge(int vOrigKey, int vDestKey);

    // Graus (Degrees)
    int outDegree(V vert);
    int inDegree(V vert);

    // Arestas de saída/entrada
    Collection<Edge<V, E>> outgoingEdges(V vert);
    Collection<Edge<V, E>> incomingEdges(V vert);

    // Modificar o Grafo
    boolean addVertex(V vert);
    boolean addEdge(V vOrig, V vDest, E weight);
    boolean removeVertex(V vert);
    boolean removeEdge(V vOrig, V vDest);

    Graph<V, E> clone();
}