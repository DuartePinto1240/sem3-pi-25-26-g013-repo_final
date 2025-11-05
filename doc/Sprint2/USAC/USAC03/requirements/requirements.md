# USAC03 — Extract Sensor Data

## 1. User Story Description

As a station operator system developer, I want to extract a specific unit and value from a formatted data string based on a given token (e.g., "TEMP" or "HUM"), so that sensor data can be correctly parsed and used by the station management system.

## 2. Requirements / Specifications

The function to implement is:
```
int extract_data(char* str, char* token, char* unit, int* value)
```

### Input

- ``str`` — null-terminated string containing formatted data (e.g., "TOKEN &unit:unit_name &value:123#...")

- ``token`` — null-terminated string (e.g., "TEMP") to search for within str

- ``unit`` — pointer to an allocated string where the extracted unit (e.g., "celsius") will be written

- ``value`` — pointer to an integer where the extracted numeric value (e.g., 20) will be written


TOKEN&unit:xxxxxxx&value:xx#TOKEN&unit:xxxxxxxx&value:xx

where each `TOKEN` can be:
- `"TEMP"` → temperature
- `"HUM"` → humidity
- `token` — string representing the token to search for (e.g., `"TEMP"` or `"HUM"`)
- `unit` — pointer to an allocated string buffer where the extracted unit will be stored
- `value` — pointer to an integer where the extracted numeric value will be stored

### Extraction Rules
- Each section in `str` starts with the token name followed by two fields: `&unit:` and `&value:`
- `unit` is a text string representing the measurement unit
- `value` is a numeric integer value
- Tokens are separated by `#`

**Example format:**

TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80

### Invalid Conditions
- Null pointer arguments (`str`, `token`, `unit`, or `value`)
- Token not found in `str`
- Missing `&unit:` or `&value:` after the token
- Incorrect format preventing extraction

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | The function must locate the section in `str` that matches the given `token`. |
| **AC2** | Extract the text following `&unit:` and store it into `unit`. |
| **AC3** | Extract the integer following `&value:` and store it into `value`. |
| **AC4** | Function returns **1** on successful extraction. |
| **AC5** | Function returns **0** on failure and sets `unit` to an empty string (`""`) and `value` to `0`. |
| **AC6** | Must handle multiple tokens separated by `#` in the input string. |

## 4. Input / Output Examples

### ✅ Valid (Token "TEMP")

**Input** 

```
str   = "TEMP &unit:celsius &value:20#HUM &unit:percentage &value:80"
token = "TEMP"

```

**Output** (written to pointers)
```
unit   = "celsius"
*value = 20
return = 1
```

### ✅ Valid (Token "HUM")

**Input**
```
str   = "TEMP &unit:celsius &value:20#HUM &unit:percentage &value:80"
token = "HUM"
```

**Output** (written to pointers)
```
unit   = "percentage"
*value = 80
return = 1
```

### ❌ Invalid (Token not found)

**Input**
```
str   = "TEMP &unit:celsius &value:20#"
token = "AAA"
```

**Output** (written to pointers)
```
unit   = ""
*value = 0
return = 0
```

### ❌ Invalid (Malformed string)

**Input**
```
str   = "TEMP &unit:celsius &value:nan"
token = "TEMP"
```

**Output** (written to pointers)
```
unit   = ""
*value = 0
return = 0
```

### ❌ Invalid (Null pointer)

**Input**
```
str   = "TEMP &unit:celsius &value:20#"
token = NULL
```

**Output** (written to pointers)
```
unit   = ""
*value = 0
return = 0
```
---
## 5. Error Handling
When validation or parsing fails:
- Do **not** modify the input string.
- Write:
  `unit[0] = '\0';`
  `*value = 0;`
- Checks must include:
- `str`, `token`, `unit`, and `value` are not `NULL`
- The token exists in `str`
- Both substrings `&unit:` and `&value:` exist after the token
- The format follows the expected pattern
---
## 6. Implementation Notes

- Follow **RISC-V ABI**  (register preservation).

- Use string search functions (like strstr) to find the token and tags.

- Use sscanf or equivalent (e.g., strtol) for parsing the value.

- The parser must be robust to whitespace variations (e.g., &unit:celsius and &unit : celsius should both be valid).

- Ensure the extracted unit string does not overflow the allocated buffer.