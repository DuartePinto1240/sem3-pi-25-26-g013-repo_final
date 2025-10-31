# Supplementary Specification (FURPS+)

## Functionality

- The system must support **railway network management**, including **infrastructure modeling** (tracks, stations, terminals) and **rolling stock management** (locomotives, wagons).
- **Security requirements** include **user authentication** for roles like **planners** and **operators**, requiring passwords with **at least eight characters**, including **uppercase letters**, **digits**, and **special symbols**.
- All key algorithms (e.g., **FEFO/FIFO for cargo handling**, **graph-based routing**) must use **primitive Java operations only**, not external libraries.

---

## Usability

- Provide **command-line prompts and menus** to guide users in **data import**, **query execution**, and **cargo simulation**.
- The interface shall be **text-based**, with clear options for user stories like **listing line segments** or **generating picking plans**.
- **Helpful error messages** and **validation feedback** shall assist users during **CSV file imports** or **warehouse operations**.
- **User roles** (e.g., planner, station manager) may have **role-specific menus** to reduce complexity and input errors.

---

## Reliability

The system shall be built using **Test-Driven Development (TDD)** with:

- **JUnit 5** for unit testing.
- **Rule validations** for business logic (e.g., gauge compatibility, capacity constraints).
- Target code coverage is at least **80%**, verified using **JaCoCo**.

---

## Performance

- Algorithms for **graph traversal**, **cargo allocation (FEFO/FIFO)**, or **picking path sequencing** must be optimized to handle **large datasets** from `.csv` files (e.g., thousands of wagons or bays).

---

## Supportability

- The codebase shall follow **object-oriented design principles (OOP)**.
- Classes must be **modular and reusable**, with **Javadoc documentation** for maintainability.
- **Markdown documentation** (e.g., for **USEI04’s sequencing strategies**) shall be included in the repository.
- The system shall support **SVG export** for **warehouse plants** or **network visualizations**.
- The use of **Java** as the primary language should facilitate **scalability** and **long-term maintenance**.

---

## +

### Design Constraints

- The software must be developed in **Java**, following **Object-Oriented (OO) best practices**.
- The solution must use **Test-Driven Development (TDD)** using **JUnit 5** for unit tests.
- The project must follow the **Scrum methodology**, ensuring **iterative and incremental development across three sprints**.

---

### Implementation Constraints

- **Java** is **mandatory** for the main application, with **PL/SQL** for database components and **C/Assembly** for station interactions, adhering to coding standards (e.g., **CamelCase**).
- The project must include **comprehensive unit tests**, achieving at least **80% code coverage**, verified via the **JaCoCo plugin**.
- Logic related to **cargo handling**, **inventory management**, or **path calculations** must use **primitive loops**, **conditionals**, and **collections**, not Java **Stream APIs**.
- The **GitHub repository** must:
    - Be named according to the format `sem3-pi-25-26-gNNN-repo`
    - Remain **private**
    - Include a **working Scrum board** with columns: *Backlog*, *ToDo*, *In Process*, *Testing*, *Done*, *Logbook*

---

### Interface Constraints

- The system must be **compatible with external data formats**, such as **SVG for images** generated during software development (e.g., **warehouse plants**).
- The system shall support **import/export of data via .csv files** (e.g., for **bays**, **wagons**, **items**, **orders**).

---

### Physical Constraints

- The application should be **lightweight and optimized** to run efficiently on **standard desktop and laptop computers**.
- The application must remain **lightweight and fully executable** on machines with **Java 11+** and **Oracle DBMS**, without heavy external dependencies.  
