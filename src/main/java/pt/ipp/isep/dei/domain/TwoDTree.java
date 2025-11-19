package main.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.util.GeoUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementação da 2D-Tree (KD-Tree, k=2) para a USEI07
 *
 * Esta classe ESTENDE a classe BST e usa TwoDNode como
 * o seu elemento.
 *
 * MODIFIED for USEI09: Added nearest-neighbour search methods with proper filtering logic.
 */
public class TwoDTree extends BST<TwoDNode> {

    /**
     * Construtor principal da USEI07.
     * Recebe a lista de TODAS as estações e
     * constrói a árvore balanceada.
     *
     * @param allStations A lista completa de TrainStation.
     */
    public TwoDTree(List<TrainStation> allStations) {
        super(); // Chama o construtor da BST (que faz root = null)

        // 1. Pré-processamento: Agrupar estações por coordenada
        Map<String, TwoDNode> nodesMap = new HashMap<>();
        for (TrainStation station : allStations) {
            String coordKey = station.getLatitude() + "," + station.getLongitude();

            TwoDNode bucketNode = nodesMap.get(coordKey);
            if (bucketNode == null) {
                // Primeira estação nesta coordenada
                nodesMap.put(coordKey, new TwoDNode(station));
            } else {
                // Já existe um "bucket", adiciona a estação
                bucketNode.addStation(station);
            }
        }

        // 2. Obter a lista de "buckets" (nós únicos)
        List<TwoDNode> uniqueNodes = new ArrayList<>(nodesMap.values());

        // 3. Construir a árvore balanceada e definir o root
        this.root = buildRecursive(uniqueNodes, 0);
    }

    /**
     * O coração da "balanced build strategy"
     * Constrói a árvore recursivamente, dividindo pela mediana.
     */
    private Node<TwoDNode> buildRecursive(List<TwoDNode> nodes, int depth) {

        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        // 1. Determinar o eixo (dimensão) para ordenar
        int axis = depth % 2;

        // 2. Ordenar a lista com base no eixo atual
        if (axis == 0) { // Compara por Latitude
            nodes.sort(Comparator.comparingDouble(TwoDNode::getLatitude));
        } else { // Compara por Longitude
            nodes.sort(Comparator.comparingDouble(TwoDNode::getLongitude));
        }

        // 3. Encontrar a mediana
        int medianIndex = nodes.size() / 2;
        TwoDNode medianElement = nodes.get(medianIndex);

        // 4. Criar o nó da BST
        Node<TwoDNode> medianNode = new Node<>(medianElement, null, null);

        // 5. Criar as sub-listas para os filhos
        List<TwoDNode> leftHalf = new ArrayList<>(nodes.subList(0, medianIndex));
        List<TwoDNode> rightHalf = new ArrayList<>(nodes.subList(medianIndex + 1, nodes.size()));

        // 6. Chamar recursivamente e ligar os filhos
        medianNode.setLeft(buildRecursive(leftHalf, depth + 1));
        medianNode.setRight(buildRecursive(rightHalf, depth + 1));

        return medianNode;
    }

    /**
     * Esta classe não suporta o insert() normal da BST (que é 1D).
     */
    @Override
    public void insert(TwoDNode element) {
        throw new UnsupportedOperationException(
                "Use o construtor (bulk build) para criar esta 2D-Tree.");
    }

    // --- MÉTODOS PARA OS "RETURNS" DA USEI07 ---
    //
    @Override
    public int size() { return super.size(); }
    @Override
    public int height() { return super.height(); }

    public Set<Integer> getDistinctBucketSizes() {
        Set<Integer> bucketSizes = new HashSet<>();
        collectBucketSizes(this.root, bucketSizes);
        return bucketSizes;
    }

    private void collectBucketSizes(Node<TwoDNode> node, Set<Integer> bucketSizes) {
        if (node == null) return;
        bucketSizes.add(node.getElement().getBucketSize());
        collectBucketSizes(node.getLeft(), bucketSizes);
        collectBucketSizes(node.getRight(), bucketSizes);
    }

    // =========================================================================
    // === MÉTODOS ADICIONADOS PARA A USEI09 - CORRECTED FOR FILTERING
    // =========================================================================

    /**
     * Public entry point for the Nearest-N search (AC1, AC3).
     *
     * IMPORTANT: The previous single-argument nearestN signature (used by the flawed service logic)
     * is functionally replaced by this two-argument method that includes the filter.
     *
     * @param lat Target latitude
     * @param lon Target longitude
     * @param N Number of stations to find
     * @param timeZoneFilter Optional filter by timeZoneGroup (NEW PARAM)
     * @param visitedNodes (out) An atomic counter to track visited nodes (AC6)
     * @return A sorted list of the N nearest StationDistance objects that match the filter.
     */
    public List<StationDistance> nearestN(double lat, double lon, int N,
                                          String timeZoneFilter, AtomicInteger visitedNodes) {
        validateInputs(lat, lon, N); // AC0: Validate inputs

        // Use a PriorityQueue as a Max-Heap to store the N best candidates.
        PriorityQueue<StationDistance> maxHeap = new PriorityQueue<>(N,
                (sd1, sd2) -> Double.compare(sd2.getDistanceKm(), sd1.getDistanceKm())
        );

        // Start the recursive search, passing the filter.
        recursiveSearch(this.root, lat, lon, N, timeZoneFilter, maxHeap, 0, visitedNodes);

        // Convert heap to sorted list (AC4)
        List<StationDistance> results = new ArrayList<>(maxHeap);
        Collections.sort(results); // Uses StationDistance.compareTo (distance ascending, name tie-breaker)
        return results;
    }

