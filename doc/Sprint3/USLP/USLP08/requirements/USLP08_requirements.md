# USLP08 — Route Planner for Freights

## 1. Requirements Engineering

### 1.1. User Story Description

As a **Traffic Manager**, I want the system to plan routes for pending freights, so that cargo can be transported from origin to destination using a feasible railway path that respects the physical railway network.

This user story focuses on **freight route planning**, not on detailed scheduling, speed computation, or conflict resolution, which are handled in later user stories.

---

### 1.2. Customer Specifications and Clarifications

- A **freight** represents a cargo shipment with:
  - Origin station
  - Destination station
  - Cargo description and quantity

- A **route** defines *what* must be transported and *between which stations*.

- A **path** is the ordered list of stations through which the freight will be transported.

- The route planner must:
  - Identify a valid path between the freight’s origin and destination
  - Use the railway network topology to compute feasible paths
  - Support multiple pending freights

- **Scope limitation:**
  - No train, locomotive, or wagon assignment is performed in this US
  - No speed calculation, timetable generation, or crossing detection is required
  - The planner focuses only on connectivity and route feasibility

- **Path calculation:**
  - The system may use graph-based algorithms (e.g., shortest path by distance)
  - Only physically connected stations may appear consecutively in a path

- **Output representation:**
  - For each freight, the system must produce:
    - The computed path (sequence of stations)
    - Loading operation at the origin
    - Unloading operation at the destination

---

### 1.3. Acceptance Criteria

| ID | Requirement |
|----|-------------|
| **AC1** | The system must retrieve all pending freights. |
| **AC2** | Each freight must have a defined origin and destination station. |
| **AC3** | The system must compute a feasible path between origin and destination using the railway network. |
| **AC4** | The computed path must consist only of physically connected stations. |
| **AC5** | For each freight, the system must generate a route description including loading and unloading operations. |
| **AC6** | If no feasible path exists, the freight must be marked as not plannable and reported to the user. |
| **AC7** | The system must return a list of planned freight routes. |

---

### 1.4. Found-Out Dependencies

- **Railway Network Data:** stations and line segments must be available.
- **Graph Representation:** the railway network must be represented as a graph.
- **Database Access:** pending freights and network data may be retrieved from the database.

---

### 1.5. Input and Output Data

**Input:**
- List of pending freights:
  - `freightId`
  - `originStation`
  - `destinationStation`
  - `cargoDescription`

- Railway network graph:
  - Stations
  - Line segments (connectivity)

**Output:**
- List of planned routes, each containing:
  - `freightId`
  - `originStation`
  - `destinationStation`
  - `path` (ordered list of stations)
  - `operations` (LOAD at origin, UNLOAD at destination)

---

### 1.6. Other Relevant Remarks

- This user story provides the foundation for:
  - USLP09 — Train assembly and assignment
  - USLP10 — Advanced train scheduling
- The distinction between **route** and **path** must be preserved consistently.
- The solution should be deterministic for identical inputs.

---

## 2. Out of Scope

The following features are explicitly out of scope for this US:
- Train creation or dispatch
- Locomotive or wagon assignment
- Speed calculation
- Timetable generation
- Conflict detection on single-track segments
- Crossing operations
