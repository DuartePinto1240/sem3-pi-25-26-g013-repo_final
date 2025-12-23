--------------------------------------------------------------------------------
-- USBD40 - Register a freight with wagons
-- Single file with full tests (BDDAD compliant)
--------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE pkg_freight IS
    FUNCTION register_freight (
        p_origin_facility_id      IN Freight.origin_facility_id%TYPE,
        p_destination_facility_id IN Freight.destination_facility_id%TYPE,
        p_wagonmodel_id           IN Wagon.wagonmodel_id%TYPE,
        p_number_of_wagons        IN NUMBER,
        p_depart_time             IN DATE,
        p_arrival_time            IN DATE
    ) RETURN Freight.freight_id%TYPE;
END pkg_freight;
/

--------------------------------------------------------------------------------
CREATE OR REPLACE PACKAGE BODY pkg_freight IS

    FUNCTION register_freight (
        p_origin_facility_id      IN Freight.origin_facility_id%TYPE,
        p_destination_facility_id IN Freight.destination_facility_id%TYPE,
        p_wagonmodel_id           IN Wagon.wagonmodel_id%TYPE,
        p_number_of_wagons        IN NUMBER,
        p_depart_time             IN DATE,
        p_arrival_time            IN DATE
    ) RETURN Freight.freight_id%TYPE
    IS
        v_freight_id NUMBER;
        v_count      NUMBER;
BEGIN
        ----------------------------------------------------------------
        -- Validate number of wagons
        ----------------------------------------------------------------
        IF p_number_of_wagons <= 0 THEN
            RAISE_APPLICATION_ERROR(-20100, 'Number of wagons must be positive.');
END IF;

        ----------------------------------------------------------------
        -- Validate facilities
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Facility
WHERE facility_id = p_origin_facility_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20101, 'Origin facility does not exist.');
END IF;

SELECT COUNT(*)
INTO v_count
FROM Facility
WHERE facility_id = p_destination_facility_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20102, 'Destination facility does not exist.');
END IF;

        ----------------------------------------------------------------
        -- Validate wagon model exists
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM WagonModel
WHERE wagonmodel_id = p_wagonmodel_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20105, 'Wagon model does not exist.');
END IF;

        ----------------------------------------------------------------
        -- Validate dates
        ----------------------------------------------------------------
        IF p_arrival_time < p_depart_time THEN
            RAISE_APPLICATION_ERROR(-20103, 'Arrival before departure.');
END IF;

        ----------------------------------------------------------------
        -- Validate number of free wagons
        -- Free = not in FreightWagon AND not in TrainWagon
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Wagon w
WHERE w.wagonmodel_id = p_wagonmodel_id
  AND w.current_facility_id = p_origin_facility_id
  AND NOT EXISTS (
    SELECT 1
    FROM FreightWagon fw
    WHERE fw.wagon_id = w.wagon_id
)
  AND NOT EXISTS (
    SELECT 1
    FROM TrainWagon tw
    WHERE tw.wagon_id = w.wagon_id
);

IF v_count < p_number_of_wagons THEN
            RAISE_APPLICATION_ERROR(-20104,
                'Not enough free wagons. Available: ' || v_count || ', Required: ' || p_number_of_wagons);
END IF;

        ----------------------------------------------------------------
        -- Generate freight_id
        ----------------------------------------------------------------
SELECT NVL(MAX(freight_id), 0) + 1
INTO v_freight_id
FROM Freight;

----------------------------------------------------------------
-- Insert freight
----------------------------------------------------------------
INSERT INTO Freight (
    freight_id,
    origin_facility_id,
    destination_facility_id,
    train_id,
    depart_time,
    arrival_time
)
VALUES (
           v_freight_id,
           p_origin_facility_id,
           p_destination_facility_id,
           NULL,
           p_depart_time,
           p_arrival_time
       );

