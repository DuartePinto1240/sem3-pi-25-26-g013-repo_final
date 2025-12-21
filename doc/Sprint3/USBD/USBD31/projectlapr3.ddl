CREATE TABLE Company
(
    company_id NUMBER(10),
    name VARCHAR2(255)
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
    country VARCHAR2(255)
        CONSTRAINT NN_Operator_Country NOT NULL,
    CONSTRAINT PK_Operator PRIMARY KEY (operator_id)
);

CREATE TABLE FacilityType
(
    facilitytype_id NUMBER(10),
    type VARCHAR2(255)
        CONSTRAINT NN_FacilityType_Type NOT NULL
        CONSTRAINT UQ_FacilityType_Type UNIQUE,
    CONSTRAINT PK_FacilityType PRIMARY KEY (facilitytype_id)
);

CREATE TABLE Facility
(
    facility_id NUMBER(10),
    facilitytype_id NUMBER(10)
        CONSTRAINT NN_Facility_FacilityType NOT NULL,
    name VARCHAR2(255)
        CONSTRAINT NN_Facility_Name NOT NULL
        CONSTRAINT UQ_Facility_Name UNIQUE,
    location VARCHAR2(255)
        CONSTRAINT NN_Facility_Location NOT NULL,
    CONSTRAINT PK_Facility PRIMARY KEY (facility_id)
);

CREATE TABLE Gauge
(
    gauge_id NUMBER(10),
    name VARCHAR2(255)
        CONSTRAINT NN_Gauge_Name NOT NULL
        CONSTRAINT UQ_Gauge_Name UNIQUE,
    width_mm NUMBER(10)
        CONSTRAINT NN_Gauge_Width NOT NULL
        CONSTRAINT UQ_Gauge_Width UNIQUE,
    CONSTRAINT PK_Gauge PRIMARY KEY (gauge_id)
);

CREATE TABLE RailwayLine
(
    line_id NUMBER(10),
    gauge_id NUMBER(10)
        CONSTRAINT NN_RailwayLine_Gauge NOT NULL,
    start_facility_id NUMBER(10)
        CONSTRAINT NN_RailwayLine_StartFacility NOT NULL,
    end_facility_id NUMBER(10)
        CONSTRAINT NN_RailwayLine_EndFacility NOT NULL,
    owner_id NUMBER(10)
        CONSTRAINT NN_RailwayLine_Owner NOT NULL,
    name VARCHAR2(255)
        CONSTRAINT NN_RailwayLine_Name NOT NULL
        CONSTRAINT UQ_RailwayLine_Name UNIQUE,
    CONSTRAINT PK_RailwayLine PRIMARY KEY (line_id)
);

CREATE TABLE LineSegment
(
    line_segment_id NUMBER(10),
    line_id NUMBER(10)
        CONSTRAINT NN_LineSegment_Line NOT NULL,
    segment_order NUMBER(10)
        CONSTRAINT NN_LineSegment_Order NOT NULL
        CONSTRAINT CHK_LineSegment_Order_GT0 CHECK (segment_order > 0),
    length_m NUMBER(10)
        CONSTRAINT NN_LineSegment_Length NOT NULL
        CONSTRAINT CHK_LineSegment_Length_GT0 CHECK (length_m > 0),
    speed_limit NUMBER(10)
        CONSTRAINT NN_LineSegment_SpeedLimit NOT NULL
        CONSTRAINT CHK_LineSegment_SpeedLimit_GT0 CHECK (speed_limit > 0),
    max_weight NUMBER(10)
        CONSTRAINT NN_LineSegment_MaxWeight NOT NULL
        CONSTRAINT CHK_LineSegment_MaxWeight_GT0 CHECK (max_weight > 0),
    track_type VARCHAR2(6)
        CONSTRAINT NN_LineSegment_TrackType NOT NULL
        CONSTRAINT CHK_LineSegment_TrackType CHECK (track_type IN ('single','double')),
    electrified VARCHAR2(3)
        CONSTRAINT NN_LineSegment_Electrified NOT NULL
        CONSTRAINT CHK_LineSegment_Electrified CHECK (electrified IN ('Yes','No')),
    siding_position NUMBER(10)
        CONSTRAINT NN_LineSegment_SidingPos NOT NULL,
    siding_length NUMBER(10)
        CONSTRAINT NN_LineSegment_SidingLen NOT NULL
        CONSTRAINT CHK_LineSegment_SidingLen_GT0 CHECK (siding_length > 0),
    CONSTRAINT PK_LineSegment PRIMARY KEY (line_segment_id),
    CONSTRAINT UQ_LineSegment_Line_Order UNIQUE (line_id, segment_order)
);

