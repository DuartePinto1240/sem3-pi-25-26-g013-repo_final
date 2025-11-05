# User Manual and Technical Documentation for *Logistics on Rails*
**Version 1.0**  
**October 2025**

---

## **Cover**

**Title:** Logistics on Rails — Railway Terminal Management & Simulation System  
**Institution:** Instituto Superior de Engenharia do Porto (ISEP)

**Date:** October 2025

---

## **Table of Contents**

1. Introduction
2. System Overview
3. System Requirements
4. Features & Functionalities
5. Operating Instructions
6. Troubleshooting and Helpdesk
7. FAQs (Frequently Asked Questions)
8. Glossary
9. Contact & Support

---

## 1. Introduction

### **Purpose**
This manual provides complete guidance for installing, configuring, and operating the **Logistics on Rails** system — an integrated railway terminal management and simulation solution.  
It assists users in managing railway networks, warehouses, and cargo flows, covering data management (BDDAD), algorithms and inventory operations (ESINF), and simulation/visualization (LAPR3).

### **Scope**
- Model and instantiate the railway system database.
- Manage cargo unloading, storage, allocation, and returns in warehouses.
- Plan and simulate train movement, dispatch operations, and performance.
- Support decision-making with visualizations and statistics.

### **Target Audience**
- **Database Designers (BDDAD):** Define and manage the railway data model.
- **Warehouse Managers (ESINF):** Operate cargo flows using FIFO/FEFO rules.
- **Simulation Operators / Players (LAPR3):** Simulate routes, trains, and scenarios.
- **Product Owners:** Oversee integration and ensure project deliverables.

---

## 2. Overview of the System

The **Logistics on Rails** platform integrates three core modules that represent distinct stages of logistics and simulation:

| Module | Description | Technologies |
|--------|--------------|--------------|
| **BDDAD – Database Layer** | Relational model and data dictionary for the railway infrastructure, rolling stock, and terminals. | Oracle SQL / PL-SQL |
| **ESINF – Information Structures** | Manages cargo operations, warehouses, unloading, dispatching, and returns using in-memory data structures. | Java |
| **LAPR3 – Simulation Interface** | User-facing simulator to build, visualize, and manage the railway network (stations, lines, trains). | Java |

### **Architecture Overview**
1. **Database Layer (BDDAD):**  
   Stores all static data (stations, lines, locomotives, wagons, industries).

2. **Business Logic Layer (ESINF):**  
   Loads warehouse datasets from CSV files, processes stock operations (USEI01–USEI05).

3. **Simulation & UI Layer (LAPR3):**  
   Provides an interactive environment for players to build and manage the virtual railway network and run simulations.

---

## 3. System Requirements

### **Software**
- **Operating System:** Windows, macOS, or Linux
- **Java Runtime:** JDK 17+
- **Database:** Oracle LiveSQL or Local OracleDB
- **IDE:** IntelliJ IDEA / Eclipse / VS Code


### **Hardware**
- Processor: Intel i5 or higher
- Memory: 8 GB RAM minimum
- Disk Space: 1 GB free

---

## 4. Features & Functionalities

### **4.1 Database Design (BDDAD)**
- Define all railway entities: Stations, Lines, Segments, Wagons, Locomotives.
- Create logical and physical models in Visual Paradigm.
- Generate and execute SQL DDL scripts in Oracle LiveSQL.
- Populate initial data using CSV imports (USBD01–USBD04).
- Implement analytical queries (USBD05–USBD20) such as:
    - List all line segments by owner or gauge.
    - Calculate electrified track length.
    - Compute average segment length per track type.

---

### **4.2 Warehouse Management (ESINF)**

#### **USEI01 — Wagons Unloading (Inventory Replenishment)**
- Import `bays.csv` and `wagons.csv`.
- Assign boxes to bays ordered by:
    1. Expiry Date (earliest first, null last)
    2. ReceivedAt (oldest first)
    3. BoxId (ascending, tie-breaker)
- Implements FEFO/FIFO storage logic.

#### **USEI02 — Order Eligibility & Allocation**
- Import `orders.csv` and `order_lines.csv`.
- Allocate inventory from boxes according to FEFO/FIFO.
- Output allocation tables per order and per line with ELIGIBLE, PARTIAL, or UNDISPATCHABLE status.

#### **USEI03 — Picking Plans**
- Optimize picking of orders by trolley capacity.
- Implement heuristics:
    - First Fit (FF)
    - First Fit Decreasing (FFD)
    - Best Fit Decreasing (BFD)
- Display per-trolley utilization and box list.

