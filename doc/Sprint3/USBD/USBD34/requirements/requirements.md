# USBD34 - Associate a Freight with a Train

## 1. Requirements Engineering

### 1.1. User Story Description

As a **Freight Manager**, I want to **associate a freight with a train**, so that all wagons belonging to that freight become 
linked to that specific planned train.
---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> All the wagons included in the freight will be associated with the train.
> US must be implemented as a PL/SQL function.
> The project must ensure data integrity and business rule validation when registering or updating data.

---

### 1.3. Acceptance Criteria

- **AC1:** The system must create an association between a freight and a train.
- **AC2:** The function must verify that both the freight and the train exist.
- **AC3:** The freight must not already be assigned to another train.
- **AC5:** Data integrity and foreign key relationships must be enforced.
- **AC6:** The function must not allow duplicate assignments of the same freight.

---

### 1.4. Found out Dependencies

This user story depends on:
- USBD23 for all imported base data (freight list, train list).
- The following tables:
    - `freight` 
    - `train` 
    - `wagon`
    - association table `train_wagon`

---

### 1.5. Input and Output Data

**Input Data:**

- Freight identifier
- Train identifier

**Output Data:**

- A **SYS_REFCURSOR** containing:
    - Freight ID
    - Freight description (optional, depending on schema)
    - Train ID
    - Train designation

---

### 1.6. System Sequence Diagram (SSD)

![USBD34-SSD](svg/SSD.svg)

---

### 1.7. Other Relevant Remarks

- A PL/SQL function must be created (mandatory for Sprint 3).
- An anonymous PL/SQL block must be provided to fully test the function.
- The function must not use SYSDATE to avoid non-deterministic behaviour during tests.
- All business validations must occur before any modification to the database.
- Error handling should be deterministic and meaningful.
- Implementation should handle errors and raise exceptions when necessary.
- Suggested PL/SQL function signature:

```sql
FUNCTION associate_freight_train (
  p_freight_id IN NUMBER,   
  p_train_id   IN NUMBER
) RETURN SYS_REFCURSOR;