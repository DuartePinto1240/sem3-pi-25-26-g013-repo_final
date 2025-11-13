CREATE TABLE Company
(
    company_id NUMBER(10) GENERATED AS IDENTITY,
    name       VARCHAR2(255),
    short_name VARCHAR2(255),
    vat_number VARCHAR2(255),
    CONSTRAINT PK_Company PRIMARY KEY (company_id),
    CONSTRAINT NN_Company_Name CHECK (name IS NOT NULL),
    CONSTRAINT NN_Company_ShortName CHECK (short_name IS NOT NULL),
    CONSTRAINT NN_Company_VAT CHECK (vat_number IS NOT NULL),
    CONSTRAINT UQ_Company_VAT UNIQUE (vat_number)
);

CREATE TABLE Owner
(
    owner_id NUMBER(10),
    CONSTRAINT PK_Owner PRIMARY KEY (owner_id)
);

CREATE TABLE Operator
(
    operator_id NUMBER(10),
    country     NVARCHAR2(255),
    CONSTRAINT PK_Operator PRIMARY KEY (operator_id),
    CONSTRAINT NN_Operator_Country CHECK (country IS NOT NULL)
);

CREATE TABLE FacilityType
(
    facilitytype_id NUMBER(10) GENERATED AS IDENTITY,
    type            VARCHAR2(255),
    CONSTRAINT PK_FacilityType PRIMARY KEY (facilitytype_id),
    CONSTRAINT NN_FacilityType_Type CHECK (type IS NOT NULL)
);

CREATE TABLE Facility
(
    facility_id     NUMBER(10) GENERATED AS IDENTITY,
    facilitytype_id NUMBER(10),
    name            NVARCHAR2(255),
    location        VARCHAR2(255),
    CONSTRAINT PK_Facility PRIMARY KEY (facility_id),
    CONSTRAINT NN_Facility_FType CHECK (facilitytype_id IS NOT NULL),
    CONSTRAINT NN_Facility_Name CHECK (name IS NOT NULL),
    CONSTRAINT NN_Facility_Location CHECK (location IS NOT NULL),
    CONSTRAINT UQ_Facility_Name UNIQUE (name)
);

CREATE TABLE Gauge
(
    gauge_id NUMBER(10) GENERATED AS IDENTITY,
    width_mm NUMBER(10),
    CONSTRAINT PK_Gauge PRIMARY KEY (gauge_id),
    CONSTRAINT NN_Gauge_Width CHECK (width_mm IS NOT NULL),
    CONSTRAINT UQ_Gauge_Width UNIQUE (width_mm)
);

CREATE TABLE RailwayLine
(
    line_id          NUMBER(10) GENERATED AS IDENTITY,
    gauge_id         NUMBER(10),
    start_station_id NUMBER(10),
    end_station_id   NUMBER(10),
    name             NVARCHAR2(255),
    owner_id         NUMBER(10),
    CONSTRAINT PK_RailwayLine PRIMARY KEY (line_id),
    CONSTRAINT NN_RLine_Gauge CHECK (gauge_id IS NOT NULL),
    CONSTRAINT NN_RLine_Start CHECK (start_station_id IS NOT NULL),
    CONSTRAINT NN_RLine_End CHECK (end_station_id IS NOT NULL),
    CONSTRAINT NN_RLine_Name CHECK (name IS NOT NULL),
    CONSTRAINT NN_RLine_Owner CHECK (owner_id IS NOT NULL),
    CONSTRAINT UQ_RailwayLine_Name UNIQUE (name)
);

