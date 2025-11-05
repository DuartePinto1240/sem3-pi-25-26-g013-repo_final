-- Gauge (IDENTITY ALWAYS -> DEFAULT for gauge_id)
INSERT INTO Gauge (gauge_id, width_mm, description)
VALUES (DEFAULT, 1435, 'AUTO');
INSERT INTO Gauge (gauge_id, width_mm, description)
VALUES (DEFAULT, 1668, 'AUTO');

-- FacilityType (IDENTITY ALWAYS)
INSERT INTO FacilityType (facilitytype_id, type)
VALUES (DEFAULT, 'Station');

-- Facility (IDENTITY ALWAYS) — IDs 1..23 by insertion order; FacilityType=1 ('Station')
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'São Romão', 'São Romão', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Tamel', 'Tamel', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Senhora das Dores', 'Senhora das Dores', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Lousado', 'Lousado', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Porto Campanhã', 'Porto Campanhã', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Leandro', 'Leandro', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Porto São Bento', 'Porto São Bento', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Barcelos', 'Barcelos', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Vila Nova da Cerveira', 'Vila Nova da Cerveira', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Midões', 'Midões', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Valença', 'Valença', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Darque', 'Darque', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Contumil', 'Contumil', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Ermesinde', 'Ermesinde', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'São Frutuoso', 'São Frutuoso', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'São Pedro da Torre', 'São Pedro da Torre', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Viana do Castelo', 'Viana do Castelo', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Famalicão', 'Famalicão', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Barroselas', 'Barroselas', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Nine', 'Nine', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Caminha', 'Caminha', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Carvalha', 'Carvalha', 1);
INSERT INTO Facility (facility_id, name, location, facilitytype_id)
VALUES (DEFAULT, 'Carreço', 'Carreço', 1);

-- Owner (IDENTITY ALWAYS) — owner_id=1 (IP)
INSERT INTO Owner (owner_id, name, short_name, vat_number)
VALUES (DEFAULT, 'Infraestruturas de Portugal, SA', 'IP', 'PT503933813');

-- Operator (IDENTITY ALWAYS) — operator_id=1 (Medway)
INSERT INTO Operator (operator_id, name, country, vat_number, short_name)
VALUES (DEFAULT, 'Medway - Operador Ferroviário de Mercadorias, S.A', 'PT', 'PT509017800', 'Medway');

-- WagonType (IDENTITY ALWAYS) — wagontype_id=1..3 by insertion order
INSERT INTO WagonType (wagontype_id, type)
VALUES (DEFAULT, 'Cereal wagon');
INSERT INTO WagonType (wagontype_id, type)
VALUES (DEFAULT, 'Covered wagon with sliding door');
INSERT INTO WagonType (wagontype_id, type)
VALUES (DEFAULT, 'Container wagon (max 40'' HC)');

-- WagonModel (BY DEFAULT ON NULL) — explicit IDs; gauge_id=2 (1668); wagon types 1:cereal, 2:covered, 3:container
INSERT INTO WagonModel (wagonmodel_id, wagontype_id, gauge_id, name, maker, length_mm, weight_t, payload_t,
                        max_speed_kmh, number_of_bogies, bogies, volume_m3, height_mm, width_mm)
VALUES (1245, 1, 2, 'Tadgs 32 94 082 3', 'Metalsines', 17240, 24.0, 56.0, 120.0, 2, 'Duplo', 75.0, 4270, 3072);
INSERT INTO WagonModel (wagonmodel_id, wagontype_id, gauge_id, name, maker, length_mm, weight_t, payload_t,
                        max_speed_kmh, number_of_bogies, bogies, volume_m3, height_mm, width_mm)
VALUES (1278, 1, 2, 'Tdgs 41 94 074 1', 'Equimetal', 9640, 13.8, 26.2, 100.0, 2, 'Duplo', 38.0, 4165.5, 3120);
INSERT INTO WagonModel (wagonmodel_id, wagontype_id, gauge_id, name, maker, length_mm, weight_t, payload_t,
                        max_speed_kmh, number_of_bogies, bogies, volume_m3, height_mm, width_mm)
