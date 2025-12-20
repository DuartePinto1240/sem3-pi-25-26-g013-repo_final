# User Manual and Technical Documentation for *Logistics on Rails*
**Version 1.2**  
**December 2025**

---

## **Cover**

**Title:** Logistics on Rails — Railway Terminal Management & Simulation System  
**Institution:** Instituto Superior de Engenharia do Porto (ISEP)

**Date:** December 2025

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
This manual provides complete guidance for installing, configuring, and operating the **Logistics on Rails** system — an integrated logistics, railway network management, and simulation solution developed within the **Integrative Project (sem3pi 2025/26)**.

### **Scope**
- Railway station control and monitoring.
- Database-backed railway infrastructure management.
- Warehouse cargo handling and optimization.
- Spatial indexing and railway network graph optimization.
- Simulation of routes, freights, trains, and schedules.

### **Target Audience**
- **System Administrators (ARQCP)**
- **Database Designers & Planners (BDDAD)**
- **Warehouse Managers & Analysts (ESINF)**
- **Simulation Operators / Players (LAPR3)**
- **Product Owners**

---

## 2. Overview of the System

The **Logistics on Rails** platform integrates four core modules:

| Module | Description | Technologies |
|--------|--------------|--------------|
| **ARQCP – Station Management** | Track control, sensors, and signaling | C, RISC-V Assembly, Arduino |
| **BDDAD – Database Layer** | Railway data model and analytics | Oracle SQL / PL-SQL |
| **ESINF – Information Structures** | Warehouses, spatial indexes, graph algorithms | Java |
| **LAPR3 – Simulation Interface** | Domain model, routing, scheduling | Java, GraphStream |

---

## 3. System Requirements

### **Software**
- Operating System: Windows, macOS, or Linux
- Java Runtime: JDK 17+
- Database: Oracle LiveSQL or OracleDB
- IDE: IntelliJ / Eclipse / VS Code

### **Hardware**
- CPU: Intel i5 or higher
- RAM: 8 GB minimum
- Disk: 1 GB free

---

## 4. Features & Functionalities

### **4.1 Railway Station Management (ARQCP)**
- Track assignment and signaling.
- Temperature and humidity monitoring.
- Secure logging and user authentication.

### **4.2 Database Design (BDDAD)**
- Logical and physical models.
- PL/SQL analytical queries.
- Freight, route, and train constraints.

### **4.3 Warehouse Management (ESINF)**
- FEFO/FIFO unloading.
- Order allocation and picking plans.
- Spatial and proximity queries.
- Railway network optimization with graphs.

### **4.4 Simulation (LAPR3)**
- Domain modeling.
- Route planning (manual and automatic).
- Train scheduling and visualization.

---

## 5. Operating Instructions

### **5.1 Database Initialization**
1. Generate schema via Visual Paradigm.
2. Execute DDL in Oracle.
3. Load CSV data.

### **5.2 Warehouse Operations**
- Import CSV files.
- Execute USEI01–USEI15 features.

### **5.3 Simulation**
1. Launch simulator.
2. Load scenario.
3. Assign trains and routes.
4. Run simulation.

---

## 6. Troubleshooting

| Issue | Cause | Solution |
|------|-------|----------|
| Import errors | Invalid CSV | Validate formats |
| No allocations | Insufficient stock | Check inventory |
| Train blocked | Track constraints | Review line type |

---

## 7. FAQs

**Q:** Are perishable goods supported?  
**A:** Yes, via FEFO logic.

**Q:** Is route planning automatic?  
**A:** Yes, in Sprint 3 features.

---

## 8. Glossary

| Term | Definition |
|------|-------------|
| FEFO | First-Expired-First-Out |
| FIFO | First-In-First-Out |
| SKU | Stock Keeping Unit |
| Route | Sequence of shipments |
| Path | Ordered list of stations |

---

## 9. Contact & Support

- 1240654@isep.ipp.pt
- 1240657@isep.ipp.pt
- 1240676@isep.ipp.pt
- 1240628@isep.ipp.pt

---

**End of Manual**
