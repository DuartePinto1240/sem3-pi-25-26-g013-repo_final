# USAC14 — Control Track Sign Light

## 1. User Story Description

As a **system user**, I want to **control a railway track signal light**, so that I can manage train operations visually.

---

## 2. Requirements / Specifications

Function prototype:

```
int control_track_light(int track_id, const char* command)
```

### Input
- `track_id` — target track.
- `command` — instruction string.

### Valid Commands:
- `RE` — red on
- `YE` — yellow on
- `GE` — green on
- `RB` — red blinking

### Operation Rules
- Turn off previous LED state first.
- Update internal track status.
- Forward command to LightSigns module.
- Uses assembly format_command()

### Invalid Conditions
- Track not found
- `command == NULL`
- Invalid command
- Track disabled

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | Track selected by ID. |
| **AC2** | Command formatted & validated. |
| **AC3** | Track memory state updated. |
| **AC4** | Return `1` success. |
| **AC5** | Return `0` fail. |

---

## 4. Output Example
```
control_track_light(3, "ge") → "GE,03"
return = 1
```

---

## 5. Error Handling
- Invalid track → return `0`
- Unknown command → return `0`

---

## 6. Implementation Notes
- Uses format_command()
- Synchronize with Lights component
