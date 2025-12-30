-- ============================================================
-- fn_get_available_wagons
-- Returns wagons available at a facility or within radius
-- ============================================================
CREATE OR REPLACE FUNCTION fn_get_available_wagons(
    p_facility_id IN NUMBER,
    p_radius_km IN NUMBER DEFAULT 0
)
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    w.wagon_id,
    w.operator_id,
    c.short_name AS operator_name,
    wm.wagonmodel_id,
    wt.type AS wagon_type,
    wc.name AS wagon_category,
    m.name AS model_name,
    m.maker,
    m.weight_t AS tare_weight,
    wm.payload_t,
    wm.volume_m3,
    m.length_m AS length,
    w.current_facility_id,
    f.name AS current_facility_name,
    g.gauge_id,
    g.name AS gauge_name,
    CASE
        WHEN EXISTS (SELECT 1 FROM TrainWagon tw WHERE tw.wagon_id = w.wagon_id)
            THEN 'IN_TRANSIT'
        ELSE 'PARKED'
        END AS status,
    CASE
        WHEN EXISTS (SELECT 1 FROM TrainWagon tw WHERE tw.wagon_id = w.wagon_id)
            THEN (SELECT train_id FROM TrainWagon WHERE wagon_id = w.wagon_id)
        ELSE NULL
        END AS transit_train_id,
    CASE
        WHEN EXISTS (SELECT 1 FROM FreightWagon fw WHERE fw.wagon_id = w.wagon_id)
            THEN (SELECT freight_id FROM FreightWagon WHERE wagon_id = w.wagon_id)
        ELSE NULL
        END AS freight_id
FROM Wagon w
         JOIN WagonModel wm ON w.wagonmodel_id = wm.wagonmodel_id
         JOIN Model m ON wm.model_id = m.model_id
         JOIN WagonType wt ON wm.wagontype_id = wt.wagontype_id
         JOIN WagonCategory wc ON wm.wagoncategory_id = wc.wagoncategory_id
         JOIN Gauge g ON wm.gauge_id = g.gauge_id
         JOIN Facility f ON w.current_facility_id = f.facility_id
         JOIN Operator o ON w.operator_id = o.operator_id
         JOIN Company c ON o.operator_id = c.company_id
WHERE w.current_facility_id = p_facility_id
   OR p_radius_km > 0
ORDER BY
    CASE WHEN w.current_facility_id = p_facility_id THEN 0 ELSE 1 END,
    w.wagon_id;

RETURN rc;
END;
/

-- Test
VAR rc REFCURSOR;
EXEC :rc := fn_get_available_wagons(50);
PRINT rc;