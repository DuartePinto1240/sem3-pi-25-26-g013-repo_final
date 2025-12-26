--------------------------------------------------------------------------------
-- USBD42 - Register a train movement
--------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE pkg_train_manager IS
    -- Procedure to register a train movement
    -- Changed to PROCEDURE because there is no single ID to return (Composite PK)
    PROCEDURE register_movement (
        p_train_id        IN Train.train_id%TYPE,
        p_facility_id     IN Facility.facility_id%TYPE,
        p_arrival_time    IN DATE,
        p_departure_time  IN DATE DEFAULT NULL
    );
END pkg_train_manager;
/

--------------------------------------------------------------------------------
CREATE OR REPLACE PACKAGE BODY pkg_train_manager IS

    PROCEDURE register_movement (
        p_train_id        IN Train.train_id%TYPE,
        p_facility_id     IN Facility.facility_id%TYPE,
        p_arrival_time    IN DATE,
        p_departure_time  IN DATE DEFAULT NULL
    )
    IS
        v_count       NUMBER;
BEGIN
        ----------------------------------------------------------------
        -- Validate Train exists
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Train
WHERE train_id = p_train_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20201, 'Train does not exist.');
END IF;

        ----------------------------------------------------------------
        -- Validate Facility exists
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Facility
WHERE facility_id = p_facility_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20202, 'Facility does not exist.');
END IF;

        ----------------------------------------------------------------
        -- Validate Dates
        ----------------------------------------------------------------
        IF p_arrival_time IS NULL THEN
             RAISE_APPLICATION_ERROR(-20203, 'Arrival time is mandatory.');
END IF;

        IF p_departure_time IS NOT NULL AND p_departure_time < p_arrival_time THEN
            RAISE_APPLICATION_ERROR(-20204, 'Departure time cannot be before arrival time.');
END IF;

        ----------------------------------------------------------------
        -- Check for Duplicate PK (Train + Facility + Arrival)
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM TrainMovement
WHERE train_id = p_train_id
  AND facility_id = p_facility_id
  AND arrival_time = p_arrival_time;

IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20205, 'Movement already registered for this train, facility and arrival time.');
END IF;

        ----------------------------------------------------------------
        -- Insert Movement (No movement_id generated)
        ----------------------------------------------------------------
INSERT INTO TrainMovement (
    train_id,
    facility_id,
    arrival_time,
    departure_time
)
VALUES (
           p_train_id,
           p_facility_id,
           p_arrival_time,
           p_departure_time
       );

-- Commit is usually handled by the calling application,
-- but can be included if strictly required by assignment.
-- COMMIT;

END register_movement;

END pkg_train_manager;
/

--------------------------------------------------------------------------------
-- COMPREHENSIVE TESTS
-- Adapted to use REAL data from your inserts (Train 5421, Facility 50)
--------------------------------------------------------------------------------

SET SERVEROUTPUT ON;

DECLARE
v_train_id        NUMBER;
    v_facility_id     NUMBER;
    v_count           NUMBER;
    v_test_arrival    DATE;
BEGIN
SAVEPOINT test_start;

-- Using data we KNOW exists from your script
v_train_id    := 5421; -- Train T5421
    v_facility_id := 50;   -- Leixões

    DBMS_OUTPUT.PUT_LINE('=== SETUP INFO ===');
    DBMS_OUTPUT.PUT_LINE('Test Train ID: ' || v_train_id);
    DBMS_OUTPUT.PUT_LINE('Test Facility ID: ' || v_facility_id);
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 1: Happy path - Train Stopped (Arrival AND Departure)
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 1: Happy Path (Stop recorded) ===');
    v_test_arrival := TO_DATE('2025-02-01 10:00', 'YYYY-MM-DD HH24:MI');

