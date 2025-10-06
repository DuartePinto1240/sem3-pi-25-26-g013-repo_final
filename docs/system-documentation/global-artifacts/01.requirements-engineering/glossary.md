# Glossary


| **Term or Expression** (EN) | **Termo ou Expressão** (PT) | **Description (Definition and Information)** (EN) |
|:----------------------------|:----------------------------|:--------------------------------------------------|
| **Aisle** | **Corredor** | Path separating rows of [WarehouseBay](#warehousebay) in a warehouse, also used in distance calculations for picking path sequencing ([DeterministicSweep](#deterministicsweep)). |
| **AnnualProfit** | **LucroAnual** | Represents the profit of a given station for a specific year, used for statistical analysis in [BestFitDecreasing](#bestfitdecreasing). |
| **AuditLog** | **RegistoDeAuditoria** | File that records all operations on returned products, specifying timestamps, returnId, SKU, action taken, and quantities. |
| **Bay** | **Baia** | Storage location inside a [Aisle](#aisle). Each bay can hold multiple [Box](#box) and is managed under [FEFO/FIFO](#fefofifo) rules. |
| **bays.csv** | **bays.csv** | Input dataset defining warehouse structure: warehouseId, aisle, bay, and capacity of boxes, used to initialize the storage model in memory. |
| **BestFitDecreasing (BFD)** | **MelhorAjusteDecrescente (BFD)** | Packing heuristic used in [Trolley](#trolley) allocation in USEI03. Items are sorted by weight (descending) and placed in the trolley that will leave the least remaining capacity unused. |
| **Bridges** | **Pontes** | Engineering structures in [RailwayInfrastructure](#railwayinfrastructure) that allow trains to cross obstacles like rivers or valleys. |
| **Box** | **Caixa** | Smallest inventory unit transported in [Wagon](#wagon) and stored in [Bay](#bay), each with SKU, quantity, and possibly expiry date (perishables). |
| **Boxcar** | **VagãoFechado** | Type of [Wagon](#wagon) used to transport general goods; part of [RollingStock](#rollingstock) in the [Railway](#railway) domain. |
| **Bulk goods** | **Mercadorias a granel** | Unpackaged large-volume commodities such as coal, grain, or minerals that are transported in [HopperCar](#hoppercar) rather than in containers. |
| **Capacity** | **Capacidade** | Constraint of [Wagon](#wagon), [Bay](#bay), or [Trolley](#trolley) defining the maximum weight or volume allowed. Used in planning and allocation. |
| **Cargo** | **Carga** | Goods or materials transported via freight services, typically using containers on [Train](#train), [Truck](#truck), or ships within the intermodal logistics network. |
| **DeterministicSweep** | **PercursoDeterminístico** | Picking path sequencing strategy that sorts [WarehouseBay](#warehousebay) in ascending order of [Aisle](#aisle), used in USEI04. |
| **Dock** | **DocaDeDescarga** | Access point in the [Terminal](#terminal) where [Wagon](#wagon) unloads [Cargo](#cargo) into [Warehouse](#warehouse) for storage and later dispatch. |
| **DoubleTrack** | **LinhaDupla** | [LineSegment](#linesegment) type where [Train](#train) can circulate in opposite directions simultaneously without stopping at [Station](#station). |
| **EligibilityStatus** | **EstadoDeElegibilidade** | Classification of an [OrderLine](#orderline) after allocation: ELIGIBLE, PARTIAL, or UNDISPATCHABLE, depending on available inventory. |
| **FEFO/FIFO** | **FEFO/FIFO** | Inventory management rules applied in unloading and dispatch: *First-Expired-First-Out* for perishables and *First-In-First-Out* for non-perishables. |
| **Flatcar** | **Plataforma** | Type of [Wagon](#wagon) used to carry containers, vehicles, or heavy machinery. |
| **Freight** | **Carga** | A set of [Wagon](#wagon) transported from one [Station](#station) to another, defining a logistical flow within the [Railway](#railway) network managed by the system. |
| **Freight Trains** | **Comboios de Mercadoria** | [Train](#train) designed to transport [Freight](#freight), raw materials, or containers instead of passengers. |
| **FreightYard** | **PátioDeMercadorias** | Area inside a [Terminal](#terminal) or [Station](#station) where [Wagon](#wagon) are sorted, parked, or assembled before being dispatched in the network. |
| **Gauge** | **Bitola** | Width of a [Track](#track). A property of [LineSegment](#linesegment) and [RollingStock](#rollingstock), influencing compatibility of [Locomotive](#locomotive) and [Wagon](#wagon). |
| **HopperCar** | **VagãoGraneleiro** | Type of [Wagon](#wagon) designed to transport coal, grains, or minerals in bulk. |
| **Intermodal Terminal** | **Terminal Intermodal** | Logistics facility where [Cargo](#cargo) is transferred between different modes of transport — such as [Railway](#railway), road, or sea — without handling the goods themselves. |
| **items.csv** | **items.csv** | Input dataset with master data of [Box](#box) (SKU, name, category, unit, volume, unitWeight) required for validation and inventory operations. |
| **Line** | **Linha** | [Railway](#railway) route composed of one or more [LineSegment](#linesegment), connecting [Station](#station) and managed by an [Owner](#owner). |
| **LineSegment** | **SegmentoDeLinha** | Portion of [Line](#line) connecting two points, with properties like [Gauge](#gauge), electrification, and track type (single/double), used in planning and statistics in the project. |
| **Locomotive** | **Locomotiva** | Engine unit of [Train](#train), diesel or electric, responsible for pulling [Wagon](#wagon); defined by attributes like power, acceleration, gauge, and fuel capacity. |
| **Manufacture products** | **Fabricar produtos** | To use machines, labour, and [Raw materials](#raw-materials) in an industrial setting to create finished goods that can be transported or distributed through the [Supply chains](#supply-chains). |
| **NearestNeighbour** | **VizinhoMaisPróximo** | Picking path sequencing strategy that always chooses the next [WarehouseBay](#warehousebay) with the shortest distance from the current position, used in USEI04. |
| **Operator** | **Operador** | Company that owns and operates [Locomotive](#locomotive) or [Wagon](#wagon) within the [RailwayInfrastructure](#railwayinfrastructure). |
| **Order** | **Encomenda** | Request containing one or more [OrderLine](#orderline), each line specifying a SKU and quantity to be dispatched from [Warehouse](#warehouse) stock. |
| **OrderLine** | **LinhaDeEncomenda** | Single item request within an [Order](#order), specifying SKU, quantity, and due date; used in allocation and picking planning. |
| **order_lines.csv** | **order_lines.csv** | Input dataset containing detailed [OrderLine](#orderline) (orderId, lineNo, SKU, qty), used for order allocation (USEI02). |
| **orders.csv** | **orders.csv** | Input dataset containing [Order](#order) (orderId, dueDate, priority), used for allocation and picking planning. |
| **Owner** | **Proprietário** | Entity responsible for a [Line](#line). Owners maintain their [LineSegment](#linesegment) but allow usage by other [Operator](#operator)’s [Train](#train). |
| **PackingHeuristic** | **HeurísticaDeEmpacotamento** | Strategy used in USEI03 to allocate [OrderLine](#orderline) to [Trolley](#trolley): First Fit (FF), First Fit Decreasing (FFD), and [BestFitDecreasing](#bestfitdecreasing). |
| **Path** | **Percurso** | Ordered list of [Station](#station) through which a [Freight](#freight) passes from origin to destination. |
| **Passengers** | **Passageiros** | People who travel by [Train](#train) or other transport modes rather than [Cargo](#cargo). |
| **Picker** | **Recolhedor** | User who executes the physical picking of items from [WarehouseBay](#warehousebay) according to the [PickingPlan](#pickingplan) and picking path strategy. |
| **PickingPath** | **PercursoDeRecolha** | Optimized sequence of [WarehouseBay](#warehousebay) visits for picking [OrderLine](#orderline), calculated using [DeterministicSweep](#deterministicsweep) or [NearestNeighbour](#nearestneighbour) strategies to minimize distance. |
| **PickingPlan** | **PlanoDeRecolha** | Plan generated to collect [Box](#box) from [WarehouseBay](#warehousebay) to fulfill [Order](#order), considering [Trolley](#trolley) capacity and optimization heuristics defined in Sprint 1. |
| **PressureGasCar** | **VagãoDeGasesLiquefeitos** | [Wagon](#wagon) designed to transport liquefied gases (e.g., propane, butane) under pressure. |
| **QualityOperator** | **OperadorDeQualidade** | User responsible for managing [Return](#return), inspecting items in [ReturnQuarantine](#returnquarantine), and deciding whether they are restocked or discarded. |
| **Railway** | **Ferrovia** | System for transporting [Freight](#freight) by [Train](#train), including [Track](#track), [Station](#station), [Locomotive](#locomotive), [Wagon](#wagon), and operations that ensure efficient and sustainable logistics. |
| **RailwayGraph** | **GrafoFerroviário** | Data structure modeling [Station](#station) and [LineSegment](#linesegment) as nodes and edges, supporting pathfinding, maintenance routes, and infrastructure analysis within the simulation. |
| **RailwayInfrastructure** | **InfraestruturaFerroviária** | Physical elements of [Railway](#railway) such as [Track](#track), [Station](#station), [FreightYard](#freightyard), and [Terminal](#terminal), which enable circulation of [Train](#train) and [Cargo](#cargo) handling. |
| **Raw materials** | **Matérias-primas** | Basic natural resources or unprocessed materials used to [Manufacture products](#manufacture-products) or supply industries, such as wood, iron ore, or crude oil. |
| **RefrigeratedCar** | **VagãoFrigorífico** | [Wagon](#wagon) type with cooling systems used to transport perishable [Box](#box). |
| **Return** | **Devolução** | [Cargo](#cargo) item returned to the [Terminal](#terminal), requiring inspection before reintroduction into [Warehouse](#warehouse) or disposal. |
| **ReturnQuarantine** | **QuarentenaDeDevoluções** | Temporary stage where returned [Box](#box) are inspected before reintegration or disposal, with [AuditLog](#auditlog) ensuring traceability of actions taken. |
| **RollingStock** | **MaterialCirculante** | Vehicles that circulate on the [Railway](#railway), including [Locomotive](#locomotive) and [Wagon](#wagon), each with attributes (type, gauge, capacity, [Operator](#operator)) relevant to planning and [Cargo](#cargo) operations. |
| **Route** | **Rota** | Complete connection between an origin and destination [Station](#station), possibly including several [Path](#path) and [Freight](#freight). |
| **Scheduler/Planner** | **Planeador** | System user responsible for analyzing and creating timetables, [Route](#route)s, and allocations for [Train](#train) and [Warehouse](#warehouse) operations. |
| **SingleTrack** | **LinhaÚnica** | [LineSegment](#linesegment) type where [Train](#train) share the same [Track](#track) in both directions, requiring coordination at [Station](#station) or sidings. |
| **SKU** | **SKU (Unidade de Manutenção de Stock)** | Unique identifier of a [Box](#box) type managed in [Wagon](#wagon) and [Warehouse](#warehouse), essential for [Order](#order) allocation, inventory management, and [Return](#return) processing. |
| **Station** | **Estação** | Node in [RailwayInfrastructure](#railwayinfrastructure) where [Train](#train) can stop, load/unload [Cargo](#cargo), or cross in the case of [SingleTrack](#singletrack). |
| **Supply chains** | **Cadeias de abastecimento** | The network of organisations, resources, and processes involved in producing, transporting, and delivering [Cargo](#cargo) from suppliers to final users. |
| **TankCar** | **VagãoTanque** | [Wagon](#wagon) type used to transport liquids, chemicals, or fuels. Includes subtypes like Mineral Oil Cars and Chemical Tank Cars. |
| **Terminal** | **Terminal** | Specialized [Station](#station) with logistics functions, including unloading docks, [Warehouse](#warehouse), and intermodal connections, central in Sprint 1 [Cargo](#cargo) handling operations. |
| **TerminalOperator** | **OperadorDeTerminal** | User responsible for [Wagon](#wagon) unloading, [Warehouse](#warehouse) stock management, and ensuring [FEFO/FIFO](#fefofifo) compliance during [Cargo](#cargo) handling. |
| **Track** | **Via férrea** | Physical [Railway](#railway) pathway where [Train](#train) run, made up of rails, sleepers, and ballast, forming the basic infrastructure of the [Railway](#railway) network. |
| **Train** | **Comboio** | Set of coupled [RollingStock](#rollingstock), usually a [Locomotive](#locomotive) pulling [Wagon](#wagon), used to transport [Freight](#freight) across the [Railway](#railway) network. |
| **Trolley** | **CarrinhoDeRecolha** | Capacity-bounded cart used by [Picker](#picker) to collect [Box](#box) from [WarehouseBay](#warehousebay); central in USEI03 picking plan heuristics. |
| **Tunnels** | **Túneis** | Underground passages in [RailwayInfrastructure](#railwayinfrastructure) that allow [Train](#train) to cross mountains or other obstacles. |
| **Validation Rules** | **RegrasDeValidação** | Set of constraints applied when importing CSV files (e.g., rejecting unknown [SKU](#sku), invalid quantities, missing dates, non-unique boxIds). |
| **Wagon** | **Vagão** | [RollingStock](#rollingstock) designed for specific [Cargo](#cargo) types (e.g., [Boxcar](#boxcar), [Flatcar](#flatcar), [TankCar](#tankcar), [HopperCar](#hoppercar), [RefrigeratedCar](#refrigeratedcar)). Each wagon has payload and volume constraints. |
| **wagons.csv** | **wagons.csv** | Input dataset with information on [Wagon](#wagon) and their transported [Box](#box) (boxId, SKU, qty, expiryDate, receivedAt), used to initialize inventory. |
| **Warehouse** | **Armazém** | Storage facility connected to unloading docks, divided into [Aisle](#aisle)s and [WarehouseBay](#warehousebay), used to store [Box](#box) unloaded from [Wagon](#wagon). |
| **WarehouseBay** | **BaiaDeArmazém** | Storage unit inside a [Warehouse](#warehouse) [Aisle](#aisle), where [Box](#box) unloaded from [Wagon](#wagon) are stored and organized following [FEFO/FIFO](#fefofifo) rules. |
