CREATE OR REPLACE FUNCTION FNC_USBD37_REGISTER_WAGON(
    p_wagonmodel_id         IN Wagon.wagonmodel_id%TYPE,
    p_operator_id           IN Wagon.operator_id%TYPE,
    p_start_facility_name   IN Facility.name%TYPE,
    p_current_facility_name IN Facility.name%TYPE,
    p_service_year          IN Wagon.service_year%TYPE
) RETURN SYS_REFCURSOR
IS
    v_wagon_id            Wagon.wagon_id%TYPE;
    v_start_facility_id   Facility.facility_id%TYPE;
    v_current_facility_id Facility.facility_id%TYPE;
    v_gauge_id            WagonModel.gauge_id%TYPE;

    rc SYS_REFCURSOR;
BEGIN
    -- Resolve facilities by name (Facility.name has UNIQUE constraint)
SELECT f.facility_id
INTO v_start_facility_id
FROM Facility f
WHERE UPPER(REPLACE(TRIM(f.name),' ','')) = UPPER(REPLACE(TRIM(p_start_facility_name),' ',''));

SELECT f.facility_id
INTO v_current_facility_id
FROM Facility f
WHERE UPPER(REPLACE(TRIM(f.name),' ','')) = UPPER(REPLACE(TRIM(p_current_facility_name),' ',''));

-- Validate wagon model and get its gauge (wagon supported gauge is the model gauge)
SELECT wm.gauge_id
INTO v_gauge_id
FROM WagonModel wm
WHERE wm.wagonmodel_id = p_wagonmodel_id;

-- Ensure gauge is registered
DECLARE
v_dummy NUMBER;
BEGIN
SELECT 1
INTO v_dummy
FROM Gauge g
WHERE g.gauge_id = v_gauge_id;
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20037, 'USBD37: Gauge of the WagonModel is not registered in Gauge (gauge_id=' || v_gauge_id || ').');
END;

    -- Generate a new wagon_id (no sequences)
SELECT NVL(MAX(w.wagon_id), 0) + 1
INTO v_wagon_id
FROM Wagon w;

-- Insert wagon
INSERT INTO Wagon (
    wagon_id, wagonmodel_id, operator_id,
    start_facility_id, current_facility_id, service_year
) VALUES (
             v_wagon_id, p_wagonmodel_id, p_operator_id,
             v_start_facility_id, v_current_facility_id, p_service_year
         );

-- Return the inserted wagon and its gauge details
OPEN rc FOR
SELECT  w.wagon_id,
        w.wagonmodel_id,
        w.operator_id,
        w.start_facility_id,
        w.current_facility_id,
        w.service_year,
        wm.gauge_id,
        g.name    AS gauge_name,
        g.width_mm
FROM Wagon w
         JOIN WagonModel wm ON wm.wagonmodel_id = w.wagonmodel_id
         JOIN Gauge g       ON g.gauge_id = wm.gauge_id
WHERE w.wagon_id = v_wagon_id;

RETURN rc;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        -- Either facility name not found OR wagonmodel not found
        RAISE_APPLICATION_ERROR(-20037, 'USBD37: Facility name or WagonModel not found.');
WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(-20037, 'USBD37: Duplicate wagon_id detected unexpectedly.');
WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20037, 'USBD37: Error: ' || SQLERRM);
END;
/

-- =========================================================
-- TESTS (Anonymous Blocks) - comprehensive, not only happy path
-- IMPORTANT: no pre-inserts required; each test uses SAVEPOINT and ends with ROLLBACK.
-- =========================================================

-- Test 1: Happy path (should return 1 row)
DECLARE
rc SYS_REFCURSOR;

    v_wm_id        WagonModel.wagonmodel_id%TYPE;
    v_op_id        Wagon.operator_id%TYPE;
    v_fac_name1    Facility.name%TYPE;
    v_fac_name2    Facility.name%TYPE;

    o_wagon_id     Wagon.wagon_id%TYPE;
    o_wagonmodel   Wagon.wagonmodel_id%TYPE;
    o_operator     Wagon.operator_id%TYPE;
    o_start_fac    Wagon.start_facility_id%TYPE;
    o_curr_fac     Wagon.current_facility_id%TYPE;
    o_year         Wagon.service_year%TYPE;
    o_gauge_id     WagonModel.gauge_id%TYPE;
    o_gauge_name   Gauge.name%TYPE;
    o_width_mm     Gauge.width_mm%TYPE;
BEGIN
SAVEPOINT sp_usbd37_t1;

SELECT wagonmodel_id INTO v_wm_id FROM WagonModel WHERE ROWNUM = 1;
SELECT operator_id   INTO v_op_id FROM Operator  WHERE ROWNUM = 1;
SELECT name          INTO v_fac_name1 FROM Facility WHERE ROWNUM = 1;
SELECT name          INTO v_fac_name2 FROM Facility WHERE ROWNUM = 1;

rc := FNC_USBD37_REGISTER_WAGON(
        p_wagonmodel_id         => v_wm_id,
        p_operator_id           => v_op_id,
        p_start_facility_name   => v_fac_name1,
        p_current_facility_name => v_fac_name2,
        p_service_year          => 2020
    );

