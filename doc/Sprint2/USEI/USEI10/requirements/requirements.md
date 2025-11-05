# USEI10 — Multi-Modal Nearest Facility Queries

## 1. Requirements Engineering

### 1.1. User Story Description

To support route planning and intermodal logistics optimization, the system must provide efficient nearest-facility search capabilities over the indexed dataset of railway stations, airports, and seaports.  
This functionality allows planners to quickly identify the closest facility of a given transport mode to a specific location or to another facility, enabling cost-efficient route combinations between rail, air, and sea transportation.

**User Story:**
> As a planner, I want to query the nearest facility (station, airport, or seaport) to a given location or reference node, so that I can efficiently determine intermodal transfer points and optimize transportation routes.
---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> The query must allow searching for the nearest facility of a specific type, such as:
    - The closest airport to a railway station
    - The closest seaport to a given latitude/longitude point
    - The closest railway terminal to an airport
  
> The search must rely on spatial indexes (KD-Trees or equivalent) built in previous user stories (USEI07–USEI09).  
A sequential scan through all facilities is not acceptable.

> Distance must be computed using the Haversine formula or a comparable geographic distance function.  
Results must include:
    - Facility ID
    - Name
    - Type (station, airport, seaport)
    - Country
    - Coordinates
    - Calculated distance (in km)

> If no facility of the requested type exists, the system must return an empty result set gracefully.

**From client clarifications:**

> **Q:**
> **A:**

---

### 1.3. Acceptance Criteria

| ID      | Requirement                                                                                                                      |
|---------|----------------------------------------------------------------------------------------------------------------------------------|
| **AC0** | The dataset of facilities (stations, airports, seaports) must be pre-loaded and indexed using KD-Trees or equivalent structures. |
| **AC1** | The query must support filtering by facility type (e.g., `STATION`, `AIRPORT`, `SEAPORT`).                                       |
| **AC2** | The nearest facility must be determined using a valid geographic distance function (Haversine).                                  |
| **AC3** | The function must return the nearest facility’s details and the computed distance in kilometers.                                 |
| **AC4** | When no facility of the requested type exists, the result must be empty, not NULL.                                               |
| **AC5** | Queries must execute in logarithmic or near-logarithmic time relative to dataset size.                                           |
| **AC6** | The system must support both point-to-point (lat/lon) and facility-to-facility nearest searches.                                 |
| **AC7** | Unit tests must validate correctness of results, ordering, and computational accuracy of distance values.                        |

---

### 1.4. Found Out Dependencies

- Depends on the KD-Tree and index structures built in:
    - USEI07 — KD-Tree Construction for spatial search
    - USEI08 — Range and radius queries
    - USEI09 — Facility classification and indexing by type

- Uses validated datasets for:
    - Stations (`stations.csv`)
    - Airports (`airports.csv`)
    - Seaports (`seaports.csv`)
    
Interacts with:
- The intermodal route planner subsystem
- Geographic analytics modules for distance computation and clustering

---

### 1.5. Input and Output Data

#### Input Data:
| Parameter      | Description                                                         |
|----------------|---------------------------------------------------------------------|
| `latitude`     | Latitude of the reference point or facility                         |
| `longitude`    | Longitude of the reference point or facility                        |
| `facilityType` | Type of facility to search for (`STATION`, `AIRPORT`, or `SEAPORT`) |


#### Output Data:
| Field        | Description                                                      |
|--------------|------------------------------------------------------------------|
| `facilityId` | Unique identifier of the found facility                      |
| `name`       | Facility name                       |
| `type`       | Facility type ( (`STATION`, `AIRPORT`, `SEAPORT`) |
| `country`    | Country code or name                      |
| `latitude`   | Latitude of the facility                       |
| `longitude`       | Longitude of the facility |
| `distanceKm`       | Calculated distance from the reference point (km) |

---

### 1.6. System Sequence Diagram (SSD)

![USEI10-SSD](svg/SSD.svg)

---

### 1.7. Other Relevant Remarks

- The KD-Tree structure must store references to facility objects for spatial efficiency and fast traversal.

- Distance computations should rely on double-precision arithmetic to avoid rounding errors on large distances.

- The algorithm must handle:
    - Facilities with identical coordinates (tie-breaking by name ASC)
    - Missing or invalid coordinates (discarded during data validation)
    - Empty datasets gracefully (return empty result)

- Performance goals:
    - O(log n) average query time for nearest search
    - Scalable to datasets containing tens of thousands of facilities

- Implementation must follow deterministic traversal rules to ensure reproducible query results.

- Must support unit and integration tests validating:
    - Spatial correctness (distance ordering)
    - Filtering by type
    - Empty or invalid inputs
    - Cross-modal nearest searches (station→airport, port→station, etc.)