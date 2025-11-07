# USAC09 — Median Calculation

## 1. User Story Description
As a station monitoring system developer, I want to calculate the median value from a numeric array.

---

## 2. Requirements / Specifications
The function to implement is:

```
int median(int* vec, int length, int* me)
```

### Input
- `vec` — pointer to an integer array containing the sample values.
- `lenght` — number of elements in the array.
- `me` — pointer to an integer where the median result will be stored.

### Operation Rules
- Sort the array in ascending order before calculating the median.
- The median is:
  - For odd length: middle element → `vec[length / 2]`
  - For even length: average of the two middle elements → `(vec[length/2 - 1] + vec[length/2]) / 2`
- The function must store the result in `*me`.
    

### Invalid Conditions
- vec or me are NULL
- length <= 0

---

## 3. Acceptance Criteria

| ID      | Description                                                                           |
|---------|---------------------------------------------------------------------------------------|
| **AC1** | The array must be sorted in ascending order before calculating the median.            |
| **AC2** | For odd-sized arrays, the median is the central element.                              |
| **AC3** | For even-sized arrays, the median is the integer average of the two central elements. |
| **AC4** | The computed median must be stored in the integer pointer `*me.`                      |
| **AC5** | The function returns 1 on success.                                                    |
| **AC6** | The function returns 0 on failure and sets `*me = 0`.                                 |

---

## 4. Input / Output Examples

### Valid Example (Odd length)
**Input**
```
vec = {5, 2, 9}
length = 3
```

**Output**
```
Sorted: {2, 5, 9}
*me = 5
return = 1
```

### Valid Example (Even length)
**Input**
```
vec = {4, 2, 8, 6}
length = 4
```

**Output**
```
Sorted: {2, 4, 6, 8}
*me = (4 + 6) / 2 = 5
return = 1
```

### Invalid
**Input**
```
vec = NULL
length = 5
```

**Output**
```
return = 0
```

---

## 5. Error Handling
When validation fails:
- Do not sort or access the array.
- Return `0`

Checks must include:
- `vec != NULL` and `me != NULL`
- `length > 0`

---

## 6. Implementation Notes
- Follow RISC-V ABI conventions (register usage and preservation).
- Sorting can reuse logic from USAC08 (sort_array).
- Median calculation must occur after the array is sorted.
- Use integer division for even-length averages (fractional parts truncated).

Example logic::
```
if (length % 2 == 1)
    *me = vec[length / 2];
else
    *me = (vec[length / 2 - 1] + vec[length / 2]) / 2;
```