----------------------------------------------------------------
-- Assign wagons
----------------------------------------------------------------
FOR r IN (
            SELECT wagon_id
            FROM Wagon w
            WHERE w.wagonmodel_id = p_wagonmodel_id
              AND w.current_facility_id = p_origin_facility_id
              AND NOT EXISTS (
                  SELECT 1
                  FROM FreightWagon fw
                  WHERE fw.wagon_id = w.wagon_id
              )
              AND NOT EXISTS (
                  SELECT 1
                  FROM TrainWagon tw
                  WHERE tw.wagon_id = w.wagon_id
              )
              AND ROWNUM <= p_number_of_wagons
        ) LOOP
            INSERT INTO FreightWagon (freight_id, wagon_id)
            VALUES (v_freight_id, r.wagon_id);
END LOOP;

RETURN v_freight_id;
END register_freight;

END pkg_freight;
/

--------------------------------------------------------------------------------
-- COMPREHENSIVE TESTS (anonymous blocks in the SAME file)
--------------------------------------------------------------------------------

SET SERVEROUTPUT ON;

DECLARE
v_origin_facility_id      NUMBER;
    v_destination_facility_id NUMBER;
    v_another_facility_id     NUMBER;
    v_wagonmodel_id           NUMBER;
    v_another_wagonmodel_id   NUMBER;
    v_freight_id              NUMBER;
    v_test_wagon_id           NUMBER;
    v_test_freight_id         NUMBER;
    v_test_train_id           NUMBER;
    v_count                   NUMBER;
BEGIN
SAVEPOINT test_start;

--------------------------------------------------------------------
-- Setup test data
--------------------------------------------------------------------
DBMS_OUTPUT.PUT_LINE('=== SETTING UP TEST DATA ===');

    -- Create facilities
SELECT NVL(MAX(facility_id), 0) + 1 INTO v_origin_facility_id FROM Facility;
INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_origin_facility_id, 1, 'Test_Origin', 'City A');

SELECT NVL(MAX(facility_id), 0) + 1 INTO v_destination_facility_id FROM Facility;
INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_destination_facility_id, 1, 'Test_Destination', 'City B');

SELECT NVL(MAX(facility_id), 0) + 1 INTO v_another_facility_id FROM Facility;
INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_another_facility_id, 1, 'Test_Another', 'City C');

-- Create wagon models
SELECT NVL(MAX(wagonmodel_id), 0) + 1 INTO v_wagonmodel_id FROM WagonModel;
INSERT INTO WagonModel (
    wagonmodel_id, wagontype_id, gauge_id, model_id, wagoncategory_id,
    payload_t, volume_m3
) VALUES (v_wagonmodel_id, 1, 1668, 1, 1, 10, 20);

SELECT NVL(MAX(wagonmodel_id), 0) + 1 INTO v_another_wagonmodel_id FROM WagonModel;
INSERT INTO WagonModel (
    wagonmodel_id, wagontype_id, gauge_id, model_id, wagoncategory_id,
    payload_t, volume_m3
) VALUES (v_another_wagonmodel_id, 2, 1668, 2, 2, 15, 30);

-- Create a test train for TrainWagon tests
SELECT NVL(MAX(train_id), 0) + 1 INTO v_test_train_id FROM Train;
INSERT INTO Train (train_id, operator_id, name, max_length_m)
VALUES (v_test_train_id, 2, 'TEST_TRN', 500);

-- Create a test freight for FreightWagon tests
SELECT NVL(MAX(freight_id), 0) + 1 INTO v_test_freight_id FROM Freight;
INSERT INTO Freight (freight_id, origin_facility_id, destination_facility_id,
                     train_id, depart_time, arrival_time)
VALUES (v_test_freight_id, v_origin_facility_id, v_destination_facility_id,
        NULL, SYSDATE, SYSDATE + 1);

