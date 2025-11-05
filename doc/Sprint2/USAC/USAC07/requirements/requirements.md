# USAC07 — Move N Elements from Circular Buffer to Array

## 1. User Story
As a station operator system developer, I need a function that removes the **oldest N elements** from a circular integer buffer and copies them into a given output array, so multiple data samples can be consumed in FIFO order efficiently.

---

## 2. Specifications / Requirements
Expected function prototype:

```c
int move_n_to_array(int* buffer, int length, int *nelem, int *tail, int *head, int n, int* array);
```

### Inputs:
- `buffer` — pointer to an integer array allocated with length `length`.
- `length` — positive integer representing the total capacity of the buffer.
- `nelem` — pointer to an integer representing the number of elements currently stored in the buffer.
- `tail` — pointer to the index of the oldest element in the buffer.
- `head` — pointer to the index of the next insertion position.
- `n` — integer specifying how many elements to move from the buffer to the output array.
- `array` — pointer to an integer array where the moved elements will be stored.

### Buffer semantics:
- The buffer is **circular (FIFO)**, meaning after reaching the end, indices wrap back to zero.
- `*tail` points to the **oldest** element.
- `*head` points to the **next insertion position**.
- `*nelem` indicates the current number of stored elements.

### Operation:
- If the buffer contains **at least n elements**, copy them sequentially from the oldest (starting at `*tail`) to `array[0..n-1]`.
- After copying:
    - Advance `*tail = (*tail + n) % length`
    - Decrease `*nelem -= n`
- Return `1` on success.
- If the buffer has fewer than `n` elements, or invalid parameters are passed, do nothing and return `0`.

### Invalid conditions:
- Any NULL pointer among `buffer`, `nelem`, `tail`, `array`.
- `length <= 0`
- `n <= 0`
- `*nelem < n`
- `*tail` or `*head` outside `[0, length - 1]`

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | Function moves exactly `n` oldest elements from `buffer` into `array` if enough elements exist. |
| **AC2** | After success, `*tail` advances correctly with wrap-around (`% length`). |
| **AC3** | After success, `*nelem` decreases by `n`. |
| **AC4** | Function returns `1` on success, `0` on failure. |
| **AC5** | Function performs no modifications on failure and leaves `buffer` unchanged. |
| **AC6** | FIFO order is preserved during transfer (first in → first out). |

---

## 4. I/O Examples

### Valid example:
- Initial state:
    - `buffer = [10, 20, 30, 40, 50]`
    - `length = 5`
    - `*nelem = 5`
    - `*tail = 0`
    - `*head = 0`
    - `n = 3`
- Call:
    ```c
    move_n_to_array(buffer, length, &nelem, &tail, &head, n, array);
    ```
- Result:
    ```
    array = [10, 20, 30]
    tail = 3
    nelem = 2
    return = 1
    ```

### Invalid example (not enough elements):
- Initial state:
    - `*nelem = 2`
    - `n = 4`
- Call:
    ```
    move_n_to_array(...)
    ```
- Result:
    ```
    array unchanged
    tail unchanged
    nelem unchanged
    return = 0
    ```

---

## 5. Error Handling
On validation failure:
- Do not read or remove elements from `buffer`.
- Do not modify `*nelem` or `*tail`.
- Return `0`.

Mandatory checks before moving elements:
- All pointer arguments are valid (non-NULL).
- `length > 0` and `n > 0`.
- `*nelem >= n`.

---

## 6. Implementation Notes
- Use modulo arithmetic for circular progression:
  ```c
  t = (t + 1) % length;
  ```
- Validate parameters early and exit immediately on invalid conditions.
- Keep updates atomic: only modify `*tail` and `*nelem` after successful copy.
- Ensure memory safety: never read or write out of bounds.
- Time complexity: **O(n)** (one pass per element).
- Must follow **RISC-V 32-bit ABI (ILP32D)** calling convention:
  ```
  a0 = buffer
  a1 = length
  a2 = nelem
  a3 = tail
  a4 = head
  a5 = n
  a6 = array
  ```
