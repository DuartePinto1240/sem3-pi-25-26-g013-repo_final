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
    v_cursor := get_grain_wagons_used_in_all_trains(
        TO_DATE('01/10/2025','DD/MM/YYYY'),
        TO_DATE('31/10/2025','DD/MM/YYYY')
    );

    DBMS_OUTPUT.PUT_LINE('=======================================================');
    DBMS_OUTPUT.PUT_LINE('Grain wagons used in all trains - october 2025');
    DBMS_OUTPUT.PUT_LINE('=======================================================');

    LOOP
        FETCH v_cursor INTO
            v_wagon_id, v_wagonmodel_id, v_model_name, v_maker,
            v_wagon_type, v_category_code, v_category_name,
            v_operator, v_service_year, v_payload_t, v_volume_m3;

        EXIT WHEN v_cursor%NOTFOUND;

        v_count := v_count + 1;

        DBMS_OUTPUT.PUT_LINE(
            'Wagon ID: ' || v_wagon_id ||
            ' | Model: ' || v_model_name ||
            ' | Operator: ' || v_operator
        );
    END LOOP;

    CLOSE v_cursor;

    DBMS_OUTPUT.PUT_LINE('-------------------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('Total grain wagons used in all trains: ' || v_count);
    DBMS_OUTPUT.PUT_LINE('=======================================================');
END;
/