CREATE TABLE Company
(
    company_id NUMBER(10) GENERATED AS IDENTITY,
    name       VARCHAR2(255)
        CONSTRAINT NN_Company_Name NOT NULL,
    short_name VARCHAR2(255)
        CONSTRAINT NN_Company_ShortName NOT NULL,
    vat_number VARCHAR2(255)
        CONSTRAINT NN_Company_VAT NOT NULL
        CONSTRAINT UQ_Company_VAT UNIQUE,
    CONSTRAINT PK_Company PRIMARY KEY (company_id)
);

CREATE TABLE Owner
(
    owner_id NUMBER(10),
    CONSTRAINT PK_Owner PRIMARY KEY (owner_id)
);

CREATE TABLE Operator
(
    operator_id NUMBER(10),
    country     NVARCHAR2(255)
        CONSTRAINT NN_Operator_Country NOT NULL,
    CONSTRAINT PK_Operator PRIMARY KEY (operator_id)
);

CREATE TABLE FacilityType
(
    facilitytype_id NUMBER(10) GENERATED AS IDENTITY,
    type            VARCHAR2(255)
        CONSTRAINT NN_FacilityType_Type NOT NULL,
    CONSTRAINT PK_FacilityType PRIMARY KEY (facilitytype_id)
);

CREATE TABLE Facility
(
    facility_id     NUMBER(10) GENERATED AS IDENTITY,
    facilitytype_id NUMBER(10)
        CONSTRAINT NN_Facility_FType NOT NULL,
    name            NVARCHAR2(255)
        CONSTRAINT NN_Facility_Name NOT NULL
        CONSTRAINT UQ_Facility_Name UNIQUE,
    location        VARCHAR2(255)
        CONSTRAINT NN_Facility_Location NOT NULL,
    CONSTRAINT PK_Facility PRIMARY KEY (facility_id)
);

CREATE TABLE Gauge
(
    gauge_id NUMBER(10) GENERATED AS IDENTITY,
    name     VARCHAR2(255)
        CONSTRAINT NN_Gauge_Name NOT NULL,
    width_mm NUMBER(10)
        CONSTRAINT NN_Gauge_Width NOT NULL
        CONSTRAINT UQ_Gauge_Width UNIQUE,
    CONSTRAINT PK_Gauge PRIMARY KEY (gauge_id)
);


CREATE TABLE RailwayLine
(
    line_id           NUMBER(10) GENERATED AS IDENTITY,
    gauge_id          NUMBER(10)
        CONSTRAINT NN_RLine_Gauge NOT NULL,
    start_facility_id NUMBER(10)
        CONSTRAINT NN_RLine_StartFacility NOT NULL,
    end_facility_id   NUMBER(10)
        CONSTRAINT NN_RLine_EndFacility NOT NULL,
    owner_id          NUMBER(10)
        CONSTRAINT NN_RLine_Owner NOT NULL,
    name              NVARCHAR2(255)
        CONSTRAINT NN_RLine_Name NOT NULL
        CONSTRAINT UQ_RailwayLine_Name UNIQUE,
    CONSTRAINT PK_RailwayLine PRIMARY KEY (line_id)
);

