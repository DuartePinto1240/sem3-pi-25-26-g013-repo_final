# OO Analysis — Railway Logistics Management System (Updated v2.21)

## Rationale to Identify Domain Conceptual Classes
### Conceptual Class Category List

---

## Business Transactions
- **Freight Transport**  
  Represents the movement of cargo from an origin station to a destination station. A freight consists of a set of wagons transported from endpoint A to endpoint B.
- **Loading/Unloading Operation**  
  The process of transferring cargo between wagons and warehouses at terminals.
- **Scheduling and Dispatching**  
  Planning train departures, routes, and resource allocation. Each train has an associated route (simple or complex) and a planned departure date/time.
- **Order Handling**  
  Creation and management of transport or warehouse orders with priority and due dates.
- **Return Processing**  
  Management of returned goods placed in quarantine for inspection.
- **Train Crossing Operation**  
  Coordination of trains passing each other on single-track lines at stations or sidings.

---

## Transaction Line Items
- **FreightWagon** — Association between a freight operation and the wagons that compose it.
- **OrderLine** — A single line of a warehouse order specifying a SKU, quantity, and line number.
- **ReturnItem** — A specific product or SKU being returned with reason and timestamp.
- **RouteSegment** — A portion of a train's path between two consecutive stations.

---

## Products/Services related to a Transaction
- **Cargo** — Represents the goods transported by the railway system.
- **Item (SKU)** — Individual Stock Keeping Unit with name, category, unit, volume, and weight. May have expiry dates for perishable goods.
- **Order** — Request for products to be dispatched, with order ID, due date, and priority.
- **Box** — Container unit for storing SKU items with unique boxId, quantity, expiry date, and receivedAt timestamp.

---

## Transaction Records
- **Freight Record** — Records details of a freight journey (train composition, cargo list, departure, arrival).
- **Warehouse Log** — Record of cargo movements (load, unload, dispatch, relocation) with timestamps and locations.
- **Return Log** — Audit records of items processed during quarantine inspection (restocked or discarded).
- **Train Passage Record** — Records the instant a train crosses or arrives at an endpoint, including departure time if stopped.
- **Station Operation Log** — Timestamped log of all station operator instructions to ensure traceability.

---

## Roles of People or Organizations
- **Operator (Train Operating Company)** — The company that owns and operates locomotives and wagons.
- **Freight Manager** — Coordinates freight operations and manually defines train routes during dispatch.
- **Station Master** — Oversees station operations, coordinates arrivals and departures.
- **Terminal Operator** — Manages warehouse and cargo handling at terminals.
- **Storage Manager** — Manages cargo handling functionality at railway terminals.
- **Traffic Dispatcher** — Calculates travel times and manages train scheduling.
- **Planner/Scheduler** — Plans routes, allocations, dispatch operations, and analyzes timetables.
- **Train Driver (Locomotive Pilot/Engineer)** — Operates locomotives safely according to schedule.
- **Quality Operator** — Inspects returned goods and decides on restock/discard.
- **Picker** — Collects items from warehouse locations.
- **Station Operator** — Assigns tracks, issues departure orders and emergency stops.
- **System Administrator** — Manages station configuration and operational data.
- **Line Owner** — Owns and maintains railway lines.

---

## Places
- **Station (Endpoint)** — Train arrivals/departures; metadata like geo-coordinates, timezone, isCity etc.
- **Terminal** — Large cargo handling facility with circulation and parking lines.
- **Warehouse** — Storage area divided into aisles and bays.
- **RailwayLine** — Connection between endpoints; made of line segments with properties.
- **LineSegment** — Track fragment with electrification, weight, speed limit, gauge type.
- **Route** — Ordered list of stations a train passes through.
- **Path** — Station sequence for freight origin→destination.
- **Freight Yard** — Allows assembling/sorting freight trains.
- **Siding** — Crossing space on single-track lines.
- **Circulation Line** — Track for wagon movement internal to terminals.
- **Parking Line** — Temporary wagon storage.
- **Unloading Dock** — Access to warehouses for unloading.
- **Track** — Individual rail line in station (max 99).

---

## Noteworthy Events
- **ArrivalEvent**
- **DepartureEvent**
- **InspectionEvent**
- **StopEvent**
- **EmergencyStopOrder**
- **TrackAssignmentEvent**
- **CrossingEvent**

All describe time-stamped operational occurrences impacting train traffic and handling.

---

## Physical Objects
- **Train** — Composition of locomotives + wagons with schedule.
- **Locomotive** — Engine, with attributes like power, gauge support, dimensions.
- **Wagon (Freight Car)** — Types: Boxcar, Flatcar, Tank, Hopper, Refrigerated.
- **Container** — 20/40/45ft regular or HC units.
- **Bay** — Warehouse location with capacity measured in boxes.
- **Trolley (Cart)** — Used in picking, weight limitation.
- **Light Sign** — Track state signal (Green, Red, Yellow, Blinking).
- **Sensor** — Environmental monitoring devices.
- **Synoptic Board** — Visual UI for operational parameters.

---

## Descriptions of Things (Enumerations & Domain Policies)
- **Gauge**
- **FuelType**
- **WagonType**
- **TrackType**
- **TrackState**
- **FacilityType**
- **ElectrificationStatus**
- **DispatchStrategy**
- **SequencingStrategy**
- **StockManagementPolicy**
- **OrderEligibilityMode**
- **LineStatus**
- **ReturnReason**
- **InspectionAction**
- **TimeZone**

_All defining constraints or classifications._

---

## Catalogs
- **CargoType**
- **LineOwnerCatalog**
- **StationCatalog**

---

## Containers
- **RailwayNetwork**
- **Warehouse**
- **Freight**
- **Aisle**
- **Quarantine**
- **PickingPlan**

---

## Elements of Containers
- **Bay**
- **RoutePoint**
- **AllocationFragment**
- **PickingStop**

---

## Spatial/Indexed Structures
- **2D-Tree (KD-Tree)** — Geo-searching stations.
- **BST/AVL Tree** — Indexing stations by several attributes.
- **CircularBuffer (RingBuffer)** — Station operation data buffer.

---

## Other Systems (External Interfaces)
- **Truck Interface**
- **Ship Interface**
- **Raspberry PI Pico**

---

## Finance, Work, Contracts, Legal
- **Budget**
- **MaintenanceRecord**

---

## Documents Mentioned/Used
- **Waybill**
- **InspectionLog**
- **PickingPlan Document**
- **AuditLog**
- **StationOperationLog**
