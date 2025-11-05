package main.java.pt.ipp.isep.dei.domain;

import java.util.function.BiConsumer;

/**
 * Generic AVL Tree implementation for key-value pairs.
 * Self-balancing binary search tree with O(log n) operations.
 *
 * @param <K> the type of keys (must be Comparable)
 * @param <V> the type of values
 */
public class AVLTree<K extends Comparable<K>, V> {

    /**
     * Node class for AVL tree.
     */
    private class Node {
        K key;
        V value;
        Node left;
        Node right;
        int height;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node root;
    private int size;

    public AVLTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Returns the number of key-value pairs in the tree.
     *
     * Temporal Complexity: O(1)
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if the tree is empty.
     *
     * Temporal Complexity: O(1)
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Returns the height of the tree.
     *
     * Temporal Complexity: O(1)
     */
    public int height() {
        return height(root);
    }

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    /**
     * Returns the balance factor of a node.
     */
    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    /**
     * Updates the height of a node.
     */
    private void updateHeight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    /**
     * Right rotation.
     */
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    /**
     * Left rotation.
     */
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    /**
     * Balances the node after insertion or deletion.
     */
    private Node balance(Node node) {
        if (node == null) return null;

        updateHeight(node);
        int balance = getBalance(node);

        // Left-Left Case
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        // Left-Right Case
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right-Right Case
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Right-Left Case
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    /**
     * Inserts a key-value pair into the tree.
     * If key exists, updates the value.
     *
     * Temporal Complexity: O(log n)
     *
     * @param key the key
     * @param value the value
     */
    public void insert(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        root = insertRec(root, key, value);
    }

    private Node insertRec(Node node, K key, V value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = insertRec(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, key, value);
        } else {
            // Key exists, update value
            node.value = value;
            return node;
        }

        return balance(node);
    }

    /**
     * Finds and returns the value associated with the given key.
     *
     * Temporal Complexity: O(log n)
     *
     * @param key the key to search for
     * @return the value, or null if not found
     */
    public V find(K key) {
        if (key == null) return null;
        return findRec(root, key);
    }

    private V findRec(Node node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            return findRec(node.left, key);
        } else if (cmp > 0) {
            return findRec(node.right, key);
        } else {
            return node.value;
        }
    }

    /**
     * Returns true if the tree contains the given key.
     *
     * Temporal Complexity: O(log n)
     */
    public boolean contains(K key) {
        return find(key) != null;
    }

    /**
     * Removes the key-value pair with the given key.
     *
     * Temporal Complexity: O(log n)
     *
     * @param key the key to remove
     * @return the value that was removed, or null if key not found
     */
    public V remove(K key) {
        if (key == null) return null;

        V[] removed = (V[]) new Object[1];
        root = removeRec(root, key, removed);

        if (removed[0] != null) {
            size--;
        }

        return removed[0];
    }

    private Node removeRec(Node node, K key, V[] removed) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = removeRec(node.left, key, removed);
        } else if (cmp > 0) {
            node.right = removeRec(node.right, key, removed);
        } else {
            // Found the node to remove
            removed[0] = node.value;

            // Node with only one child or no child
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }

            // Node with two children: get inorder successor
            Node successor = findMin(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = removeRec(node.right, successor.key, (V[]) new Object[1]);
        }

        return balance(node);
    }

    /**
     * Finds the node with minimum key in the subtree.
     */
    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Performs an in-order traversal and applies the consumer to each key-value pair.
     * Keys are visited in ascending order.
     *
     * Temporal Complexity: O(n)
     *
     * @param consumer the function to apply to each key-value pair
     */
    public void inOrder(BiConsumer<K, V> consumer) {
        inOrderRec(root, consumer);
    }

    private void inOrderRec(Node node, BiConsumer<K, V> consumer) {
        if (node != null) {
            inOrderRec(node.left, consumer);
            consumer.accept(node.key, node.value);
            inOrderRec(node.right, consumer);
        }
    }

    /**
     * Returns the smallest key in the tree.
     *
     * Temporal Complexity: O(log n)
     */
    public K min() {
        if (isEmpty()) return null;
        return findMin(root).key;
    }

    /**
     * Returns the largest key in the tree.
     *
     * Temporal Complexity: O(log n)
     */
    public K max() {
        if (isEmpty()) return null;
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.key;
    }

    /**
     * Clears all entries from the tree.
     *
     * Temporal Complexity: O(1)
     */
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        inOrder((k, v) -> {
            sb.append(k).append("=").append(v).append(", ");
        });
        sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }
}
