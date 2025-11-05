# USAC04 — Format Command

## 1. User Story Description

As a station operator system developer, I want to **format command strings** using a given operation code and numeric value, so that standardized command messages can be generated and transmitted correctly within the station management system.

---

## 2. Requirements / Specifications

The function to implement is:

```
int format_command(char* op, int n, char* cmd)
```

### Input

- `op` — null-terminated string containing the operation code (e.g., `"rB"`, `"Ye"`, `"GTh"`)
- `n` — integer value (e.g., `5`, `25`, `125`)
- `cmd` — pointer to an allocated string where the formatted command will be stored

### Formatting Rules

- The function formats the output string `cmd` based on the content of `op`:
  - For operation codes `RE`, `YE`, `GE`, or `RB`, the format must be:
    ```
    CMD,xx
    ```
    where `xx` is a **two-digit representation** of `n` (with leading zero if necessary).
    - Example:
      - `"RB"` and `5` → `"RB,05"`
      - `"YE"` and `25` → `"YE,25"`
  - For operation code `GTH`, the format must be:
    ```
    GTH
    ```
    (the value `n` is ignored).

- The operation code must be:
  - **Trimmed** (no leading or trailing spaces)
  - **Converted to uppercase**

### Invalid Conditions

- Null pointer arguments (`op` or `cmd`)
- `n` not in the range **[0, 99]** for commands that include a numeric value
- Invalid or unrecognized operation code (not one of `RE`, `YE`, `GE`, `RB`, `GTH`)

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | The function must clean and capitalize the `op` string before processing. |
| **AC2** | If `op` is `RE`, `YE`, `GE`, or `RB`, format the result as `"CMD,xx"`, where `xx` is a two-digit decimal number. |
| **AC3** | If `op` is `GTH`, format the result as `"GTH"` (ignore the numeric value). |
| **AC4** | Function returns **1** on success and writes the result to `cmd`. |
| **AC5** | Function returns **0** on failure and sets `cmd` to an empty string (`""`). |
| **AC6** | Invalid or out-of-range inputs must be handled gracefully without crashing. |

---

## 4. Input / Output Examples

### ✅ Valid (Standard Command)

**Input**
```
op = " rB "
n  = 5
```

**Output**
```
cmd = "RB,05"
return = 1
```

---

### ✅ Valid (Another Command)

**Input**
```
op = " Ye "
n  = 25
```

**Output**
```
cmd = "YE,25"
return = 1
```

---

### ✅ Valid (Special Command - GTH)

**Input**
```
op = " gTh "
n  = 50
```

**Output**
```
cmd = "GTH"
return = 1
```

---

### ❌ Invalid (Value Too Large)

**Input**
```
op = " Ye "
n  = 125
```

**Output**
```
cmd = ""
return = 0
```

---

### ❌ Invalid (Unknown Operation)

**Input**
```
op = " aaa "
n  = 25
```

**Output**
```
cmd = ""
return = 0
```

---

### ❌ Invalid (Null Pointer)

**Input**
```
op  = NULL
cmd = valid pointer
```

**Output**
```
cmd = ""
return = 0
```

---

## 5. Error Handling

When validation or formatting fails:
- Do **not** perform any string write operations other than setting:
  ```
  cmd[0] = '\0';
  ```
- Return `0`.
- The function must validate:
  - `op` and `cmd` are not `NULL`
  - `op` matches one of the valid operation codes: `RE`, `YE`, `GE`, `RB`, `GTH`
  - If operation is not `GTH`, check that `n ∈ [0, 99]`

---

## 6. Implementation Notes

- Follow **RISC-V ABI** (register preservation)
- Ensure trimming of whitespace around `op` before processing
- Convert `op` to uppercase before comparison
- Valid operation codes:
  ```
  {"RE", "YE", "GE", "RB", "GTH"}
  ```
- Use string functions (`toupper`, `strncpy`, `sprintf`, etc.) or equivalent assembly logic

**Formatting logic (pseudocode):**

```c
if (op == NULL || cmd == NULL) return 0;

trim(op);
to_upper(op);

if (strcmp(op, "GTH") == 0) {
    strcpy(cmd, "GTH");
    return 1;
}

if ((strcmp(op, "RE") == 0 || strcmp(op, "YE") == 0 ||
     strcmp(op, "GE") == 0 || strcmp(op, "RB") == 0) && n >= 0 && n < 100) {
    sprintf(cmd, "%s,%02d", op, n);
    return 1;
}

cmd[0] = '\0';
return 0;
```

---

## 7. Input / Output Examples in C

### Example 1 — Valid Command (RB)

```
int value = 5;
char str[] = " rB ";
char cmd[20];
int res = format_command(str, value, cmd);
printf("%d:%s\n", res, cmd);
```

**Output**
```
1:RB,05
```

---

### Example 2 — Valid Command (YE)

```
strcpy(str, " Ye ");
value = 25;
res = format_command(str, value, cmd);
printf("%d:%s\n", res, cmd);
```

**Output**
```
1:YE,25
```

---

### Example 3 — Invalid Value (>99)

```
strcpy(str, " Ye ");
value = 125;
res = format_command(str, value, cmd);
printf("%d:%s\n", res, cmd);
```

**Output**
```
0:
```

---

### Example 4 — Invalid Operation

```
strcpy(str, " aaa ");
value = 25;
res = format_command(str, value, cmd);
printf("%d:%s\n", res, cmd);
```

**Output**
```
0:
```

---

### Example 5 — Special Command (GTH)

```
strcpy(str, " gTh ");
value = 50;
res = format_command(str, value, cmd);
printf("%d:%s\n", res, cmd);
```

**Output**
```
1:GTH
```
