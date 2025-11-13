# USEI09 — Proximity Search (Nearest-N with Filters)

## 1. Requirements Engineering

### 1.1. User Story Description

Context: Analysts need to efficiently locate nearby railway stations using geographic coordinates. The 2D KD-tree built in USEI07 should now support **nearest-neighbour queries** with **optional filters**. This search allows planners to find the N closest stations to any coordinate without scanning the full dataset.

**User Story:**
> As an analyst, I want to find the nearest N stations to a given (lat, lon) using the 2D-tree, with optional filters, so I can get accurate nearby results efficiently.

### 1.2. Customer Specifications and Clarifications

- The search must use **Haversine distance** (km) for accurate geographic distances on Earth’s surface.
- The query must support an optional **filter by timeZoneGroup** (e.g., only stations in 'CET' or 'WET/GMT').
- Input validation: latitude ∈ [-90,90], longitude ∈ [-180,180], N > 0.
- Output: list of N nearest stations sorted by distance ascending; if distances are equal, tie-break lexicographically by station name.
- Include **temporal complexity analysis** for the query.

---

### 1.3. Acceptance Criteria

| ID | Requirement |
|----|-------------|
| **AC0** | Validate inputs: coordinates must be within valid ranges and N > 0. |
| **AC1** | Use the KD-tree built in USEI07 to perform nearest-neighbour searches. |
| **AC2** | Apply Haversine formula to compute precise distances between two coordinates. |
| **AC3** | Allow an optional filter by timeZoneGroup. |
| **AC4** | Return exactly N nearest stations sorted by ascending distance, tie-breaking by name. |
| **AC5** | Query must minimize visited KD-tree nodes through pruning. |
| **AC6** | Return and display temporal complexity results (average and worst case). |
| **AC7** | Provide unit tests verifying correctness, ordering, and filtering. |

---

### 1.4. Found-Out Dependencies

- Depends on **USEI07** (balanced KD-tree on latitude/longitude).
- Uses station dataset validated in **USEI06** (BST/AVL indices).
- Optional filter depends on station field `timeZoneGroup`.
- Downstream dependency: **USEI10** (radius-based and density queries).

---

### 1.5. Input and Output Data

**Input:**
- `lat` (float): target latitude
- `lon` (float): target longitude
- `N` (int): number of nearest stations to retrieve
- `timeZoneFilter` (optional string): restricts results to matching time zone group

**Output:**
A sorted list (ascending distance) of up to N stations, where each station object contains:
`stationName`, `country`, `latitude`, `longitude`, `timeZoneGroup`, `distanceKm`.

Example (JSON):
```json
[
  {"stationName": "Lisbon Oriente", "country": "PT", "latitude": 38.767, "longitude": -9.099, "timeZoneGroup": "WET/GMT", "distanceKm": 3.4},
  {"stationName": "Lisbon Santa Apolonia", "country": "PT", "latitude": 38.713, "longitude": -9.122, "timeZoneGroup": "WET/GMT", "distanceKm": 5.1}
]
```

---

## 2. Design & Algorithm

### 2.1. Query Algorithm Overview

1. Validate parameters (lat, lon, N, optional filter).
2. Search the **KD-tree** recursively using the nearest-neighbour algorithm with pruning:
    - Compare target coordinate to current node’s split dimension.
    - Visit nearer child first; prune subtrees where bounding box distance exceeds current max distance.
3. Maintain a max-heap (priority queue) of size N storing nearest candidates.
4. Once traversal finishes, sort the heap ascending by distance (and name as tie-breaker).
5. If `timeZoneFilter` is provided, exclude stations not matching it before final sort.
6. Return the top N results.

### 2.2. Haversine Distance Formula

**Formula:**
d = 2r * arcsin(sqrt(sin²(Δφ/2) + cos(φ₁) * cos(φ₂) * sin²(Δλ/2)))

Where:
- r = 6371 km (Earth radius)
- φ = latitude (radians), λ = longitude (radians)

### 2.3. Pseudocode

```java
List<Station> findNearestStations(double lat, double lon, int N, String timeZoneFilter) {
    validateInputs(lat, lon, N);
    MaxHeap<StationDistance> nearest = new MaxHeap<>(N);
    recursiveSearch(root, target, N, nearest);
    List<Station> results = nearest.toSortedList();
    if (timeZoneFilter != null)
        results = results.stream()
                         .filter(s -> s.timeZoneGroup.equalsIgnoreCase(timeZoneFilter))
                         .toList();
    return results.stream()
                  .sorted(Comparator.comparing(Station::getDistanceKm)
                  .thenComparing(Station::getName))
                  .limit(N)
                  .toList();
}
```

---

## 3. Implementation Notes

- Core classes: `ProximitySearchService`, `KDTree2D`, `Station`, `StationDistance`.
- The `KDTree2D` exposes method: `List<Station> nearestN(double lat, double lon, int N, String timeZoneFilter)`
- Use a **priority queue** (max-heap) to maintain the best N candidates during traversal.
- When both children are equally distant, prioritize left subtree for determinism.
- Complexity logging (average/worst) is required at the end of search.

---

## 4. Returns

| Field | Description |
|--------|-------------|
| **nearestStations** | List of N nearest stations sorted ascending by distance |
| **searchTimeMs** | Time to complete query |
| **visitedNodes** | Number of KD-tree nodes visited |
| **averageComplexity** | Expected O(log n) performance |
| **worstCaseComplexity** | Worst O(n) |
| **filterApplied** | Applied timeZoneGroup filter (if any) |

Example output JSON:
```json
{
  "nearestStations": ["Lisbon Oriente", "Lisbon Santa Apolonia"],
  "searchTimeMs": 7,
  "visitedNodes": 42,
  "averageComplexity": "O(log n)",
  "worstCaseComplexity": "O(n)",
  "filterApplied": "WET/GMT"
}
```

---

## 5. Temporal Analysis (Complexity)

| Operation | Average | Worst | Space |
|------------|----------|--------|--------|
| **Nearest-N Search** | O(log n + N log N) | O(n) | O(N) |
| **Filtering** | O(N) | O(N) | O(1) |
| **Haversine Calculations** | O(N) | O(N) | O(1) |

The overall complexity for typical balanced trees remains near **O(log n)** for search and **O(N)** for output sorting.

---

## 6. Tests

1. **Basic search correctness:** Provide known coordinates and verify that the nearest N results are correct.
2. **Filter logic:** Apply a `timeZoneGroup` filter and confirm excluded results are removed.
3. **Tie-breaking:** Verify lexicographic name ordering for equal distances.
4. **Invalid input handling:** Latitude, longitude, and N validation.
5. **Performance benchmark:** Ensure sublinear average time for 64k dataset.

---

## 7. System Sequence Diagram (SSD)


![USEI09-SSD](doc/Sprint2/USEI/USEI09/requirements/svg/SSD-USEI09.svg)

---

## 8. Other Remarks

- Haversine computation should be encapsulated in a utility class (e.g., `GeoUtils`).
- Ensure the algorithm handles edge cases near poles and the ±180° longitude wrap.
- When fewer than N stations satisfy the filter, return all that match (no error).
- Deterministic results must be guaranteed for identical input data.

*Generated automatically by ChatGPT on 2025-11-13*