CREATE TABLE LineSegment
(
    line_segment_id NUMBER(10) GENERATED AS IDENTITY,
    line_id         NUMBER(10),
    segment_order   NUMBER(10),
    length_m        NUMBER(10),
    speed_limit     NUMBER(10),
    max_weight      NUMBER(10),
    track_type      VARCHAR2(6),
    electrified     VARCHAR2(3),
    CONSTRAINT PK_LineSegment PRIMARY KEY (line_segment_id),
    CONSTRAINT NN_LSeg_Line CHECK (line_id IS NOT NULL),
    CONSTRAINT NN_LSeg_Order CHECK (segment_order IS NOT NULL),
    CONSTRAINT NN_LSeg_Length CHECK (length_m IS NOT NULL),
    CONSTRAINT NN_LSeg_Speed CHECK (speed_limit IS NOT NULL),
    CONSTRAINT NN_LSeg_MaxWeight CHECK (max_weight IS NOT NULL),
    CONSTRAINT NN_LSeg_TrackType CHECK (track_type IS NOT NULL),
    CONSTRAINT NN_LSeg_Electrified CHECK (electrified IS NOT NULL),
    CONSTRAINT UQ_LineSegment_Line_Order UNIQUE (line_id, segment_order),
    CONSTRAINT CHK_LSeg_Order_GT0 CHECK (segment_order > 0),
    CONSTRAINT CHK_LSeg_Length_GT0 CHECK (length_m > 0),
    CONSTRAINT CHK_LSeg_Speed_GT0 CHECK (speed_limit > 0),
    CONSTRAINT CHK_LSeg_MaxWeight_GT0 CHECK (max_weight > 0),
    CONSTRAINT CHK_LSeg_Track_Valid CHECK (track_type IN ('single', 'double')),
    CONSTRAINT CHK_LSeg_Electrified CHECK (electrified IN ('Yes', 'No'))
);

CREATE TABLE Model
(
    model_id         NUMBER(10) GENERATED AS IDENTITY,
    name             VARCHAR2(255),
    maker            VARCHAR2(255),
    max_speed_kmh    NUMBER(5,2),
    number_of_bogies NUMBER(10),
    bogies           VARCHAR2(255),
    length_m         NUMBER(6,3),
    height_m         NUMBER(6,2),
    weight_t         NUMBER(7,2),
    width_m          NUMBER(6,2),
    CONSTRAINT PK_Model PRIMARY KEY (model_id),
    CONSTRAINT NN_Model_Name CHECK (name IS NOT NULL),
    CONSTRAINT NN_Model_Maker CHECK (maker IS NOT NULL),
    CONSTRAINT NN_Model_MaxSpeed CHECK (max_speed_kmh IS NOT NULL),
    CONSTRAINT NN_Model_NBogies CHECK (number_of_bogies IS NOT NULL),
    CONSTRAINT NN_Model_Bogies CHECK (bogies IS NOT NULL),
    CONSTRAINT NN_Model_Length CHECK (length_m IS NOT NULL),
    CONSTRAINT NN_Model_Height CHECK (height_m IS NOT NULL),
    CONSTRAINT NN_Model_Weight CHECK (weight_t IS NOT NULL),
    CONSTRAINT NN_Model_Width CHECK (width_m IS NOT NULL),
    CONSTRAINT UQ_Model_Name UNIQUE (name),
    CONSTRAINT CHK_Model_MaxSpeed_GT0 CHECK (max_speed_kmh > 0),
    CONSTRAINT CHK_Model_NumberOfBogies_GE1 CHECK (number_of_bogies >= 1),
    CONSTRAINT CHK_Model_Length_GT0 CHECK (length_m > 0),
    CONSTRAINT CHK_Model_Height_GT0 CHECK (height_m > 0),
    CONSTRAINT CHK_Model_Width_GT0 CHECK (width_m > 0),
    CONSTRAINT CHK_Model_Weight_GT0 CHECK (weight_t > 0)
);

CREATE TABLE LocomotiveModel
(
    locomotive_model_id   NUMBER(10) GENERATED AS IDENTITY,
    model_id              NUMBER(10),
    power_kw              NUMBER(7,2),
    traction_kn           NUMBER(10),
    operational_speed_kmh NUMBER(5,2),
    CONSTRAINT PK_LocomotiveModel PRIMARY KEY (locomotive_model_id),
    CONSTRAINT NN_LocoModel_Model CHECK (model_id IS NOT NULL),
    CONSTRAINT NN_LocoModel_Power CHECK (power_kw IS NOT NULL),
    CONSTRAINT NN_LocoModel_Traction CHECK (traction_kn IS NOT NULL),
    CONSTRAINT NN_LocoModel_OpSpeed CHECK (operational_speed_kmh IS NOT NULL),
    CONSTRAINT CHK_LocoModel_Power_GT0 CHECK (power_kw > 0),
    CONSTRAINT CHK_LocoModel_Traction_GT0 CHECK (traction_kn > 0),
    CONSTRAINT CHK_LocoModel_Speed_GT0 CHECK (operational_speed_kmh > 0)
);

