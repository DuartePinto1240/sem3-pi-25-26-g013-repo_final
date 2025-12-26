--------------------------------------------------------------------------------
-- USBD36 - Add a building type to an existing facility
--------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE pkg_building IS
    -- Changed to PROCEDURE because FacilityBuilding uses a composite PK (no single ID to return)
    PROCEDURE register_building_association (
        p_facility_id     IN Facility.facility_id%TYPE,
        p_buildingtype_id IN BuildingType.buildingtype_id%TYPE
    );
END pkg_building;
/

--------------------------------------------------------------------------------
CREATE OR REPLACE PACKAGE BODY pkg_building IS

    PROCEDURE register_building_association (
        p_facility_id     IN Facility.facility_id%TYPE,
        p_buildingtype_id IN BuildingType.buildingtype_id%TYPE
    )
    IS
        v_count NUMBER;
BEGIN
        ----------------------------------------------------------------
        -- Validate Facility existence
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Facility
WHERE facility_id = p_facility_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Facility does not exist.');
END IF;

        ----------------------------------------------------------------
        -- Validate Building Type existence
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM BuildingType
WHERE buildingtype_id = p_buildingtype_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Building Type does not exist.');
END IF;

        ----------------------------------------------------------------
        -- Check if association already exists
        -- (FacilityBuilding PK is facility_id + buildingtype_id)
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM FacilityBuilding
WHERE facility_id = p_facility_id
  AND buildingtype_id = p_buildingtype_id;

IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20003, 'This facility already has a building of this type.');
END IF;

        ----------------------------------------------------------------
        -- Insert Association
        ----------------------------------------------------------------
INSERT INTO FacilityBuilding (facility_id, buildingtype_id)
VALUES (p_facility_id, p_buildingtype_id);

END register_building_association;

END pkg_building;
/

--------------------------------------------------------------------------------
-- TESTS (Compatible with provided DDL/Inserts)
--------------------------------------------------------------------------------
SET SERVEROUTPUT ON;

DECLARE
v_facility_id     NUMBER;
    v_buildingtype_id NUMBER;
    v_count           NUMBER;
BEGIN
SAVEPOINT test_start;

-- 1. Setup Data from your INSERTS
-- Facility 50 = Leixões
v_facility_id := 50;

    -- BuildingType 2 = 'Maintenance Shed' (Assuming from your inserts)
    -- Your inserts had: 1=Warehouse, 2=Maintenance Shed, 3=Office
    v_buildingtype_id := 2;

    DBMS_OUTPUT.PUT_LINE('=== TEST SETUP ===');
    DBMS_OUTPUT.PUT_LINE('Facility: ' || v_facility_id);
    DBMS_OUTPUT.PUT_LINE('Building Type: ' || v_buildingtype_id);
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- Test 1: Happy path - Add Maintenance Shed to Leixões
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 1: Add Building Type to Facility ===');
BEGIN
        pkg_building.register_building_association(
            p_facility_id     => v_facility_id,
            p_buildingtype_id => v_buildingtype_id
        );

        -- Verify insertion
SELECT COUNT(*) INTO v_count
FROM FacilityBuilding
WHERE facility_id = v_facility_id
  AND buildingtype_id = v_buildingtype_id;

IF v_count = 1 THEN
            DBMS_OUTPUT.PUT_LINE('✓ TEST 1 PASSED - Association created.');
ELSE
            DBMS_OUTPUT.PUT_LINE('✗ TEST 1 FAILED - Record not found.');
END IF;
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('✗ TEST 1 FAILED - ' || SQLERRM);
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- Test 2: Duplicate insertion (Constraint Check)
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 2: Duplicate Insertion ===');
BEGIN
        pkg_building.register_building_association(
            p_facility_id     => v_facility_id,
            p_buildingtype_id => v_buildingtype_id -- Same as Test 1
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 2 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20003 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 2 PASSED - Duplicate correctly rejected.');
            ELSIF SQLCODE = -00001 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 2 PASSED - PK Constraint caught duplicate.');
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 2 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- Test 3: Invalid Facility
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 3: Invalid Facility ===');
BEGIN
        pkg_building.register_building_association(
            p_facility_id     => 99999,
            p_buildingtype_id => v_buildingtype_id
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 3 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20001 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 3 PASSED - Correctly rejected.');
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 3 FAILED - Wrong error: ' || SQLERRM);
END IF;
END;
    DBMS_OUTPUT.PUT_LINE('');

    --------------------------------------------------------------------
    -- Test 4: Invalid Building Type
    --------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE('=== TEST 4: Invalid Building Type ===');
BEGIN
        pkg_building.register_building_association(
            p_facility_id     => v_facility_id,
            p_buildingtype_id => 99999
        );
        DBMS_OUTPUT.PUT_LINE('✗ TEST 4 FAILED - Should have been rejected');
EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20002 THEN
                DBMS_OUTPUT.PUT_LINE('✓ TEST 4 PASSED - Correctly rejected.');
ELSE
                DBMS_OUTPUT.PUT_LINE('✗ TEST 4 FAILED - Wrong error: ' || SQLERRM);
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