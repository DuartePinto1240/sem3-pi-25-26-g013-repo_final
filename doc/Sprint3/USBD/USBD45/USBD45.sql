CREATE OR REPLACE FUNCTION fnc_usbd45_add_line(
    -- RailwayLine Parameters
    p_line_id           IN RailwayLine.line_id%TYPE,
    p_line_name         IN RailwayLine.name%TYPE,
    p_gauge_id          IN RailwayLine.gauge_id%TYPE,
    p_owner_id          IN RailwayLine.owner_id%TYPE,
    p_start_facility_id IN RailwayLine.start_facility_id%TYPE,
    p_end_facility_id   IN RailwayLine.end_facility_id%TYPE,

    -- First Segment Parameters (Required to satisfy "At least one segment")
    p_segment_id        IN LineSegment.line_segment_id%TYPE,
    p_length            IN LineSegment.length_m%TYPE,
    p_speed             IN LineSegment.speed_limit%TYPE,
    p_weight            IN LineSegment.max_weight%TYPE,
    p_track_type        IN LineSegment.track_type%TYPE,
    p_electrified       IN LineSegment.electrified%TYPE,
    p_siding_pos        IN LineSegment.siding_position%TYPE,
    p_siding_len        IN LineSegment.siding_length%TYPE
) RETURN VARCHAR2 IS

    -- Check variables (Counters are strictly NUMBER)
    v_gauge_check    NUMBER;
    v_owner_check    NUMBER;
    v_start_check    NUMBER;
    v_end_check      NUMBER;
    v_line_dup_check NUMBER;

BEGIN
    -- ==========================================
    -- 1. VALIDATIONS
    -- ==========================================

    -- Check Gauge Existence
SELECT COUNT(*) INTO v_gauge_check FROM Gauge WHERE gauge_id = p_gauge_id;
IF v_gauge_check = 0 THEN
        RETURN 'Error: Gauge ID ' || p_gauge_id || ' not found.';
END IF;

    -- Check Owner Existence
SELECT COUNT(*) INTO v_owner_check FROM Owner WHERE owner_id = p_owner_id;
IF v_owner_check = 0 THEN
        RETURN 'Error: Owner ID ' || p_owner_id || ' not found.';
END IF;

    -- Check Start Facility Existence
SELECT COUNT(*) INTO v_start_check FROM Facility WHERE facility_id = p_start_facility_id;
IF v_start_check = 0 THEN
        RETURN 'Error: Start Facility ID ' || p_start_facility_id || ' not found.';
END IF;

    -- Check End Facility Existence
SELECT COUNT(*) INTO v_end_check FROM Facility WHERE facility_id = p_end_facility_id;
IF v_end_check = 0 THEN
        RETURN 'Error: End Facility ID ' || p_end_facility_id || ' not found.';
END IF;

    -- Check Line Name Duplicates (Case Insensitive per teacher note)
SELECT COUNT(*) INTO v_line_dup_check
FROM RailwayLine
WHERE UPPER(name) = UPPER(p_line_name);

IF v_line_dup_check > 0 THEN
        RETURN 'Error: A line with name "' || p_line_name || '" already exists.';
END IF;

    -- ==========================================
    -- 2. INSERTIONS (Atomic Operation)
    -- ==========================================

    -- Step A: Insert the Railway Line
INSERT INTO RailwayLine (
    line_id, gauge_id, start_facility_id, end_facility_id, owner_id, name
) VALUES (
             p_line_id, p_gauge_id, p_start_facility_id, p_end_facility_id, p_owner_id, p_line_name
         );

-- Step B: Insert the Mandatory First Segment (Order = 1)
-- This ensures the business rule "At least one segment" is met immediately.
INSERT INTO LineSegment (
    line_segment_id, line_id, segment_order, length_m, speed_limit,
    max_weight, track_type, electrified, siding_position, siding_length
) VALUES (
             p_segment_id, p_line_id, 1, p_length, p_speed,
             p_weight, p_track_type, p_electrified, p_siding_pos, p_siding_len
         );

