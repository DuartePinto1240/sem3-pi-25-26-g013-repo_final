# USBD35 - Register a New Facility

## 1. Requirements Engineering

### 1.1. User Story Description

As an **operator**, I want to **register a new facility** (e.g., station, depot, terminal) in the system, so that it can be used in railway operations, logistics planning, and freight management.

---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> A PL/SQL function must be implemented for each user story, returning a **SYS_REFCURSOR**.  
> Only user stories with data allowing their proper functioning will be evaluated.  
> Every query must return at least one record.  
> Whenever possible, data imported in **USBD23** must be used.

**From the client clarifications:**

> **Question:** What information is required to register a facility?  
> **Answer:** Each facility must be uniquely identified and include at least its **name**, **type**, and **location**.

> **Question:** Are facilities associated with operators?  
> **Answer:** Yes. Each facility must belong to a specific **operator**.

> **Question:** Should duplicate facilities be allowed?  
> **Answer:** No. Facilities with the same name and operator should not be duplicated.

---

### 1.3. Acceptance Criteria

- **AC1:** A new facility must be registered in the database.
- **AC2:** The facility must be associated with an existing operator.
- **AC3:** The function must return a **SYS_REFCURSOR** confirming the registered facility.
- **AC4:** Each record must include:
    - Facility identifier
    - Facility name
    - Facility type
    - Operator name
- **AC5:** The function must prevent duplicate facilities for the same operator.
- **AC6:** The function must ensure data integrity and valid foreign key references.

---

### 1.4. Found out Dependencies

- Depends on **USBD23** for base operator data.
- Depends on:
    - `facility` table
    - `operator` table

---

### 1.5. Input and Output Data

**Input Data:**

- Facility name
- Facility type
- Location (if applicable)
- Operator identifier or name

**Output Data:**

- A **SYS_REFCURSOR** containing:
    - Facility ID
    - Facility name
    - Facility type
    - Operator name

---

### 1.7. Other Relevant Remarks

- Referential integrity between facility and operator must be guaranteed.
- The function should be optimized for Oracle LiveSQL execution.
- Suggested PL/SQL function signature:

```sql
FUNCTION register_facility (
    p_name     IN VARCHAR2,
    p_type     IN VARCHAR2,
    p_operator IN VARCHAR2
) RETURN SYS_REFCURSOR;
