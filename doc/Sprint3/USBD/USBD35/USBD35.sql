


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
        ----------------------------------------------------------------
        -- Validate FacilityType existence
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM FacilityType
WHERE facilitytype_id = p_facilitytype_id;

IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Facility type does not exist.');
END IF;

        ----------------------------------------------------------------
        -- Case and space insensitive uniqueness validation
        ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Facility
WHERE UPPER(REPLACE(TRIM(name), ' ', '')) = UPPER(REPLACE(TRIM(p_name), ' ', ''));

IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Facility name already exists.');
END IF;

        ----------------------------------------------------------------
        -- Generate new facility_id (no sequences, no identity)
        ----------------------------------------------------------------
SELECT NVL(MAX(facility_id), 0) + 1
INTO v_facility_id
FROM Facility;

----------------------------------------------------------------
-- Insert Facility
----------------------------------------------------------------
INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_facility_id, p_facilitytype_id, TRIM(p_name), p_location);

RETURN v_facility_id;
END register_facility;

END pkg_facility;
/
--------------------------------------------------------------------------------
-- TESTS (anonymous blocks in the SAME file)
-- NO PRE-INSERTS: Uses existing FacilityType from populate script
--------------------------------------------------------------------------------

DECLARE
v_facilitytype_id FacilityType.facilitytype_id%TYPE;
    v_facility_id     Facility.facility_id%TYPE;
    v_dummy_id        Facility.facility_id%TYPE;
BEGIN
SAVEPOINT test_start;

--------------------------------------------------------------------
-- Get existing FacilityType (from populate script - document 2)
--------------------------------------------------------------------
SELECT facilitytype_id
INTO v_facilitytype_id
FROM FacilityType
WHERE ROWNUM = 1;

--------------------------------------------------------------------
-- Test 1: Happy path
--------------------------------------------------------------------
v_facility_id := pkg_facility.register_facility(
        v_facilitytype_id,
        ' Porto Hub ',
        'Porto'
    );

    DBMS_OUTPUT.PUT_LINE(
        'Test 1 OK - Facility created with ID = ' || v_facility_id
    );

    --------------------------------------------------------------------
    -- Test 2: Duplicate name (case + space insensitive)
    --------------------------------------------------------------------
BEGIN
        v_dummy_id := pkg_facility.register_facility(
            v_facilitytype_id,
            'porto hub',
            'Another location'
        );
        DBMS_OUTPUT.PUT_LINE('Test 2 FAILED');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE(
                'Test 2 OK - Duplicate name rejected'
            );
END;

    --------------------------------------------------------------------
    -- Test 3: Invalid facility type
    --------------------------------------------------------------------
BEGIN
        v_dummy_id := pkg_facility.register_facility(
            -1,
            'Lisbon Hub',
            'Lisbon'
        );
        DBMS_OUTPUT.PUT_LINE('Test 3 FAILED');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE(
                'Test 3 OK - Invalid facility type rejected'
            );
END;

    --------------------------------------------------------------------
    -- Test 4: Case/space insensitive facility name (advanced test)
    --------------------------------------------------------------------
BEGIN
        v_dummy_id := pkg_facility.register_facility(
            v_facilitytype_id,
            '  P O R T O   H U B  ',  -- extra spaces
            'Porto'
        );
        DBMS_OUTPUT.PUT_LINE('Test 4 FAILED');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE(
                'Test 4 OK - Case/space insensitive duplicate rejected'
            );
END;

    --------------------------------------------------------------------
    -- Cleanup
    --------------------------------------------------------------------
ROLLBACK TO test_start;
DBMS_OUTPUT.PUT_LINE('All tests executed successfully.');

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK TO test_start;
        RAISE_APPLICATION_ERROR(-20999,
            'USBD35 SETUP ERROR: No FacilityType found in database. ' ||
            'Please run the populate script first.');
WHEN OTHERS THEN
        ROLLBACK TO test_start;
        RAISE;
END;
/
--------------------------------------------------------------------------------