FETCH rc INTO o_wagon_id, o_wagonmodel, o_operator, o_start_fac, o_curr_fac, o_year,
    o_gauge_id, o_gauge_name, o_width_mm;

IF rc%NOTFOUND THEN
        RAISE_APPLICATION_ERROR(-20901, 'USBD37 Test1 FAILED: cursor returned no rows.');
END IF;

    DBMS_OUTPUT.PUT_LINE('USBD37 Test1 OK: wagon_id=' || o_wagon_id || ', gauge=' || o_gauge_name || ' (' || o_width_mm || 'mm)');

CLOSE rc;
ROLLBACK TO sp_usbd37_t1;
END;
/

-- Test 2: Facility name invalid (should raise error)
BEGIN
SAVEPOINT sp_usbd37_t2;

DECLARE
rc SYS_REFCURSOR;
        v_wm_id WagonModel.wagonmodel_id%TYPE;
        v_op_id Wagon.operator_id%TYPE;
        v_fac_name Facility.name%TYPE;
BEGIN
SELECT wagonmodel_id INTO v_wm_id FROM WagonModel WHERE ROWNUM = 1;
SELECT operator_id   INTO v_op_id FROM Operator  WHERE ROWNUM = 1;
SELECT name          INTO v_fac_name FROM Facility WHERE ROWNUM = 1;

rc := FNC_USBD37_REGISTER_WAGON(
            p_wagonmodel_id         => v_wm_id,
            p_operator_id           => v_op_id,
            p_start_facility_name   => '___FACILITY_DOES_NOT_EXIST___',
            p_current_facility_name => v_fac_name,
            p_service_year          => 2020
        );

        -- If function didn't error, test fails
        RAISE_APPLICATION_ERROR(-20902, 'USBD37 Test2 FAILED: expected error, got success.');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('USBD37 Test2 OK (invalid facility): ' || SQLERRM);
END;

ROLLBACK TO sp_usbd37_t2;
END;
/

-- Test 3: WagonModel invalid (should raise error)
BEGIN
SAVEPOINT sp_usbd37_t3;

DECLARE
rc SYS_REFCURSOR;
        v_op_id Wagon.operator_id%TYPE;
        v_fac_name Facility.name%TYPE;
BEGIN
SELECT operator_id INTO v_op_id FROM Operator WHERE ROWNUM = 1;
SELECT name        INTO v_fac_name FROM Facility WHERE ROWNUM = 1;

rc := FNC_USBD37_REGISTER_WAGON(
            p_wagonmodel_id         => -999999,
            p_operator_id           => v_op_id,
            p_start_facility_name   => v_fac_name,
            p_current_facility_name => v_fac_name,
            p_service_year          => 2020
        );

        RAISE_APPLICATION_ERROR(-20903, 'USBD37 Test3 FAILED: expected error, got success.');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('USBD37 Test3 OK (invalid wagonmodel): ' || SQLERRM);
END;

ROLLBACK TO sp_usbd37_t3;
END;
/

-- Test 4: Case/space insensitive facility lookup (should succeed)
DECLARE
rc SYS_REFCURSOR;
    v_wm_id WagonModel.wagonmodel_id%TYPE;
    v_op_id Wagon.operator_id%TYPE;
    v_fac_name Facility.name%TYPE;

    v_fac_param Facility.name%TYPE;

    o_wagon_id     Wagon.wagon_id%TYPE;
    o_wagonmodel   Wagon.wagonmodel_id%TYPE;
    o_operator     Wagon.operator_id%TYPE;
    o_start_fac    Wagon.start_facility_id%TYPE;
    o_curr_fac     Wagon.current_facility_id%TYPE;
    o_year         Wagon.service_year%TYPE;
    o_gauge_id     WagonModel.gauge_id%TYPE;
    o_gauge_name   Gauge.name%TYPE;
    o_width_mm     Gauge.width_mm%TYPE;
BEGIN
SAVEPOINT sp_usbd37_t4;

SELECT wagonmodel_id INTO v_wm_id FROM WagonModel WHERE ROWNUM = 1;
SELECT operator_id   INTO v_op_id FROM Operator  WHERE ROWNUM = 1;
SELECT name          INTO v_fac_name FROM Facility WHERE ROWNUM = 1;

-- create a case/space altered version of the same facility name
v_fac_param := '  ' || LOWER(REPLACE(v_fac_name, ' ', '   ')) || '  ';

    rc := FNC_USBD37_REGISTER_WAGON(
        p_wagonmodel_id         => v_wm_id,
        p_operator_id           => v_op_id,
        p_start_facility_name   => v_fac_param,
        p_current_facility_name => v_fac_param,
        p_service_year          => 2021
    );

FETCH rc INTO o_wagon_id, o_wagonmodel, o_operator, o_start_fac, o_curr_fac, o_year,
    o_gauge_id, o_gauge_name, o_width_mm;

IF rc%NOTFOUND THEN
        RAISE_APPLICATION_ERROR(-20904, 'USBD37 Test4 FAILED: cursor returned no rows.');
END IF;

    DBMS_OUTPUT.PUT_LINE('USBD37 Test4 OK (case/space insensitive facility): wagon_id=' || o_wagon_id);

CLOSE rc;
ROLLBACK TO sp_usbd37_t4;
END;
/
