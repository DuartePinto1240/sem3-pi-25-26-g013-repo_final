
# USAC01 — Encrypt Data (Caesar Cipher)

## 1. User Story Description
As a station operator system developer, I want to **encrypt a text string** composed of capital English alphabet letters using the **Caesar Cipher**, so that commands or messages can be safely transmitted between components of the station management system.

---

## 2. Requirements / Specifications
The function to implement is:

```
int encrypt_data(char* in, int key, char* out)
```

### Input
- `in` — null-terminated string containing only **A–Z**
- `key` — integer shift value in range **[1, 26]**
- `out` — pointer to an allocated string where encrypted characters will be written

### Encryption Rules
- Shift each character by `key` positions forward in the alphabet
- Wrap after `Z` back to `A`
    - Example:
        - `A + 3 → D`
        - `Y + 5 → D`

### Invalid Conditions
- Null pointer arguments
- Empty input string
- Key outside range [1, 26]
- Any character outside 'A'–'Z'

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | Each character in `in` must be converted using the Caesar Cipher shift based on `key`. |
| **AC2** | Result must be written to `out` and must be null-terminated. |
| **AC3** | Function returns **1** on success. |
| **AC4** | Function returns **0** on failure and sets `out` to an empty string (`'\0'`). |
| **AC5** | Only uppercase alphabetic characters (`A–Z`) are accepted. |

---

## 4. Input / Output Examples

### ✅ Valid
**Input**
```
in  = "HELLO"
key = 3
```

**Output**
```
out = "KHOOR"
return = 1
```

### ❌ Invalid
**Input**
```
in  = "HELLO123"
key = 4
```

**Output**
```
out = ""
return = 0
```

---

## 5. Error Handling
When validation fails:
- Do **not** perform encryption
- Write `out[0] = '\0'`
- Return `0`

Checks must include:
- `in` and `out` not NULL
- Key ∈ [1, 26]
- All characters ∈ ['A'..'Z']

---

## 6. Implementation Notes
- Follow **RISC-V ABI** (register preservation)
- Use arithmetic operations (`ADD`, `SUB`, etc.)
- Modulo wrap must be applied for A↔Z transition

Formula:
```
encrypted = ((in[i] - 'A' + key) % 26) + 'A'
```