    /**
     * Recursive nearest-neighbor search algorithm with pruning (AC5).
     */
    private void recursiveSearch(Node<TwoDNode> node,
                                 double lat, double lon, int N,
                                 String timeZoneFilter, // ADDED PARAMETER
                                 PriorityQueue<StationDistance> maxHeap,
                                 int depth, AtomicInteger visitedNodes) {

        if (node == null) {
            return;
        }
        visitedNodes.incrementAndGet(); // Track visited nodes (AC6)

        int axis = depth % 2; // 0 for latitude, 1 for longitude
        TwoDNode currentNode = node.getElement();

        // 1. Calculate distance from target to current node
        double distKm = GeoUtils.haversineDistance(lat, lon,
                currentNode.getLatitude(),
                currentNode.getLongitude());

        // Add all stations at this location as candidates
        for (TrainStation station : currentNode.getStations()) {
            // *** THE FIX (AC3) ***: Apply filter here before processing candidate
            if (isFilterMatch(station, timeZoneFilter)) {
                handleCandidate(station, distKm, maxHeap, N);
            }
        }

        // 2. Determine which child subtree to visit first
        double nodeCoord = (axis == 0) ? currentNode.getLatitude() : currentNode.getLongitude();
        double targetCoord = (axis == 0) ? lat : lon;

        Node<TwoDNode> nearerChild = (targetCoord < nodeCoord) ? node.getLeft() : node.getRight();
        Node<TwoDNode> fartherChild = (targetCoord < nodeCoord) ? node.getRight() : node.getLeft();

        // 3. Visit nearer child first
        recursiveSearch(nearerChild, lat, lon, N, timeZoneFilter, maxHeap, depth + 1, visitedNodes); // Pass filter

        // 4. Pruning: Check if the farther child's bounding box *could* have a better candidate (AC5).
        double bestDistSoFar = maxHeap.isEmpty() ? Double.MAX_VALUE : maxHeap.peek().getDistanceKm();

        // Visit farther child if necessary
        if (maxHeap.size() < N ||
                distanceToSplittingPlane(lat, lon, nodeCoord, axis) < bestDistSoFar) {

            recursiveSearch(fartherChild, lat, lon, N, timeZoneFilter, maxHeap, depth + 1, visitedNodes); // Pass filter
        }
    }

    /**
     * Helper method to check if a station matches the time zone filter. (AC3)
     */
    private boolean isFilterMatch(TrainStation station, String timeZoneFilter) {
        if (timeZoneFilter == null || timeZoneFilter.trim().isEmpty()) {
            return true; // No filter applied, always matches
        }
        // Apply filter check (case-insensitive comparison)
        String stationTimeZoneGroup = station.getTimeZoneGroup();
        return stationTimeZoneGroup != null && stationTimeZoneGroup.equalsIgnoreCase(timeZoneFilter);
    }

    /**
     * Helper to check and add a candidate to the max-heap.
     */
    private void handleCandidate(TrainStation station, double distKm,
                                 PriorityQueue<StationDistance> maxHeap, int N) {

        StationDistance candidate = new StationDistance(station, distKm);

        if (maxHeap.size() < N) {
            maxHeap.offer(candidate);
        } else if (distKm < maxHeap.peek().getDistanceKm()) {
            // This new station is closer than the farthest one in our heap
            maxHeap.poll();   // Remove the farthest
            maxHeap.offer(candidate); // Add the new, closer one
        }
    }

    /**
     * Calculates the "distance" from the target point to the splitting plane.
     */
    private double distanceToSplittingPlane(double lat, double lon, double planeCoord, int axis) {
        if (axis == 0) {
            // Plane is horizontal (constant latitude)
            return GeoUtils.haversineDistance(lat, lon, planeCoord, lon);
        } else {
            // Plane is vertical (constant longitude)
            return GeoUtils.haversineDistance(lat, lon, lat, planeCoord);
        }
    }

    /**
     * Validates input parameters as per AC0.
     */
    private void validateInputs(double lat, double lon, int N) {
        if (lat < -90.0 || lat > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90.");
        }
        if (lon < -180.0 || lon > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }
        if (N <= 0) {
            throw new IllegalArgumentException("N (number of stations) must be greater than 0.");
        }
    }
}