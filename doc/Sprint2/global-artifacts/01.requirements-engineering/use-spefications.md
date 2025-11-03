
# Supplementary Specification — FURPS+ (Aligned with v2.21)

## Functionality

- The system shall support:
    - **Railway network management** including infrastructure modeling (tracks, stations, terminals)
    - **Rolling stock management** including locomotives, trains, and wagons
- Security requirements:
    - **User authentication** with passwords of at least 8 characters including uppercase letters, digits, and a special character
    - Role‑based access control (planner, operator, administrator…)
- Core operational algorithms must be implemented in **pure Java**, without external libraries:
    - **FEFO/FIFO** for inventory and cargo handling
    - **Graph‑based traversal algorithms** for routing and travel time
- Integration with other languages:
    - **C/Assembly** for station hardware interaction
    - **PL/SQL** for data persistence and queries

---

## Usability

- **Console‑based interface** with clear menus and command prompts
- Role‑specific workflows to simplify user interactions
- Helpful validation and **deterministic error messages** for operations such as CSV imports
- Accessible execution of all relevant user stories via the terminal UI

---

## Reliability

- Full **Test‑Driven Development (TDD)** using **JUnit 5**
- Minimum test coverage: **80%**, validated with **JaCoCo**
- Enforcement of business rule validation:
    - gauge compatibility
    - cargo capacity and weight constraints
    - warehouse and rail operational limits

---

## Performance

- Efficient handling of **large in‑memory datasets** such as:
    - ~64k European station records
    - Thousands of shipments and warehouse records
- Algorithms must demonstrate acceptable time complexity and predictable performance
- Indexed data structures (KD‑Tree, AVL/BST) for near‑constant‑time queries

---

## Supportability

- Architecture must follow **OOP principles** (modular, reusable, maintainable)
- **Javadoc** documentation required for core Java classes
- Markdown documentation (including strategy descriptions and architecture notes) included in repository
- Supports **SVG export** for network or warehouse views

---

## + (Additional Requirements)

### Design Constraints

- Developed in **Java** as the primary language
- **SCRUM methodology** with 3 sprints and a private Scrum board
    - Columns must include: Backlog, To Do, In Progress, Testing, Done, Logbook
- Logging and traceability mandatory for station operations and cargo handling

---

### Implementation Constraints

- Java **Stream API not allowed** for core business logic — use:
    - loops
    - conditionals
    - primitive collections
- GitHub repository:
    - Must be named `sem3-pi-25-26-gNNN-repo`
    - Must remain **private**
    - Instructor/TA access required
- Storage managed in **Oracle DBMS** via PL/SQL scripts/functions

---

### Interface Constraints

- Supports **.csv import/export**
- Compatible with **SVG** outputs created during simulation and reporting

---

### Physical Constraints

- Lightweight execution using **Java 11+** on standard desktop/laptop hardware
- Minimal third‑party dependencies to ensure portability and maintainability