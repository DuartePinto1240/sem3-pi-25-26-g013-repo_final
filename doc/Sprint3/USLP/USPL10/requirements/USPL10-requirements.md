# USLP10 — Advanced Train Scheduler with Manual and Automatic Path Definition

## 1. Requirements Engineering

### 1.1. User Story Description
As a **Traffic Manager**, I want to use a scheduler that allows me to dispatch a list of trains. Each train has an associated route that can be simple or complex, so I can efficiently plan and coordinate multiple freight operations with enhanced path planning capabilities.

The scheduler must support:

- Creating and managing a list of trains to be dispatched
- Associating each train with a route (simple or complex)
- Defining paths manually or automatically
- Computing effective travel speeds based on track limitations, locomotive power, and train weight
- Managing train crossings on single-track segments
- Accessing database through PL/SQL functions
- Displaying comprehensive schedules with passage times and crossing operations

---

### 1.2. Customer Specifications and Clarifications

- A **route** defines the freight operations (what cargo to transport from where to where).
- A **path** is the ordered list of stations through which the train must pass (not necessarily stopping) to accomplish a route.

**Routes can be:**

- **Simple route**: A single freight/shipment (e.g., 300 cars from Mangualde to Sines)
- **Complex route**: Multiple shipments (e.g., freight A from Mangualde to Sines, then freight B from Sines to Vilar Formoso)

**Each train must have:**

- A unique identifier
- An assigned locomotive (or set of locomotives)
- An assigned route (simple or complex)
- A scheduled departure date and time
- A path (manually defined or automatically calculated)

**Speed Calculation per Line Segment:**

- Segment speed limit
- Combined power of locomotives
- Total train weight (locomotives + wagons, loaded or empty)

**Single-track behavior:**

- A single-track segment cannot be occupied simultaneously by two trains in opposite directions
- Crossing may occur at:
    - a siding, or
    - the station immediately before the conflicting segment

**Crossing Resolution:**

- Determine correct crossing point
- One train may wait
- Propagate waiting time forward, adjusting future passage times
- Record crossing as a **Crossing Operation**

**Passage Times:**

- Time of passage at each station in the path
- Time of passage at sidings (if used for crossing)
- Estimated arrival at final destination
- Departure time from stations where loading/unloading occurs

**Database Integration:**

- All data retrieval and updates must be performed via **PL/SQL functions**
- Includes station data, line segments, locomotives, wagons, train schedules, crossing operations
- Application layer communicates exclusively via function calls

**Path Definition Modes:**

- **Manual Path Definition**: Traffic Manager specifies station sequence, validated for connectivity and feasibility
- **Automatic Path Calculation**: System calculates optimal/feasible path using graph algorithms, respecting constraints like distance, track capacity, speed limits, gauge, electrification

**Effective Speed Calculation:**

- Minimum of:
    - Segment speed limit
    - Speed supported by combined locomotive power relative to total train weight
    - Temporary operational restrictions
- Weight includes locomotive tare, wagon tare, cargo payload

---

### 1.3. Acceptance Criteria

| ID  | Requirement |
|-----|-------------|
| AC1  | Allow Traffic Manager to create a list of trains to be dispatched |
| AC2  | Each train must be associated with a route (simple or complex) |
| AC3  | For simple routes, identify origin and destination stations |
| AC4  | For complex routes, identify all intermediate loading/unloading points |
| AC5  | Assign at least one locomotive compatible with gauge/electrification |
| AC6  | Support manual path definition |
| AC7  | Support automatic path calculation |
| AC8  | Validate path connectivity and station existence |
| AC9  | Calculate effective travel speed per segment |
| AC10 | Compute and display passage times at all stations along the path |
| AC11 | Detect conflicts on single-track segments |
| AC12 | Compute waiting times and propagate delays for crossings |
| AC13 | Output Crossing Operations (location, trains involved, waiting train, duration) |
| AC14 | All database access via PL/SQL functions |
| AC15 | Display complete dispatch schedule (train ID, route, path, locomotives, wagons, departure, estimated arrival, passage times, crossings) |
| AC16 | Invalid trains not added to dispatch list; show appropriate error messages |

