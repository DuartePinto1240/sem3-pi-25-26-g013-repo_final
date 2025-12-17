# USBD42 — Register a Train Movement

## 1. Requirements Engineering

### 1.1. User Story Description

As a **Freight Manager**, I want to **register a train movement** in the database. If the train stopped at the facility, the departure time shall also be registered.

**Context:**
> A train, when planned, has a scheduled route and a start date and time. As it runs, the instant it crosses or arrives at an endpoint is recorded. If the train stops at an endpoint, the leaving instant should also be recorded.

**User Story:**
> As a Freight Manager, I want to register a train movement in the database. If the train stopped at the facility, the departure time shall also be registered.

---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> A PL/SQL function must be implemented for each user story, returning a **SYS_REFCURSOR**.
> Only user stories with data allowing their proper functioning will be evaluated.
> As a train runs, the instant it **crosses** or **arrives** at an endpoint is recorded.

**From the client clarifications:**

> **Question:** What constitutes a train movement registration?
> **Answer:** It records that a specific **train** arrived at or passed through a specific **facility/station** at a certain **time**.

> **Question:** Is the departure time mandatory?
> **Answer:** No. If the train only passes through (does not stop), only the arrival instant is required. If it stops, both arrival and departure times must be registered.

> **Question:** How do we handle logical constraints on time?
> **Answer:** The departure time (if present) must be strictly later than the arrival time.

---

### 1.3. Acceptance Criteria

- **AC1:** A movement record must be inserted linking a train to a facility/station.
- **AC2:** The system must register the **arrival timestamp**.
- **AC3:** If a **departure timestamp** is provided, it must also be registered.
- **AC4:** The function must validate that the train and facility exist.
- **AC5:** The function must validate that **Departure Time > Arrival Time** (if departure is not null).
- **AC6:** The function must return a **SYS_REFCURSOR** confirming the registered movement details.

---

### 1.4. Found out Dependencies

- Depends on **USBD39** or **USBD34** to ensure trains exist.
- Depends on **USBD35** to ensure facilities exist.
- Depends on:
  - `train` table
  - `facility` table
  - `train_movement` table

---

### 1.5. Input and Output Data

**Input Data:**
- Train Identifier
- Facility/Station Identifier
- Arrival Timestamp
- Departure Timestamp (Optional / Nullable)

**Output Data:**
- A **SYS_REFCURSOR** containing:
  - Movement ID
  - Train Identifier
  - Facility Name
  - Arrival Time
  - Departure Time

---

### 1.7. Other Relevant Remarks

- Suggested PL/SQL function signature:
```sql
FUNCTION register_train_movement (
    p_train_id       IN NUMBER,
    p_facility_id    IN NUMBER,
    p_arrival_time   IN TIMESTAMP,
    p_departure_time IN TIMESTAMP DEFAULT NULL
) RETURN SYS_REFCURSOR;