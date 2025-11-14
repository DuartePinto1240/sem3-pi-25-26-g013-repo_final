package main.java.pt.ipp.isep.dei.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;

/**
 * Implementação da 2D-Tree (KD-Tree, k=2) para a USEI07
 *.
 *
 * Esta classe ESTENDE a classe BST e usa TwoDNode como
 * o seu elemento.
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
        //
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
     *.
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

    /**
     * Retorna o tamanho da árvore (herdado da BST).
     */
    @Override
    public int size() {
        return super.size();
    }

    /**
     * Retorna a altura da árvore (herdado da BST).
     */
    @Override
    public int height() {
        return super.height();
    }

    /**
     * Retorna todos os tamanhos distintos de "buckets".
     */
    public Set<Integer> getDistinctBucketSizes() {
        Set<Integer> bucketSizes = new HashSet<>();
        collectBucketSizes(this.root, bucketSizes);
        return bucketSizes;
    }

    private void collectBucketSizes(Node<TwoDNode> node, Set<Integer> bucketSizes) {
        if (node == null) {
            return;
        }
        bucketSizes.add(node.getElement().getBucketSize());
        collectBucketSizes(node.getLeft(), bucketSizes);
        collectBucketSizes(node.getRight(), bucketSizes);
    }
}