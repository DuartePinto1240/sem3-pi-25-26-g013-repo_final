# USAC08 — Sort Integer Array

## 1. User Story
As a station operator system developer, I need a function that sorts an integer array in ascending or descending order, according to a given parameter, so that numerical data can be processed and displayed consistently.

---

## 2. Specifications / Requirements
Expected function prototype:

```
int sort_array(int* vec, int length, char order);
```

### Inputs:
- `vec` — pointer to an integer array to be sorted.
- `length` — integer indicating the number of elements in the array.
- `order` — character flag defining the sorting order:
  - `1` —> ascending
  - `0` —>descending

### Outputs:
- Returns `1` if sorting succeeds.
- Returns `0` if input parameters are invalid (e.g., `length <= 0` or `vec== null`).

### Operation:
- When `order == 1`, elements must be arranged from smallest to largest.
- When `order == 0`, elements must be arranged from largest to smallest.
- The sorting must be in-place, modifying the contents of `vec`.
- No new arrays should be dynamically allocated.

---

## 3. Acceptance Criteria

| ID | Description                                                      |
|----|------------------------------------------------------------------|
| **AC1** | Function sorts array in ascending order when `order == 1`.       |
| **AC2** | Function sorts array in descending order when `order == 0`.      |
| **AC3** | Function returns `1 when sorting completes successfully.         |
| **AC4** | Function returns `0` if `length <= 0` or `vec == NULL`.          |
| **AC5** | Sorting is performed in-place (array modified directly).         |
| **AC6** | Function must not access invalid memory addresses (safe bounds). |

---

## 4. I/O Examples

### Example 1 — Ascending Order
- Input:
    ```
    int vec[] = {5, 1, 4, 2, 3};
    int length = 5;
    char order = 1;
    int res = sort_array(vec, length, order);
    ```
- Result:
    ```c
    vec = [1, 2, 3, 4, 5]
    res = 1
    ```

### Example 2 — Descending Order
- Input:
    ```
    int vec[] = {5, 1, 4, 2, 3};
    int length = 5;
    char order = 0;
    int res = sort_array(vec, length, order);
    ```
- Result:
    ```
    vec = [5, 4, 3, 2, 1]
    res = 1
    ```
  
### Example 3 — Invalid Input:
- Input:
    ```
    int* vec = NULL;
    int length = 5;
    char order = 1;
    int res = sort_array(vec, length, order);
    ```
- Result:
    ```
    vec unchanged
    res = 0
    ```
---

## 5. Error Handling
On validation failure:
- The function must not attempt to sort or access the array.
- Return `0` immediately.
- Conditions that trigger an error:
  - `vec == NULL`
  - `length <= 0`
  - `order` not equal to `0` or `1`

---

## 6. Implementation Notes
- Sorting algorithm choice is flexible (e.g., Bubble Sort, Selection Sort, or Insertion Sort), but it must be deterministic and O(n²) or better.
- Use simple comparison-based sorting, avoiding recursion or dynamic allocation.
- Example of ascending comparison:
    ```
  if (vec[i] > vec[j]) swap(vec[i], vec[j]);
    ```
- Example of descending comparison:
    ```
    if (vec[i] < vec[j]) swap(vec[i], vec[j]);
    ```
- Time complexity: O(n²) (acceptable for short buffers in embedded systems).
- Must follow RISC-V 32-bit ABI (ILP32D) calling convention:
    ```
    a0 = vec
    a1 = length
    a2 = order
    ```