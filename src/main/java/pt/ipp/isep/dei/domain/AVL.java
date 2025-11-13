package main.java.pt.ipp.isep.dei.domain;

/**
 * AVL Tree implementation extending BST.
 * @param <E> Type of element, must be Comparable.
 */
public class AVL<E extends Comparable<E>> extends BST<E> {

    // ---------- MÉTODOS AUXILIARES DE BALANCEAMENTO ----------



    private int balanceFactor(Node<E> node) {
        if (node == null) return 0;
        return height(node.getLeft()) - height(node.getRight());
    }

    private Node<E> rightRotation(Node<E> node) {
        Node<E> newRoot = node.getLeft();
        node.setLeft(newRoot.getRight());
        newRoot.setRight(node);
        return newRoot;
    }

    private Node<E> leftRotation(Node<E> node) {
        Node<E> newRoot = node.getRight();
        node.setRight(newRoot.getLeft());
        newRoot.setLeft(node);
        return newRoot;
    }

    /**
     * Realiza duas rotações quando o balanceamento requer.
     * É usada para casos LR (Left-Right) ou RL (Right-Left).
     */
    private Node<E> twoRotations(Node<E> node) {
        if (balanceFactor(node) > 1) { // Left-Right
            node.setLeft(leftRotation(node.getLeft()));
            node = rightRotation(node);
        } else if (balanceFactor(node) < -1) { // Right-Left
            node.setRight(rightRotation(node.getRight()));
            node = leftRotation(node);
        }
        return node;
    }

    private Node<E> balanceNode(Node<E> node) {
        int balance = balanceFactor(node);

        // Caso de desbalanceamento para a esquerda
        if (balance > 1) {
            if (balanceFactor(node.getLeft()) >= 0)
                node = rightRotation(node);          // Caso LL
            else
                node = twoRotations(node);           // Caso LR
        }

        // Caso de desbalanceamento para a direita
        else if (balance < -1) {
            if (balanceFactor(node.getRight()) <= 0)
                node = leftRotation(node);           // Caso RR
            else
                node = twoRotations(node);           // Caso RL
        }

        return node;
    }

    // ---------- INSERÇÃO ----------

    @Override
    public void insert(E element) {
        root = insert(element, root);
    }

    private Node<E> insert(E element, Node<E> node) {
        if (node == null)
            return new Node<>(element, null, null);

        if (element.compareTo(node.getElement()) < 0)
            node.setLeft(insert(element, node.getLeft()));
        else if (element.compareTo(node.getElement()) > 0)
            node.setRight(insert(element, node.getRight()));
        else
            return node; // duplicados não são permitidos

        return balanceNode(node);
    }

    // ---------- REMOÇÃO ----------

    @Override
    public void remove(E element) {
        root = remove(element, root());
    }

    private Node<E> remove(E element, Node<E> node) {
        if (node == null)
            return null;

        if (element.compareTo(node.getElement()) == 0) { // nó a remover
            if (node.getLeft() == null && node.getRight() == null)
                return null;
            if (node.getLeft() == null)
                return node.getRight();
            if (node.getRight() == null)
                return node.getLeft();

            // nó com dois filhos → substitui pelo menor da subárvore direita
            E smallElem = smallestElement(node.getRight());
            node.setElement(smallElem);
            node.setRight(remove(smallElem, node.getRight()));
        } else if (element.compareTo(node.getElement()) < 0) {
            node.setLeft(remove(element, node.getLeft()));
        } else {
            node.setRight(remove(element, node.getRight()));
        }

        return balanceNode(node);
    }

    // ---------- EQUALS ----------

    public boolean equals(Object otherObj) {
        if (this == otherObj)
            return true;
        if (otherObj == null || this.getClass() != otherObj.getClass())
            return false;

        AVL<E> second = (AVL<E>) otherObj;
        return equals(root, second.root);
    }

    public boolean equals(Node<E> root1, Node<E> root2) {
        if (root1 == null && root2 == null)
            return true;
        else if (root1 != null && root2 != null) {
            if (root1.getElement().compareTo(root2.getElement()) == 0) {
                return equals(root1.getLeft(), root2.getLeft())
                        && equals(root1.getRight(), root2.getRight());
            } else
                return false;
        } else
            return false;
    }
}