CREATE TABLE WagonType
(
    wagontype_id NUMBER(10) GENERATED AS IDENTITY,
    type         VARCHAR2(255),
    CONSTRAINT PK_WagonType PRIMARY KEY (wagontype_id),
    CONSTRAINT NN_WagonType_Type CHECK (type IS NOT NULL)
);

CREATE TABLE WagonCategory
(
    wagoncategory_id NUMBER(10) GENERATED AS IDENTITY,
    code             VARCHAR2(255),
    name             VARCHAR2(255),
    CONSTRAINT PK_WagonCategory PRIMARY KEY (wagoncategory_id),
    CONSTRAINT NN_WagonCategory_Code CHECK (code IS NOT NULL),
    CONSTRAINT NN_WagonCategory_Name CHECK (name IS NOT NULL),
    CONSTRAINT UQ_WagonCategory_Code UNIQUE (code)
);

CREATE TABLE WagonModel
(
    wagonmodel_id    NUMBER(10) GENERATED AS IDENTITY,
    wagontype_id     NUMBER(10),
    gauge_id         NUMBER(10),
    model_id         NUMBER(10),
    wagoncategory_id NUMBER(10),
    payload_t        NUMBER(6,2),
    volume_m3        NUMBER(7,2),
    CONSTRAINT PK_WagonModel PRIMARY KEY (wagonmodel_id),
    CONSTRAINT NN_WModel_WType CHECK (wagontype_id IS NOT NULL),
    CONSTRAINT NN_WModel_Gauge CHECK (gauge_id IS NOT NULL),
    CONSTRAINT NN_WModel_Model CHECK (model_id IS NOT NULL),
    CONSTRAINT NN_WModel_WCat CHECK (wagoncategory_id IS NOT NULL),
    CONSTRAINT NN_WModel_Payload CHECK (payload_t IS NOT NULL),
    CONSTRAINT NN_WModel_Volume CHECK (volume_m3 IS NOT NULL),
    CONSTRAINT CHK_WModel_Volume_GT0 CHECK (volume_m3 > 0),
    CONSTRAINT CHK_WModel_Payload_GE0 CHECK (payload_t >= 0)
);

CREATE TABLE Locomotive
(
    loco_id             NUMBER(10) GENERATED AS IDENTITY,
    operator_id         NUMBER(10),
    locomotive_model_id NUMBER(10),
    service_year        NUMBER(4),
    CONSTRAINT PK_Locomotive PRIMARY KEY (loco_id),
    CONSTRAINT NN_Loco_Operator CHECK (operator_id IS NOT NULL),
    CONSTRAINT NN_Loco_Model CHECK (locomotive_model_id IS NOT NULL),
    CONSTRAINT NN_Loco_SrvYear CHECK (service_year IS NOT NULL)
);

CREATE TABLE Diesel_Locomotive
(
    loco_id       NUMBER(10),
    fuel_capacity NUMBER(10),
    CONSTRAINT PK_Diesel_Locomotive PRIMARY KEY (loco_id),
    CONSTRAINT NN_Diesel_Fuel CHECK (fuel_capacity IS NOT NULL)
);

CREATE TABLE Electric_Locomotive
(
    loco_id      NUMBER(10),
    voltage_kv   NUMBER(6,2),
    frequency_hz NUMBER(6,2),
    CONSTRAINT PK_Electric_Locomotive PRIMARY KEY (loco_id),
    CONSTRAINT NN_Electric_Voltage CHECK (voltage_kv IS NOT NULL),
    CONSTRAINT NN_Electric_Freq CHECK (frequency_hz IS NOT NULL)
);

CREATE TABLE Wagon
(
    wagon_id      NUMBER(10) GENERATED AS IDENTITY,
    wagonmodel_id NUMBER(10),
    operator_id   NUMBER(10),
    service_year  NUMBER(4),
    CONSTRAINT PK_Wagon PRIMARY KEY (wagon_id),
    CONSTRAINT NN_Wagon_Model CHECK (wagonmodel_id IS NOT NULL),
    CONSTRAINT NN_Wagon_Oper CHECK (operator_id IS NOT NULL),
    CONSTRAINT NN_Wagon_SrvYear CHECK (service_year IS NOT NULL)
);

