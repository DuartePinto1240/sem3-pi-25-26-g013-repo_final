
# USAC11 — Initial Setup from Text File

## 1. User Story Description

As a **railway system administrator**, I want to **initialize the system by loading data from a text file**, so that railway lines, stations, and initial configuration can be automatically set up at startup.

---

## 2. Requirements / Specifications

The function to implement is:

```
int load_initial_config(const char* filename)
```

### Input
- `filename` — pointer to a null-terminated string containing the path to the configuration text file.

### Operation Rules
- The function must open and read the specified text file.
- The file contains structured data describing:
    - Stations
    - Railway tracks
    - Connections between stations
- Each line of the file represents a configuration command or entity.
- The function must parse the file line by line and initialize internal system structures accordingly.
- If the file is successfully processed, the system must be ready for operation.

### Invalid Conditions
- `filename` is NULL
- The file does not exist or cannot be opened
- The file format is invalid or contains inconsistent data

---

## 3. Acceptance Criteria

| ID      | Description                                                            |
|---------|------------------------------------------------------------------------|
| **AC1** | The function must open and read the text file successfully.            |
| **AC2** | The file contents must be parsed line by line.                          |
| **AC3** | Stations and railway tracks must be correctly initialized in memory.   |
| **AC4** | Invalid or malformed lines must cause the function to fail safely.     |
| **AC5** | The function returns `1` on successful initialization.                 |
| **AC6** | The function returns `0` on failure without altering existing data.    |

---

## 4. Input / Output Examples

### Valid Example

**Input file (`config.txt`)**
```
STATION;Porto
STATION;Lisboa
TRACK;Porto;Lisboa
```

**Function Call**
```
load_initial_config("config.txt")
```

**Output**
```
Stations loaded: Porto, Lisboa
Track created: Porto -> Lisboa
return = 1
```

---

### Invalid Example (File not found)

**Input**
```
load_initial_config("missing.txt")
```

**Output**
```
return = 0
```

---

### Invalid Example (Malformed file)

**Input file**
```
STATION-Porto
TRACK;Porto
```

**Output**
```
return = 0
```

---

## 5. Error Handling

When validation fails:
- The file must be closed immediately (if opened).
- No partial configuration must remain in the system.
- The function must return `0`.

Checks must include:
- `filename != NULL`
- File open success
- Valid format and number of parameters per line

---

## 6. Implementation Notes

- Must be implemented in **C**, respecting the project architecture.
- File reading should use standard C file I/O (`fopen`, `fgets`, `fclose`).
- Parsing logic should be modular and reusable.
- Dynamic memory allocation must be properly managed.
- This functionality is typically executed once during system startup.

Example logic:
```
if (filename == NULL)
    return 0;

open file
for each line:
    parse and validate
    load entity into system

close file
return 1;
```