#### **USEI04 — Pick Path Sequencing**
- Compute optimal 2D picking routes based on aisle/bay coordinates.
- Two strategies supported:
    - Deterministic sweep (ascending by aisle)
    - Nearest-Neighbour (greedy algorithm)
- Calculate and display total walking distance.

#### **USEI05 — Returns & Quarantine**
- Process `returns.csv`.
- Handle returned goods in reverse arrival order.
- For each return:
    - If **Damaged/Expired** → Discard.
    - If **CustomerRemorse/CycleCount** → Restock as new box (RET-ID).
- Generate **Audit Log** entries for each inspection.

---

### **4.3 Simulation and Player Interaction (LAPR3)**

#### **USLP01 — Domain Model Creation**
- Model all railway entities (Train, StationType, Map, Player, etc.) in UML.
- Synchronize conceptual model with database structure.

#### **USLP02 — Cargo Handling Interface**
- Access ESINF functionalities (USEI01–USEI05) via a text-based interface.
- Display warehouse state and inventory lists after each operation.

#### **USLP03 — Travel Time Estimation**
- Calculate estimated travel time between directly connected stations.
- Consider line type (single/double track) and locomotive speed.

---

## 5. Operating Instructions

### **5.1 Database Initialization**
1. Open Visual Paradigm → Import relational model.
2. Generate DDL script → Run on Oracle LiveSQL.
3. Import CSV data → Execute PL/SQL scripts for data population.

### **5.2 Warehouse Module Setup**
1. Place CSV files in `/data` folder.
2. Run Java main class `WarehouseMain.java`.
3. Validate data imports; invalid lines produce descriptive error logs.

### **5.3 Executing Operations**
- **Unloading:** Calls `unloadWagons()` to populate bays.
- **Order Allocation:** Runs `allocateOrders()` and produces eligibility reports.
- **Picking:** Executes chosen heuristic and displays trolley utilization.
- **Returns:** Runs `processReturns()`, producing `audit_log.txt`.

### **5.4 Simulation Interface**
1. Launch `Simulator.java`.
2. Build or load map and scenario.
3. Create and connect stations.
4. Add locomotives and assign routes.
5. Run simulation and observe results through **GraphStream visualization**.

---

## 6. Troubleshooting

| Issue | Possible Cause | Solution |
|-------|----------------|-----------|
| CSV Import fails | Wrong format or missing field | Check header names and date formats |
| Order not allocated | No available boxes for SKU | Check stock or expiry constraints |
| Trains not moving | Non-electrified line used with electric train | Switch to diesel or electrify line |
| Simulation crash | Missing map data or invalid link | Verify station coordinates |

---

## 7. FAQs

**Q1:** Can the warehouse handle both perishable and non-perishable goods?  
**A1:** Yes. FEFO applies to perishable goods (with expiry date), FIFO to non-perishable goods.

**Q2:** Does the system support multiple warehouses?  
**A2:** Yes. Each warehouse has multiple aisles and bays managed independently.

**Q3:** Can trains operate without defined scenarios?  
**A3:** No. Every simulation must belong to a scenario that defines constraints and time period.

**Q4:** What happens if two returns have the same timestamp?  
**A4:** The system sorts by timestamp descending and then by returnId ascending.

---

## 8. Glossary

| Term | Definition |
|------|-------------|
| **FEFO** | First-Expired-First-Out: earliest expiry dispatched first |
| **FIFO** | First-In-First-Out: oldest received dispatched first |
| **SKU** | Stock Keeping Unit — unique product identifier |
| **Bay** | Small storage area within an aisle |
| **Aisle** | Vertical corridor of bays in a warehouse |
| **Wagon** | Freight car transporting goods to terminal |
| **Allocation** | Process of assigning boxes to order lines |
| **Quarantine** | Temporary storage for returned items pending inspection |
| **Scenario** | Context with rules, constraints, and time limits for simulation |
| **GraphStream** | Library used for visualizing railway network graphs |
| **Audit Log** | Text file recording all return inspections and outcomes |

---

## 9. Contact & Support

For technical assistance or feedback:
- **Email:**  
  1240654@isep.ipp.pt  
  1240657@isep.ipp.pt  
  1240676@isep.ipp.pt  
  1240628@isep.ipp.pt

- **Repository:**  
  [Departamento-de-Engenharia-Informatica/sem3-pi-25-26-gXXX-repo](https://github.com/Departamento-de-Engenharia-Informatica/)

---

**End of Manual**  
*This document adheres to ISEP’s technical writing and integration standards (2025/26).*  

