-- ============================================================
-- USLP10 - Extra Test Data (Train Assembly & Scheduling)
-- Keeps professor dataset intact. Adds ONLY test data for USLP10.
-- ============================================================

-- ============================================================
-- SECTION 1: LOCOMOTIVES FOR TESTING
-- (Using IDs not in professor's dataset)
-- ============================================================

-- Add some locomotives that are "available" at different facilities
INSERT INTO Locomotive (loco_id, operator_id, locomotive_model_id, start_facility_id, name, service_year)
VALUES (9001, 2, 3, 50, 'USLP10-Test-1', 2020);

INSERT INTO Locomotive (loco_id, operator_id, locomotive_model_id, start_facility_id, name, service_year)
VALUES (9002, 2, 3, 5, 'USLP10-Test-2', 2021);

INSERT INTO Locomotive (loco_id, operator_id, locomotive_model_id, start_facility_id, name, service_year)
VALUES (9003, 2, 1, 11, 'USLP10-Test-3', 2019);

INSERT INTO Diesel_Locomotive (loco_id, fuel_capacity) VALUES (9001, 6700);
INSERT INTO Diesel_Locomotive (loco_id, fuel_capacity) VALUES (9002, 6700);
INSERT INTO Electric_Locomotive (loco_id, voltage_kv, frequency_hz) VALUES (9003, 25, '50 Hz');


-- ============================================================
-- SECTION 2: WAGONS FOR TESTING
-- (Using IDs not in professor's dataset)
-- ============================================================

-- Wagons at Leixões (50) - available for assembly
INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (9001001, 1104, 2, 50, 50, 2018);

INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (9001002, 1104, 2, 50, 50, 2018);

INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (9001003, 1245, 2, 50, 50, 2019);

INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (9001004, 1245, 2, 50, 50, 2019);

-- Wagons at Porto Campanhã (5) - available for assembly
INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (9002001, 1104, 2, 5, 5, 2020);

INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (9002002, 1278, 2, 5, 5, 2020);


-- ============================================================
-- SECTION 3: TRAINS FOR TESTING (USLP09 - Assembly)
-- ============================================================

-- Train 9100: Will be assembled with loco 9001 + wagons from Leixões
INSERT INTO Train (train_id, operator_id, name, max_length_m)
VALUES (9100, 2, 'T9100', 100);

-- Train 9200: Will be assembled with loco 9002 + wagons from Porto Campanhã
INSERT INTO Train (train_id, operator_id, name, max_length_m)
VALUES (9200, 2, 'T9200', 50);

-- Train 9300: Already assembled (for testing dispatch only)
INSERT INTO Train (train_id, operator_id, name, max_length_m)
VALUES (9300, 2, 'T9300', 150);


-- ============================================================
-- SECTION 4: PRE-ASSEMBLED TRAIN (for dispatch testing)
-- ============================================================

-- Train 9300 is already assembled with locomotive and wagons
INSERT INTO TrainLocomotive (train_id, loco_id) VALUES (9300, 9003);

INSERT INTO TrainWagon (train_id, wagon_id) VALUES (9300, 9001001);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (9300, 9001002);


-- ============================================================
-- SECTION 5: ROUTE FOR PRE-ASSEMBLED TRAIN
-- ============================================================

-- Route for Train 9300: Valença(11) -> Caminha(21) -> Viana(17) -> Darque(12)
INSERT INTO RouteEndpoint (route_endpoint_id, facility_id, train_id, endpoint_order)
VALUES (9001, 11, 9300, 1);

INSERT INTO RouteEndpoint (route_endpoint_id, facility_id, train_id, endpoint_order)
VALUES (9002, 21, 9300, 2);

INSERT INTO RouteEndpoint (route_endpoint_id, facility_id, train_id, endpoint_order)
VALUES (9003, 17, 9300, 3);

INSERT INTO RouteEndpoint (route_endpoint_id, facility_id, train_id, endpoint_order)
VALUES (9004, 12, 9300, 4);


-- ============================================================
-- SECTION 6: FREIGHT FOR TRAIN 9300 (with cargo)
-- ============================================================

INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id, train_id, depart_time, arrival_time)
VALUES (9301, 11, 17, 9300, TO_DATE('2026-01-05 08:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2026-01-05 12:00', 'YYYY-MM-DD HH24:MI'));

INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id, train_id, depart_time, arrival_time)
VALUES (9302, 17, 12, 9300, TO_DATE('2026-01-05 13:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2026-01-05 15:00', 'YYYY-MM-DD HH24:MI'));

-- Link wagons to freights
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (9301, 9001001);
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (9302, 9001002);

-- Cargo for testing
INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99401, 9001001, 'Test cargo - Electronics', 'GENERAL', 20, 40.0, 15000, DATE '2026-06-01');

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99402, 9001002, 'Test cargo - Machinery', 'GENERAL', 10, 50.0, 25000, DATE '2026-07-01');


-- ============================================================
-- SECTION 7: PENDING FREIGHTS (for route planning)
-- ============================================================

-- Pending freight: Leixões(50) -> Nine(20)
INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id, train_id, depart_time, arrival_time)
VALUES (9401, 50, 20, NULL, TO_DATE('2026-01-10 09:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2026-01-10 14:00', 'YYYY-MM-DD HH24:MI'));

INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (9401, 9001003);

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99501, 9001003, 'Test pending cargo', 'GENERAL', 30, 35.0, 18000, DATE '2026-08-01');

-- Pending freight: Porto Campanhã(5) -> Braga(30)
INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id, train_id, depart_time, arrival_time)
VALUES (9402, 5, 30, NULL, TO_DATE('2026-01-11 10:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2026-01-11 13:00', 'YYYY-MM-DD HH24:MI'));

INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (9402, 9002001);

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99502, 9002001, 'Test pending cargo 2', 'GENERAL', 25, 30.0, 16000, DATE '2026-09-01');


COMMIT;

-- ============================================================
-- SANITY CHECKS
-- ============================================================

-- Check locomotives available for assembly
-- SELECT loco_id, name, start_facility_id FROM Locomotive WHERE loco_id IN (9001, 9002, 9003);

-- Check wagons available for assembly
-- SELECT wagon_id, current_facility_id FROM Wagon WHERE wagon_id BETWEEN 9001001 AND 9002002;

-- Check pre-assembled train
-- SELECT t.train_id, t.name, tl.loco_id, tw.wagon_id
-- FROM Train t
-- LEFT JOIN TrainLocomotive tl ON t.train_id = tl.train_id
-- LEFT JOIN TrainWagon tw ON t.train_id = tw.train_id
-- WHERE t.train_id = 9300;

-- Check pending freights
-- SELECT freight_id, origin_facility_id, destination_facility_id, train_id
-- FROM Freight
-- WHERE freight_id IN (9401, 9402);