CREATE TABLE LocomotiveGauge
(
    loco_id  NUMBER(10),
    gauge_id NUMBER(10),
    CONSTRAINT PK_LocomotiveGauge PRIMARY KEY (loco_id, gauge_id),
    CONSTRAINT NN_LocoGauge_Loco CHECK (loco_id IS NOT NULL),
    CONSTRAINT NN_LocoGauge_Gauge CHECK (gauge_id IS NOT NULL)
);

CREATE TABLE Train
(
    train_id          NUMBER(10) GENERATED AS IDENTITY,
    name              VARCHAR2(10),
    planned_departure DATE,
    planned_arrival   DATE,
    CONSTRAINT PK_Train PRIMARY KEY (train_id),
    CONSTRAINT NN_Train_Name CHECK (name IS NOT NULL),
    CONSTRAINT NN_Train_Depart CHECK (planned_departure IS NOT NULL),
    CONSTRAINT NN_Train_Arrival CHECK (planned_arrival IS NOT NULL)
);

CREATE TABLE Route
(
    route_id NUMBER(10) GENERATED AS IDENTITY,
    train_id NUMBER(10),
    name     VARCHAR2(255),
    CONSTRAINT PK_Route PRIMARY KEY (route_id),
    CONSTRAINT NN_Route_Train CHECK (train_id IS NOT NULL),
    CONSTRAINT NN_Route_Name CHECK (name IS NOT NULL)
);

CREATE TABLE RouteSegment
(
    route_segment_id NUMBER(10) GENERATED AS IDENTITY,
    segment_id       NUMBER(10),
    route_id         NUMBER(10),
    segment_order    NUMBER(10),
    CONSTRAINT PK_RouteSegment PRIMARY KEY (route_segment_id),
    CONSTRAINT NN_RSeg_Segment CHECK (segment_id IS NOT NULL),
    CONSTRAINT NN_RSeg_Route CHECK (route_id IS NOT NULL),
    CONSTRAINT NN_RSeg_Order CHECK (segment_order IS NOT NULL),
    CONSTRAINT UQ_RouteSegment_Order UNIQUE (route_id, segment_order)
);

CREATE TABLE RouteStop
(
    route_stop_id NUMBER(10) GENERATED AS IDENTITY,
    facility_id   NUMBER(10),
    route_id      NUMBER(10),
    stop_order    NUMBER(5),
    planned_stop  VARCHAR2(3),
    CONSTRAINT PK_RouteStop PRIMARY KEY (route_stop_id),
    CONSTRAINT NN_RStop_Facility CHECK (facility_id IS NOT NULL),
    CONSTRAINT NN_RStop_Route CHECK (route_id IS NOT NULL),
    CONSTRAINT NN_RStop_Order CHECK (stop_order IS NOT NULL),
    CONSTRAINT NN_RStop_Planned CHECK (planned_stop IS NOT NULL),
    CONSTRAINT UQ_RouteStop_Order UNIQUE (route_id, stop_order),
    CONSTRAINT CHK_RouteStop_YesNo CHECK (planned_stop IN ('Yes', 'No'))
);

CREATE TABLE TrainEvent
(
    train_event_id NUMBER(10) GENERATED AS IDENTITY,
    train_id       NUMBER(10),
    facility_id    NUMBER(10),
    pass_time      DATE,
    depart_time    DATE,
    stop_flag      VARCHAR2(3),
    CONSTRAINT PK_TrainEvent PRIMARY KEY (train_event_id),
    CONSTRAINT NN_TEvent_Train CHECK (train_id IS NOT NULL),
    CONSTRAINT NN_TEvent_Fac CHECK (facility_id IS NOT NULL),
    CONSTRAINT NN_TEvent_Pass CHECK (pass_time IS NOT NULL),
    CONSTRAINT NN_TEvent_Depart CHECK (depart_time IS NOT NULL),
    CONSTRAINT NN_TEvent_StopFlg CHECK (stop_flag IS NOT NULL),
    CONSTRAINT CHK_TEvent_YesNo CHECK (stop_flag IN ('Yes', 'No'))
);

