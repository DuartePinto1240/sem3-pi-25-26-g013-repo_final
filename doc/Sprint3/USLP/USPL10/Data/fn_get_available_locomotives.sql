-- ============================================================
-- fn_get_available_locomotives
-- Returns locomotives available at a facility or within radius
-- ============================================================
CREATE OR REPLACE FUNCTION fn_get_available_locomotives(
    p_facility_id IN NUMBER,
    p_radius_km IN NUMBER DEFAULT 0
)
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    l.loco_id,
    l.name AS locomotive_name,
    l.operator_id,
    c.short_name AS operator_name,
    lm.locomotive_model_id,
    m.name AS model_name,
    m.maker,
    lm.power_kw,
    lm.operational_speed_kmh AS max_speed,
    m.weight_t AS weight,
    m.length_m AS length,
    l.start_facility_id AS current_facility_id,
    f.name AS current_facility_name,
    g.gauge_id,
    g.name AS gauge_name,
    CASE
        WHEN EXISTS (SELECT 1 FROM Diesel_Locomotive dl WHERE dl.loco_id = l.loco_id)
            THEN 'DIESEL'
        WHEN EXISTS (SELECT 1 FROM Electric_Locomotive el WHERE el.loco_id = l.loco_id)
            THEN 'ELECTRIC'
        ELSE 'UNKNOWN'
        END AS locomotive_type,
    CASE
        WHEN EXISTS (SELECT 1 FROM TrainLocomotive tl WHERE tl.loco_id = l.loco_id)
            THEN 'IN_TRANSIT'
        ELSE 'PARKED'
        END AS status,
    CASE
        WHEN EXISTS (SELECT 1 FROM TrainLocomotive tl WHERE tl.loco_id = l.loco_id)
            THEN (SELECT train_id FROM TrainLocomotive WHERE loco_id = l.loco_id)
        ELSE NULL
        END AS transit_train_id
FROM Locomotive l
         JOIN LocomotiveModel lm ON l.locomotive_model_id = lm.locomotive_model_id
         JOIN Model m ON lm.model_id = m.model_id
         JOIN Gauge g ON lm.gauge_id = g.gauge_id
         JOIN Facility f ON l.start_facility_id = f.facility_id
         JOIN Operator o ON l.operator_id = o.operator_id
         JOIN Company c ON o.operator_id = c.company_id
WHERE l.start_facility_id = p_facility_id
   OR p_radius_km > 0  -- Future: add distance calculation
ORDER BY
    CASE WHEN l.start_facility_id = p_facility_id THEN 0 ELSE 1 END,
    l.loco_id;

RETURN rc;
END;
/

-- Test
VAR rc REFCURSOR;
EXEC :rc := fn_get_available_locomotives(50);
PRINT rc;