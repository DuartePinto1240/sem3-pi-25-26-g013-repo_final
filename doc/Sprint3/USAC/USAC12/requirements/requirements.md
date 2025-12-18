# USAC12 — Log Export by User

## 1. User Story Description

As a **railway system administrator**, I want to **create a text file (.txt) containing a sequence of actions performed by a specific user**, so that I can analyze or audit that user’s past activity.

---

## 2. Requirements / Specifications

The function to implement is:

```
int export_user_log(const char* username, const char* filename)
```

### Input
- `username` — valid system username.
- `filename` — pointer to a null-terminated string defining the output file name/path.

### Operation Rules
- Validate user existence.
- Access internal log structure.
- Export logs chronologically, one entry per line.
- Include ID, timestamp, and description.
- Overwrite file if exists.

### Invalid Conditions
- Unknown username
- No logs for user
- `filename == NULL`
- File creation error
- Log system not initialized

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | System must locate target user. |
| **AC2** | Only that user’s logs exported. |
| **AC3** | Logs sorted by timestamp. |
| **AC4** | Output `.txt` file created. |
| **AC5** | Return `1` on success. |
| **AC6** | Return `0` on failure. |

---

## 4. Input / Output Example

### Example Output File
```
LOG001 – 2024-11-18 16:33 – Track 02 set GREEN
LOG002 – 2024-11-18 16:35 – Train 17 assigned to Track 01
```

---

## 5. Error Handling
- User not found → return `0`.
- No logs → return `0`.
- `filename == NULL` → return `0`.

---

## 6. Implementation Notes
- Implement in C.
- Iterate log structure.
- Overwrite file.
- Ensure formatting consistency.

