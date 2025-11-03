# USAC05 — Implement insert/encrypt function into buffer

## 1. User Story
As a station operator system developer, I need a function that encrypts a text string composed of uppercase English letters using the Caesar Cipher and writes it into a provided buffer, so commands/messages can be safely transmitted between components.

---

## 2. Specifications / Requirements
Expected function prototype:

    int encrypt_data(char *input, int shift, char *output)

Inputs:
- `input` — null-terminated string containing only characters `A`..`Z`
- `shift` — integer value in range `[1, 26]`
- `output` — pointer to an already allocated buffer where result will be written

Encryption rules:
- Each letter advances `shift` positions in the alphabet
- After `Z` wrap to `A`
- Example: `A` with shift `3` → `D`; `Y` with shift `5` → `D`

Invalid conditions:
- Null pointers (`input` or `output`)
- Empty `input` string
- `shift` outside `[1, 26]`
- Any character outside `A`..`Z`

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | Each character of `input` is transformed applying the `shift`. |
| **AC2** | Result is written into `output` and null-terminated. |
| **AC3** | Function returns `1` on success. |
| **AC4** | On failure, function returns `0` and sets `output` to an empty string (`output[0] = '\0'`). |
| **AC5** | Only uppercase characters `A`..`Z` are accepted. |

---

## 4. I/O Examples

Valid example:
- Input: `input = "HELLO"`, `shift = 3`
- Output: `output = "KHOOR"`, return `1`

Invalid example:
- Input: `input = "HELLO123"`, `shift = 4`
- Output: `output = ""`, return `0`

---

## 5. Error Handling
When validation fails:
- Do not perform encryption
- Set `output[0] = '\0'`
- Return `0`

Mandatory checks before processing:
- `input` and `output` are not NULL
- `shift` ∈ `[1, 26]`
- All characters in `input` are between `'A'` and `'Z'`

---

## 6. Implementation Notes
- Use simple arithmetic for shift and wrap-around.
- Per-character formula:

  encrypted_char = ((input[i] - 'A' + shift) % 26) + 'A'

- Ensure `output` is null-terminated on all paths (success and error).
- Validate all preconditions before writing to `output`.