-- Create 5 free wagons at origin
FOR i IN 1..5 LOOP
SELECT NVL(MAX(wagon_id), 0) + 1 INTO v_test_wagon_id FROM Wagon;
INSERT INTO Wagon (
    wagon_id, wagonmodel_id, operator_id,
    start_facility_id, current_facility_id, service_year
)
VALUES (
           v_test_wagon_id, v_wagonmodel_id, 2,
           v_origin_facility_id, v_origin_facility_id, 2020
       );
END LOOP;

    -- Create 2 wagons at origin but already in FreightWagon
FOR i IN 1..2 LOOP
SELECT NVL(MAX(wagon_id), 0) + 1 INTO v_test_wagon_id FROM Wagon;
INSERT INTO Wagon (
    wagon_id, wagonmodel_id, operator_id,
    start_facility_id, current_facility_id, service_year
)
VALUES (
           v_test_wagon_id, v_wagonmodel_id, 2,
           v_origin_facility_id, v_origin_facility_id, 2020
       );
-- Assign to test freight
INSERT INTO FreightWagon (freight_id, wagon_id)
VALUES (v_test_freight_id, v_test_wagon_id);
END LOOP;

    -- Create 1 wagon at origin but already in TrainWagon
SELECT NVL(MAX(wagon_id), 0) + 1 INTO v_test_wagon_id FROM Wagon;
INSERT INTO Wagon (
    wagon_id, wagonmodel_id, operator_id,
    start_facility_id, current_facility_id, service_year
)
VALUES (
           v_test_wagon_id, v_wagonmodel_id, 2,
           v_origin_facility_id, v_origin_facility_id, 2020
       );
-- Assign to test train
INSERT INTO TrainWagon (train_id, wagon_id)
VALUES (v_test_train_id, v_test_wagon_id);

-- Create 2 wagons of same model but at different facility
FOR i IN 1..2 LOOP
SELECT NVL(MAX(wagon_id), 0) + 1 INTO v_test_wagon_id FROM Wagon;
INSERT INTO Wagon (
    wagon_id, wagonmodel_id, operator_id,
    start_facility_id, current_facility_id, service_year
)
VALUES (
           v_test_wagon_id, v_wagonmodel_id, 2,
           v_another_facility_id, v_another_facility_id, 2020
       );
END LOOP;

    DBMS_OUTPUT.PUT_LINE('Test data created successfully.');
    DBMS_OUTPUT.PUT_LINE('  - Created 5 free wagons at origin');
    DBMS_OUTPUT.PUT_LINE('  - Created 2 wagons in FreightWagon');
    DBMS_OUTPUT.PUT_LINE('  - Created 1 wagon in TrainWagon');
    DBMS_OUTPUT.PUT_LINE('  - Created 2 wagons at different facility');
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 1: Happy path - Register freight with 3 wagons
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 1: Happy Path ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            v_wagonmodel_id,
            3,
            DATE '2025-01-01',
            DATE '2025-01-02'
        );

        -- Verify freight was created
SELECT COUNT(*) INTO v_count
FROM Freight
WHERE freight_id = v_freight_id;

IF v_count = 1 THEN
            -- Verify wagons were assigned
SELECT COUNT(*) INTO v_count
FROM FreightWagon
WHERE freight_id = v_freight_id;

IF v_count = 3 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 1 PASSED - Freight ID: ' || v_freight_id || ', Wagons: ' || v_count);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 1 FAILED - Wrong number of wagons: ' || v_count);
END IF;
ELSE
            DBMS_OUTPUT.PUT_LINE('✗ TEST 1 FAILED - Freight not created');
