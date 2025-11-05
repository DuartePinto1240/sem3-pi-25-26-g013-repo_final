# USAC05 — Insert new element into circular buffer (enqueue)

## 1. User Story
As a station operator system developer, I need a function that inserts a new integer into a circular buffer used by the station controller, so data samples can be stored and managed in FIFO order for later processing.

---

## 2. Specifications / Requirements
**Expected function prototype:**
```
int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value);
```

**Inputs:**
- `buffer` — pointer to an integer array allocated with capacity `length`.
- `length` — positive integer representing the buffer capacity.
- `nelem` — pointer to the current number of elements stored (0..length).
- `tail` — pointer to the index of the oldest element (0..length-1).
- `head` — pointer to the index of the next insertion position (0..length-1).
- `value` — integer to be inserted.

**Buffer semantics (circular FIFO):**
- **Empty** when `*nelem == 0`.
- **Full** when `*nelem == length`.

**Operation (enqueue):**
1. If the buffer is **full** (`*nelem == length`), **discard the oldest element** by advancing `*tail = (*tail + 1) % length` (overwrite policy).
2. Write `value` at `buffer[*head]`.
3. Advance `*head = (*head + 1) % length`.
4. If the buffer was not full, increment `*nelem` (`*nelem = *nelem + 1`; never exceed `length`).
5. **Return value:**
  - `1` if **after insertion** the buffer is full (`*nelem == length`);
  - `0` otherwise.

**Invalid conditions (fail-fast, no state change besides safe-guards):**
- Any null pointer among `buffer`, `nelem`, `tail`, `head`.
- `length <= 0`.
- `*nelem` outside `[0, length]`.
- `*head` or `*tail` outside `[0, length-1]`.

_On invalid input: do not modify `buffer`, `*nelem`, `*head` or `*tail`; return `0`._

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | The function inserts `value` according to circular FIFO semantics. |
| **AC2** | If the buffer is full before insertion, the oldest element is discarded by advancing `*tail` with wrap-around. |
| **AC3** | `*head` is advanced with wrap-around and remains within `[0, length-1]`. |
| **AC4** | `*nelem` is incremented only when the buffer was not full and never exceeds `length`. |
| **AC5** | The function returns `1` when the buffer becomes/continues **full after** the insertion; otherwise returns `0`. |
| **AC6** | On invalid input, the function returns `0` and does not corrupt `buffer`, `*nelem`, `*head`, or `*tail`. |

---

## 4. I/O Examples

**Valid example — buffer initially empty**
- State (before):
  - `buffer = [0, 0, 0, 0]`
  - `length = 4`
  - `*nelem = 0`, `*head = 0`, `*tail = 0`
- Call: `enqueue_value(buffer, 4, &nelem, &tail, &head, 10)`
- Result (after):
  - `buffer = [10, 0, 0, 0]`
  - `nelem = 1`, `head = 1`, `tail = 0`
  - return `0` (not full yet)

---

**Valid example — last insertion makes buffer full**
- State (before):
  - `buffer = [10, 20, 30, 0]`
  - `length = 4`
  - `*nelem = 3`, `*head = 3`, `*tail = 0`
- Call: `enqueue_value(buffer, 4, &nelem, &tail, &head, 40)`
- Result (after):
  - `buffer = [10, 20, 30, 40]`
  - `nelem = 4`, `head = 0`, `tail = 0`
  - return `1` (full after insertion)

---

**Valid example — insertion when already full (overwrite)**
- State (before):
  - `buffer = [10, 20, 30, 40]`
  - `length = 4`
  - `*nelem = 4`, `*head = 0`, `*tail = 0`
- Call: `enqueue_value(buffer, 4, &nelem, &tail, &head, 50)`
- Result (after):
  - `buffer = [50, 20, 30, 40]`  (overwrote oldest value)
  - `nelem = 4`, `head = 1`, `tail = 1`  (both advanced with wrap-around)
  - return `1` (still full)

---

**Invalid example — parameters out of range**
- State (before):
  - `length = 4`
  - `*nelem = 5` (invalid)
- Call: `enqueue_value(buffer, 4, &nelem, &tail, &head, 99)`
- Result (after):
  - State unchanged (no writes, no index changes)
  - return `0`

---

## 5. Error Handling
On any validation failure:
- Do not insert or modify the buffer.
- Do not modify `*nelem`, `*head` or `*tail`.
- **Return:** `0`.

Mandatory validations before insertion:
- `buffer`, `nelem`, `tail`, `head` are not `NULL`.
- `length > 0`.
- `*nelem ∈ [0, length]`.
- `*head`, `*tail ∈ [0, length-1]`.

---

## 6. Implementation Notes
- **Wrap-around** using modular arithmetic:
  - `*head = (*head + 1) % length`
  - `*tail = (*tail + 1) % length`
- **Full before insertion:** if `*nelem == length`, only advance `*tail` (discard oldest); `*nelem` remains `length`.
- **Full after insertion:** return `1`; otherwise return `0`.
- **Complexity:** O(1).
- **Assembly integration (RV32I/M, ABI convention):**
  - `a0` = buffer
  - `a1` = length
  - `a2` = nelem
  - `a3` = tail
  - `a4` = head
  - `a5` = value
  - return in `a0`.
- **Registers:** use only caller-saved (`t0–t6`, `a*`) to avoid prologue/epilogue; no stack frame required if not using `s*` registers.
- **Robustness:** even if the test harness assumes valid input, keeping validation documented clarifies the function contract and avoids invalid states in future integrations.

---

> **Note for the report:** This specification describes the function to be implemented in Assembly (RISC-V). The overwrite behavior when the buffer is full is intentional and required (FIFO policy discarding the oldest element).