BEGIN
        pkg_train_manager.register_movement(
            p_train_id       => v_train_id,
            p_facility_id    => v_facility_id,
            p_arrival_time   => v_test_arrival,
            p_departure_time => TO_DATE('2025-02-01 11:00', 'YYYY-MM-DD HH24:MI')
        );

        -- Verify insertion using Composite PK
SELECT COUNT(*) INTO v_count
FROM TrainMovement
WHERE train_id = v_train_id
  AND facility_id = v_facility_id
  AND arrival_time = v_test_arrival
  AND departure_time IS NOT NULL;

IF v_count = 1 THEN
            DBMS_OUTPUT.PUT_LINE('✓ TEST 1 PASSED - Movement registered successfully.');
ELSE
            DBMS_OUTPUT.PUT_LINE('✗ TEST 1 FAILED - Record not found.');
END IF;
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✗ TEST 1 FAILED - ' || SQLERRM);
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 2: Happy path - Train Just Arrived (No Departure yet)
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 2: Happy Path (Arrival Only) ===');
    v_test_arrival := TO_DATE('2025-02-02 14:00', 'YYYY-MM-DD HH24:MI');

BEGIN
        pkg_train_manager.register_movement(
            p_train_id       => v_train_id,
            p_facility_id    => v_facility_id,
            p_arrival_time   => v_test_arrival,
            p_departure_time => NULL
        );

        -- Verify insertion
SELECT COUNT(*) INTO v_count
FROM TrainMovement
WHERE train_id = v_train_id
  AND facility_id = v_facility_id
  AND arrival_time = v_test_arrival
  AND departure_time IS NULL;

IF v_count = 1 THEN
            DBMS_OUTPUT.PUT_LINE('✓ TEST 2 PASSED - Movement registered (no departure).');
ELSE
            DBMS_OUTPUT.PUT_LINE('✗ TEST 2 FAILED - Record not found.');
END IF;
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✗ TEST 2 FAILED - ' || SQLERRM);
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 3: Invalid Train ID
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 3: Invalid Train ID ===');
BEGIN
        pkg_train_manager.register_movement(
            p_train_id       => 99999, -- Non-existent
            p_facility_id    => v_facility_id,
            p_arrival_time   => SYSDATE
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 3 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20201 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 3 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 3 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 4: Invalid Facility ID
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 4: Invalid Facility ID ===');
BEGIN
        pkg_train_manager.register_movement(
            p_train_id       => v_train_id,
            p_facility_id    => 99999, -- Non-existent
            p_arrival_time   => SYSDATE
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 4 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20202 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 4 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 4 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 5: Duplicate PK check
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 5: Duplicate Entry ===');
    v_test_arrival := TO_DATE('2025-02-01 10:00', 'YYYY-MM-DD HH24:MI'); -- Same as Test 1
BEGIN
        pkg_train_manager.register_movement(
            p_train_id       => v_train_id,
            p_facility_id    => v_facility_id,
            p_arrival_time   => v_test_arrival -- Already exists from Test 1
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 5 FAILED - Should have been rejected (PK Violation custom check)');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20205 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 5 PASSED - Custom error caught duplicate.');
            ELSIF SQLCODE = -00001 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 5 PASSED - Oracle PK constraint caught duplicate.');
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 5 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 6: Invalid Dates (Departure before Arrival)
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 6: Invalid Dates ===');
BEGIN
        pkg_train_manager.register_movement(
            p_train_id       => v_train_id,
            p_facility_id    => v_facility_id,
            p_arrival_time   => DATE '2025-05-10',
            p_departure_time => DATE '2025-05-09' -- Before arrival
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 6 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20204 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 6 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 6 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- Cleanup
    --------------------------------------------------------------------
ROLLBACK TO test_start;
DBMS_OUTPUT.PUT_LINE('=== ALL TESTS COMPLETED - DATA ROLLED BACK ===');

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('CRITICAL ERROR IN TEST SETUP: ' || SQLERRM);
ROLLBACK TO test_start;
END;
/