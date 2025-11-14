-- ========================================
-- SCRIPT FINAL - 100% FUNCIONAL
-- CORRIGIDO: ORA-01747, ORA-00933, ORA-02291
-- ========================================

-- 1. GAUGE
INSERT INTO Gauge (width_mm) VALUES (1668);
INSERT INTO Gauge (width_mm) VALUES (1435);

-- 2. COMPANY
INSERT INTO Company (name, short_name, vat_number) VALUES ('Infraestruturas de Portugal, SA', 'IP', 'PT503933813');
INSERT INTO Company (name, short_name, vat_number) VALUES ('Medway - Operador Ferroviário de Mercadorias, SA', 'Medway', 'PT509017800');
INSERT INTO Company (name, short_name, vat_number) VALUES ('Captrain Portugal SA', 'Captrain', 'PT507832388');

-- 3. OWNER e OPERATOR
INSERT INTO Owner (owner_id) VALUES (1);
INSERT INTO Operator (operator_id, country) VALUES (2, 'Portugal');
INSERT INTO Operator (operator_id, country) VALUES (3, 'Portugal');

-- 4. FACILITY TYPE
INSERT INTO FacilityType (type) VALUES ('Station');
INSERT INTO FacilityType (type) VALUES ('Terminal');
INSERT INTO FacilityType (type) VALUES ('Port');

-- 5. FACILITIES (27)
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'São Romão', 'São Romão');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Tamel', 'Tamel');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Senhora das Dores', 'Senhora das Dores');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Lousado', 'Lousado');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Porto Campanhã', 'Porto');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Leandro', 'Leandro');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Porto São Bento', 'Porto');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Barcelos', 'Barcelos');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Vila Nova da Cerveira', 'Vila Nova da Cerveira');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Midões', 'Midões');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Valença', 'Valença');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Darque', 'Darque');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Contumil', 'Porto');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Ermesinde', 'Ermesinde');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'São Frutuoso', 'São Frutuoso');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'São Pedro da Torre', 'São Pedro da Torre');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Viana do Castelo', 'Viana do Castelo');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Famalicão', 'Famalicão');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Barroselas', 'Barroselas');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Nine', 'Nine');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Caminha', 'Caminha');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Carvalha', 'Carvalha');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Carreço', 'Carreço');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'São Gemil', 'São Gemil');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'São Mamede de Infesta', 'São Mamede de Infesta');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (1, 'Leça do Balio', 'Leça do Balio');
INSERT INTO Facility (facilitytype_id, name, location) VALUES (3, 'Leixões', 'Matosinhos');

-- 6. RAILWAY LINES (13)
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 7, 5, 'Ramal São Bento - Campanhã', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 5, 13, 'Ramal Campanhã - Contumil', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 13, 20, 'Ramal Contumil - Nine', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 20, 8, 'Ramal Nine - Barcelos', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 8, 12, 'Ramal Barcelos - Darque', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 12, 17, 'Ramal Darque - Viana', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 17, 21, 'Ramal Viana - Caminha', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 21, 16, 'Ramal Caminha - Torre', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 16, 11, 'Ramal Torre - Valença', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 13, 24, 'Ramal Contumil - São Gemil', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 24, 25, 'Ramal São Gemil - São Mamede de Infesta', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 25, 26, 'Ramal São Mamede de Infesta - Leça do Balio', 1);
INSERT INTO RailwayLine (gauge_id, start_station_id, end_station_id, name, owner_id) VALUES (1, 26, 27, 'Ramal Leça do Balio - Leixões', 1);

