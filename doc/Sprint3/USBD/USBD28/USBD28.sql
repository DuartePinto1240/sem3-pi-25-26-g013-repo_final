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
    l.name              AS locomotive_name,
    c.short_name        AS operator_short_name,
    m.name              AS model_name,
    COUNT(lg.gauge_id)  AS number_of_gauges_supported
FROM
    Locomotive l
        INNER JOIN LocomotiveGauge lg ON l.loco_id = lg.loco_id
        INNER JOIN Company c ON l.operator_id = c.company_id
        INNER JOIN LocomotiveModel lm ON l.locomotive_model_id = lm.locomotive_model_id
        INNER JOIN Model m ON lm.model_id = m.model_id
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

DECLARE
v_cursor SYS_REFCURSOR;
    v_loco_id NUMBER(10);
    v_name VARCHAR2(255);
    v_operator VARCHAR2(255);
    v_model VARCHAR2(255);
    v_gauge_count NUMBER(10);
BEGIN
    -- 1. Execute USBD28 for Medway (Operator ID 2)
    v_cursor := get_multi_gauge_locos(2);

    DBMS_OUTPUT.PUT_LINE('--- USBD28 RESULTS: Medway (Operator 2) with ORIGINAL data (Expected: No Results) ---');
    DBMS_OUTPUT.PUT_LINE(RPAD('LOCO_ID', 8) || ' ' || RPAD('NAME', 15) || ' ' || RPAD('MODEL', 15) || ' ' || RPAD('GAUGES', 8));
    DBMS_OUTPUT.PUT_LINE(RPAD('-', 8, '-') || ' ' || RPAD('-', 15, '-') || ' ' || RPAD('-', 15, '-') || ' ' || RPAD('-', 8, '-'));

    -- 2. Loop through the cursor and print results
    LOOP
FETCH v_cursor INTO v_loco_id, v_name, v_operator, v_model, v_gauge_count;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
            RPAD(v_loco_id, 8) || ' ' ||
            RPAD(v_name, 15) || ' ' ||
            RPAD(v_model, 15) || ' ' ||
            RPAD(v_gauge_count, 8)
        );
END LOOP;

    IF v_cursor%ROWCOUNT = 0 THEN
        DBMS_OUTPUT.PUT_LINE('No locomotives support multiple gauges for this operator in the current dataset.');
END IF;

CLOSE v_cursor;
END;
/