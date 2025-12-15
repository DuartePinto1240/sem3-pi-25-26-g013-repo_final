# USBD36 — Add a Building to an Existing Facility

## 1. Requirements Engineering

### 1.1. User Story Description

As a **Planner**, I want to **add a building** (e.g., Warehouse, Silo, Maintenance Hangar) to an existing facility, so that the capacity and operational capabilities of that facility are correctly modeled in the system.

---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> A PL/SQL function must be implemented for each user story, returning a **SYS_REFCURSOR**.
> Only user stories with data allowing their proper functioning will be evaluated.
> Facilities may include **warehouses**, **refrigerated areas**, and **grain silos**.
> Whenever possible, data imported in **USBD23** or created in **USBD35** must be used.

**From the client clarifications:**

> **Question:** What defines a building within a facility?
> **Answer:** A building is a physical structure located inside a facility. It must have a **name/identifier** and a **type** (e.g., 'Warehouse', 'Silo').

> **Question:** Can a building exist without a facility?
> **Answer:** No. Every building must be associated with a valid, existing **facility**.

> **Question:** Are there constraints on building names?
> **Answer:** Yes. A building name must be unique within the context of its facility (i.e., a facility cannot have two buildings with the exact same name).

---

### 1.3. Acceptance Criteria

- **AC1:** A new building must be registered in the database linked to a specific facility.
- **AC2:** The function must validate that the parent facility exists.
- **AC3:** The function must return a **SYS_REFCURSOR** containing the details of the created building.
- **AC4:** Each record in the output must include:
  - Building Identifier
  - Building Name
  - Building Type
  - Parent Facility Name
- **AC5:** The function must prevent duplicate building names within the same facility.
- **AC6:** Data integrity must be maintained (e.g., building type should be valid).

---

### 1.4. Found out Dependencies

- Depends on **USBD35** (Register a New Facility) as the facility must exist first.
- Depends on:
  - `facility` table
  - `building` (or `facility_building`) table

---

### 1.5. Input and Output Data

**Input Data:**

- Parent Facility Identifier (ID or Name)
- Building Name
- Building Type (e.g., 'Warehouse', 'Silo')

**Output Data:**

- A **SYS_REFCURSOR** containing:
  - Building ID
  - Building Name
  - Building Type
  - Facility Name

---

### 1.7. Other Relevant Remarks

- Suggested PL/SQL function signature:

```sql
FUNCTION add_building_to_facility (
    p_facility_id IN NUMBER, -- or VARCHAR2 depending on your PK
    p_name        IN VARCHAR2,
    p_type        IN VARCHAR2
) RETURN SYS_REFCURSOR;