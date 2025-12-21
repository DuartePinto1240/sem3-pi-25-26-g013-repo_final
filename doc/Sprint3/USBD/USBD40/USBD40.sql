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
        -- Validate dates
        ----------------------------------------------------------------
        IF p_arrival_time < p_depart_time THEN
            RAISE_APPLICATION_ERROR(-20103, 'Arrival before departure.');
END IF;

        ----------------------------------------------------------------
        -- Validate number of free wagons
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
);

IF v_count < p_number_of_wagons THEN
            RAISE_APPLICATION_ERROR(-20104, 'Not enough free wagons.');
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
    depart_time,
    arrival_time
)
VALUES (
           v_freight_id,
           p_origin_facility_id,
           p_destination_facility_id,
           p_depart_time,
           p_arrival_time
       );

----------------------------------------------------------------
-- Assign wagons
----------------------------------------------------------------
FOR r IN (
            SELECT wagon_id
            FROM Wagon
            WHERE wagonmodel_id = p_wagonmodel_id
              AND current_facility_id = p_origin_facility_id
              AND NOT EXISTS (
                  SELECT 1
                  FROM FreightWagon fw
                  WHERE fw.wagon_id = Wagon.wagon_id
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
-- TESTS (anonymous blocks in the SAME file)
--------------------------------------------------------------------------------

DECLARE
v_origin_facility_id      NUMBER;
    v_destination_facility_id NUMBER;
    v_wagonmodel_id           NUMBER;
    v_freight_id              NUMBER;
BEGIN
SAVEPOINT test_start;

--------------------------------------------------------------------
-- Setup
--------------------------------------------------------------------
-- Facilities
SELECT NVL(MAX(facility_id), 0) + 1 INTO v_origin_facility_id FROM Facility;
INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_origin_facility_id, 1, 'Origin', 'City A');

SELECT NVL(MAX(facility_id), 0) + 1 INTO v_destination_facility_id FROM Facility;
INSERT INTO Facility (facility_id, facilitytype_id, name, location)
VALUES (v_destination_facility_id, 1, 'Destination', 'City B');

-- WagonModel
SELECT NVL(MAX(wagonmodel_id), 0) + 1 INTO v_wagonmodel_id FROM WagonModel;
INSERT INTO WagonModel (
    wagonmodel_id, wagontype_id, gauge_id, model_id, wagoncategory_id,
    payload_t, volume_m3
) VALUES (v_wagonmodel_id, 1, 1, 1, 1, 10, 20);

-- Wagons
FOR i IN 1..3 LOOP
        INSERT INTO Wagon (
            wagon_id, wagonmodel_id, operator_id,
            start_facility_id, current_facility_id, service_year
        )
        VALUES (
            i, v_wagonmodel_id, 1,
            v_origin_facility_id, v_origin_facility_id, 2020
        );
END LOOP;

    --------------------------------------------------------------------
    -- Test 1: Happy path
    --------------------------------------------------------------------
    v_freight_id := pkg_freight.register_freight(
        v_origin_facility_id,
        v_destination_facility_id,
        v_wagonmodel_id,
        2,
        DATE '2025-01-01',
        DATE '2025-01-02'
    );

    DBMS_OUTPUT.PUT_LINE('Test 1 OK - Freight ID = ' || v_freight_id);

    --------------------------------------------------------------------
    -- Test 2: Not enough wagons
    --------------------------------------------------------------------
BEGIN
        pkg_freight.register_freight(
            v_origin_facility_id,
            v_destination_facility_id,
            v_wagonmodel_id,
            5,
            DATE '2025-01-01',
            DATE '2025-01-02'
        );
        DBMS_OUTPUT.PUT_LINE('Test 2 FAILED');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 2 OK - Insufficient wagons rejected');
END;

    --------------------------------------------------------------------
    -- Cleanup
    --------------------------------------------------------------------
ROLLBACK TO test_start;
DBMS_OUTPUT.PUT_LINE('All USBD40 tests executed successfully.');

END;
/
--------------------------------------------------------------------------------
