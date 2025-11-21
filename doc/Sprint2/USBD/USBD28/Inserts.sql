-- DML to insert a record so the query returns a result.

-- Locomotive ID 1 ('Inês', Operator 2) is already linked to Gauge ID 1.
-- This insert links it to Gauge ID 2 (1435mm), making it dual-gauge.
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (1, 2);

-- Locomotive ID 3 ('Helena', Operator 2) is already linked to Gauge ID 1.
-- This insert links it to Gauge ID 2 (1435mm), making it dual-gauge.
INSERT INTO LocomotiveGauge (loco_id, gauge_id) VALUES (3, 2);
COMMIT;