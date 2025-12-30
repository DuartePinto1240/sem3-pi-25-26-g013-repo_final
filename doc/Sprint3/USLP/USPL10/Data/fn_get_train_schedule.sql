-- ============================================================
-- fn_get_train_schedule
-- Returns complete schedule for a train
-- ============================================================
CREATE OR REPLACE FUNCTION fn_get_train_schedule(
    p_train_id IN NUMBER
)
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    tm.train_id,
    tm.facility_id,
    f.name AS facility_name,
    tm.arrival_time,
    tm.departure_time,
    CASE
        WHEN tm.departure_time IS NULL THEN 0
        ELSE 1
        END AS stops,
    re.endpoint_order
FROM TrainMovement tm
         JOIN Facility f ON tm.facility_id = f.facility_id
         LEFT JOIN RouteEndpoint re ON tm.train_id = re.train_id
    AND tm.facility_id = re.facility_id
WHERE tm.train_id = p_train_id
ORDER BY tm.arrival_time, re.endpoint_order;

RETURN rc;
END;
/

-- Test
VAR rc REFCURSOR;
EXEC :rc := fn_get_train_schedule(9300);
PRINT rc;