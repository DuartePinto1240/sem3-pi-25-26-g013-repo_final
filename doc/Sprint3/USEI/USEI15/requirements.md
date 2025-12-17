# USEI15 — Risk-Aware Shortest Paths

## 1. Requirements Engineering

### 1.1. User Story Description
Context: Some railway connections may experience delays, restrictions, or operational risks. Each connection is modeled as a directed weighted edge with a cost combining distance, penalties, and bonuses. These costs may be negative, preventing the use of Dijkstra’s algorithm.
User Story:

> As a route planner, I want to compute the shortest path between two stations using a cost function that includes distance and penalties, and detect negative cycles, so that I can return correct routes or flag invalid configurations.

### 1.2. Customer Specifications and Clarifications
The network is a directed weighted graph, where:
* Vertices = stations
* Edges = connections with risk-adjusted cost

Cost formula:
$$\text{distance} + \text{penalties} - \text{bonuses (may be negative)}$$

* Must **NOT** use Dijkstra.
* Must use **Bellman–Ford** or equivalent.
* Must **detect negative cycles**.
* If a negative cycle exists:
    * The algorithm must report stations and edges forming the cycle.
* Must provide temporal analysis.

### 1.3. Acceptance Criteria
| ID | Requirement |
| :--- | :--- |
| **AC0** | Validate that the risk-adjusted graph is correctly initialized. |
| **AC1** | Use a directed graph for station connections. |
| **AC2** | Compute shortest paths with an algorithm supporting negative weights. |
| **AC3** | Detect negative cycles. |
| **AC4** | If a cycle exists, return the stations and edges involved. |
| **AC5** | If no cycle exists, return the ordered path and total cost. |
| **AC6** | Output must be deterministic for the same input. |
| **AC7** | Provide temporal complexity analysis. |

### 1.4. Found-Out Dependencies
* Depends on prior graph-construction use cases (USEI06 / USEI07).
* Requires a directed weighted graph structure.
* Requires risk-adjusted edge cost computation.

### 1.5. Input and Output Data
Input:
* Source station ID
* Target station ID
* Directed weighted graph (stations + connections)

Output:
* If no negative cycle: ordered list of stations + total cost.
* If negative cycle exists: error + list of stations/edges forming the cycle.

---

## 2. Design & Algorithm

### 2.1. Algorithm Overview
This user story uses the **Bellman–Ford algorithm**:
* Supports negative weights
* Detects negative cycles
* Produces shortest paths in directed graphs

### 2.2. Pseudocode

```java
ShortestPathResult computeRiskAwarePath(Graph g, Station source, Station target) {
    Map<Station, Double> dist = initializeDistances(g, source);
    Map<Station, Station> parent = new HashMap<>();

    // Relax edges V-1 times
    for (int i = 1; i <= g.size() - 1; i++) {
        for (Edge e : g.edges()) {
            if (dist.get(e.from) + e.cost < dist.get(e.to)) {
                dist.put(e.to, dist.get(e.from) + e.cost);
                parent.put(e.to, e.from);
            }
        }
    }

    // Detect cycles
    for (Edge e : g.edges()) {
        if (dist.get(e.from) + e.cost < dist.get(e.to)) {
            return new NegativeCycleResult(findCycle(e.to, parent));
        }
    }

    return new PathResult(reconstructPath(parent, target), dist.get(target));
}
```
---

## 5. Temporal Analysis (Complexity)

| Operation       | Average  | Worst    | Space |
|-----------------|----------|----------|-------|
| Bellman–Ford    | O(V x E) | O(V x E) | O(V)  |
| Cycle Detection | O(E)     | O(E)     | O(1)  | 

---