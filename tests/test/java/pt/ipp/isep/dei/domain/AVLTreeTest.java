package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.AVLTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    private AVLTree<Integer, String> tree;

    @BeforeEach
    void setup() {
        tree = new AVLTree<>();
    }

    @Test
    void testEmptyTree() {
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertNull(tree.min());
        assertNull(tree.max());
        assertEquals("{}", tree.toString());
    }

    @Test
    void testInsertAndFind() {
        tree.insert(10, "A");
        tree.insert(20, "B");
        tree.insert(5, "C");

        assertEquals(3, tree.size());
        assertEquals("A", tree.find(10));
        assertEquals("B", tree.find(20));
        assertEquals("C", tree.find(5));
    }

    @Test
    void testInsertUpdateValue() {
        tree.insert(10, "A");
        tree.insert(10, "Updated");

        assertEquals(1, tree.size());
        assertEquals("Updated", tree.find(10));
    }

    @Test
    void testContains() {
        tree.insert(10, "A");
        assertTrue(tree.contains(10));
        assertFalse(tree.contains(20));
    }

    @Test
    void testMinMax() {
        tree.insert(10, "A");
        tree.insert(5, "B");
        tree.insert(15, "C");

        assertEquals(5, tree.min());
        assertEquals(15, tree.max());
    }

    @Test
    void testRemoveLeafNode() {
        tree.insert(10, "A");
        tree.insert(5, "B");
        tree.insert(20, "C");

        assertEquals("B", tree.remove(5));
        assertEquals(2, tree.size());
        assertFalse(tree.contains(5));
    }

    @Test
    void testRemoveNodeWithOneChild() {
        tree.insert(10, "A");
        tree.insert(5, "B");
        tree.insert(2, "C");

        assertEquals("B", tree.remove(5));
        assertEquals(2, tree.size());
        assertTrue(tree.contains(2));
    }

    @Test
    void testRemoveNodeWithTwoChildren() {
        tree.insert(10, "A");
        tree.insert(5, "B");
        tree.insert(20, "C");
        tree.insert(15, "D");
        tree.insert(25, "E");

        assertEquals("C", tree.remove(20)); // has two children
        assertEquals(4, tree.size());
        assertFalse(tree.contains(20));
    }

    @Test
    void testInOrderTraversal() {
        tree.insert(10, "A");
        tree.insert(5, "B");
        tree.insert(15, "C");

        StringBuilder sb = new StringBuilder();
        tree.inOrder((k, v) -> sb.append(k).append("-"));

        assertEquals("5-10-15-", sb.toString());
    }

    @Test
    void testToString() {
        tree.insert(10, "A");
        tree.insert(5, "B");
        tree.insert(15, "C");

        String result = tree.toString();
        assertTrue(result.contains("5=B"));
        assertTrue(result.contains("10=A"));
        assertTrue(result.contains("15=C"));
        assertTrue(result.startsWith("{") && result.endsWith("}"));
    }

    @Test
    void testClear() {
        tree.insert(10, "A");
        tree.insert(20, "B");

        tree.clear();

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
    }


}

