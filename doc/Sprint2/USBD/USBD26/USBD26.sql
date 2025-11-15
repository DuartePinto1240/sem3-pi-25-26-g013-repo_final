-- USBD26: List wagons not used in any train during a given period
-- Modify the dates in the WHERE clause below to set your desired period

SELECT
    w.wagon_id,
    wm.wagonmodel_id,
    m.name AS model_name,
    m.maker,
    wt.type AS wagon_type,
    wc.code AS category_code,
    wc.name AS category_name,
    c.short_name AS operator,
    w.service_year,
    wm.payload_t,
    wm.volume_m3
FROM
    Wagon w
        INNER JOIN WagonModel wm ON w.wagonmodel_id = wm.wagonmodel_id
        INNER JOIN Model m ON wm.model_id = m.model_id
        INNER JOIN WagonType wt ON wm.wagontype_id = wt.wagontype_id
        INNER JOIN WagonCategory wc ON wm.wagoncategory_id = wc.wagoncategory_id
        INNER JOIN Operator op ON w.operator_id = op.operator_id
        INNER JOIN Company c ON op.operator_id = c.company_id
WHERE
    w.wagon_id NOT IN (
        SELECT DISTINCT tw.wagon_id
        FROM TrainWagon tw
                 INNER JOIN Train t ON tw.train_id = t.train_id
        WHERE t.planned_departure BETWEEN TO_DATE('01/10/2025', 'DD/MM/YYYY')
                  AND TO_DATE('31/10/2025', 'DD/MM/YYYY')
    )
ORDER BY
    wc.code,
    m.name,
    w.wagon_id;

-- To change the period, modify the two dates in the WHERE clause above
-- Example for different periods:
-- September 2025: '01/09/2025' to '30/09/2025'
-- Q4 2025: '01/10/2025' to '31/12/2025'
