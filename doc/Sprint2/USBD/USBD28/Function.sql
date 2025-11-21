-- Function to list all locomotives of a given operator that support multiple gauges.
CREATE OR REPLACE FUNCTION get_multi_gauge_locos (
    p_operator_id IN NUMBER
)
RETURN SYS_REFCURSOR
IS
    rc_result SYS_REFCURSOR;
BEGIN
OPEN rc_result FOR
SELECT
    l.loco_id,
    l.name AS locomotive_name,
    c.short_name AS operator_short_name,
    m.name AS model_name,
    COUNT(lg.gauge_id) AS number_of_gauges_supported
FROM
    Locomotive l
        JOIN
    LocomotiveGauge lg ON l.loco_id = lg.loco_id
        JOIN
    Company c ON l.operator_id = c.company_id
        JOIN
    LocomotiveModel lm ON l.locomotive_model_id = lm.locomotive_model_id
        JOIN
    Model m ON lm.model_id = m.model_id
WHERE
    l.operator_id = p_operator_id
GROUP BY
    l.loco_id, l.name, c.short_name, m.name
HAVING
    COUNT(lg.gauge_id) > 1 -- Filters for locomotives with > 1 gauge record
ORDER BY
    l.loco_id;

RETURN rc_result;
END;
/