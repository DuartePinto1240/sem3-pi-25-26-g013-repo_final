# USAC06 — Remove oldest element from buffer and output it to integer pointer

## 1. User Story
As a station operator system developer, I need a function that removes the oldest element from a circular integer buffer and returns it via a provided integer pointer, so components can consume buffered data in FIFO order.

---

## 2. Specifications / Requirements
Expected function prototype:

    int remove_oldest(int *buffer, int capacity, int *head, int *tail, int *value)

Inputs:
- `buffer` — pointer to an integer array allocated with length `capacity`
- `capacity` — positive integer representing the buffer length
- `head` — pointer to the index of the oldest element (0..capacity-1)
- `tail` — pointer to the index of the next insertion position (0..capacity-1)
- `value` — pointer to an integer where the removed value will be stored

Buffer semantics (circular FIFO):
- Buffer is empty when `*head == *tail`
- Buffer is full when advancing `tail` would equal `head` (not used by this function)

Operation:
- If buffer not empty: read `buffer[*head]`, write it to `*value`, advance `*head = (*head + 1) % capacity`, return `1`.
- If buffer empty or invalid input: do not modify buffer indices, set `*value = 0`, return `0`.

Invalid conditions:
- Any null pointer among `buffer`, `head`, `tail`, `value`
- `capacity <= 0`
- `*head` or `*tail` outside range `[0, capacity-1]`
- Buffer empty (`*head == *tail`)

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | When buffer is non-empty, the function removes the oldest element and stores it into `*value`. |
| **AC2** | `*head` is advanced with wrap-around and remains within `[0, capacity-1]`. |
| **AC3** | Function returns `1` on successful removal. |
| **AC4** | On failure (invalid input or empty), function returns `0` and sets `*value = 0`. |
| **AC5** | Function does not corrupt `buffer`, `*head`, or `*tail` on failure. |

---

## 4. I/O Examples

Valid example:
- State:
    - `buffer = [10, 20, 30, _, _]`
    - `capacity = 5`
    - `*head = 0`
    - `*tail = 3`  (three elements stored: 10,20,30)
- Call: `remove_oldest(buffer, 5, &head, &tail, &value)`
- Result:
    - `value = 10`
    - `head = 1`
    - return `1`

Invalid example (empty buffer):
- State:
    - `*head = 2`
    - `*tail = 2`
- Call: `remove_oldest(...)`
- Result:
    - `value = 0`
    - `head` and `tail` unchanged
    - return `0`

---

## 5. Error Handling
On validation failure:
- Do not read or remove elements from `buffer`
- Set `*value = 0`
- Return `0`

Mandatory checks before removal:
- `buffer`, `head`, `tail`, `value` are not NULL
- `capacity > 0`
- `*head` and `*tail` within `[0, capacity-1]`
- Buffer is not empty (`*head != *tail`)

---

## 6. Implementation Notes
- Use modulo arithmetic for wrap-around:
  `*head = (*head + 1) % capacity`
- Validate all preconditions before performing the removal.
- Keep updates to `*head` atomic with respect to the return value (update index only after reading value).
- If function used in multithreaded context, synchronization must be done externally.
- Ensure function is efficient (O(1)) and does not access memory out of bounds.
