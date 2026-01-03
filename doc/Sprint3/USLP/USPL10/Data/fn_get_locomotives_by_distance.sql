-- ============================================================
-- fn_get_locomotives_by_distance
-- Retorna locomotivas ordenadas por distância ao início da rota
-- ============================================================
CREATE OR REPLACE FUNCTION fn_get_locomotives_by_distance(
    p_route_id IN NUMBER
)
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
    v_start_facility_id NUMBER;
BEGIN
    -- Get start facility of route
SELECT facility_id INTO v_start_facility_id
FROM RouteStop
WHERE route_id = p_route_id AND stop_order = 1;

OPEN rc FOR
SELECT
    l.loco_id,
    l.name AS locomotive_name,
    l.operator_id,
    c.short_name AS operator_name,
    lm.locomotive_model_id,
    m.name AS model_name,
    lm.power_kw,
    lm.operational_speed_kmh AS max_speed,
    m.weight_t AS weight,
    m.length_m AS length,
    l.start_facility_id AS current_facility_id,
    f.name AS current_facility_name,
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
    -- Simplified distance (in real implementation, would use actual distances from line segments)
    ABS(l.start_facility_id - v_start_facility_id) AS distance_estimate
FROM Locomotive l
         JOIN LocomotiveModel lm ON l.locomotive_model_id = lm.locomotive_model_id
         JOIN Model m ON lm.model_id = m.model_id
         JOIN Facility f ON l.start_facility_id = f.facility_id
         JOIN Operator o ON l.operator_id = o.operator_id
         JOIN Company c ON o.operator_id = c.company_id
WHERE NOT EXISTS (SELECT 1 FROM TrainLocomotive tl WHERE tl.loco_id = l.loco_id)
ORDER BY
    CASE WHEN l.start_facility_id = v_start_facility_id THEN 0 ELSE 1 END,
    distance_estimate,
    l.loco_id;

RETURN rc;
END;
/