---

### 1.4. Found Out Dependencies

**Prerequisites:**

- USLP03 — Calculate estimated travel time between two stations
- USLP07 — Basic scheduler functionality
- USLP08 — Route planner for freight assignment
- USLP09 — Train assembly and assignment

**Uses:**

- Station data (via PL/SQL)
- Locomotive data (via PL/SQL)
- Wagon data (via PL/SQL)
- Line segment data: connectivity, speed limits, track type, electrification (via PL/SQL)
- Siding information (via PL/SQL)
- Route definitions (simple/complex freights)
- Graph algorithms for automatic path calculation

---

### 1.5. Input and Output Data

**Input Data (per train):**

- `trainId` (String)
- `routeId` (String/Integer)
- `pathDefinitionMode` ("MANUAL" or "AUTOMATIC")
- `manualPath` (List<String>) if mode is MANUAL
- `locomotiveIds` (List<String>)
- `wagonIds` (List<String>)
- `departureDateTime` (DateTime)

**System Configuration:**

- Database connection parameters
- PL/SQL function names for data retrieval

**Output Data:**

**Dispatch Schedule (per train) Example:**

```json
{
  "trainId": "T001",
  "routeId": "R045",
  "routeType": "COMPLEX",
  "routeDescription": "Mangualde→Sines (300 cars), Sines→Vilar Formoso (100 containers)",
  "pathDefinitionMode": "AUTOMATIC",
  "path": ["Mangualde","Nelas","Coimbra-B","Entroncamento","Setúbal","Sines","Setúbal","Lisboa-Sta.Apolónia","Entroncamento","Guarda","Vilar Formoso"],
  "locomotives": [{"id":"L123","type":"Electric","power":5000}],
  "wagons": [{"id":"W501","type":"Boxcar","tare":20000,"payload":60000}],
  "totalTrainWeight": 2450000,
  "departureDateTime": "2025-12-10T08:00:00",
  "estimatedArrivalDateTime": "2025-12-10T18:35:00",
  "totalTravelTime": "10h 35min",
  "status": "VALIDATED"
}

"passageTimes": [
  { "station": "Mangualde", "arrivalTime": "08:00:00", "departureTime": "08:00:00", "operation": "START" },
  { "station": "Nelas", "arrivalTime": "08:12:00", "departureTime": "08:12:00", "operation": "PASS" },
  { "station": "Coimbra-B", "arrivalTime": "08:45:00", "departureTime": "08:45:00", "operation": "PASS" },
  { "station": "Sines", "arrivalTime": "12:30:00", "departureTime": "13:15:00", "operation": "UNLOAD_LOAD" },
  { "station": "Vilar Formoso", "arrivalTime": "18:35:00", "departureTime": "18:35:00", "operation": "END" }
]

"segmentDetails": [
  { "from": "Mangualde", "to": "Nelas", "distance": 15.2, "speedLimit": 100, "effectiveSpeed": 85, "limitingFactor": "LOCOMOTIVE_POWER", "travelTime": "00:10:43" }
]

"crossingOperations": [
  {
    "location": "Siding between Nelas and Coimbra-B",
    "segmentId": "SEG-045",
    "involvedTrains": ["T001","T007"],
    "conflictType": "SINGLE_TRACK_OPPOSITE_DIRECTION",
    "waitingTrainId": "T007",
    "proceedingTrainId": "T001",
    "waitingDuration": "00:08:15",
    "crossingTime": "08:30:00"
  }
]

"validationMessages": [
  { "type": "WARNING", "message": "Train T001 shares segment SEG-023 with Train T004 between 09:15 and 09:45" },
  { "type": "ERROR", "message": "No valid path found for Train T005 - gauge incompatibility at segment SEG-078" }
]
```

