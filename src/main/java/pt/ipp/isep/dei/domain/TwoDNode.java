package main.java.pt.ipp.isep.dei.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Representa o "Elemento" <E> que será armazenado na TwoDTree.
 * Guarda uma lista de objetos TrainStation.
 *
 * Implementa Comparable APENAS para satisfazer o compilador
 * (a restrição da classe BST<E extends Comparable<E>>).
 */
public class TwoDNode implements Comparable<TwoDNode> {

    private double latitude;
    private double longitude;

    // Lista de estações (agora do tipo TrainStation)
    private List<TrainStation> stationsAtLocation;

    /**
     * Construtor de um novo nó (bucket) com a sua primeira estação.
     */
    public TwoDNode(TrainStation firstStation) {
        this.latitude = firstStation.getLatitude();
        this.longitude = firstStation.getLongitude();
        this.stationsAtLocation = new ArrayList<>();
        this.addStation(firstStation);
    }

    /**
     * Adiciona uma estação a este "bucket" (nó).
     * Mantém a lista ordenada por nome (requisito da USEI07)
     *.
     */
    public void addStation(TrainStation station) {
        // Comparador usa TrainStation::getStation para obter o nome
        Comparator<TrainStation> byName = Comparator.comparing(TrainStation::getStation);

        int index = Collections.binarySearch(stationsAtLocation, station, byName);
        if (index < 0) {
            index = -index - 1;
        }
        stationsAtLocation.add(index, station);
    }

    // --- Getters ---

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<TrainStation> getStations() { return stationsAtLocation; }
    public int getBucketSize() { return stationsAtLocation.size(); }

    @Override
    public String toString() {
        return String.format("(%.6f, %.6f): %d estações",
                latitude, longitude, stationsAtLocation.size());
    }

    /**
     * ATENÇÃO: Método adicionado APENAS para satisfazer o compilador
     * e a restrição da classe BST <E extends Comparable<E>>.
     */
    @Override
    public int compareTo(TwoDNode other) {
        int latCmp = Double.compare(this.latitude, other.latitude);
        if (latCmp != 0) {
            return latCmp;
        }
        return Double.compare(this.longitude, other.longitude);
    }
}