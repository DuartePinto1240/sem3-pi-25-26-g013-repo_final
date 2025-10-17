CREATE TABLE RailwayLine
(
    line_id          number(10) GENERATED AS IDENTITY,
    gauge_id         number(10) NOT NULL,
    owner_id         number(10) NOT NULL,
    start_station_id number(10) NOT NULL,
    end_station_id   number(10) NOT NULL,
    name             nvarchar2(255) NOT NULL,
    PRIMARY KEY (line_id)
);
CREATE TABLE LineSegment
(
    line_segment_id NUMBER(10) GENERATED AS IDENTITY,
    line_id         NUMBER(10) NOT NULL,
    length_m        NUMBER(10) NOT NULL,
    speed_limit     NUMBER(10) NOT NULL,
    max_weight      NUMBER(10) NOT NULL,
    segment_order   NUMBER(5)  NOT NULL,
    track_type      VARCHAR2(6) NOT NULL,
    electrified     VARCHAR2(3) NOT NULL,
    PRIMARY KEY (line_segment_id),
    CONSTRAINT CHK_is_electrified    CHECK (electrified IN ('Yes','No')),
    CONSTRAINT CHK_track_type_valid   CHECK (track_type IN ('single','double')),
    CONSTRAINT CHK_SEGMENT_ORDER_GT0  CHECK (segment_order > 0),
    CONSTRAINT CHK_LENGTH_GT0         CHECK (length_m > 0),
    CONSTRAINT CHK_MAXWEIGHT_GT0      CHECK (max_weight > 0)
);

CREATE TABLE Locomotive
(
    loco_id        number(10) GENERATED AS IDENTITY,
    flueltype_id    number(10) NOT NULL,
    gauge_id       number(10) NOT NULL,
    operator_id    number(10) NOT NULL,
    name           varchar2(10) NOT NULL,
    model          nvarchar2(255) NOT NULL,
    fluel_capacity number(10),
    power_kn    number(10) NOT NULL,
    weight         number(10) NOT NULL,
    make           varchar2(255) NOT NULL,
    service_year   number(4) NOT NULL,
    voltage_kv     varchar2(10) NOT NULL,
    frequency_hz   varchar2(10) NOT NULL,
    PRIMARY KEY (loco_id)
);
CREATE TABLE Wagon
(
    wagon_id      number(10) GENERATED AS IDENTITY,
    operator_id   number(10) NOT NULL,
    wagonmodel_id number(10) NOT NULL,
    service_year  number(4) NOT NULL,
    PRIMARY KEY (wagon_id)
);
CREATE TABLE Station
(
    station_id  number(10) GENERATED AS IDENTITY,
    facility_id number(10) NOT NULL,
    PRIMARY KEY (station_id)
);
CREATE TABLE Freight
(
    freight_id             NUMBER(10) GENERATED AS IDENTITY,
    origin_station_id      NUMBER(10) NOT NULL,
    destination_station_id NUMBER(10) NOT NULL,
    train_id               NUMBER(10) NOT NULL,
    departure_time         DATE NOT NULL,
    arrival_time           DATE NOT NULL,
    PRIMARY KEY (freight_id),
    CONSTRAINT CHK_FREIGHT_DATES CHECK (arrival_time >= departure_time)
);

