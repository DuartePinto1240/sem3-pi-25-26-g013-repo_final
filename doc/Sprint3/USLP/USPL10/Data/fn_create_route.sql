-- ============================================================
-- fn_create_route - Cria uma rota a partir de freights
-- ============================================================
CREATE OR REPLACE FUNCTION fn_create_route(
    p_freight_ids IN VARCHAR2,  -- comma-separated: '9101,9102,9103'
    p_route_type IN VARCHAR2    -- 'SIMPLE' or 'COMPLEX'
)
RETURN NUMBER
IS
    v_route_id NUMBER;
    v_freight_id NUMBER;
    v_pos NUMBER;
    v_next_pos NUMBER;
    v_ids VARCHAR2(4000);
    v_stop_order NUMBER := 0;
    v_prev_dest NUMBER := NULL;
    v_origin NUMBER;
    v_destination NUMBER;
BEGIN
    -- Generate route_id
SELECT NVL(MAX(route_id), 0) + 1 INTO v_route_id FROM Route;

-- Create route
INSERT INTO Route (route_id, route_type, created_date)
VALUES (v_route_id, p_route_type, SYSDATE);

-- Process freight IDs
v_ids := p_freight_ids || ',';
    v_pos := 1;

    LOOP
v_next_pos := INSTR(v_ids, ',', v_pos);
        EXIT WHEN v_next_pos = 0;

        v_freight_id := TO_NUMBER(TRIM(SUBSTR(v_ids, v_pos, v_next_pos - v_pos)));

        -- Update freight with route_id
UPDATE Freight
SET route_id = v_route_id
WHERE freight_id = v_freight_id;

-- Get origin and destination
SELECT origin_facility_id, destination_facility_id
INTO v_origin, v_destination
FROM Freight
WHERE freight_id = v_freight_id;

-- Add origin to route stops (if first or different from previous destination)
IF v_prev_dest IS NULL OR v_prev_dest != v_origin THEN
            v_stop_order := v_stop_order + 1;
INSERT INTO RouteStop (route_stop_id, route_id, facility_id, stop_order)
VALUES (
           (SELECT NVL(MAX(route_stop_id), 0) + 1 FROM RouteStop),
           v_route_id,
           v_origin,
           v_stop_order
       );
END IF;

        -- Add destination
        v_stop_order := v_stop_order + 1;
INSERT INTO RouteStop (route_stop_id, route_id, facility_id, stop_order)
VALUES (
           (SELECT NVL(MAX(route_stop_id), 0) + 1 FROM RouteStop),
           v_route_id,
           v_destination,
           v_stop_order
       );

v_prev_dest := v_destination;
        v_pos := v_next_pos + 1;
END LOOP;

COMMIT;
RETURN v_route_id;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/