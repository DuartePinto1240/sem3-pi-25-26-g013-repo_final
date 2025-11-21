package main.java.pt.ipp.isep.dei.domain;

import java.util.*;

/**
 * Binary Search Tree implementation.
 * @param <E> Type of element, must implement Comparable.
 */
public class BST<E extends Comparable<E>> implements BSTInterface<E> {

    /** Nested static class for a binary search tree node. */
    protected static class Node<E> {
        private E element;
        private Node<E> left;
        private Node<E> right;

        public Node(E e, Node<E> leftChild, Node<E> rightChild) {
            element = e;
            left = leftChild;
            right = rightChild;
        }

        public E getElement() { return element; }
        public Node<E> getLeft() { return left; }
        public Node<E> getRight() { return right; }

        public void setElement(E e) { element = e; }
        public void setLeft(Node<E> leftChild) { left = leftChild; }
        public void setRight(Node<E> rightChild) { right = rightChild; }
    }

    protected Node<E> root = null; // root of the tree

    public BST() {
        root = null;
    }

    protected Node<E> root() {
        return root;
    }

    public boolean isEmpty() {
        return root == null;
    }

    // =====================================================================
    // FIND
    // =====================================================================
    protected Node<E> find(Node<E> node, E element) {
        if (node == null)
            return null;

        int cmp = element.compareTo(node.getElement());
        if (cmp == 0)
            return node;
        else if (cmp < 0)
            return find(node.getLeft(), element);
        else
            return find(node.getRight(), element);
    }

    // =====================================================================
    // INSERT
    // =====================================================================
    @Override
    public void insert(E element) {
        root = insert(element, root);
    }

    @Override
    public void remove(E element) {

    }

    private Node<E> insert(E element, Node<E> node) {
        if (node == null)
            return new Node<>(element, null, null);

        int cmp = element.compareTo(node.getElement());
        if (cmp < 0)
            node.setLeft(insert(element, node.getLeft()));
        else if (cmp > 0)
            node.setRight(insert(element, node.getRight()));
        // se for igual, não faz nada (sem duplicados)

        return node;
    }

    // =====================================================================
    // SIZE
    // =====================================================================
    public int size() {
        return size(root);
    }

    private int size(Node<E> node) {
        if (node == null)
            return 0;
        return 1 + size(node.getLeft()) + size(node.getRight());
    }

    // =====================================================================
    // HEIGHT
    // =====================================================================
    public int height() {
        return height(root);
    }

    protected int height(Node<E> node) {
        if (node == null)
            return -1; // por convenção, árvore vazia tem altura -1
        return 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }

    // =====================================================================
    // SMALLEST ELEMENT
    // =====================================================================
    public E smallestElement() {
        if (root == null)
            return null;
        return smallestElement(root);
    }

    @Override
    public Iterable<E> inOrder() {
        List<E> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node<E> node, List<E> result) {
        if (node == null) return;
        inOrderRec(node.getLeft(), result);
        result.add(node.getElement());
        inOrderRec(node.getRight(), result);
    }

    @Override
    public Iterable<E> preOrder() {
        List<E> result = new ArrayList<>();
        preOrderRec(root, result);
        return result;
    }

    private void preOrderRec(Node<E> node, List<E> result) {
        if (node == null) return;
        result.add(node.getElement());
        preOrderRec(node.getLeft(), result);
        preOrderRec(node.getRight(), result);
    }

    @Override
    public Iterable<E> posOrder() {
        List<E> result = new ArrayList<>();
        posOrderRec(root, result);
        return result;
    }

    private void posOrderRec(Node<E> node, List<E> result) {
        if (node == null) return;
        posOrderRec(node.getLeft(), result);
        posOrderRec(node.getRight(), result);
        result.add(node.getElement());
    }

    protected E smallestElement(Node<E> node) {
        if (node == null)
            return null;
        while (node.getLeft() != null)
            node = node.getLeft();
        return node.getElement();
    }

    // =====================================================================
    // NODES BY LEVEL
    // =====================================================================
    public Map<Integer, List<E>> nodesByLevel() {
        Map<Integer, List<E>> result = new TreeMap<>();
        processBstByLevel(root, result, 0);
        return result;
    }

    private void processBstByLevel(Node<E> node, Map<Integer, List<E>> result, int level) {
        if (node == null)
            return;

        result.computeIfAbsent(level, k -> new ArrayList<>()).add(node.getElement());

        processBstByLevel(node.getLeft(), result, level + 1);
        processBstByLevel(node.getRight(), result, level + 1);
    }

    // =====================================================================
    // TO STRING (já estava implementado)
    // =====================================================================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringRec(root, 0, sb);
        return sb.toString();
    }

    private void toStringRec(Node<E> root, int level, StringBuilder sb) {
        if (root == null)
            return;
        toStringRec(root.getRight(), level + 1, sb);
        if (level != 0) {
            for (int i = 0; i < level - 1; i++)
                sb.append("|\t");
            sb.append("|-------").append(root.getElement()).append("\n");
        } else
            sb.append(root.getElement()).append("\n");
        toStringRec(root.getLeft(), level + 1, sb);
    }
}