CREATE TABLE Terminal
(
    terminal_id number(10) GENERATED AS IDENTITY,
    facility_id number(10) NOT NULL,
    capacity    number(10) NOT NULL,
    PRIMARY KEY (terminal_id),
    CONSTRAINT CHK_Capacity_Bigger_Than_Zero CHECK (capacity > 0)
);
CREATE TABLE Operator
(
    operator_id number(10) GENERATED AS IDENTITY,
    name        varchar2(255) NOT NULL,
    country     nvarchar2(255) NOT NULL,
    vat_number  varchar2(255) NOT NULL,
    short_name  varchar2(255) NOT NULL,
    PRIMARY KEY (operator_id)
);
CREATE TABLE Facility
(
    facility_id number(10) GENERATED AS IDENTITY,
    address_id  number(10) NOT NULL,
    type        nvarchar2(255) NOT NULL,
    name        nvarchar2(255) NOT NULL,
    PRIMARY KEY (facility_id)
);
CREATE TABLE FreightYard
(
    freightyard_id number(10) GENERATED AS IDENTITY,
    facility_id    number(10) NOT NULL,
    PRIMARY KEY (freightyard_id)
);
CREATE TABLE Train
(
    train_id NUMBER(10) GENERATED AS IDENTITY,
    name     VARCHAR2(255) NOT NULL,
    PRIMARY KEY (train_id)
);
CREATE TABLE WagonType
(
    wagontype_id number(10) GENERATED AS IDENTITY,
    type         varchar2(255) NOT NULL UNIQUE,
    PRIMARY KEY (wagontype_id)
);
CREATE TABLE FluelType
(
    flueltype_id number(10) GENERATED AS IDENTITY,
    type        varchar2(8) NOT NULL,
    PRIMARY KEY (flueltype_id),
    CONSTRAINT "CHK_flueltype_valid " CHECK (type IN ('Diesel', 'Electric') )
);
CREATE TABLE Gauge
(
    gauge_id    number(10) GENERATED AS IDENTITY,
    width_mm    number(10) NOT NULL UNIQUE,
    description nvarchar2(255) NOT NULL,
    PRIMARY KEY (gauge_id)
);
CREATE TABLE Cargo
(
    cargo_id    number(10) GENERATED AS IDENTITY,
    wagon_id    number(10) NOT NULL,
    description nvarchar2(255) NOT NULL,
    type        nvarchar2(255) NOT NULL,
    quantity    number(10) NOT NULL,
    volume      number(10) NOT NULL,
    weight      number(10) NOT NULL,
    expiry_date date NOT NULL,
    PRIMARY KEY (cargo_id)
);
CREATE TABLE Address
(
    address_id    number(10) GENERATED AS IDENTITY,
    postalCode_id number(10) NOT NULL,
    doorNumber    number(10) NOT NULL,
    PRIMARY KEY (address_id)
);
CREATE TABLE PostalCode
(
    postalCode_id number(10) GENERATED AS IDENTITY,
    street        nvarchar2(255) NOT NULL,
    city          nvarchar2(255) NOT NULL,
    country       nvarchar2(255) NOT NULL,
    PRIMARY KEY (postalCode_id)
);
CREATE TABLE TrainLocomotive
(
    train_id number(10) NOT NULL,
    loco_id  number(10) NOT NULL,
    PRIMARY KEY (train_id, loco_id)
);
CREATE TABLE TrainWagon
(
    train_id number(10) NOT NULL,
    wagon_id number(10) NOT NULL,
    PRIMARY KEY (train_id, wagon_id)
);
CREATE TABLE Owner
(
    owner_id   number(10) GENERATED AS IDENTITY,
    name       varchar2(10) NOT NULL,
    short_name varchar2(10) NOT NULL,
    vat_number varchar2(255) NOT NULL,
    PRIMARY KEY (owner_id)
);
CREATE TABLE WagonModel
(
    wagonmodel_id        number(10) GENERATED AS IDENTITY,
    wagontype_id         number(10) NOT NULL,
    gauge_id             number(10) NOT NULL,
    manufacturer         varchar2(255) NOT NULL,
    name                 varchar2(255) NOT NULL UNIQUE,
    axles                number(2) NOT NULL,
    decks                number(2) NOT NULL,
    length_m             number(10) NOT NULL,
    tare_t               number(10) NOT NULL,
    payload_t            number(10) NOT NULL,
    max_speed_loaded_kmh number(10) NOT NULL,
    max_speed_empty_kmh  number(10) NOT NULL,
    PRIMARY KEY (wagonmodel_id)
);

