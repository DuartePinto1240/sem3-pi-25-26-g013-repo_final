package main.java.pt.ipp.isep.dei.domain;

import java.util.*;

/**
 * Extension of BST class to add inOrder traversal implementation.
 * This can be added to the existing BST class or used as reference.
 */
public class BTSExtension {



    /**
     * Add this method to your BST class to enable inOrder traversal.
     *
     * Returns an iterable collection of elements in ascending order.
     * Temporal Complexity: O(n)
     */
    public static <E extends Comparable<E>> Iterable<E> inOrder(BST<E> tree) {
        List<E> result = new ArrayList<>();
        inOrderRec(tree.root(), result);
        return result;
    }

    private static <E extends Comparable<E>> void inOrderRec(BST.Node<E> node, List<E> result) {
        if (node == null) return;

        inOrderRec(node.getLeft(), result);
        result.add(node.getElement());
        inOrderRec(node.getRight(), result);
    }

    /**
     * Alternatively, if you can modify BST.java directly, add this:
     */
    public static String getCodeToAddToBST() {
        return """
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
        """;
    }
}
