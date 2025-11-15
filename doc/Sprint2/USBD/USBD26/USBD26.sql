-- USBD26: PL/SQL Function to list wagons not used in any train during a given period
-- Returns a SYS_REFCURSOR with the results

CREATE OR REPLACE FUNCTION get_unused_wagons(
    p_start_date IN DATE,
    p_end_date IN DATE
) RETURN SYS_REFCURSOR IS
    v_cursor SYS_REFCURSOR;
BEGIN
OPEN v_cursor FOR
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
        WHERE t.planned_departure BETWEEN p_start_date AND p_end_date
    )
ORDER BY
    wc.code,
    m.name,
    w.wagon_id;

RETURN v_cursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END get_unused_wagons;
/

-- Example usage with DBMS_OUTPUT to display results
SET SERVEROUTPUT ON;

DECLARE
v_cursor SYS_REFCURSOR;
    v_wagon_id Wagon.wagon_id%TYPE;
    v_wagonmodel_id WagonModel.wagonmodel_id%TYPE;
    v_model_name Model.name%TYPE;
    v_maker Model.maker%TYPE;
    v_wagon_type WagonType.type%TYPE;
    v_category_code WagonCategory.code%TYPE;
    v_category_name WagonCategory.name%TYPE;
    v_operator Company.short_name%TYPE;
    v_service_year Wagon.service_year%TYPE;
    v_payload_t WagonModel.payload_t%TYPE;
    v_volume_m3 WagonModel.volume_m3%TYPE;
    v_count NUMBER := 0;
BEGIN
    -- Call the function for October 2025
    v_cursor := get_unused_wagons(
        TO_DATE('01/10/2025', 'DD/MM/YYYY'),
        TO_DATE('31/10/2025', 'DD/MM/YYYY')
    );

    DBMS_OUTPUT.PUT_LINE('=======================================================');
    DBMS_OUTPUT.PUT_LINE('Unused Wagons - October 2025');
    DBMS_OUTPUT.PUT_LINE('=======================================================');

    LOOP
FETCH v_cursor INTO
            v_wagon_id, v_wagonmodel_id, v_model_name, v_maker,
            v_wagon_type, v_category_code, v_category_name,
            v_operator, v_service_year, v_payload_t, v_volume_m3;

        EXIT WHEN v_cursor%NOTFOUND;

        v_count := v_count + 1;
        DBMS_OUTPUT.PUT_LINE('Wagon ID: ' || v_wagon_id ||
                           ' | Model: ' || v_model_name ||
                           ' | Type: ' || v_wagon_type ||
                           ' | Category: ' || v_category_code ||
                           ' | Operator: ' || v_operator);
END LOOP;

CLOSE v_cursor;

DBMS_OUTPUT.PUT_LINE('-------------------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('Total unused wagons: ' || v_count);
    DBMS_OUTPUT.PUT_LINE('=======================================================');
END;
/
