-- ============================================================
-- fn_save_passage_time
-- Saves estimated passage time at a facility
-- ============================================================
CREATE OR REPLACE PROCEDURE sp_save_passage_time(
    p_train_id IN NUMBER,
    p_facility_id IN NUMBER,
    p_arrival_time IN DATE,
    p_departure_time IN DATE DEFAULT NULL
)
IS
    v_count NUMBER;
BEGIN
    -- Check if already exists
SELECT COUNT(*) INTO v_count
FROM TrainMovement
WHERE train_id = p_train_id
  AND facility_id = p_facility_id
  AND arrival_time = p_arrival_time;

IF v_count = 0 THEN
        INSERT INTO TrainMovement (train_id, facility_id, arrival_time, departure_time)
        VALUES (p_train_id, p_facility_id, p_arrival_time, p_departure_time);
ELSE
UPDATE TrainMovement
SET departure_time = p_departure_time
WHERE train_id = p_train_id
  AND facility_id = p_facility_id
  AND arrival_time = p_arrival_time;
END IF;

COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/