# USEI11 — Directed Line Upgrade Plan

## 1. Requirements Engineering

### 1.1. User Story Description

Context: Railway infrastructure planners need to schedule **upgrade operations on directed railway lines** while respecting technical and dependency constraints. Some lines can only be upgraded after others are completed, forming a **directed dependency graph**.

**User Story:**
> As an infrastructure planner, I want to compute a valid upgrade sequence for directed railway lines, detecting dependency conflicts, so that upgrades can be executed safely and in the correct order.

---

### 1.2. Customer Specifications and Clarifications

- The railway network is modeled as a **directed graph**, where:
    - Vertices represent railway line upgrades.
    - Directed edges represent dependency constraints.
- The algorithm must determine whether a valid upgrade order exists.
- If a cycle exists, the system must report that no valid upgrade plan is possible.
- Temporal complexity analysis must be included.

---

### 1.3. Acceptance Criteria

| ID | Requirement |
|----|-------------|
| **AC0** | Validate that the dependency graph is correctly initialized. |
| **AC1** | Use a directed graph representation of railway line dependencies. |
| **AC2** | Compute a valid upgrade ordering using topological sorting. |
| **AC3** | Detect cycles in the dependency graph. |
| **AC4** | If a cycle exists, report failure. |
| **AC5** | If valid, return the ordered list of upgrades. |
| **AC6** | Provide temporal complexity analysis. |

---

### 1.4. Found-Out Dependencies

- Depends on USEI06 / USEI07 for graph construction utilities.
- Uses a directed graph data structure.

---

### 1.5. Input and Output Data

**Input:** Directed graph of line dependencies.

**Output:** Ordered list of upgrades or cycle detection error.

---

## 2. Design & Algorithm

### 2.1. Algorithm Overview

This user story uses **Kahn’s Algorithm** for topological sorting.

---

### 2.2. Pseudocode

```java
List<String> computeUpgradePlan(Graph g) {
    Queue<Vertex> q = new LinkedList<>();
    Map<Vertex, Integer> indegree = computeIndegrees(g);

    for (Vertex v : g.vertices())
        if (indegree.get(v) == 0)
            q.add(v);

    List<String> order = new ArrayList<>();

    while (!q.isEmpty()) {
        Vertex v = q.poll();
        order.add(v.getName());
        for (Vertex n : g.adjacent(v)) {
            indegree.put(n, indegree.get(n) - 1);
            if (indegree.get(n) == 0)
                q.add(n);
        }
    }

    if (order.size() != g.size())
        throw new CycleDetectedException();

    return order;
}
```

---

## 5. Temporal Analysis (Complexity)

| Operation | Average | Worst | Space |
|------------|----------|--------|--------|
| Topological Sort | O(V + E) | O(V + E) | O(V) |

---