CREATE TABLE Model
(
    model_id NUMBER(10),
    name VARCHAR2(255)
        CONSTRAINT NN_Model_Name NOT NULL
        CONSTRAINT UQ_Model_Name UNIQUE,
    maker VARCHAR2(255)
        CONSTRAINT NN_Model_Maker NOT NULL,
    max_speed_kmh NUMBER(5,2)
        CONSTRAINT NN_Model_MaxSpeed NOT NULL
        CONSTRAINT CHK_Model_MaxSpeed_GT0 CHECK (max_speed_kmh > 0),
    number_of_bogies NUMBER(10)
        CONSTRAINT NN_Model_BogiesCount NOT NULL
        CONSTRAINT CHK_Model_NumberOfBogies_GE1 CHECK (number_of_bogies >= 1),
    bogies VARCHAR2(255)
        CONSTRAINT NN_Model_Bogies NOT NULL,
    length_m NUMBER(6,3)
        CONSTRAINT NN_Model_Length NOT NULL
        CONSTRAINT CHK_Model_Length_GT0 CHECK (length_m > 0),
    height_m NUMBER(6,2)
        CONSTRAINT NN_Model_Height NOT NULL
        CONSTRAINT CHK_Model_Height_GT0 CHECK (height_m > 0),
    weight_t NUMBER(7,2)
        CONSTRAINT NN_Model_Weight NOT NULL
        CONSTRAINT CHK_Model_Weight_GT0 CHECK (weight_t > 0),
    width_m NUMBER(6,2)
        CONSTRAINT NN_Model_Width NOT NULL
        CONSTRAINT CHK_Model_Width_GT0 CHECK (width_m > 0),
    CONSTRAINT PK_Model PRIMARY KEY (model_id)
);

CREATE TABLE LocomotiveModel
(
    locomotive_model_id NUMBER(10),
    model_id NUMBER(10)
        CONSTRAINT NN_LocomotiveModel_Model NOT NULL,
    gauge_id NUMBER(10)
        CONSTRAINT NN_LocomotiveModel_Gauge NOT NULL,
    power_kw NUMBER(7,2)
        CONSTRAINT NN_LocomotiveModel_Power NOT NULL
        CONSTRAINT CHK_LocomotiveModel_Power_GT0 CHECK (power_kw > 0),
    traction_kn NUMBER(10)
        CONSTRAINT NN_LocomotiveModel_Traction NOT NULL
        CONSTRAINT CHK_LocomotiveModel_Traction_GT0 CHECK (traction_kn > 0),
    operational_speed_kmh NUMBER(5,2)
        CONSTRAINT NN_LocomotiveModel_OpSpeed NOT NULL
        CONSTRAINT CHK_LocomotiveModel_OpSpeed_GT0 CHECK (operational_speed_kmh > 0),
    CONSTRAINT PK_LocomotiveModel PRIMARY KEY (locomotive_model_id)
);

CREATE TABLE WagonType
(
    wagontype_id NUMBER(10),
    type VARCHAR2(255)
        CONSTRAINT NN_WagonType_Type NOT NULL
        CONSTRAINT UQ_WagonType_Type UNIQUE,
    CONSTRAINT PK_WagonType PRIMARY KEY (wagontype_id)
);

CREATE TABLE WagonCategory
(
    wagoncategory_id NUMBER(10),
    name VARCHAR2(255)
        CONSTRAINT NN_WagonCategory_Name NOT NULL
        CONSTRAINT UQ_WagonCategory_Name UNIQUE,
    CONSTRAINT PK_WagonCategory PRIMARY KEY (wagoncategory_id)
);

CREATE TABLE WagonModel
(
    wagonmodel_id NUMBER(10),
    wagontype_id NUMBER(10)
        CONSTRAINT NN_WagonModel_WagonType NOT NULL,
    gauge_id NUMBER(10)
        CONSTRAINT NN_WagonModel_Gauge NOT NULL,
    model_id NUMBER(10)
        CONSTRAINT NN_WagonModel_Model NOT NULL,
    wagoncategory_id NUMBER(10)
        CONSTRAINT NN_WagonModel_WagonCategory NOT NULL,
    payload_t NUMBER(6,2)
        CONSTRAINT NN_WagonModel_Payload NOT NULL
        CONSTRAINT CHK_WagonModel_Payload_GE0 CHECK (payload_t >= 0),
    volume_m3 NUMBER(7,2)
        CONSTRAINT NN_WagonModel_Volume NOT NULL
        CONSTRAINT CHK_WagonModel_Volume_GT0 CHECK (volume_m3 > 0),
    CONSTRAINT PK_WagonModel PRIMARY KEY (wagonmodel_id)
);