CREATE TABLE LineSegment
(
    line_segment_id NUMBER(10) GENERATED AS IDENTITY,
    line_id         NUMBER(10)
        CONSTRAINT NN_LSeg_Line NOT NULL,
    segment_order   NUMBER(10)
        CONSTRAINT NN_LSeg_Order NOT NULL
        CONSTRAINT CHK_LSeg_Order_GT0 CHECK (segment_order > 0),
    length_m        NUMBER(10)
        CONSTRAINT NN_LSeg_Length NOT NULL
        CONSTRAINT CHK_LSeg_Length_GT0 CHECK (length_m > 0),
    speed_limit     NUMBER(10)
        CONSTRAINT NN_LSeg_Speed NOT NULL
        CONSTRAINT CHK_LSeg_Speed_GT0 CHECK (speed_limit > 0),
    max_weight      NUMBER(10)
        CONSTRAINT NN_LSeg_MaxWeight NOT NULL
        CONSTRAINT CHK_LSeg_MaxWeight_GT0 CHECK (max_weight > 0),
    track_type      VARCHAR2(6)
        CONSTRAINT NN_LSeg_TrackType NOT NULL
        CONSTRAINT CHK_LSeg_Track_Valid CHECK (track_type IN ('single', 'double')),
    electrified     VARCHAR2(3)
        CONSTRAINT NN_LSeg_Electrified NOT NULL
        CONSTRAINT CHK_LSeg_Electrified CHECK (electrified IN ('Yes', 'No')),
    siding_position NUMBER(10)
        CONSTRAINT NN_LSeg_SidingPos NOT NULL,
    siding_lenght   NUMBER(10)
        CONSTRAINT NN_LSeg_SidingLen NOT NULL
        CONSTRAINT CHK_LSeg_SidingLen_GT0 CHECK (siding_lenght > 0),
    CONSTRAINT PK_LineSegment PRIMARY KEY (line_segment_id),
    CONSTRAINT UQ_LineSegment_Line_Order UNIQUE (line_id, segment_order)
);


CREATE TABLE Model
(
    model_id         NUMBER(10) GENERATED AS IDENTITY,
    name             VARCHAR2(255)
        CONSTRAINT NN_Model_Name NOT NULL
        CONSTRAINT UQ_Model_Name UNIQUE,
    maker            VARCHAR2(255)
        CONSTRAINT NN_Model_Maker NOT NULL,
    max_speed_kmh    NUMBER(5,2)
        CONSTRAINT NN_Model_MaxSpeed NOT NULL
        CONSTRAINT CHK_Model_MaxSpeed_GT0 CHECK (max_speed_kmh > 0),
    number_of_bogies NUMBER(10)
        CONSTRAINT NN_Model_NBogies NOT NULL
        CONSTRAINT CHK_Model_NumberOfBogies_GE1 CHECK (number_of_bogies >= 1),
    bogies           VARCHAR2(255)
        CONSTRAINT NN_Model_Bogies NOT NULL,
    length_m         NUMBER(6,3)
        CONSTRAINT NN_Model_Length NOT NULL
        CONSTRAINT CHK_Model_Length_GT0 CHECK (length_m > 0),
    height_m         NUMBER(6,2)
        CONSTRAINT NN_Model_Height NOT NULL
        CONSTRAINT CHK_Model_Height_GT0 CHECK (height_m > 0),
    weight_t         NUMBER(7,2)
        CONSTRAINT NN_Model_Weight NOT NULL
        CONSTRAINT CHK_Model_Weight_GT0 CHECK (weight_t > 0),
    width_m          NUMBER(6,2)
        CONSTRAINT NN_Model_Width NOT NULL
        CONSTRAINT CHK_Model_Width_GT0 CHECK (width_m > 0),
    CONSTRAINT PK_Model PRIMARY KEY (model_id)
);

CREATE TABLE LocomotiveModel
(
    locomotive_model_id   NUMBER(10) GENERATED AS IDENTITY,
    model_id              NUMBER(10)
        CONSTRAINT NN_LocoModel_Model NOT NULL,
    power_kw              NUMBER(7,2)
        CONSTRAINT NN_LocoModel_Power NOT NULL
        CONSTRAINT CHK_LocoModel_Power_GT0 CHECK (power_kw > 0),
    traction_kn           NUMBER(10)
        CONSTRAINT NN_LocoModel_Traction NOT NULL
        CONSTRAINT CHK_LocoModel_Traction_GT0 CHECK (traction_kn > 0),
    operational_speed_kmh NUMBER(5,2)
        CONSTRAINT NN_LocoModel_OpSpeed NOT NULL
        CONSTRAINT CHK_LocoModel_Speed_GT0 CHECK (operational_speed_kmh > 0),
    CONSTRAINT PK_LocomotiveModel PRIMARY KEY (locomotive_model_id)
);

CREATE TABLE WagonType
(
    wagontype_id NUMBER(10) GENERATED AS IDENTITY,
    type         VARCHAR2(255)
        CONSTRAINT NN_WagonType_Type NOT NULL,
    CONSTRAINT PK_WagonType PRIMARY KEY (wagontype_id)
);