END IF;
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✗ TEST 1 FAILED - ' || SQLERRM);
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 2: Not enough free wagons (only 2 free left, requesting 3)
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 2: Insufficient Free Wagons ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            v_wagonmodel_id,
            3,
            DATE '2025-01-03',
            DATE '2025-01-04'
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 2 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20104 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 2 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 2 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 3: Invalid origin facility
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 3: Invalid Origin Facility ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            99999,
            v_destination_facility_id,
            v_wagonmodel_id,
            1,
            DATE '2025-01-05',
            DATE '2025-01-06'
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 3 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20101 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 3 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 3 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 4: Invalid destination facility
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 4: Invalid Destination Facility ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            99999,
            v_wagonmodel_id,
            1,
            DATE '2025-01-07',
            DATE '2025-01-08'
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 4 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20102 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 4 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 4 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 5: Invalid dates (arrival before departure)
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 5: Invalid Dates ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            v_wagonmodel_id,
            1,
            DATE '2025-01-10',
            DATE '2025-01-09'
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 5 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20103 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 5 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 5 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 6: Invalid wagon model
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 6: Invalid Wagon Model ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            99999,
            1,
            DATE '2025-01-11',
            DATE '2025-01-12'
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 6 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20105 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 6 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 6 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 7: Zero wagons requested
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 7: Zero Wagons ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            v_wagonmodel_id,
            0,
            DATE '2025-01-13',
            DATE '2025-01-14'
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 7 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20100 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 7 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 7 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 8: Negative wagons requested
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 8: Negative Wagons ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            v_wagonmodel_id,
            -5,
            DATE '2025-01-15',
            DATE '2025-01-16'
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 8 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20100 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 8 PASSED - Correctly rejected: ' || SQLERRM);
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 8 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 9: Same departure and arrival time (should be allowed)
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 9: Same Departure and Arrival Time ===');
BEGIN
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            v_wagonmodel_id,
            1,
            DATE '2025-01-17',
            DATE '2025-01-17'
        );
        DBMS_OUTPUT.PUT_LINE('✓ TEST 9 PASSED - Freight ID: ' || v_freight_id);
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✗ TEST 9 FAILED - ' || SQLERRM);
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 10: Verify wagons at wrong facility are not used
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 10: Wagons at Different Facility Not Used ===');
BEGIN
        -- Request 1 wagon from origin (only 1 free left after previous tests)
        v_freight_id := pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            v_wagonmodel_id,
            1,
            DATE '2025-01-18',
            DATE '2025-01-19'
        );

        -- Verify the wagon is from origin facility
SELECT COUNT(*) INTO v_count
FROM FreightWagon fw
         JOIN Wagon w ON fw.wagon_id = w.wagon_id
WHERE fw.freight_id = v_freight_id
  AND w.current_facility_id = v_origin_facility_id;

IF v_count = 1 THEN
            DBMS_OUTPUT.PUT_LINE('✓ TEST 10 PASSED - Wagon from correct facility');
ELSE
            DBMS_OUTPUT.PUT_LINE('✗ TEST 10 FAILED - Wagon from wrong facility');
END IF;
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✗ TEST 10 FAILED - ' || SQLERRM);
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- TEST 11: Verify wagons in TrainWagon are not used
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 11: Wagons in TrainWagon Not Used ===');
BEGIN
        -- Count free wagons before
SELECT COUNT(*) INTO v_count
FROM Wagon w
WHERE w.wagonmodel_id = v_wagonmodel_id
  AND w.current_facility_id = v_origin_facility_id
  AND NOT EXISTS (SELECT 1 FROM FreightWagon fw WHERE fw.wagon_id = w.wagon_id)
  AND NOT EXISTS (SELECT 1 FROM TrainWagon tw WHERE tw.wagon_id = w.wagon_id);

DBMS_OUTPUT.PUT_LINE('Free wagons remaining: ' || v_count);

        IF v_count = 0 THEN
            DBMS_OUTPUT.PUT_LINE('✓ TEST 11 PASSED - All free wagons were used, wagon in TrainWagon was excluded');
ELSE
            DBMS_OUTPUT.PUT_LINE('✗ TEST 11 FAILED - Expected 0 free wagons, found ' || v_count);
END IF;
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✗ TEST 11 FAILED - ' || SQLERRM);
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