VALUES (1325, 2, 2, 'Gabs 81 94 181 1', 'Sepsa Cometna', 21700, 29.8, 50.2, 100.0, 2, 'Duplo', 110.0, 4170, 3180);
INSERT INTO WagonModel (wagonmodel_id, wagontype_id, gauge_id, name, maker, length_mm, weight_t, payload_t,
                        max_speed_kmh, number_of_bogies, bogies, volume_m3, height_mm, width_mm)
VALUES (1104, 3, 2, 'Regmms 32 94 356 3', 'Metalsines', 14040, 21.2, 60.6, 120.0, 2, 'Duplo', 76.3, 2535, 3104);
INSERT INTO WagonModel (wagonmodel_id, wagontype_id, gauge_id, name, maker, length_mm, weight_t, payload_t,
                        max_speed_kmh, number_of_bogies, bogies, volume_m3, height_mm, width_mm)
VALUES (985, 3, 2, 'Lgs 22 94 441 6', 'Metalsines', 13860, 11.9, 28.1, 120.0, 1, 'Simples', 76.3, 1060, 2850);
INSERT INTO WagonModel (wagonmodel_id, wagontype_id, gauge_id, name, maker, length_mm, weight_t, payload_t,
                        max_speed_kmh, number_of_bogies, bogies, volume_m3, height_mm, width_mm)
VALUES (987, 3, 2, 'Sgnss 12 94 455 2', 'Emef', 18116, 21.6, 68.4, 120.0, 2, 'Duplo', 76.3, 1030, 2950);

-- RailwayLine (IDENTITY ALWAYS) — FK by known numeric IDs from above
INSERT INTO RailwayLine (line_id, gauge_id, owner_id, start_station_id, end_station_id, name)
VALUES (DEFAULT, 2, 1, 7, 5, 'Ramal São Bento - Campanhã');
INSERT INTO RailwayLine (line_id, gauge_id, owner_id, start_station_id, end_station_id, name)
VALUES (DEFAULT, 2, 1, 5, 20, 'Ramal Campanhã - Nine');
INSERT INTO RailwayLine (line_id, gauge_id, owner_id, start_station_id, end_station_id, name)
VALUES (DEFAULT, 2, 1, 20, 8, 'Ramal Nine - Barcelos');
INSERT INTO RailwayLine (line_id, gauge_id, owner_id, start_station_id, end_station_id, name)
VALUES (DEFAULT, 2, 1, 8, 17, 'Ramal Barcelos - Viana');
INSERT INTO RailwayLine (line_id, gauge_id, owner_id, start_station_id, end_station_id, name)
VALUES (DEFAULT, 2, 1, 17, 21, 'Ramal Viana - Caminha');
INSERT INTO RailwayLine (line_id, gauge_id, owner_id, start_station_id, end_station_id, name)
VALUES (DEFAULT, 2, 1, 21, 16, 'Ramal Caminha - Torre');
INSERT INTO RailwayLine (line_id, gauge_id, owner_id, start_station_id, end_station_id, name)
VALUES (DEFAULT, 2, 1, 16, 11, 'Ramal Torre - Valença');

