# 🚂 Integrative Project - Sprint 1

## 📌 Overview
This project is part of the **Integrative Project (PI)** for the 3rd semester and aims to develop a computer-based solution to support **Logistics Operators in Railway Infrastructure**.  

The solution involves **data modeling, information structures, domain modeling**, and implementation across multiple languages (**Java, PL/SQL, C/Assembly**).  
The development follows an **agile (SCRUM)** and **Test-Driven Development (TDD)** approach.

---

## 👥 Team Operation Framework
- Each team must appoint a **Scrum Master** for each Sprint (rotating role).  
- The **Project Board** (GitHub Projects) must include the following columns:  
  - `Backlog`, `To Do`, `In Process`, `Testing`, `Done`, `Logbook`.  
- The GitHub repository must follow these rules:  
  - Name: `sem3-pi-25-26-gNNN-repo` (replace `NNN` with group number).  
  - Repository must be **private**.  
  - Teachers are added with **Reader** role only.  

---

## 🎯 Problem Description
The project focuses on supporting **rail freight logistics**, including:
- Management of cargo at railway terminals.  
- Planning and execution of freight transport operations.  
- Modeling of **railway infrastructure, rolling stock (locomotives and wagons), routes, and operations**.  
- Warehouse management, loading/unloading, and multimodal integration (train, truck, ship).  

---

## 👤 System Users
Main user profiles:  
- 🚆 **Train Drivers** – operate trains safely and on schedule.  
- 🏫 **Station Masters** – coordinate arrivals and departures.  
- 📦 **Warehouse Managers** – manage storage and dispatch.  
- 📊 **Schedulers/Planners** – optimize itineraries and timetables.  

---

## 🛠️ Minimum Viable Product (MVP)

### 📂 Databases (BDDAD) – Sprint 1
- USBD01 – Data dictionary / Glossary.  
- USBD02 – Relational model (logical level).  
- USBD03 – Model instantiation in SQL (Oracle LiveSQL).  
- USBD04 – Insert provided data into the new database.  
- +3 additional User Stories assigned by the teacher (USBD05–USBD20).  

---

### 📊 Information Structures (ESINF) – Sprint 1
**Theme: Cargo Handling at a Railway Terminal**  
Input datasets: `bays.csv`, `wagons.csv`, `items.csv`, `orders.csv`, `order_lines.csv`, `returns.csv`.  

User Stories:  
- USEI01 – Wagon unloading (FIFO/FEFO).  
- USEI02 – Order eligibility & allocation.  
- USEI03 – Picking plans (FF, FFD, BFD heuristics).  
- USEI04 – Pick path sequencing (Strategy A & B).  
- USEI05 – Returns & quarantine management.  

---

### 🏗️ Laboratory Project 3 (LAPR3) – Sprint 1
- USLP01 – Domain model creation (PlantUML / Visual Paradigm).  
- USLP02 – Cargo Handling functionality via a **text-based interface**.  

---

## ⚙️ Non-functional Requirements
- Business rules validation during data registration and updates.  
- Database must be centralized in **Oracle DBMS**.  
- Main application in **Java**, with integration in **PL/SQL** and **C/Assembly**.  
- Object-oriented design for maintainability and extensibility.  
- Use of **CamelCase** and **Javadoc**.  
- All diagrams stored in **SVG** format.  

---

## 📅 Timeline
- **Sprint 1** → September 29 – October 26  
- **Sprint 2** → October 27 – November 23  
- **Sprint 3** → November 24 – January 4  

---

## 📖 References
- [Medway – Railway Network Maps](https://www.medway-iberia.com/pt/pages/mapas-de-rede)  
- Official Document: *Project Assignment - Sprint 1 (v1.0, September 2025)*  

---
🚀 *Integrative Project – Semester 3 (2025/26)*
