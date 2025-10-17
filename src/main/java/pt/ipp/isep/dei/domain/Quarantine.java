package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Quarantine {

    private final Deque<ReturnItem> stack = new ArrayDeque<>();

    public void add(ReturnItem item) {
        stack.push(item);
    }

    public void addAll(List<ReturnItem> items) {
        items.stream()
                .sorted() // by timestamp DESC
                .forEach(this::add);
    }

    public ReturnItem next() {
        return stack.poll();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}