-- LineSegment (BY DEFAULT ON NULL) — explicit IDs; line_id 1..7 in insertion order above
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (1, 1, 1, 2618, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (10, 2, 1, 29003, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (11, 2, 2, 10000, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (15, 3, 1, 5286, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (16, 3, 2, 6000, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (14, 4, 1, 10387, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (12, 4, 2, 12000, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (13, 4, 3, 8000, 120, 6400, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (20, 5, 1, 6000, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (21, 5, 2, 3000, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (22, 5, 3, 15000, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (25, 6, 1, 20829, 120, 8000, 'double', 'Yes');
INSERT INTO LineSegment (line_segment_id, line_id, segment_order, length_m, speed_limit, max_weight, track_type,
                         electrified)
VALUES (26, 7, 1, 4264, 120, 8000, 'double', 'Yes');

-- Locomotive (BY DEFAULT ON NULL) — explicit loco_id; gauge_id=2; operator_id=1
INSERT INTO Locomotive (loco_id, gauge_id, operator_id, name, make, model, power_kw, weight_t, service_year, height_m,
                        width_m, length_m, max_speed_kmh, operational_speed_kmh, traction_kn, number_of_bogies, bogies)
VALUES (5621, 2, 1, 'Inês', 'Siemens', 'Eurosprinter', 5600, 87.0, 1995, 4.375, 3, 19.2, 220, 70, 300, 2, 'Bo-Bo');
INSERT INTO Locomotive (loco_id, gauge_id, operator_id, name, make, model, power_kw, weight_t, service_year, height_m,
                        width_m, length_m, max_speed_kmh, operational_speed_kmh, traction_kn, number_of_bogies, bogies)
VALUES (5623, 2, 1, 'Paz', 'Siemens', 'Eurosprinter', 5600, 87.0, 1995, 4.375, 3, 19.2, 220, 70, 300, 2, 'Bo-Bo');
INSERT INTO Locomotive (loco_id, gauge_id, operator_id, name, make, model, power_kw, weight_t, service_year, height_m,
                        width_m, length_m, max_speed_kmh, operational_speed_kmh, traction_kn, number_of_bogies, bogies)
VALUES (5630, 2, 1, 'Helena', 'Siemens', 'Eurosprinter', 5600, 87.0, 1996, 4.375, 3, 18.980, 220, 70, 300, 2, 'Bo-Bo');
INSERT INTO Locomotive (loco_id, gauge_id, operator_id, name, make, model, power_kw, weight_t, service_year, height_m,
                        width_m, length_m, max_speed_kmh, operational_speed_kmh, traction_kn, number_of_bogies, bogies)
VALUES (1903, 2, 1, 'Eva', 'Sorefame - Alsthom', 'CP 1900', 1623, 117.0, 1981, 4.375, 3.062, 19.084, 100, 42.5, 396, 2,
        'Co-Co');

-- Electric_Locomotive / Diesel_Locomotive
INSERT INTO Electric_Locomotive (loco_id, voltage_kv, frequency_hz)
VALUES (5621, 25, 50);
INSERT INTO Electric_Locomotive (loco_id, voltage_kv, frequency_hz)
VALUES (5623, 25, 50);
INSERT INTO Electric_Locomotive (loco_id, voltage_kv, frequency_hz)
VALUES (5630, 25, 50);
INSERT INTO Diesel_Locomotive (loco_id, fuel_capacity)
VALUES (1903, 4882.0);

-- Wagons (IDENTITY ALWAYS) — wagon_id via DEFAULT; operator_id=1; model_ids explicit
-- Regmms 32 94 356 3 — 1987 (16 unidades)
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563077, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563078, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563079, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563080, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563081, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563082, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563083, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563084, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563085, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563086, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563087, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563088, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563089, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563090, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563091, 1, 1104, 1987);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (3563092, 1, 1104, 1987);

-- Tadgs 32 94 082 3 — 1990 (4 unidades)
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0823045, 1, 1245, 1990);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0823046, 1, 1245, 1990);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0823047, 1, 1245, 1990);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0823048, 1, 1245, 1990);

-- Gabs 81 94 181 1 — 1977 (6 unidades)
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0741001, 1, 1325, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0741002, 1, 1325, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0741003, 1, 1325, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0741004, 1, 1325, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0741005, 1, 1325, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (0741006, 1, 1325, 1977);

-- Tdgs 41 94 074 1 — 1977 (5 unidades)
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (1811010, 1, 1278, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (1811011, 1, 1278, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (1811012, 1, 1278, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (1811013, 1, 1278, 1977);
INSERT INTO Wagon (wagon_id, operator_id, wagonmodel_id, service_year)
VALUES (1811014, 1, 1278, 1977);