CREATE TABLE WagonCategory
(
    wagoncategory_id NUMBER(10) GENERATED AS IDENTITY,
    name             VARCHAR2(255)
        CONSTRAINT NN_WagonCategory_Name NOT NULL,
    CONSTRAINT PK_WagonCategory PRIMARY KEY (wagoncategory_id)
);

CREATE TABLE WagonModel
(
    wagonmodel_id    NUMBER(10) GENERATED AS IDENTITY,
    wagontype_id     NUMBER(10)
        CONSTRAINT NN_WModel_WType NOT NULL,
    gauge_id         NUMBER(10)
        CONSTRAINT NN_WModel_Gauge NOT NULL,
    model_id         NUMBER(10)
        CONSTRAINT NN_WModel_Model NOT NULL,
    wagoncategory_id NUMBER(10)
        CONSTRAINT NN_WModel_WCat NOT NULL,
    payload_t        NUMBER(6,2)
        CONSTRAINT NN_WModel_Payload NOT NULL
        CONSTRAINT CHK_WModel_Payload_GE0 CHECK (payload_t >= 0),
    volume_m3        NUMBER(7,2)
        CONSTRAINT NN_WModel_Volume NOT NULL
        CONSTRAINT CHK_WModel_Volume_GT0 CHECK (volume_m3 > 0),
    CONSTRAINT PK_WagonModel PRIMARY KEY (wagonmodel_id)
);


CREATE TABLE Locomotive
(
    loco_id             NUMBER(10) GENERATED AS IDENTITY,
    operator_id         NUMBER(10)
        CONSTRAINT NN_Loco_Operator NOT NULL,
    locomotive_model_id NUMBER(10)
        CONSTRAINT NN_Loco_Model NOT NULL,
    name                VARCHAR2(255),
    service_year        NUMBER(4)
        CONSTRAINT NN_Loco_SrvYear NOT NULL,
    CONSTRAINT PK_Locomotive PRIMARY KEY (loco_id)
);

CREATE TABLE Diesel_Locomotive
(
    loco_id        NUMBER(10),
    fluel_capacity NUMBER(10)
        CONSTRAINT NN_Diesel_Fuel NOT NULL,
    CONSTRAINT PK_Diesel_Locomotive PRIMARY KEY (loco_id)
);

CREATE TABLE Electric_Locomotive
(
    loco_id      NUMBER(10),
    voltage_kv   NUMBER(6,2)
        CONSTRAINT NN_Electric_Voltage NOT NULL,
    frequency_hz VARCHAR2(6)
        CONSTRAINT NN_Electric_Freq NOT NULL,
    CONSTRAINT PK_Electric_Locomotive PRIMARY KEY (loco_id)
);

CREATE TABLE Wagon
(
    wagon_id      NUMBER(10) GENERATED AS IDENTITY,
    wagonmodel_id NUMBER(10)
        CONSTRAINT NN_Wagon_Model NOT NULL,
    operator_id   NUMBER(10)
        CONSTRAINT NN_Wagon_Oper NOT NULL,
    service_year  NUMBER(4)
        CONSTRAINT NN_Wagon_SrvYear NOT NULL,
    CONSTRAINT PK_Wagon PRIMARY KEY (wagon_id)
);

CREATE TABLE LocomotiveGauge
(
    loco_id  NUMBER(10)
        CONSTRAINT NN_LocoGauge_Loco NOT NULL,
    gauge_id NUMBER(10)
        CONSTRAINT NN_LocoGauge_Gauge NOT NULL,
    CONSTRAINT PK_LocomotiveGauge PRIMARY KEY (loco_id, gauge_id)
);


CREATE TABLE Train
(
    train_id    NUMBER(10) GENERATED AS IDENTITY,
    operator_id NUMBER(10),
    name        VARCHAR2(10)
        CONSTRAINT NN_Train_Name NOT NULL,
    CONSTRAINT PK_Train PRIMARY KEY (train_id)
);