CREATE TABLE Locomotive
(
    loco_id NUMBER(10),
    operator_id NUMBER(10)
        CONSTRAINT NN_Locomotive_Operator NOT NULL,
    locomotive_model_id NUMBER(10)
        CONSTRAINT NN_Locomotive_Model NOT NULL,
    start_facility_id NUMBER(10)
        CONSTRAINT NN_Locomotive_StartFacility NOT NULL,
    name VARCHAR2(255), -- only justifiable nullable
    service_year NUMBER(4)
        CONSTRAINT NN_Locomotive_ServiceYear NOT NULL,
    CONSTRAINT PK_Locomotive PRIMARY KEY (loco_id)
);

CREATE TABLE Diesel_Locomotive
(
    loco_id NUMBER(10),
    fuel_capacity NUMBER(10)
        CONSTRAINT NN_DieselLocomotive_FuelCapacity NOT NULL
        CONSTRAINT CHK_DieselLocomotive_Fuel_GT0 CHECK (fuel_capacity > 0),
    CONSTRAINT PK_Diesel_Locomotive PRIMARY KEY (loco_id)
);

CREATE TABLE Electric_Locomotive
(
    loco_id NUMBER(10),
    voltage_kv NUMBER(6,2)
        CONSTRAINT NN_ElectricLocomotive_Voltage NOT NULL
        CONSTRAINT CHK_ElectricLocomotive_Voltage_GT0 CHECK (voltage_kv > 0),
    frequency_hz VARCHAR2(6)
        CONSTRAINT NN_ElectricLocomotive_Frequency NOT NULL,
    CONSTRAINT PK_Electric_Locomotive PRIMARY KEY (loco_id)
);

CREATE TABLE Wagon
(
    wagon_id NUMBER(10),
    wagonmodel_id NUMBER(10)
        CONSTRAINT NN_Wagon_Model NOT NULL,
    operator_id NUMBER(10)
        CONSTRAINT NN_Wagon_Operator NOT NULL,
    start_facility_id NUMBER(10)
        CONSTRAINT NN_Wagon_StartFacility NOT NULL,
    current_facility_id NUMBER(10)
        CONSTRAINT NN_Wagon_CurrentFacility NOT NULL,
    service_year NUMBER(4)
        CONSTRAINT NN_Wagon_ServiceYear NOT NULL,
    CONSTRAINT PK_Wagon PRIMARY KEY (wagon_id)
);

CREATE TABLE Train
(
    train_id NUMBER(10),
    operator_id NUMBER(10)
        CONSTRAINT NN_Train_Operator NOT NULL,
    name VARCHAR2(10)
        CONSTRAINT NN_Train_Name NOT NULL,
    max_length_m NUMBER(10)
        CONSTRAINT NN_Train_MaxLength NOT NULL
        CONSTRAINT CHK_Train_MaxLength_GT0 CHECK (max_length_m > 0),
    CONSTRAINT PK_Train PRIMARY KEY (train_id)
);

CREATE TABLE RouteEndpoint
(
    route_endpoint_id NUMBER(10),
    facility_id NUMBER(10)
        CONSTRAINT NN_RouteEndpoint_Facility NOT NULL,
    train_id NUMBER(10)
        CONSTRAINT NN_RouteEndpoint_Train NOT NULL,
    endpoint_order NUMBER(10)
        CONSTRAINT NN_RouteEndpoint_Order NOT NULL
        CONSTRAINT CHK_RouteEndpoint_Order_GT0 CHECK (endpoint_order > 0),
    CONSTRAINT PK_RouteEndpoint PRIMARY KEY (route_endpoint_id),
    CONSTRAINT UQ_RouteEndpoint_Order UNIQUE (train_id, endpoint_order)
);

CREATE TABLE TrainLocomotive
(
    train_id NUMBER(10)
        CONSTRAINT NN_TrainLocomotive_Train NOT NULL,
    loco_id NUMBER(10)
        CONSTRAINT NN_TrainLocomotive_Loco NOT NULL
        CONSTRAINT UQ_TrainLocomotive_Loco UNIQUE,
    CONSTRAINT PK_TrainLocomotive PRIMARY KEY (train_id, loco_id)
);

