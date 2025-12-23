
# USBD45 - Add a New Railway Line with at Least One Segment

## 1. Requirements Engineering

### 1.1. User Story Description

As a **Planner**, I want to **add a new railway line with at least one segment**, so that railway infrastructure can be correctly defined and used for routing and planning.

---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> Each PL/SQL function must enforce all business rules defined in the data model.  
> Operations must be atomic whenever multiple inserts are required.

**From the client clarifications:**

> **Question:** Can a railway line exist without segments?  
> **Answer:** No. A line must have **at least one segment** at creation time.

> **Question:** Are line names unique?  
> **Answer:** Yes. Line names must be unique, **case-insensitive**.

---

### 1.3. Acceptance Criteria

- **AC1:** A railway line must be created in the database.
- **AC2:** At least one segment must be created and associated with the line.
- **AC3:** Gauge, owner, and facilities must exist.
- **AC4:** Line names must be unique (case-insensitive).
- **AC5:** The operation must be atomic.
- **AC6:** The function must return a success or error message.

---

### 1.4. Found out Dependencies

- Depends on:
    - `RailwayLine` table
    - `LineSegment` table
    - `Gauge` table
    - `Owner` table
    - `Facility` table

---

### 1.5. Input and Output Data

**Input Data:**

- Line identifier
- Line name
- Gauge identifier
- Owner identifier
- Start facility identifier
- End facility identifier
- Segment attributes:
    - Length
    - Speed limit
    - Maximum weight
    - Track type
    - Electrification
    - Siding data (optional)

**Output Data:**

- A textual result indicating:
    - Successful creation of the line and segment, or
    - The reason for failure

---

### 1.6. Other Relevant Remarks

- Segment creation order must start at 1.
- Constraint enforcement should rely on database checks when appropriate.
- Suggested PL/SQL function signature:

```sql
FUNCTION fnc_usbd45_add_line (
    p_line_id           IN RailwayLine.line_id%TYPE,
    p_line_name         IN RailwayLine.name%TYPE,
    p_gauge_id          IN RailwayLine.gauge_id%TYPE,
    p_owner_id          IN RailwayLine.owner_id%TYPE,
    p_start_facility_id IN RailwayLine.start_facility_id%TYPE,
    p_end_facility_id   IN RailwayLine.end_facility_id%TYPE,
    p_segment_id        IN LineSegment.line_segment_id%TYPE,
    p_length            IN LineSegment.length_m%TYPE,
    p_speed             IN LineSegment.speed_limit%TYPE,
    p_weight            IN LineSegment.max_weight%TYPE,
    p_track_type        IN LineSegment.track_type%TYPE,
    p_electrified       IN LineSegment.electrified%TYPE,
    p_siding_pos        IN LineSegment.siding_position%TYPE,
    p_siding_len        IN LineSegment.siding_length%TYPE
) RETURN VARCHAR2;
```
