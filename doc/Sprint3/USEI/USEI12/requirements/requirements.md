# USEI12 — Minimal Backbone Network

## 1. Requirements Engineering

### 1.1. User Story Description

**Context:**
[cite_start]To estimate a cost baseline for maintenance or expansion, it is necessary to have a minimal track subset that connects all reachable stations[cite: 873].

**User Story:**
> [cite_start]As an infrastructure planner, I want to compute a minimum network over the rail network (treated as undirected) so that I can identify a minimal backbone that connects all stations with minimum total track length[cite: 874].

---

### 1.2. Customer Specifications and Clarifications

- [cite_start]**Undirected Graph:** The rail network must be treated as **undirected** for this calculation, ignoring original directional constraints[cite: 874].
- [cite_start]**Minimization Metric:** The objective is to minimize the **total track length** (distance)[cite: 874].
- [cite_start]**Connectivity:** The resulting backbone must connect **all reachable stations**[cite: 873].
- **Visual Output:**
  - [cite_start]Generate a **DOT file** where each vertex represents a station placed at its specific **XY coordinates** (to resemble the Belgium train global map)[cite: 877, 878].
  - [cite_start]Convert the DOT file into an **SVG file** using the specific Graphviz application **"neato"** for easy visualization[cite: 879].
- [cite_start]**Complexity:** A temporal complexity analysis is required[cite: 880].

---

### 1.3. Acceptance Criteria

| ID | Requirement |
|----|-------------|
| **AC1** | [cite_start]The algorithm must treat the graph as undirected and weighted by distance (kilometers)[cite: 874]. |
| **AC2** | Implement a Minimum Spanning Tree (MST) algorithm (e.g., Prim's or Kruskal's). |
| **AC3** | [cite_start]The result must be a connected graph (sub-graph) with the minimum possible total distance[cite: 876]. |
| **AC4** | [cite_start]Generate a `.dot` file where nodes include the `pos` attribute based on station XY coordinates[cite: 878]. |
| **AC5** | [cite_start]Successfully execute the external tool **"neato"** to generate an `.svg` file from the `.dot` file[cite: 879]. |
| **AC6** | [cite_start]Provide the temporal analysis (Big-O notation) of the solution[cite: 880]. |

---

### 1.4. Found-Out Dependencies

- [cite_start]**Graph Model:** Relies on the Node definition which includes `XY coordinates` for the "neato" visualization[cite: 855].
- [cite_start]**External Tool:** Requires **Graphviz** installed (specifically the `neato` executable)[cite: 855].

---

### 1.5. Input and Output Data

**Input:**
- [cite_start]The Railway Network Graph (Nodes with Name/ID/XY Coordinates; Edges with Distance) [cite: 855-856].

**Output:**
- [cite_start]A Graph object representing the Minimal Backbone[cite: 876].
- [cite_start]A text file with `.dot` extension[cite: 877].
- [cite_start]An image file with `.svg` extension[cite: 879].

---

## 2. Design & Algorithm

### 2.1. Algorithm Overview

The problem describes finding a **Minimum Spanning Tree (MST)**. Since the goal is to connect all vertices with minimum total weight (distance) in an undirected graph, **Prim's Algorithm** is a suitable choice given the likely density of a railway network.

The process involves:
1.  **MST Computation:** Use Prim's algorithm to identify the subset of edges.
2.  **Visualization:** Iterate over the resulting MST to generate a DOT language string, ensuring the `pos` attribute is set for each node to fix its position geographically (conceptually similar to the Belgium map).
3.  **Process Execution:** Invoke the system command to run `neato`.

---

### 2.2. Pseudocode

```java
Graph getMinimalBackbone(Graph railwayNetwork) {
    // 1. Initialize empty graph for MST (Undirected)
    Graph mst = new Graph(true); 
    
    // structures for Prim's Algorithm
    List<Vertex> visited = new ArrayList<>();
    PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(Edge::getWeight));

    // Start from an arbitrary vertex (e.g., first one)
    Vertex start = railwayNetwork.vertices().get(0);
    visited.add(start);
    addEdgesToPQ(start, pq, visited); // Add adjacent edges of start to PQ

    // 2. Build MST
    while (!pq.isEmpty() && visited.size() < railwayNetwork.numVertices()) {
        Edge edge = pq.poll();
        Vertex nextVertex = edge.getOtherVertex(visited); // Get the target node

        if (nextVertex != null && !visited.contains(nextVertex)) {
            visited.add(nextVertex);
            mst.addEdge(edge); // Add edge to the result graph
            addEdgesToPQ(nextVertex, pq, visited);
        }
    }
    return mst;
}

void exportToSvg(Graph mst, String filename) {
    // 3. Generate DOT content
    StringBuilder dot = new StringBuilder("graph Backbone {\n");
    
    for (Vertex v : mst.vertices()) {
        // '!' in neato forces the node to that exact position
        dot.append(String.format("  \"%s\" [pos=\"%f,%f!\"];\n", 
                   v.getId(), v.getX(), v.getY()));
    }

    for (Edge e : mst.edges()) {
        dot.append(String.format("  \"%s\" -- \"%s\" [label=\"%.2f\"];\n", 
                   e.getSource(), e.getTarget(), e.getWeight()));
    }
    dot.append("}");
    
    saveFile(filename + ".dot", dot.toString());
    
    // 4. Run Neato
    // Command: neato -Tsvg output.dot -o output.svg
    Runtime.getRuntime().exec("neato -Tsvg " + filename + ".dot -o " + filename + ".svg");
}
```

## 5. Temporal Analysis (Complexity)

| Operation | Complexity | Explanation |
|-----------|------------|-------------|
| **MST (Prim's)** | $O(E \log V)$ | Using a Binary Heap (Priority Queue). Each edge is processed at most once. |
| **DOT Generation** | $O(V + E)$ | Requires iterating over all $V$ vertices and $E'$ edges (where $E' = V-1$ in a tree) to build the string. |
| **Total Complexity** | **$O(E \log V)$** | The complexity is dominated by the sorting/priority queue operations in Prim's algorithm. |

*Where $V$ is the number of stations and $E$ is the number of connections in the network.*