# USAC13 — Retrieve Sensor Data

## 1. User Story Description

As a **system user**, I want to **retrieve the current processed temperature and humidity data from the Sensors component**, so that I can view station environmental readings.

---

## 2. Requirements / Specifications

Function to implement:

```
int get_sensor_data(char* data_out)
```

### Input
- `data_out` — buffer to store formatted output.

### Operation Rules
- Request updated sensor feed.
- Apply Moving Median filter.
- Response format:
```
TEMP:xx unit | HUM:xx unit
```

### Invalid Conditions
- Sensor unavailable
- `data_out == NULL`
- Data corrupted

---

## 3. Acceptance Criteria

| ID | Description |
|----|-------------|
| **AC1** | Must request live readings. |
| **AC2** | Values filtered internally. |
| **AC3** | Output must show both sensors. |
| **AC4** | Return `1` on success. |
| **AC5** | Return `0` on failure. |

---

## 4. Output Example
```
TEMP:21 celsius | HUM:78 percent
```

---

## 5. Error Handling
- On fail → empty string
- Must check NULL pointer

---

## 6. Implementation Notes
- Uses Sprint‑2 assembly helpers:
  extract_data(), median(), move_n_to_array()