CREATE TABLE RouteEndpoint
(
    route_endpoint_id NUMBER(10) GENERATED AS IDENTITY,
    facility_id       NUMBER(10)
        CONSTRAINT NN_REndpoint_Facility NOT NULL,
    train_id          NUMBER(10)
        CONSTRAINT NN_REndpoint_Train NOT NULL,
    endpoint_order    NUMBER(10)
        CONSTRAINT NN_REndpoint_Order NOT NULL,
    CONSTRAINT PK_RouteEndpoint PRIMARY KEY (route_endpoint_id),
    CONSTRAINT UQ_RouteEndpoint_Order UNIQUE (train_id, endpoint_order)
);

CREATE TABLE Freight
(
    freight_id              NUMBER(10) GENERATED AS IDENTITY,
    origin_facility_id      NUMBER(10)
        CONSTRAINT NN_Freight_Origin NOT NULL,
    destination_facility_id NUMBER(10)
        CONSTRAINT NN_Freight_Dest NOT NULL,
    train_id                NUMBER(10)
        CONSTRAINT NN_Freight_Train NOT NULL,
    depart_time             DATE
        CONSTRAINT NN_Freight_Depart NOT NULL,
    arrival_time            DATE
        CONSTRAINT NN_Freight_Arrival NOT NULL,
    CONSTRAINT CHK_Freight_Dates
        CHECK (arrival_time >= depart_time),
    CONSTRAINT PK_Freight PRIMARY KEY (freight_id)
);

CREATE TABLE FreightWagon
(
    freight_id NUMBER(10)
        CONSTRAINT NN_FreightWagon_Freight NOT NULL,
    wagon_id   NUMBER(10)
        CONSTRAINT NN_FreightWagon_Wagon NOT NULL,
    CONSTRAINT PK_FreightWagon PRIMARY KEY (freight_id, wagon_id)
);

CREATE TABLE TrainLocomotive
(
    train_id NUMBER(10)
        CONSTRAINT NN_TL_Train NOT NULL,
    loco_id  NUMBER(10)
        CONSTRAINT NN_TL_Loco NOT NULL,
    CONSTRAINT PK_TrainLocomotive PRIMARY KEY (train_id, loco_id)
);

CREATE TABLE TrainWagon
(
    train_id NUMBER(10)
        CONSTRAINT NN_TW_Train NOT NULL,
    wagon_id NUMBER(10)
        CONSTRAINT NN_TW_Wagon NOT NULL,
    CONSTRAINT PK_TrainWagon PRIMARY KEY (train_id, wagon_id)
);

CREATE TABLE Cargo
(
    cargo_id    NUMBER(10) GENERATED AS IDENTITY,
    wagon_id    NUMBER(10)
        CONSTRAINT NN_Cargo_Wagon NOT NULL,
    description NVARCHAR2(255)
        CONSTRAINT NN_Cargo_Desc NOT NULL,
    type        NVARCHAR2(255)
        CONSTRAINT NN_Cargo_Type NOT NULL,
    quantity    NUMBER(10)
        CONSTRAINT NN_Cargo_Qty NOT NULL
        CONSTRAINT CHK_Cargo_Qty_GT0 CHECK (quantity > 0),
    volume      NUMBER(10,2)
        CONSTRAINT NN_Cargo_Vol NOT NULL
        CONSTRAINT CHK_Cargo_Vol_GT0 CHECK (volume > 0),
    weight      NUMBER(10,2)
        CONSTRAINT NN_Cargo_Weight NOT NULL
        CONSTRAINT CHK_Cargo_Weight_GT0 CHECK (weight > 0),
    expiry_date DATE
        CONSTRAINT NN_Cargo_Expiry NOT NULL,
    CONSTRAINT PK_Cargo PRIMARY KEY (cargo_id)
);

CREATE TABLE BuildingType
(
    buildingtype_id NUMBER(10) GENERATED AS IDENTITY,
    name            VARCHAR2(255)
        CONSTRAINT NN_BType_Name NOT NULL,
    CONSTRAINT PK_BuildingType PRIMARY KEY (buildingtype_id)
);

