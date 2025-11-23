# USEI10 — Multi-Modal Nearest Facility Queries

## Temporal complexity


 | Action                                        | Temporal complexity |
 |-----------------------------------------------|--------------------|
 | `Build KD-Tree`                               | O(N log N)         |
 | `KD-Tree rangeSearch (bounding box)`          | O(log N + k)       |
 | `Haversine distance filtering (exact circle)` | O(k)               |
 | `Sorting k results`                           | O(k log k)         |
 | `AVL insertions`                              | O(k log k)         |
 | `Overall`                                     | O(log N + k log k) |


### Overall
> O(log N + k log k), where k = stations inside the bounding box.