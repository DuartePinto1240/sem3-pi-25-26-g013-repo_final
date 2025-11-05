package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Represents the quarantine for returned items.
 * <p>
 * Returned items are temporarily stored in this structure before being
 * processed by the {@link QualityOperator}. Items are processed in a
 * Last-In, First-Out (**LIFO**) order by default.
 * </p>
 */
public class Quarantine {

    /** Internal stack that holds the returned items. */
    private final Deque<ReturnItem> stack = new ArrayDeque<>();

    /**
     * Adds an item to the quarantine.
     *
     * @param item the returned item to be added
     */
    public void add(ReturnItem item) {
        stack.push(item);
    }

    /**
     * Adds a list of items to the quarantine.
     * <p>
     * The items are sorted by descending timestamp before being added,
     * ensuring that the most recent ones are processed first (LIFO order).
     * </p>
     *
     * @param items list of items to add
     */
    public void addAll(List<ReturnItem> items) {
        // Sort items: latest first (timestamp DESC, returnId ASC)
        items.stream()
                .sorted() // uses ReturnItem.compareTo()
                .forEachOrdered(stack::addLast); // enqueue preserving sort order
    }

    /**
     * Removes and returns the next item from the quarantine.
     *
     * @return the most recent returned item, or null if empty
     */
    public ReturnItem next() {
        return stack.poll();
    }

    /**
     * Checks if the quarantine is empty.
     *
     * @return true if there are no items, false otherwise
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Returns the number of items currently in the quarantine.
     *
     * @return quantity of items in quarantine
     */
    public int size() {
        return stack.size();
    }
}