CREATE TABLE FacilityBuilding
(
    facility_id     NUMBER(10)
        CONSTRAINT NN_FacB_Facility NOT NULL,
    buildingtype_id NUMBER(10)
        CONSTRAINT NN_FacB_BType NOT NULL,
    CONSTRAINT PK_FacilityBuilding PRIMARY KEY (facility_id, buildingtype_id)
);


ALTER TABLE Owner
    ADD CONSTRAINT FK_Owner_Company
        FOREIGN KEY (owner_id) REFERENCES Company (company_id);

ALTER TABLE Operator
    ADD CONSTRAINT FK_Operator_Company
        FOREIGN KEY (operator_id) REFERENCES Company (company_id);

ALTER TABLE Facility
    ADD CONSTRAINT FK_Facility_FacilityType
        FOREIGN KEY (facilitytype_id) REFERENCES FacilityType (facilitytype_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RLine_Gauge
        FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RLine_Owner
        FOREIGN KEY (owner_id) REFERENCES Owner (owner_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RLine_StartFacility
        FOREIGN KEY (start_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RLine_EndFacility
        FOREIGN KEY (end_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE LineSegment
    ADD CONSTRAINT FK_LSeg_RailwayLine
        FOREIGN KEY (line_id) REFERENCES RailwayLine (line_id);

ALTER TABLE LocomotiveModel
    ADD CONSTRAINT FK_LocoModel_Model
        FOREIGN KEY (model_id) REFERENCES Model (model_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WModel_WagonType
        FOREIGN KEY (wagontype_id) REFERENCES WagonType (wagontype_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WModel_Gauge
        FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WModel_Model
        FOREIGN KEY (model_id) REFERENCES Model (model_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WModel_WagonCategory
        FOREIGN KEY (wagoncategory_id) REFERENCES WagonCategory (wagoncategory_id);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Loco_Operator
        FOREIGN KEY (operator_id) REFERENCES Operator (operator_id);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Loco_LocoModel
        FOREIGN KEY (locomotive_model_id) REFERENCES LocomotiveModel (locomotive_model_id);

ALTER TABLE Diesel_Locomotive
    ADD CONSTRAINT FK_Diesel_Loco
        FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE Electric_Locomotive
    ADD CONSTRAINT FK_Electric_Loco
        FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_WagonModel
        FOREIGN KEY (wagonmodel_id) REFERENCES WagonModel (wagonmodel_id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_Operator
        FOREIGN KEY (operator_id) REFERENCES Operator (operator_id);

ALTER TABLE LocomotiveGauge
    ADD CONSTRAINT FK_LocoGauge_Loco
        FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE LocomotiveGauge
    ADD CONSTRAINT FK_LocoGauge_Gauge
        FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE Train
    ADD CONSTRAINT FK_Train_Operator
        FOREIGN KEY (operator_id) REFERENCES Operator (operator_id);

ALTER TABLE RouteEndpoint
    ADD CONSTRAINT FK_REndpoint_Facility
        FOREIGN KEY (facility_id) REFERENCES Facility (facility_id);

ALTER TABLE RouteEndpoint
    ADD CONSTRAINT FK_REndpoint_Train
        FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_OriginFacility
        FOREIGN KEY (origin_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_DestinationFacility
        FOREIGN KEY (destination_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_Train
        FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE Cargo
    ADD CONSTRAINT FK_Cargo_Wagon
        FOREIGN KEY (wagon_id) REFERENCES Wagon (wagon_id);

ALTER TABLE FacilityBuilding
    ADD CONSTRAINT FK_FacB_Facility
        FOREIGN KEY (facility_id) REFERENCES Facility (facility_id);

ALTER TABLE FacilityBuilding
    ADD CONSTRAINT FK_FacB_BuildingType
        FOREIGN KEY (buildingtype_id) REFERENCES BuildingType (buildingtype_id);

ALTER TABLE FreightWagon
    ADD CONSTRAINT FK_FreightWagon_Freight
        FOREIGN KEY (freight_id) REFERENCES Freight (freight_id);

ALTER TABLE FreightWagon
    ADD CONSTRAINT FK_FreightWagon_Wagon
        FOREIGN KEY (wagon_id) REFERENCES Wagon (wagon_id);