CREATE TABLE TrainLocomotive
(
    train_id NUMBER(10),
    loco_id  NUMBER(10),
    CONSTRAINT PK_TrainLocomotive PRIMARY KEY (train_id, loco_id),
    CONSTRAINT NN_TL_Train CHECK (train_id IS NOT NULL),
    CONSTRAINT NN_TL_Loco CHECK (loco_id IS NOT NULL)
);

CREATE TABLE TrainWagon
(
    train_id NUMBER(10),
    wagon_id NUMBER(10),
    CONSTRAINT PK_TrainWagon PRIMARY KEY (train_id, wagon_id),
    CONSTRAINT NN_TW_Train CHECK (train_id IS NOT NULL),
    CONSTRAINT NN_TW_Wagon CHECK (wagon_id IS NOT NULL)
);

CREATE TABLE Freight
(
    freight_id             NUMBER(10) GENERATED AS IDENTITY,
    origin_station_id      NUMBER(10),
    destination_station_id NUMBER(10),
    train_id               NUMBER(10),
    departure_time         DATE,
    arrival_time           DATE,
    CONSTRAINT PK_Freight PRIMARY KEY (freight_id),
    CONSTRAINT NN_Freight_Origin CHECK (origin_station_id IS NOT NULL),
    CONSTRAINT NN_Freight_Dest CHECK (destination_station_id IS NOT NULL),
    CONSTRAINT NN_Freight_Train CHECK (train_id IS NOT NULL),
    CONSTRAINT NN_Freight_Depart CHECK (departure_time IS NOT NULL),
    CONSTRAINT NN_Freight_Arrival CHECK (arrival_time IS NOT NULL),
    CONSTRAINT CHK_Freight_Dates CHECK (arrival_time >= departure_time)
);

CREATE TABLE Cargo
(
    cargo_id    NUMBER(10) GENERATED AS IDENTITY,
    wagon_id    NUMBER(10),
    description NVARCHAR2(255),
    type        NVARCHAR2(255),
    quantity    NUMBER(10),
    volume      NUMBER(10,2),
    weight      NUMBER(10,2),
    expiry_date DATE,
    CONSTRAINT PK_Cargo PRIMARY KEY (cargo_id),
    CONSTRAINT NN_Cargo_Wagon CHECK (wagon_id IS NOT NULL),
    CONSTRAINT NN_Cargo_Desc CHECK (description IS NOT NULL),
    CONSTRAINT NN_Cargo_Type CHECK (type IS NOT NULL),
    CONSTRAINT NN_Cargo_Qty CHECK (quantity IS NOT NULL),
    CONSTRAINT NN_Cargo_Vol CHECK (volume IS NOT NULL),
    CONSTRAINT NN_Cargo_Weight CHECK (weight IS NOT NULL),
    CONSTRAINT NN_Cargo_Expiry CHECK (expiry_date IS NOT NULL),
    CONSTRAINT CHK_Cargo_Qty_GT0 CHECK (quantity > 0),
    CONSTRAINT CHK_Cargo_Vol_GT0 CHECK (volume > 0),
    CONSTRAINT CHK_Cargo_Weight_GT0 CHECK (weight > 0)
);

CREATE TABLE AmenityType
(
    amenitytype_id NUMBER(10) GENERATED AS IDENTITY,
    name           VARCHAR2(255),
    code           VARCHAR2(255),
    CONSTRAINT PK_AmenityType PRIMARY KEY (amenitytype_id),
    CONSTRAINT NN_AmenityType_Name CHECK (name IS NOT NULL),
    CONSTRAINT NN_AmenityType_Code CHECK (code IS NOT NULL),
    CONSTRAINT UQ_AmenityType_Code UNIQUE (code)
);

CREATE TABLE FacilityAmenity
(
    facility_id    NUMBER(10),
    amenitytype_id NUMBER(10),
    CONSTRAINT PK_FacilityAmenity PRIMARY KEY (facility_id, amenitytype_id),
    CONSTRAINT NN_FacAmen_Fac CHECK (facility_id IS NOT NULL),
    CONSTRAINT NN_FacAmen_Type CHECK (amenitytype_id IS NOT NULL)
);