CREATE TABLE TrainWagon
(
    train_id NUMBER(10)
        CONSTRAINT NN_TrainWagon_Train NOT NULL,
    wagon_id NUMBER(10)
        CONSTRAINT NN_TrainWagon_Wagon NOT NULL
        CONSTRAINT UQ_TrainWagon_Wagon UNIQUE,
    CONSTRAINT PK_TrainWagon PRIMARY KEY (train_id, wagon_id)
);

CREATE TABLE Freight
(
    freight_id NUMBER(10),
    origin_facility_id NUMBER(10)
        CONSTRAINT NN_Freight_OriginFacility NOT NULL,
    destination_facility_id NUMBER(10)
        CONSTRAINT NN_Freight_DestinationFacility NOT NULL,
    train_id NUMBER(10), -- may be assigned later
    depart_time DATE
        CONSTRAINT NN_Freight_DepartTime NOT NULL,
    arrival_time DATE
        CONSTRAINT NN_Freight_ArrivalTime NOT NULL,
    CONSTRAINT PK_Freight PRIMARY KEY (freight_id),
    CONSTRAINT CHK_Freight_Dates CHECK (arrival_time >= depart_time)
);

CREATE TABLE FreightWagon
(
    freight_id NUMBER(10)
        CONSTRAINT NN_FreightWagon_Freight NOT NULL,
    wagon_id NUMBER(10)
        CONSTRAINT NN_FreightWagon_Wagon NOT NULL
        CONSTRAINT UQ_FreightWagon_Wagon UNIQUE,
    CONSTRAINT PK_FreightWagon PRIMARY KEY (freight_id, wagon_id)
);

CREATE TABLE Cargo
(
    cargo_id NUMBER(10),
    wagon_id NUMBER(10)
        CONSTRAINT NN_Cargo_Wagon NOT NULL,
    description VARCHAR2(255)
        CONSTRAINT NN_Cargo_Description NOT NULL,
    type VARCHAR2(255)
        CONSTRAINT NN_Cargo_Type NOT NULL,
    quantity NUMBER(10)
        CONSTRAINT NN_Cargo_Quantity NOT NULL
        CONSTRAINT CHK_Cargo_Quantity_GT0 CHECK (quantity > 0),
    volume NUMBER(10,2)
        CONSTRAINT NN_Cargo_Volume NOT NULL
        CONSTRAINT CHK_Cargo_Volume_GT0 CHECK (volume > 0),
    weight NUMBER(10,2)
        CONSTRAINT NN_Cargo_Weight NOT NULL
        CONSTRAINT CHK_Cargo_Weight_GT0 CHECK (weight > 0),
    expiry_date DATE
        CONSTRAINT NN_Cargo_ExpiryDate NOT NULL,
    CONSTRAINT PK_Cargo PRIMARY KEY (cargo_id)
);

CREATE TABLE BuildingType
(
    buildingtype_id NUMBER(10),
    name VARCHAR2(255)
        CONSTRAINT NN_BuildingType_Name NOT NULL
        CONSTRAINT UQ_BuildingType_Name UNIQUE,
    CONSTRAINT PK_BuildingType PRIMARY KEY (buildingtype_id)
);

CREATE TABLE FacilityBuilding
(
    facility_id NUMBER(10)
        CONSTRAINT NN_FacilityBuilding_Facility NOT NULL,
    buildingtype_id NUMBER(10)
        CONSTRAINT NN_FacilityBuilding_BuildingType NOT NULL,
    CONSTRAINT PK_FacilityBuilding PRIMARY KEY (facility_id, buildingtype_id)
);

