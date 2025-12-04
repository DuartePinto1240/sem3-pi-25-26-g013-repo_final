
---


# USBD40 - Register a Freight

## 1. Requirements Engineering

### 1.1. User Story Description

As a **logistics planner**, I want to **register a freight** in the system, so that it can later be planned, assigned to trains, and transported through the railway network.

---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> A PL/SQL function must be implemented for each user story, returning a **SYS_REFCURSOR**.  
> Only user stories with populated data will be evaluated.  
> Each query must return at least one record.  
> Data imported in **USBD23** should be reused whenever applicable.

**From the client clarifications:**

> **Question:** What defines a freight?  
> **Answer:** A freight is defined by its **type**, **weight**, **origin facility**, and **destination facility**.

> **Question:** Can a freight exist without being assigned to a train?  
> **Answer:** Yes. Freight can be registered independently and assigned later.

> **Question:** Should weight constraints be validated at this stage?  
> **Answer:** Only basic validation is required; full constraints are handled in later user stories.

---

### 1.3. Acceptance Criteria

- **AC1:** The system must register a new freight record.
- **AC2:** Origin and destination facilities must exist in the database.
- **AC3:** The PL/SQL function must return a **SYS_REFCURSOR** with the freight details.
- **AC4:** Each record must include:
  - Freight identifier  
  - Freight type  
  - Weight  
  - Origin facility  
  - Destination facility
- **AC5:** Weight values must be positive.
- **AC6:** Referential integrity between freight and facility must be enforced.

---

### 1.4. Found out Dependencies

- Depends on **USBD23** for base data.
- Depends on:
  - `freight` table  
  - `facility` table  

---

### 1.5. Input and Output Data

**Input Data:**

- Freight type  
- Freight weight  
- Origin facility identifier  
- Destination facility identifier  

**Output Data:**

- A **SYS_REFCURSOR** containing:
  - Freight ID  
  - Freight type  
  - Weight  
  - Origin facility  
  - Destination facility  

---

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/SSD_USBD40.svg)

---

### 1.7. Other Relevant Remarks

- The function must validate facility existence before inserting freight data.
- Designed to be fully compatible with later user stories involving train planning.
- Suggested PL/SQL function signature:

```sql
FUNCTION register_freight (
    p_type        IN VARCHAR2,
    p_weight      IN NUMBER,
    p_origin      IN VARCHAR2,
    p_destination IN VARCHAR2
) RETURN SYS_REFCURSOR;