ALTER TABLE Owner
    ADD CONSTRAINT FK_Owner_Company FOREIGN KEY (owner_id) REFERENCES Company (company_id);

ALTER TABLE Operator
    ADD CONSTRAINT FK_Operator_Company FOREIGN KEY (operator_id) REFERENCES Company (company_id);

ALTER TABLE Facility
    ADD CONSTRAINT FK_Facility_FacilityType FOREIGN KEY (facilitytype_id) REFERENCES FacilityType (facilitytype_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RailwayLine_Gauge FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RailwayLine_Owner FOREIGN KEY (owner_id) REFERENCES Owner (owner_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RailwayLine_StartStation FOREIGN KEY (start_station_id) REFERENCES Facility (facility_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RailwayLine_EndStation FOREIGN KEY (end_station_id) REFERENCES Facility (facility_id);

ALTER TABLE LineSegment
    ADD CONSTRAINT FK_LineSegment_RailwayLine FOREIGN KEY (line_id) REFERENCES RailwayLine (line_id);

ALTER TABLE LocomotiveModel
    ADD CONSTRAINT FK_LocomotiveModel_Model FOREIGN KEY (model_id) REFERENCES Model (model_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_WagonType FOREIGN KEY (wagontype_id) REFERENCES WagonType (wagontype_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_Gauge FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_Model FOREIGN KEY (model_id) REFERENCES Model (model_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_WagonCategory FOREIGN KEY (wagoncategory_id) REFERENCES WagonCategory (wagoncategory_id);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Locomotive_Operator FOREIGN KEY (operator_id) REFERENCES Operator (operator_id);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Locomotive_LocomotiveModel FOREIGN KEY (locomotive_model_id) REFERENCES LocomotiveModel (locomotive_model_id);

ALTER TABLE Diesel_Locomotive
    ADD CONSTRAINT FK_DieselLocomotive_Locomotive FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE Electric_Locomotive
    ADD CONSTRAINT FK_ElectricLocomotive_Locomotive FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_WagonModel FOREIGN KEY (wagonmodel_id) REFERENCES WagonModel (wagonmodel_id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_Operator FOREIGN KEY (operator_id) REFERENCES Operator (operator_id);

ALTER TABLE LocomotiveGauge
    ADD CONSTRAINT FK_LocomotiveGauge_Locomotive FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE LocomotiveGauge
    ADD CONSTRAINT FK_LocomotiveGauge_Gauge FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE Route
    ADD CONSTRAINT FK_Route_Train FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE RouteSegment
    ADD CONSTRAINT FK_RouteSegment_Route FOREIGN KEY (route_id) REFERENCES Route (route_id);

ALTER TABLE RouteSegment
    ADD CONSTRAINT FK_RouteSegment_LineSegment FOREIGN KEY (segment_id) REFERENCES LineSegment (line_segment_id);

ALTER TABLE RouteStop
    ADD CONSTRAINT FK_RouteStop_Facility FOREIGN KEY (facility_id) REFERENCES Facility (facility_id);

ALTER TABLE RouteStop
    ADD CONSTRAINT FK_RouteStop_Route FOREIGN KEY (route_id) REFERENCES Route (route_id);

ALTER TABLE TrainEvent
    ADD CONSTRAINT FK_TrainEvent_Train FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE TrainEvent
    ADD CONSTRAINT FK_TrainEvent_Facility FOREIGN KEY (facility_id) REFERENCES Facility (facility_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_OriginFacility FOREIGN KEY (origin_station_id) REFERENCES Facility (facility_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_DestinationFacility FOREIGN KEY (destination_station_id) REFERENCES Facility (facility_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_Train FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE Cargo
    ADD CONSTRAINT FK_Cargo_Wagon FOREIGN KEY (wagon_id) REFERENCES Wagon (wagon_id);

ALTER TABLE FacilityAmenity
    ADD CONSTRAINT FK_FacilityAmenity_Facility FOREIGN KEY (facility_id) REFERENCES Facility (facility_id);

ALTER TABLE FacilityAmenity
    ADD CONSTRAINT FK_FacilityAmenity_AmenityType FOREIGN KEY (amenitytype_id) REFERENCES AmenityType (amenitytype_id);
