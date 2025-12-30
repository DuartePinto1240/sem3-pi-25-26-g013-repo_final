-- ============================================================
-- fn_get_train_details
-- Returns complete train information
-- ============================================================
CREATE OR REPLACE FUNCTION fn_get_train_details(
    p_train_id IN NUMBER
)
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    t.train_id,
    t.name AS train_name,
    t.operator_id,
    c.short_name AS operator_name,
    t.max_length_m,
    COUNT(DISTINCT tl.loco_id) AS num_locomotives,
    COUNT(DISTINCT tw.wagon_id) AS num_wagons,
    SUM(DISTINCT m_loco.length_m) AS total_loco_length,
    SUM(DISTINCT m_wagon.length_m) AS total_wagon_length,
    SUM(DISTINCT m_loco.weight_t) AS total_loco_weight,
    SUM(DISTINCT m_wagon.weight_t + wm.payload_t) AS total_wagon_weight
FROM Train t
         JOIN Operator o ON t.operator_id = o.operator_id
         JOIN Company c ON o.operator_id = c.company_id
         LEFT JOIN TrainLocomotive tl ON t.train_id = tl.train_id
         LEFT JOIN Locomotive l ON tl.loco_id = l.loco_id
         LEFT JOIN LocomotiveModel lm ON l.locomotive_model_id = lm.locomotive_model_id
         LEFT JOIN Model m_loco ON lm.model_id = m_loco.model_id
         LEFT JOIN TrainWagon tw ON t.train_id = tw.train_id
         LEFT JOIN Wagon w ON tw.wagon_id = w.wagon_id
         LEFT JOIN WagonModel wm ON w.wagonmodel_id = wm.wagonmodel_id
         LEFT JOIN Model m_wagon ON wm.model_id = m_wagon.model_id
WHERE t.train_id = p_train_id
GROUP BY t.train_id, t.name, t.operator_id, c.short_name, t.max_length_m;

RETURN rc;
END;
/

-- Test
VAR rc REFCURSOR;
EXEC :rc := fn_get_train_details(9300);
PRINT rc;