# USAC06 — Remove oldest element from circular buffer (dequeue)

## 1. User Story
As a station operator system developer, I need a function that removes the oldest integer from a circular buffer and returns it via a provided pointer, so components can consume buffered data in FIFO order.

---

## 2. Specifications / Requirements
**Expected function prototype:**
```
int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value);
```

**Inputs:**
- `buffer` — pointer to an integer array allocated with capacity `length`.
- `length` — positive integer representing the buffer capacity.
- `nelem` — pointer to the current number of elements stored (0..length).
- `tail` — pointer to the index of the **oldest** element (0..length-1).
- `head` — pointer to the index of the **next insertion** position (0..length-1).
- `value` — pointer to an integer where the removed value will be stored.

**Buffer semantics (circular FIFO):**
- **Empty** when `*nelem == 0`.
- **Full** when `*nelem == length`. (Not directly used by this function.)

**Operation (dequeue):**
- If the buffer is **not empty**:
  1. Read `buffer[*tail]`, store into `*value`.
  2. Advance `*tail = (*tail + 1) % length`.
  3. Decrement `*nelem` (`*nelem = *nelem - 1`).
  4. Return `1`.
- If the buffer is **empty** or input is **invalid**:
  - Do **not** modify indices or `*nelem`; set `*value = 0`; return `0`.

**Invalid conditions (fail-fast, no state change besides setting `*value = 0`):**
- Any null pointer among `buffer`, `nelem`, `tail`, `head`, `value`.
- `length <= 0`.
- `*nelem` outside `[0, length]`.
- `*head` or `*tail` outside `[0, length-1]`.
- Buffer empty (`*nelem == 0`).

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | When the buffer is non-empty, the function removes the oldest element (`buffer[*tail]`) and stores it in `*value`. |
| **AC2** | `*tail` is advanced with wrap-around and remains within `[0, length-1]`. |
| **AC3** | `*nelem` is decremented exactly once on successful removal and never becomes negative. |
| **AC4** | Function returns `1` on successful removal. |
| **AC5** | On failure (invalid input or empty), function returns `0`, sets `*value = 0`, and leaves `buffer`, `*nelem`, `*head`, and `*tail` unchanged. |

---

## 4. I/O Examples

**Valid example — remove from non-empty buffer**
- State (before):
  - `buffer = [10, 20, 30, _, _]`
  - `length = 5`
  - `*nelem = 3`
  - `*tail = 0`  (oldest at index 0 → 10)
  - `*head = 3`  (next insert at index 3)
- Call: `dequeue_value(buffer, 5, &nelem, &tail, &head, &value)`
- Result (after):
  - `value = 10`
  - `tail = 1`, `nelem = 2` (head unchanged)
  - return `1`

**Invalid example — empty buffer**
- State (before):
  - `length = 5`
  - `*nelem = 0`
  - `*tail = 2`, `*head = 2` (indices arbitrary when empty)
- Call: `dequeue_value(...)`
- Result (after):
  - `value = 0`
  - `tail`, `head`, `nelem` unchanged
  - return `0`

---

## 5. Error Handling
On validation failure:
- Do not read or remove elements from `buffer`.
- Set `*value = 0`.
- Return `0`.

Mandatory checks before removal:
- `buffer`, `nelem`, `tail`, `head`, `value` are not `NULL`.
- `length > 0`.
- `*nelem ∈ [0, length]`.
- `*head`, `*tail ∈ [0, length-1]`.
- Buffer is not empty (`*nelem > 0`).

---

## 6. Implementation Notes
- **Wrap-around** uses modular arithmetic:
  - `*tail = (*tail + 1) % length`
- Update order:
  - Read value first → then advance `*tail` → then decrement `*nelem`.
- **Complexity:** O(1).
- **Assembly integration (RV32I/M, ABI):**
  - `a0` = buffer
  - `a1` = length
  - `a2` = nelem
  - `a3` = tail
  - `a4` = head
  - `a5` = value (pointer)
  - Return in `a0` (1 on success, 0 on failure).
- Use only caller-saved registers (`t0–t6`, `a*`). No stack frame required if you don’t touch `s*` registers.