ALTER TABLE LineSegment
    ADD CONSTRAINT FK_LINESEGMENT_LINE       FOREIGN KEY (line_id) REFERENCES RailwayLine(line_id);
ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RAILWAYLINE_OWNER      FOREIGN KEY (owner_id) REFERENCES Owner(owner_id);
ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RAILWAYLINE_GAUGE      FOREIGN KEY (gauge_id) REFERENCES Gauge(gauge_id);
ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RAILWAYLINE_START_ST   FOREIGN KEY (start_station_id) REFERENCES Station(station_id);
ALTER TABLE RailwayLine
    ADD CONSTRAINT FK_RAILWAYLINE_END_ST     FOREIGN KEY (end_station_id) REFERENCES Station(station_id);
ALTER TABLE Station
    ADD CONSTRAINT FK_STATION_FACILITY       FOREIGN KEY (facility_id) REFERENCES Facility(facility_id);
ALTER TABLE FreightYard
    ADD CONSTRAINT FK_FREIGHTYARD_FACILITY   FOREIGN KEY (facility_id) REFERENCES Facility(facility_id);
ALTER TABLE Terminal
    ADD CONSTRAINT FK_TERMINAL_FACILITY      FOREIGN KEY (facility_id) REFERENCES Facility(facility_id);
ALTER TABLE Facility
    ADD CONSTRAINT FK_FACILITY_ADDRESS       FOREIGN KEY (address_id) REFERENCES Address(address_id);
ALTER TABLE Address
    ADD CONSTRAINT FK_ADDRESS_POSTALCODE     FOREIGN KEY (postalCode_id) REFERENCES PostalCode(postalCode_id);
ALTER TABLE Freight
    ADD CONSTRAINT FK_FREIGHT_ORIGIN         FOREIGN KEY (origin_station_id) REFERENCES Station(station_id);
ALTER TABLE Freight
    ADD CONSTRAINT FK_FREIGHT_DESTINATION    FOREIGN KEY (destination_station_id) REFERENCES Station(station_id);
ALTER TABLE Freight
    ADD CONSTRAINT FK_FREIGHT_TRAIN          FOREIGN KEY (train_id) REFERENCES Train(train_id);
ALTER TABLE Locomotive
    ADD CONSTRAINT FK_LOCO_FlUELTYPE          FOREIGN KEY (flueltype_id) REFERENCES FluelType(flueltype_id);
ALTER TABLE Locomotive
    ADD CONSTRAINT FK_LOCO_GAUGE             FOREIGN KEY (gauge_id) REFERENCES Gauge(gauge_id);
ALTER TABLE Locomotive
    ADD CONSTRAINT FK_LOCO_OPERATOR          FOREIGN KEY (operator_id) REFERENCES Operator(operator_id);
ALTER TABLE Wagon
    ADD CONSTRAINT FK_WAGON_OPERATOR         FOREIGN KEY (operator_id) REFERENCES Operator(operator_id);
ALTER TABLE Wagon
    ADD CONSTRAINT FK_WAGON_MODEL            FOREIGN KEY (wagonmodel_id) REFERENCES WagonModel(wagonmodel_id);
ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WAGONMODEL_TYPE        FOREIGN KEY (wagontype_id) REFERENCES WagonType(wagontype_id);
ALTER TABLE WagonModel
    ADD CONSTRAINT FK_WAGONMODEL_GAUGE       FOREIGN KEY (gauge_id) REFERENCES Gauge(gauge_id);
ALTER TABLE TrainLocomotive
    ADD CONSTRAINT FK_TL_TRAIN               FOREIGN KEY (train_id) REFERENCES Train(train_id);
ALTER TABLE TrainLocomotive
    ADD CONSTRAINT FK_TL_LOCO                FOREIGN KEY (loco_id) REFERENCES Locomotive(loco_id);
ALTER TABLE TrainWagon
    ADD CONSTRAINT FK_TW_TRAIN               FOREIGN KEY (train_id) REFERENCES Train(train_id);
ALTER TABLE TrainWagon
    ADD CONSTRAINT FK_TW_WAGON               FOREIGN KEY (wagon_id) REFERENCES Wagon(wagon_id);
ALTER TABLE Cargo
    ADD CONSTRAINT FK_CARGO_WAGON            FOREIGN KEY (wagon_id) REFERENCES Wagon(wagon_id);
