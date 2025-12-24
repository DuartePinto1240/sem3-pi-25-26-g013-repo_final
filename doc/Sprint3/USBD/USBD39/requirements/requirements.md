
# USBD39 - Associate a Locomotive with a Planned Train

## 1. Requirements Engineering

### 1.1. User Story Description

As a **Freight Manager**, I want to **associate a locomotive with a planned train**, so that the train can be operated using the assigned rolling stock.

---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> A PL/SQL function must be implemented for each user story.  
> Only user stories with data allowing their proper functioning will be evaluated.  
> The function must be executable and testable using anonymous PL/SQL blocks.

**From the client clarifications:**

> **Question:** Can a locomotive be assigned to more than one train?  
> **Answer:** No. A locomotive can only be assigned to **one train at a time**.

> **Question:** What happens if the train or locomotive does not exist?  
> **Answer:** The operation must fail with an appropriate error.

---

### 1.3. Acceptance Criteria

- **AC1:** The locomotive must exist in the system.
- **AC2:** The train must exist in the system.
- **AC3:** A locomotive can only be associated with **one** train.
- **AC4:** The association must be stored in the database.
- **AC5:** The function must return a message confirming success or describing the error.
- **AC6:** Referential integrity must be preserved.

---

### 1.4. Found out Dependencies

- Depends on:
    - `Train` table
    - `Locomotive` table
    - `TrainLocomotive` association table

---

### 1.5. Input and Output Data

**Input Data:**

- Train identifier
- Locomotive identifier

**Output Data:**

- A textual result indicating:
    - Success of the association, or
    - Reason for failure (invalid IDs, business rule violation, constraint error)

---

### 1.6. Other Relevant Remarks

- Business rule enforcement is mandatory before insertion.
- The function must handle race conditions and constraint violations.
- Suggested PL/SQL function signature:

```sql
FUNCTION fnc_usbd39_associate_loco (
    p_train_id IN Train.train_id%TYPE,
    p_loco_id  IN Locomotive.loco_id%TYPE
) RETURN VARCHAR2;
```
