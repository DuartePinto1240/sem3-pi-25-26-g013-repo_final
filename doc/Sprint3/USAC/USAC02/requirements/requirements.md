
# USAC02 — Decrypt Data (Caesar Cipher)

## 1. User Story Description
As a station operator system developer, I want to **decrypt a text string** composed of capital English alphabet letters using the **Caesar Cipher**, so that I can correctly interpret encrypted commands or messages received from the station management system.

---

## 2. Requirements / Specifications
The function to implement is:

```
int decrypt_data(char* in, int key, char* out)
```

### Input
- `in` — null-terminated string containing only **A–Z**
- `key` — integer shift value in range **[1, 26]**
- `out` — pointer to an allocated string where decrypted characters will be written

### Decryption Rules
- Shift each character by `key` positions backward in the alphabet
- Wrap before `A` back to `Z`
    - Example:
        - `D - 3 → A`
        - `B - 3 → Y`

### Invalid Conditions
- Null pointer arguments
- Empty input string
- Key outside range [1, 26]
- Any character outside 'A'–'Z'

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | Each character in `in` must be converted using the Caesar Cipher reverse shift based on `key`. |
| **AC2** | Result must be written to `out` and must be null-terminated. |
| **AC3** | Function returns **1** on success. |
| **AC4** | Function returns **0** on failure and sets `out` to an empty string (`'\0'`). |
| **AC5** | Only uppercase alphabetic characters (`A–Z`) are accepted. |

---

## 4. Input / Output Examples

### ✅ Valid
**Input**
```
in  = "KHOOR"
key = 3
```

**Output**
```
out = "HELLO"
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
- Do **not** perform decryption
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
decrypted = ((in[i] - 'A' - key + 26) % 26) + 'A'
```