### 1.7. Other Relevant Remarks

**Route vs. Path Distinction:**

*   A **route** describes _what_ must be transported (freights, cargo, origin/destination)
    
*   A **path** describes _how_ the train will physically travel (ordered list of stations traversed)
    
*   One route may have multiple valid paths
    
*   The path must enable all loading/unloading operations required by the route


**Path Validation:**

*   Manual paths: verify that every consecutive station pair is connected and compatible with train characteristics
    
*   Automatic paths: must satisfy route requirements (visit all necessary loading/unloading points) and respect gauge/electrification constraints
    
*   All paths must be physically feasible (no missing segments, no gauge mismatches)
    

**Speed Calculation Model:**The effective speed for each segment is determined by:

1.  **Track speed limit** (from segment data)
    
2.  **Locomotive power limit**:
    
    *   Combined power of all locomotives in the train
        
    *   Total train weight (locomotives + wagons with cargo)
        
    *   Formula: powerLimitedSpeed = f(totalPower, totalWeight)
        
3.  **Operational constraints** (temporary restrictions, weather, etc.)
    

The **effective speed** = MIN(trackLimit, powerLimit, operationalLimit)

**Crossing Logic:**

*   Crossings are only necessary on **single-track segments**
    
*   Double-track segments allow simultaneous opposite-direction traffic
    
*   When a conflict is detected:
    
    1.  Identify the conflict point (segment + time overlap)
        
    2.  Determine valid crossing locations (sidings or previous station)
        
    3.  Assign priority (based on schedule, train importance, or other rules)
        
    4.  Lower-priority train waits at the crossing point
        
    5.  Propagate delay to all subsequent passage times for the waiting train
        

**Passage Time Computation:**

*   Start with departure time at origin
    
*   For each segment in the path:
    
    *   Calculate travel time using effective speed and segment distance
        
    *   Add time to previous station's departure time → arrival at next station
        
    *   If the station is a loading/unloading point, add handling time → departure time
        
    *   Continue to next segment
        
*   Handle waiting times from crossing operations
    

**Database Integration Requirements:**All interactions with the database must use **PL/SQL functions**, including:

*   fn\_get\_route\_details(route\_id) → returns route type, freights, endpoints
    
*   fn\_get\_stations() → returns all stations with coordinates, facilities
    
*   fn\_get\_line\_segments() → returns segments with speed limits, track type, electrification, gauge
    
*   fn\_get\_sidings() → returns siding locations and capacities
    
*   fn\_get\_locomotive\_details(loco\_id) → returns power, weight, gauge, type
    
*   fn\_get\_wagon\_details(wagon\_id) → returns type, tare, payload, gauge
    
*   fn\_save\_train\_schedule(train\_data) → persists the schedule
    
*   fn\_save\_crossing\_operation(crossing\_data) → records crossing details
    

**Automatic Path Calculation:**The system should implement graph-based algorithms such as:

*   **Dijkstra's algorithm** (for shortest distance path)
    
*   _A algorithm_\* (for heuristic-guided optimal path)
    
*   **Bellman-Ford** (if negative costs exist — per USEI15)
    
*   **Constraint satisfaction** (respecting gauge, electrification, capacity)
    

The automatic path must:

*   Start at route origin
    
*   Visit all intermediate loading/unloading points (for complex routes)
    
*   End at route destination
    
*   Minimize travel time or distance (configurable objective)
    
*   Respect all operational constraints
    

**Implementation Notes:**

*   Reuse USLP03 for segment travel time calculations
    
*   Reuse USLP07 crossing detection and resolution logic
    
*   Extend with database integration layer (PL/SQL function calls)
    
*   Implement path calculation algorithms using graph data structures
    
*   Ensure clear separation between route definition (freight operations) and path execution (physical traversal)
    
*   Output must clearly present: route, path, speeds per segment, locomotives, wagons, passage times, and crossing operations



