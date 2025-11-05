# OO Analysis — Railway Logistics Management System

## Rationale to Identify Domain Conceptual Classes

### Conceptual Class Category List

---

#### Business Transactions

**Freight Transport**  
Description: Represents the movement of cargo from an origin station to a destination station.

**Loading/Unloading Operation**  
Description: The process of transferring cargo between wagons and warehouses at terminals.

**Scheduling and Dispatching**  
Description: Planning train departures, routes, and resource allocation.

**Order Handling**  
Description: Creation and management of transport or warehouse orders.

**Return Processing**  
Description: Management of returned goods placed in quarantine for inspection.

---

#### Transaction Line Items

**FreightWagon**  
Description: Association between a freight operation and the wagons that compose it.

**OrderLine**  
Description: A single line of a warehouse order specifying a SKU and quantity.

**ReturnItem**  
Description: A specific product or SKU being returned.

---

#### Products/Services related to a Transaction

**Cargo**  
Description: Represents the goods transported by the railway system.

**Item**  
Description: Individual SKU (Stock Keeping Unit) loaded in wagons or stored in warehouses.

**Order**  
Description: Request for products to be transported or dispatched.

---

#### Transaction Records

**Freight Record**  
Description: Records details of a freight journey (train composition, cargo list, departure, arrival).

**Warehouse Log**  
Description: Record of cargo movements (load, unload, dispatch, relocation).

**Return Log**  
Description: Records of items processed during quarantine and inspection.

---

#### Roles of People or Organizations

**Operator**  
Description: The company that owns and operates locomotives and wagons.

**Freight Manager**  
Description: Responsible for coordinating freight operations and scheduling.

**Station Master**  
Description: Oversees station operations (arrivals, departures, loading).

**Terminal Operator**  
Description: Manages warehouse and cargo handling at terminals.

**Planner/Scheduler**  
Description: Plans routes, allocations, and dispatch operations.

**Train Driver**  
Description: Operates the locomotive during freight transport.

---

#### Places

**Station**  
Description: Location for train arrivals, departures, and cargo operations.

**Terminal**  
Description: A large facility for cargo handling and intermodal transfers.

**Warehouse**  
Description: Storage area divided into aisles and bays.

**RailwayLine**  
Description: Physical line connecting two or more stations.

**LineSegment**  
Description: A portion of a railway line, possibly single or double track.

**Route**  
Description: The path taken by a train through one or more stations.

---

#### Noteworthy Events

**ArrivalEvent**  
Description: Occurs when a freight arrives at a station or terminal.

**DepartureEvent**  
Description: Occurs when a freight leaves a station or terminal.

**InspectionEvent**  
Description: Logged when returned cargo is inspected and processed.

---

#### Physical Objects

**Train**  
Description: The complete set of locomotives and wagons operating together.

**Locomotive**  
Description: The powered engine pulling freight wagons.

**Wagon**  
Description: Freight car used to carry goods (boxcar, tank car, hopper, etc.).

**Container**  
Description: Represents a load unit placed inside wagons or warehouses.

**Bay**  
Description: Storage position in a warehouse aisle.

**Trolley**  
Description: Used for picking items during dispatch operations.

---

#### Descriptions of Things

**Gauge**  
Description: Distance between rails; determines train compatibility.

**FuelType**  
Description: Type of fuel used by locomotives (diesel, electric).

**WagonType**  
Description: Defines the type of wagon (boxcar, tank, flat, refrigerated, hopper).

**TrackType**  
Description: Single or double track configuration.

**FacilityType**  
Description: Describes whether a facility is a station, terminal, or yard.

---

#### Catalogs

**CargoType**  
Description: Classifies goods transported (liquid, chemical, grain, refrigerated, general).

**IndustryType**  
Description: Defines categories of industries linked to stations.

**LineOwner**  
Description: Entity that owns and maintains the railway line.

---

#### Containers

**RailwayNetwork**  
Description: The complete system of lines, stations, terminals, and routes.

**Warehouse**  
Description: Contains aisles and bays for storing products.

**Freight**  
Description: Contains wagons and cargo for a transport operation.

---

#### Elements of Containers

**Aisle**  
Description: Row in a warehouse where bays are located.

**Bay**  
Description: A specific storage location within an aisle.

**RoutePoint**  
Description: A point along a route connecting two stations.

---

#### Other (External/Collaborating) Systems

**Truck Interface**  
Description: For intermodal transport; transfer between rail and road.

**Ship Interface**  
Description: For intermodal transport; transfer between rail and sea.

---

#### Records of Finance, Work, Contracts, Legal Matters

**Budget**  
Description: Tracks operational costs, fuel expenses, maintenance, and revenue.

**MaintenanceRecord**  
Description: Tracks maintenance operations performed on locomotives or wagons.

---

#### Financial Instruments

*(none identified for this phase)*

---

#### Documents Mentioned/Used

**Waybill**  
Description: Legal transport document containing freight details (cargo, origin, destination).

**InspectionLog**  
Description: Document summarizing return/quarantine inspection outcomes.