RETURN 'Success: Line "' || p_line_name || '" created with initial segment.';

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        RETURN 'Error: Unique constraint violation (Duplicate ID).';
WHEN OTHERS THEN
        RETURN 'Error: Database Exception - ' || SQLERRM;
END;
/

SET SERVEROUTPUT ON;

DECLARE
    -- Output variable
v_result VARCHAR2(1000);

    -- =================================================================
    -- VARIABLES: Anchored Types (%TYPE) for robustness
    -- Using negative IDs to strictly avoid conflicts with real data
    -- =================================================================
    v_company_id    Company.company_id%TYPE       := -50;
    v_owner_id      Owner.owner_id%TYPE           := -50;
    v_ftype_id      FacilityType.facilitytype_id%TYPE := -50;
    v_start_fac_id  Facility.facility_id%TYPE     := -51;
    v_end_fac_id    Facility.facility_id%TYPE     := -52;
    v_gauge_id      Gauge.gauge_id%TYPE           := -50;

    -- Test Line Data
    v_line_id_ok    RailwayLine.line_id%TYPE      := -100;
    v_seg_id_ok     LineSegment.line_segment_id%TYPE := -200;

    -- Invalid Data for Testing
    v_bad_id        NUMBER                        := -999;

BEGIN
    DBMS_OUTPUT.PUT_LINE('=== USBD45 TEST SUITE STARTED ===');

    -- =========================================================
    -- 1. SETUP: Create Dependencies
    -- =========================================================
    DBMS_OUTPUT.PUT_LINE('Step 1: Inserting Dependencies...');

    -- Create Company (Required for Owner)
INSERT INTO Company (company_id, name, short_name, vat_number)
VALUES (v_company_id, 'TestInfra', 'INF', '000000000');

-- Create Owner
INSERT INTO Owner (owner_id) VALUES (v_owner_id);

-- Create Gauge
INSERT INTO Gauge (gauge_id, name, width_mm)
VALUES (v_gauge_id, 'TestGauge', 1000);

-- Create Facility Type
INSERT INTO FacilityType (facilitytype_id, type)
VALUES (v_ftype_id, 'TestType');

-- Create Start/End Facilities
INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_start_fac_id, v_ftype_id, 'Station A', 'Loc A');

INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_end_fac_id, v_ftype_id, 'Station B', 'Loc B');

DBMS_OUTPUT.PUT_LINE('Setup Complete. Running Function Tests...');
    DBMS_OUTPUT.PUT_LINE('--------------------------------------------------');

    -- =========================================================
    -- TEST 1: Happy Path
    -- Valid Line + Valid Segment Params -> Success
    -- =========================================================
    v_result := fnc_usbd45_add_line(
        p_line_id           => v_line_id_ok,
        p_line_name         => 'Test Line Alpha',
        p_gauge_id          => v_gauge_id,
        p_owner_id          => v_owner_id,
        p_start_facility_id => v_start_fac_id,
        p_end_facility_id   => v_end_fac_id,

        -- Segment Details
        p_segment_id        => v_seg_id_ok,
        p_length            => 5000,
        p_speed             => 120,
        p_weight            => 500,
        p_track_type        => 'single',
        p_electrified       => 'No',
        p_siding_pos        => 0,
        p_siding_len        => 10
    );
    DBMS_OUTPUT.PUT_LINE('Test 1 (Happy Path): ' || v_result);

    IF v_result LIKE '%Success%' THEN
        DBMS_OUTPUT.PUT_LINE('-> STATUS: PASSED');
ELSE
        DBMS_OUTPUT.PUT_LINE('-> STATUS: FAILED');
END IF;

    -- =========================================================
    -- TEST 2: Invalid Dependency (Gauge)
    -- =========================================================
    v_result := fnc_usbd45_add_line(
        p_line_id           => -101,
        p_line_name         => 'Test Line Beta',
        p_gauge_id          => v_bad_id, -- Invalid Gauge
        p_owner_id          => v_owner_id,
        p_start_facility_id => v_start_fac_id,
        p_end_facility_id   => v_end_fac_id,
        p_segment_id        => -201, p_length => 100, p_speed => 100, p_weight => 100,
        p_track_type => 'single', p_electrified => 'No', p_siding_pos => 0, p_siding_len => 1
    );
    DBMS_OUTPUT.PUT_LINE('Test 2 (Invalid Gauge): ' || v_result);

    IF v_result LIKE '%Error%' AND v_result LIKE '%Gauge ID%' THEN
        DBMS_OUTPUT.PUT_LINE('-> STATUS: PASSED');
