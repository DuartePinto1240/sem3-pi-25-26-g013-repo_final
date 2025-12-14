--------------------------------------------------------------------------------
-- USBD35 - Register a new facility
-- Single file with full tests (BDDAD compliant)
--------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE pkg_facility IS
    FUNCTION register_facility (
        p_facilitytype_id IN Facility.facilitytype_id%TYPE,
        p_name            IN Facility.name%TYPE,
        p_location        IN Facility.location%TYPE
    ) RETURN Facility.facility_id%TYPE;
END pkg_facility;
/
--------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE BODY pkg_facility IS

    FUNCTION register_facility (
        p_facilitytype_id IN Facility.facilitytype_id%TYPE,
        p_name            IN Facility.name%TYPE,
        p_location        IN Facility.location%TYPE
    ) RETURN Facility.facility_id%TYPE
    IS
        v_facility_id Facility.facility_id%TYPE;
        v_count       NUMBER;
BEGIN
        -- Validate FacilityType existence
SELECT COUNT(*)
INTO v_count
FROM FacilityType
WHERE facilitytype_id = p_facilitytype_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Facility type does not exist.');
END IF;

        -- Case and space insensitive uniqueness validation
SELECT COUNT(*)
INTO v_count
FROM Facility
WHERE UPPER(TRIM(name)) = UPPER(TRIM(p_name));

IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Facility name already exists.');
END IF;

INSERT INTO Facility (facilitytype_id, name, location)
VALUES (p_facilitytype_id, TRIM(p_name), p_location)
    RETURNING facility_id INTO v_facility_id;

RETURN v_facility_id;
END register_facility;

END pkg_facility;
/
--------------------------------------------------------------------------------
-- TESTS (anonymous blocks in the SAME file)
--------------------------------------------------------------------------------

DECLARE
v_facilitytype_id FacilityType.facilitytype_id%TYPE;
    v_facility_id     Facility.facility_id%TYPE;
BEGIN
SAVEPOINT test_start;

--------------------------------------------------------------------
-- Test setup (allowed: inside the test)
--------------------------------------------------------------------
INSERT INTO FacilityType (type)
VALUES ('Terminal')
    RETURNING facilitytype_id INTO v_facilitytype_id;

--------------------------------------------------------------------
-- Test 1: Happy path
--------------------------------------------------------------------
v_facility_id := pkg_facility.register_facility(
        v_facilitytype_id,
        ' Porto Hub ',
        'Porto'
    );

    DBMS_OUTPUT.PUT_LINE('Test 1 OK - Facility ID = ' || v_facility_id);

    --------------------------------------------------------------------
    -- Test 2: Duplicate name (case + space insensitive)
    --------------------------------------------------------------------
BEGIN
        pkg_facility.register_facility(
            v_facilitytype_id,
            'porto hub',
            'Another location'
        );
        DBMS_OUTPUT.PUT_LINE('Test 2 FAILED');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 2 OK - Duplicate name rejected');
END;

    --------------------------------------------------------------------
    -- Test 3: Invalid facility type
    --------------------------------------------------------------------
BEGIN
        pkg_facility.register_facility(
            -1,
            'Lisbon Hub',
            'Lisbon'
        );
        DBMS_OUTPUT.PUT_LINE('Test 3 FAILED');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 3 OK - Invalid facility type rejected');
END;

    --------------------------------------------------------------------
    -- Cleanup
    --------------------------------------------------------------------
ROLLBACK TO test_start;
DBMS_OUTPUT.PUT_LINE('All tests executed successfully.');

END;
/
--------------------------------------------------------------------------------