CREATE TABLE TrainMovement
(
    train_id NUMBER(10)
        CONSTRAINT NN_TrainMovement_Train NOT NULL,
    facility_id NUMBER(10)
        CONSTRAINT NN_TrainMovement_Facility NOT NULL,
    arrival_time DATE
        CONSTRAINT NN_TrainMovement_ArrivalTime NOT NULL,
    departure_time DATE,
    CONSTRAINT PK_TrainMovement PRIMARY KEY (train_id, facility_id, arrival_time),
    CONSTRAINT CHK_TrainMovement_Times CHECK (departure_time IS NULL OR departure_time >= arrival_time)
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
    ADD CONSTRAINT FK_RailwayLine_Gauge
        FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RailwayLine_StartFacility
        FOREIGN KEY (start_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RailwayLine_EndFacility
        FOREIGN KEY (end_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RailwayLine_Owner
        FOREIGN KEY (owner_id) REFERENCES Owner (owner_id);

ALTER TABLE LineSegment
    ADD CONSTRAINT FK_LineSegment_RailwayLine
        FOREIGN KEY (line_id) REFERENCES RailwayLine (line_id);

ALTER TABLE LocomotiveModel
    ADD CONSTRAINT FK_LocomotiveModel_Model
        FOREIGN KEY (model_id) REFERENCES Model (model_id);

ALTER TABLE LocomotiveModel
    ADD CONSTRAINT FK_LocomotiveModel_Gauge
        FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_WagonType
        FOREIGN KEY (wagontype_id) REFERENCES WagonType (wagontype_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_Gauge
        FOREIGN KEY (gauge_id) REFERENCES Gauge (gauge_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_Model
        FOREIGN KEY (model_id) REFERENCES Model (model_id);

ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WagonModel_WagonCategory
        FOREIGN KEY (wagoncategory_id) REFERENCES WagonCategory (wagoncategory_id);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Locomotive_Operator
        FOREIGN KEY (operator_id) REFERENCES Operator (operator_id);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Locomotive_LocomotiveModel
        FOREIGN KEY (locomotive_model_id) REFERENCES LocomotiveModel (locomotive_model_id);

ALTER TABLE Locomotive
    ADD CONSTRAINT FK_Locomotive_StartFacility
        FOREIGN KEY (start_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE Diesel_Locomotive
    ADD CONSTRAINT FK_DieselLocomotive_Locomotive
        FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE Electric_Locomotive
    ADD CONSTRAINT FK_ElectricLocomotive_Locomotive
        FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_WagonModel
        FOREIGN KEY (wagonmodel_id) REFERENCES WagonModel (wagonmodel_id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_Operator
        FOREIGN KEY (operator_id) REFERENCES Operator (operator_id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_StartFacility
        FOREIGN KEY (start_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE Wagon
    ADD CONSTRAINT FK_Wagon_CurrentFacility
        FOREIGN KEY (current_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE Train
    ADD CONSTRAINT FK_Train_Operator
        FOREIGN KEY (operator_id) REFERENCES Operator (operator_id);

ALTER TABLE RouteEndpoint
    ADD CONSTRAINT FK_RouteEndpoint_Facility
        FOREIGN KEY (facility_id) REFERENCES Facility (facility_id);

ALTER TABLE RouteEndpoint
    ADD CONSTRAINT FK_RouteEndpoint_Train
        FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE TrainLocomotive
    ADD CONSTRAINT FK_TrainLocomotive_Train
        FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE TrainLocomotive
    ADD CONSTRAINT FK_TrainLocomotive_Locomotive
        FOREIGN KEY (loco_id) REFERENCES Locomotive (loco_id);

ALTER TABLE TrainWagon
    ADD CONSTRAINT FK_TrainWagon_Train
        FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE TrainWagon
    ADD CONSTRAINT FK_TrainWagon_Wagon
        FOREIGN KEY (wagon_id) REFERENCES Wagon (wagon_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_OriginFacility
        FOREIGN KEY (origin_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_DestinationFacility
        FOREIGN KEY (destination_facility_id) REFERENCES Facility (facility_id);

ALTER TABLE Freight
    ADD CONSTRAINT FK_Freight_Train
        FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE FreightWagon
    ADD CONSTRAINT FK_FreightWagon_Freight
        FOREIGN KEY (freight_id) REFERENCES Freight (freight_id);

ALTER TABLE FreightWagon
    ADD CONSTRAINT FK_FreightWagon_Wagon
        FOREIGN KEY (wagon_id) REFERENCES Wagon (wagon_id);

ALTER TABLE Cargo
    ADD CONSTRAINT FK_Cargo_Wagon
        FOREIGN KEY (wagon_id) REFERENCES Wagon (wagon_id);

ALTER TABLE FacilityBuilding
    ADD CONSTRAINT FK_FacilityBuilding_Facility
        FOREIGN KEY (facility_id) REFERENCES Facility (facility_id);

ALTER TABLE FacilityBuilding
    ADD CONSTRAINT FK_FacilityBuilding_BuildingType
        FOREIGN KEY (buildingtype_id) REFERENCES BuildingType (buildingtype_id);

ALTER TABLE TrainMovement
    ADD CONSTRAINT FK_TrainMovement_Train
        FOREIGN KEY (train_id) REFERENCES Train (train_id);

ALTER TABLE TrainMovement
    ADD CONSTRAINT FK_TrainMovement_Facility
        FOREIGN KEY (facility_id) REFERENCES Facility (facility_id);