ELSE
        DBMS_OUTPUT.PUT_LINE('-> STATUS: FAILED');
END IF;

    -- =========================================================
    -- TEST 3: Invalid Dependency (Owner)
    -- =========================================================
    v_result := fnc_usbd45_add_line(
        p_line_id           => -102,
        p_line_name         => 'Test Line Gamma',
        p_gauge_id          => v_gauge_id,
        p_owner_id          => v_bad_id, -- Invalid Owner
        p_start_facility_id => v_start_fac_id,
        p_end_facility_id   => v_end_fac_id,
        p_segment_id        => -202, p_length => 100, p_speed => 100, p_weight => 100,
        p_track_type => 'single', p_electrified => 'No', p_siding_pos => 0, p_siding_len => 1
    );
    DBMS_OUTPUT.PUT_LINE('Test 3 (Invalid Owner): ' || v_result);

    IF v_result LIKE '%Error%' AND v_result LIKE '%Owner ID%' THEN
        DBMS_OUTPUT.PUT_LINE('-> STATUS: PASSED');
ELSE
        DBMS_OUTPUT.PUT_LINE('-> STATUS: FAILED');
END IF;

    -- =========================================================
    -- TEST 4: Duplicate Name (Case Insensitive Check)
    -- Trying to create "test line ALPHA" which conflicts with Test 1 "Test Line Alpha"
    -- =========================================================
    v_result := fnc_usbd45_add_line(
        p_line_id           => -103,
        p_line_name         => 'TEST LINE ALPHA', -- Uppercase conflict
        p_gauge_id          => v_gauge_id,
        p_owner_id          => v_owner_id,
        p_start_facility_id => v_start_fac_id,
        p_end_facility_id   => v_end_fac_id,
        p_segment_id        => -203, p_length => 100, p_speed => 100, p_weight => 100,
        p_track_type => 'single', p_electrified => 'No', p_siding_pos => 0, p_siding_len => 1
    );
    DBMS_OUTPUT.PUT_LINE('Test 4 (Duplicate Name): ' || v_result);

    IF v_result LIKE '%Error%' AND v_result LIKE '%already exists%' THEN
        DBMS_OUTPUT.PUT_LINE('-> STATUS: PASSED');
ELSE
        DBMS_OUTPUT.PUT_LINE('-> STATUS: FAILED');
END IF;

    -- =========================================================
    -- TEST 5: Constraint Check (Negative Speed)
    -- Relying on the DB constraint (CHK_LineSegment_SpeedLimit_GT0)
    -- =========================================================
    v_result := fnc_usbd45_add_line(
        p_line_id           => -104,
        p_line_name         => 'Test Line Error',
        p_gauge_id          => v_gauge_id,
        p_owner_id          => v_owner_id,
        p_start_facility_id => v_start_fac_id,
        p_end_facility_id   => v_end_fac_id,

        -- Invalid Speed (-50)
        p_segment_id        => -204,
        p_length            => 5000,
        p_speed             => -50,
        p_weight            => 500,
        p_track_type        => 'single',
        p_electrified       => 'No',
        p_siding_pos        => 0,
        p_siding_len        => 10
    );
    DBMS_OUTPUT.PUT_LINE('Test 5 (Check Constraint - Negative Speed): ' || v_result);

    -- We expect an error, but not an "not found" error
    IF v_result LIKE '%Error%' AND v_result NOT LIKE '%not found%' THEN
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
        DBMS_OUTPUT.PUT_LINE('TEST ERROR: Unique Constraint Violation inside Test Block setup.');
ROLLBACK;
WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('FATAL ERROR IN TEST BLOCK: ' || SQLERRM);
ROLLBACK;
END;
/