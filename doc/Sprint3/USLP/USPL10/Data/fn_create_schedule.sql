-- ============================================================
-- fn_create_schedule
-- Creates a new train schedule
-- Returns the new schedule_id (uses train_id as schedule_id)
-- ============================================================
CREATE OR REPLACE FUNCTION fn_create_schedule(
    p_train_id IN NUMBER,
    p_departure_time IN DATE
)
RETURN NUMBER
IS
BEGIN
    -- For simplicity, we'll use TrainMovement table to store schedule
    -- First movement is the departure
INSERT INTO TrainMovement (train_id, facility_id, arrival_time, departure_time)
SELECT
    p_train_id,
    re.facility_id,
    p_departure_time,
    p_departure_time
FROM RouteEndpoint re
WHERE re.train_id = p_train_id
  AND re.endpoint_order = 1;

COMMIT;
RETURN p_train_id;  -- Using train_id as schedule_id

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/