-- 7. LINE SEGMENTS (22) - CORRIGIDO ORA-01747
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (1, 1, 2618, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (2, 1, 2443, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (3, 1, 26560, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (3, 2, 10000, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (4, 1, 5286, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (4, 2, 6000, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (5, 1, 10387, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (5, 2, 12000, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (5, 3, 3100, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (6, 1, 4890, 6400, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (7, 1, 6000, 8000, 'single', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified, siding_position, siding_length) VALUES (7, 2, 5000, 8000, 'single', 'Yes', 2000, 864);
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (7, 3, 12000, 8000, 'single', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified, siding_position, siding_length) VALUES (8, 1, 20829, 8000, 'single', 'Yes', 11000, 266);
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (9, 1, 4264, 8000, 'single', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (10, 1, 3883, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (11, 1, 1174, 8400, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (11, 2, 2534, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (12, 1, 1566, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (12, 2, 1453, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (13, 1, 3597, 8100, 'double', 'Yes');
INSERT INTO LineSegment (line_id, segment_order, length_m, max_weight, track_type, electrified) VALUES (13, 2, 4334, 8000, 'double', 'Yes');

-- 8. WAGON TYPES e CATEGORIES
INSERT INTO WagonType (type) VALUES ('Cereal wagon');
INSERT INTO WagonType (type) VALUES ('Covered wagon with sliding door');
INSERT INTO WagonType (type) VALUES ('Container wagon max 40 HC');
INSERT INTO WagonType (type) VALUES ('Biodiesel wagon');
INSERT INTO WagonType (type) VALUES ('Wood wagon');

INSERT INTO WagonCategory (code, name) VALUES ('CER', 'Cereal wagon');
INSERT INTO WagonCategory (code, name) VALUES ('COV', 'Covered wagon');
INSERT INTO WagonCategory (code, name) VALUES ('CNT', 'Container wagon');
INSERT INTO WagonCategory (code, name) VALUES ('BIO', 'Biodiesel wagon');
INSERT INTO WagonCategory (code, name) VALUES ('WOD', 'Wood wagon');

-- 9. MODELS (8)
INSERT INTO Model (name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES ('Eurosprinter', 'Siemens', 220, 2, 'Bo-Bo', 19.2, 4.375, 87, 3);
INSERT INTO Model (name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES ('CP 1900', 'Sorefame - Alsthom', 100, 2, 'Co-Co', 19.084, 4.31, 117, 3.062);
INSERT INTO Model (name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES ('E4000', 'Stadler', 120, 3, 'Co-Co', 23.02, 4.264, 124, 3);
INSERT INTO Model (name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES ('Tadgs 32 94 082 3', 'Metalsines', 120, 2, 'Duplo', 17.24, 4.27, 24, 3.072);
INSERT INTO Model (name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES ('Tdgs 41 94 074 1', 'Equimetal', 100, 2, 'Duplo', 9.64, 4.17, 13.8, 3.12);
INSERT INTO Model (name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES ('Gabs 81 94 181 1', 'Sepsa Cometna', 100, 2, 'Duplo', 21.7, 4.17, 29.8, 3.18);
INSERT INTO Model (name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES ('Regmms 32 94 356 3', 'Metalsines', 120, 2, 'Duplo', 14.04, 2.535, 21.2, 3.104);
INSERT INTO Model (name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES ('Kbs 41 94 333', 'Simmering', 100, 2, 'Simples', 14.02, 3.3, 14.2, 2.842);

-- 10. LOCOMOTIVE MODELS
INSERT INTO LocomotiveModel (model_id, power_kw, traction_kn, operational_speed_kmh) VALUES (1, 5600, 300, 70);
INSERT INTO LocomotiveModel (model_id, power_kw, traction_kn, operational_speed_kmh) VALUES (2, 1623, 396, 42.5);
INSERT INTO LocomotiveModel (model_id, power_kw, traction_kn, operational_speed_kmh) VALUES (3, 3178, 400, 100);

-- 11. WAGON MODELS (5)
INSERT INTO WagonModel (wagontype_id, gauge_id, model_id, wagoncategory_id, payload_t, volume_m3)
VALUES (1, 1, 4, 1, 56, 75);
INSERT INTO WagonModel (wagontype_id, gauge_id, model_id, wagoncategory_id, payload_t, volume_m3)
VALUES (1, 1, 5, 1, 26.2, 38);
INSERT INTO WagonModel (wagontype_id, gauge_id, model_id, wagoncategory_id, payload_t, volume_m3)
VALUES (2, 1, 6, 2, 50.2, 110);
INSERT INTO WagonModel (wagontype_id, gauge_id, model_id, wagoncategory_id, payload_t, volume_m3)
VALUES (3, 1, 7, 3, 60.6, 76.3);
INSERT INTO WagonModel (wagontype_id, gauge_id, model_id, wagoncategory_id, payload_t, volume_m3)
VALUES (5, 1, 8, 5, 25.8, 62.7);

-- 12. LOCOMOTIVES (8) - ANTES DE DIESEL/ELECTRIC
INSERT INTO Locomotive (operator_id, locomotive_model_id, name, service_year) VALUES (2, 1, 'Inês', 1995);
INSERT INTO Locomotive (operator_id, locomotive_model_id, name, service_year) VALUES (2, 1, 'Paz', 1995);
INSERT INTO Locomotive (operator_id, locomotive_model_id, name, service_year) VALUES (2, 1, 'Helena', 1996);
INSERT INTO Locomotive (operator_id, locomotive_model_id, name, service_year) VALUES (2, 2, 'Eva', 1981);
INSERT INTO Locomotive (operator_id, locomotive_model_id, name, service_year) VALUES (2, 3, 'Adriana', 2017);
INSERT INTO Locomotive (operator_id, locomotive_model_id, name, service_year) VALUES (2, 3, 'Marina', 2017);
INSERT INTO Locomotive (operator_id, locomotive_model_id, name, service_year) VALUES (3, 3, NULL, 2019);
INSERT INTO Locomotive (operator_id, locomotive_model_id, name, service_year) VALUES (3, 3, NULL, 2019);

-- 13. ELECTRIC / DIESEL - APÓS LOCOMOTIVE
INSERT INTO Electric_Locomotive (loco_id, voltage_kv, frequency_hz) VALUES (1, 25, 50);
INSERT INTO Electric_Locomotive (loco_id, voltage_kv, frequency_hz) VALUES (2, 25, 50);
INSERT INTO Electric_Locomotive (loco_id, voltage_kv, frequency_hz) VALUES (3, 25, 50);
INSERT INTO Diesel_Locomotive (loco_id, fuel_capacity) VALUES (4, 4882);
INSERT INTO Diesel_Locomotive (loco_id, fuel_capacity) VALUES (5, 6700);
INSERT INTO Diesel_Locomotive (loco_id, fuel_capacity) VALUES (6, 6700);
INSERT INTO Diesel_Locomotive (loco_id, fuel_capacity) VALUES (7, 6700);
INSERT INTO Diesel_Locomotive (loco_id, fuel_capacity) VALUES (8, 6700);

-- 14. LOCOMOTIVE GAUGE - APÓS LOCOMOTIVE
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (1, 1);
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (2, 1);
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (3, 1);
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (4, 1);
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (5, 1);
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (6, 1);
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (7, 1);
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (8, 1);

-- 15. WAGONS (41) - APÓS WAGONMODEL
-- Container (16) → wagonmodel_id = 4
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (4, 2, 1987);

-- Cereal Tadgs (4) → wagonmodel_id = 1
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (1, 2, 1990);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (1, 2, 1990);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (1, 2, 1990);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (1, 2, 1990);

-- Cereal Tdgs (6) → wagonmodel_id = 2
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (2, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (2, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (2, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (2, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (2, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (2, 2, 1977);

-- Covered Gabs (5) → wagonmodel_id = 3
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (3, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (3, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (3, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (3, 2, 1977);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (3, 2, 1977);

-- Wood Kbs (10) → wagonmodel_id = 5
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);
INSERT INTO Wagon (wagonmodel_id, operator_id, service_year) VALUES (5, 2, 2005);

-- 16. TRAINS (3)
INSERT INTO Train (operator_id, name, planned_departure) VALUES (2, '5421', TO_DATE('03/10/2025 09:45', 'DD/MM/YYYY HH24:MI'));
INSERT INTO Train (operator_id, name, planned_departure) VALUES (2, '5435', TO_DATE('03/10/2025 18:00', 'DD/MM/YYYY HH24:MI'));
INSERT INTO Train (operator_id, name, planned_departure) VALUES (2, '5437', TO_DATE('06/10/2025 10:00', 'DD/MM/YYYY HH24:MI'));

-- 17. TRAIN LOCOMOTIVES
INSERT INTO TrainLocomotive (train_id, loco_id) VALUES (1, 1);
INSERT INTO TrainLocomotive (train_id, loco_id) VALUES (1, 2);
INSERT INTO TrainLocomotive (train_id, loco_id) VALUES (2, 2);
INSERT INTO TrainLocomotive (train_id, loco_id) VALUES (3, 1);

-- 18. FREIGHTS (9)
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (27, 12, 1, TO_DATE('03/10/2025', 'DD/MM/YYYY'));
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (13, 11, 1, TO_DATE('03/10/2025', 'DD/MM/YYYY'));
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (27, 11, 1, TO_DATE('03/10/2025', 'DD/MM/YYYY'));
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (27, 21, 1, TO_DATE('03/10/2025', 'DD/MM/YYYY'));
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (20, 11, 1, TO_DATE('03/10/2025', 'DD/MM/YYYY'));
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (12, 27, 2, TO_DATE('03/10/2025', 'DD/MM/YYYY'));
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (27, 5, 1, TO_DATE('03/10/2025', 'DD/MM/YYYY'));
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (12, 27, 3, TO_DATE('06/10/2025', 'DD/MM/YYYY'));
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, freight_date) VALUES (11, 27, 3, TO_DATE('06/10/2025', 'DD/MM/YYYY'));

-- 19. TRAIN WAGONS - CADA INSERT EM LINHA SEPARADA
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 32);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 33);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 35);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 36);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 37);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 13);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 27);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 28);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 29);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 1);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 2);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 3);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 4);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 14);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 15);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (1, 16);

INSERT INTO TrainWagon (train_id, wagon_id) VALUES (2, 34);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (2, 38);

INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 32);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 33);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 35);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 36);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 37);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 27);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 28);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 1);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 2);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 3);
INSERT INTO TrainWagon (train_id, wagon_id) VALUES (3, 4);

-- 20. ROUTES (3)
INSERT INTO Route (train_id, name) VALUES (1, 'Leixões - Valença via Contumil');
INSERT INTO Route (train_id, name) VALUES (2, 'Valença - Leixões via Contumil');
INSERT INTO Route (train_id, name) VALUES (3, 'Valença - Leixões via Contumil');

-- 21. ROUTE STOPS (36) - CADA UM EM LINHA SEPARADA
-- Route 1
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (27, 1, 1, 'Yes');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (26, 1, 2, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (25, 1, 3, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (24, 1, 4, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (13, 1, 5, 'Yes');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (20, 1, 6, 'Yes');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (8, 1, 7, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (12, 1, 8, 'Yes');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (17, 1, 9, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (21, 1, 10, 'Yes');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (16, 1, 11, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (11, 1, 12, 'Yes');

-- Route 2
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (11, 2, 1, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (16, 2, 2, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (21, 2, 3, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (17, 2, 4, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (12, 2, 5, 'Yes');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (8, 2, 6, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (20, 2, 7, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (13, 2, 8, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (24, 2, 9, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (25, 2, 10, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (26, 2, 11, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (27, 2, 12, 'Yes');

-- Route 3
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (11, 3, 1, 'Yes');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (16, 3, 2, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (21, 3, 3, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (17, 3, 4, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (12, 3, 5, 'Yes');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (8, 3, 6, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (20, 3, 7, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (13, 3, 8, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (24, 3, 9, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (25, 3, 10, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (26, 3, 11, 'No');
INSERT INTO RouteStop (facility_id, route_id, stop_order, planned_stop) VALUES (27, 3, 12, 'Yes');

-- 22. ROUTE SEGMENTS (60) - CORRIGIDO segment_id
-- Route 1 (20 segmentos)
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (1, 1, 1);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (2, 1, 2);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (3, 1, 3);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (4, 1, 4);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (5, 1, 5);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (6, 1, 6);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (7, 1, 7);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (8, 1, 8);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (9, 1, 9);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (10, 1, 10);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (11, 1, 11);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (12, 1, 12);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (13, 1, 13);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (14, 1, 14);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (15, 1, 15);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (16, 1, 16);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (17, 1, 17);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (18, 1, 18);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (19, 1, 19);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (20, 1, 20);

-- Route 2
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (15, 2, 1);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (14, 2, 2);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (13, 2, 3);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (12, 2, 4);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (11, 2, 5);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (10, 2, 6);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (9, 2, 7);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (8, 2, 8);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (7, 2, 9);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (6, 2, 10);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (5, 2, 11);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (4, 2, 12);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (3, 2, 13);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (16, 2, 14);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (17, 2, 15);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (18, 2, 16);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (19, 2, 17);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (20, 2, 18);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (21, 2, 19);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (22, 2, 20);

-- Route 3
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (15, 3, 1);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (14, 3, 2);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (13, 3, 3);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (12, 3, 4);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (11, 3, 5);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (10, 3, 6);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (9, 3, 7);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (8, 3, 8);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (7, 3, 9);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (6, 3, 10);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (5, 3, 11);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (4, 3, 12);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (3, 3, 13);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (16, 3, 14);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (17, 3, 15);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (18, 3, 16);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (19, 3, 17);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (20, 3, 18);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (21, 3, 19);
INSERT INTO RouteSegment (segment_id, route_id, segment_order) VALUES (22, 3, 20);

-- 23. AMENITIES
INSERT INTO AmenityType (name, code) VALUES ('Grain Silo', 'GSILO');
INSERT INTO AmenityType (name, code) VALUES ('Warehouse', 'WHOUSE');
INSERT INTO AmenityType (name, code) VALUES ('Refrigerated Area', 'REFRIG');
INSERT INTO AmenityType (name, code) VALUES ('Loading Dock', 'LDOCK');
INSERT INTO AmenityType (name, code) VALUES ('Maintenance Workshop', 'MAINT');

INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (27, 1);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (27, 2);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (27, 3);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (27, 4);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (5, 1);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (5, 2);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (5, 5);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (12, 1);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (12, 4);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (11, 1);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (17, 2);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (17, 4);
INSERT INTO FacilityAmenity (facility_id, amenitytype_id) VALUES (13, 5);

COMMIT;