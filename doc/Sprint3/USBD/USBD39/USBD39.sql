CREATE OR REPLACE FUNCTION fnc_usbd39_associate_loco(
    p_train_id IN Train.train_id%TYPE,      -- Good: Adapts if ID becomes String
    p_loco_id  IN Locomotive.loco_id%TYPE   -- Good: Adapts if ID becomes String
) RETURN VARCHAR2 IS
    -- These hold 'counts' (0 or 1), so they must always be NUMBER.
    v_train_check NUMBER;
    v_loco_check  NUMBER;
    v_busy_check  NUMBER;
BEGIN
    -- 1. Validate Train Existence
SELECT COUNT(*) INTO v_train_check
FROM Train
WHERE train_id = p_train_id;

IF v_train_check = 0 THEN
        RETURN 'Error: Train ID ' || p_train_id || ' not found.';
END IF;

    -- 2. Validate Locomotive Existence
SELECT COUNT(*) INTO v_loco_check
FROM Locomotive
WHERE loco_id = p_loco_id;

IF v_loco_check = 0 THEN
        RETURN 'Error: Locomotive ID ' || p_loco_id || ' not found.';
END IF;

    -- 3. Validate Business Rule (Unique)
SELECT COUNT(*) INTO v_busy_check
FROM TrainLocomotive
WHERE loco_id = p_loco_id;

IF v_busy_check > 0 THEN
        RETURN 'Error: Locomotive ' || p_loco_id || ' is already assigned to a train.';
END IF;

    -- 4. Perform Association
INSERT INTO TrainLocomotive (train_id, loco_id)
VALUES (p_train_id, p_loco_id);

RETURN 'Success: Locomotive ' || p_loco_id || ' assigned to Train ' || p_train_id;

EXCEPTION
    -- Catch duplicates if the check passed but a race condition occurred
    WHEN DUP_VAL_ON_INDEX THEN
        RETURN 'Error: Integrity Constraint Violation - This pair likely already exists.';
WHEN OTHERS THEN
        RETURN 'Error: SQL Exception - ' || SQLERRM;
END;
/

SET SERVEROUTPUT ON;

DECLARE
v_result VARCHAR2(1000);

    -- =================================================================
    -- VARIABLES: Using %TYPE to adapt to schema changes
    -- =================================================================
    v_company_id    Company.company_id%TYPE           := -10;
    v_operator_id   Operator.operator_id%TYPE         := -10;
    v_ftype_id      FacilityType.facilitytype_id%TYPE := -10;
    v_facility_id   Facility.facility_id%TYPE         := -10;
    v_gauge_id      Gauge.gauge_id%TYPE               := -10;
    v_model_id      Model.model_id%TYPE               := -10;
    v_loco_model_id LocomotiveModel.locomotive_model_id%TYPE := -10;

    -- Test Targets
    v_train_id      Train.train_id%TYPE           := -100;
    v_loco_id_1     Locomotive.loco_id%TYPE       := -200;
    v_loco_id_2     Locomotive.loco_id%TYPE       := -201;
    v_loco_id_bad   Locomotive.loco_id%TYPE       := -999;

BEGIN
    DBMS_OUTPUT.PUT_LINE('=== USBD39 TEST SUITE STARTED ===');

    -- =========================================================
    -- 1. SETUP: Create Minimal Dependency Graph
    -- =========================================================

    DBMS_OUTPUT.PUT_LINE('Step 1: Inserting Dependencies...');

INSERT INTO Company (company_id, name, short_name, vat_number)
VALUES (v_company_id, 'TestComp', 'TC', '999999990');

INSERT INTO Operator (operator_id, country)
VALUES (v_operator_id, 'TestCountry');

INSERT INTO FacilityType (facilitytype_id, type)
VALUES (v_ftype_id, 'TestStation');

INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_facility_id, v_ftype_id, 'TestFacility', 'TestLoc');

INSERT INTO Gauge (gauge_id, name, width_mm)
VALUES (v_gauge_id, 'TestGauge', 1000);

INSERT INTO Model (model_id, name, maker, max_speed_kmh, number_of_bogies, bogies, length_m, height_m, weight_t, width_m)
VALUES (v_model_id, 'TestModel', 'Maker', 100, 2, 'BoBo', 20, 4, 80, 3);

INSERT INTO LocomotiveModel (locomotive_model_id, model_id, gauge_id, power_kw, traction_kn, operational_speed_kmh)
VALUES (v_loco_model_id, v_model_id, v_gauge_id, 5000, 300, 100);

-- =========================================================
-- 2. SETUP: Create Test Targets
-- =========================================================

DBMS_OUTPUT.PUT_LINE('Step 2: Inserting Test Targets...');

INSERT INTO Train (train_id, operator_id, name, max_length_m)
VALUES (v_train_id, v_operator_id, 'TEST-T', 500);

INSERT INTO Locomotive (loco_id, operator_id, locomotive_model_id, start_facility_id, name, service_year)
VALUES (v_loco_id_1, v_operator_id, v_loco_model_id, v_facility_id, 'LOCO-1', 2020);

INSERT INTO Locomotive (loco_id, operator_id, locomotive_model_id, start_facility_id, name, service_year)
VALUES (v_loco_id_2, v_operator_id, v_loco_model_id, v_facility_id, 'LOCO-2', 2020);

DBMS_OUTPUT.PUT_LINE('Setup Complete. Running Function Tests...');
    DBMS_OUTPUT.PUT_LINE('--------------------------------------------------');

    -- =========================================================
    -- TEST 1: Happy Path
    -- =========================================================
    v_result := fnc_usbd39_associate_loco(v_train_id, v_loco_id_1);
    DBMS_OUTPUT.PUT_LINE('Test 1 (Happy Path): ' || v_result);

    IF v_result LIKE '%Success%' THEN
        DBMS_OUTPUT.PUT_LINE('-> STATUS: PASSED');
ELSE
        DBMS_OUTPUT.PUT_LINE('-> STATUS: FAILED');
END IF;

    -- =========================================================
    -- TEST 2: Invalid Locomotive ID
    -- =========================================================
    v_result := fnc_usbd39_associate_loco(v_train_id, v_loco_id_bad);
    DBMS_OUTPUT.PUT_LINE('Test 2 (Invalid Loco): ' || v_result);

    IF v_result LIKE '%Error%' AND v_result LIKE '%not found%' THEN
        DBMS_OUTPUT.PUT_LINE('-> STATUS: PASSED');
ELSE
        DBMS_OUTPUT.PUT_LINE('-> STATUS: FAILED');
END IF;

    -- =========================================================
    -- TEST 3: Business Rule - Loco already assigned
    -- =========================================================
    v_result := fnc_usbd39_associate_loco(v_train_id, v_loco_id_1);
    DBMS_OUTPUT.PUT_LINE('Test 3 (Already Assigned): ' || v_result);

    IF v_result LIKE '%Error%' AND v_result LIKE '%already assigned%' THEN
        DBMS_OUTPUT.PUT_LINE('-> STATUS: PASSED');
ELSE
        DBMS_OUTPUT.PUT_LINE('-> STATUS: FAILED');
END IF;

    -- =========================================================
    -- TEARDOWN
    -- =========================================================
    DBMS_OUTPUT.PUT_LINE('--------------------------------------------------');
ROLLBACK;
DBMS_OUTPUT.PUT_LINE('Test data rolled back. Database state restored.');

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('TEST ERROR: Unique Constraint Violation.');
ROLLBACK;
WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('FATAL ERROR IN TEST BLOCK: ' || SQLERRM);
ROLLBACK;
END;
/