-- ============================================================
-- USLP08 - Extra Test Data (Simple + Complex Routes)
-- Keeps professor dataset intact. Adds ONLY pending freights + cargo.
-- ============================================================

-- -------------------------
-- COMPLEX ROUTE (3 legs):
--   Leixões(50) -> Porto Campanhã(5) -> Contumil(13) -> Nine(20)
-- -------------------------

INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id, train_id, depart_time, arrival_time)
VALUES (9101, 50, 5,  NULL, TO_DATE('2025-12-24 08:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2025-12-24 10:00', 'YYYY-MM-DD HH24:MI'));

INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id, train_id, depart_time, arrival_time)
VALUES (9102, 5,  13, NULL, TO_DATE('2025-12-24 10:30', 'YYYY-MM-DD HH24:MI'), TO_DATE('2025-12-24 12:00', 'YYYY-MM-DD HH24:MI'));

INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id, train_id, depart_time, arrival_time)
VALUES (9103, 13, 20, NULL, TO_DATE('2025-12-24 12:30', 'YYYY-MM-DD HH24:MI'), TO_DATE('2025-12-24 15:00', 'YYYY-MM-DD HH24:MI'));


-- Link wagons to those freights (wagons NOT used in your pasted inserts)
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (9101, 823045);
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (9102, 823046);
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (9103, 823047);


-- Cargo for those wagons (so UI shows load/unload)
-- NOTE: assumes CARGO columns exist: (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99101, 823045, 'Containers - electronics', 'GENERAL', 12, 30.0, 14000, DATE '2026-02-01');

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99102, 823046, 'Pallets - food supplies', 'GENERAL', 40, 18.0, 12000, DATE '2026-01-20');

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99103, 823047, 'Grain batch', 'GRAIN', 100, 25.0, 20000, DATE '2026-03-01');


-- -------------------------
-- SIMPLE ROUTE (1 leg):
--   Leixões(50) -> Valença(11)
-- -------------------------

INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id, train_id, depart_time, arrival_time)
VALUES (9201, 50, 11, NULL, TO_DATE('2025-12-25 09:00', 'YYYY-MM-DD HH24:MI'), TO_DATE('2025-12-25 13:30', 'YYYY-MM-DD HH24:MI'));

-- wagon not used in your pasted inserts
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (9201, 3563081);

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99201, 3563081, 'Wood logs', 'WOOD', 60, 35.0, 23000, DATE '2026-04-01');

-- USLP08 - Extra cargo for freight 2007 (wagons 3563090..3563092)

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99301, 3563090, 'Spare parts', 'GENERAL', 15, 8.0, 5000, DATE '2026-02-10');

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99302, 3563091, 'Construction materials', 'GENERAL', 30, 14.0, 12000, DATE '2026-02-20');

INSERT INTO Cargo (cargo_id, wagon_id, description, type, quantity, volume, weight, expiry_date)
VALUES (99303, 3563092, 'Grain sacks', 'GRAIN', 70, 20.0, 18000, DATE '2026-03-05');

COMMIT;

-- Quick sanity checks:
-- SELECT freight_id, origin_facility_id, destination_facility_id, train_id FROM Freight WHERE freight_id IN (9101,9102,9103,9201);
-- SELECT * FROM FreightWagon WHERE freight_id IN (9101,9102,9103,9201);
-- SELECT * FROM Cargo WHERE wagon_id IN (823045,823046,823047,3563081);
