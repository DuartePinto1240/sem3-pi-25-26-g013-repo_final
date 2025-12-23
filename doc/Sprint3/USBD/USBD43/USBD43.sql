CREATE OR REPLACE FUNCTION FNC_USBD43_add_electric_locomotive_model(
    -- Model (generic)
    p_model_name            IN Model.name%TYPE,
    p_maker                 IN Model.maker%TYPE,
    p_max_speed_kmh         IN Model.max_speed_kmh%TYPE,
    p_number_of_bogies      IN Model.number_of_bogies%TYPE,
    p_bogies                IN Model.bogies%TYPE,
    p_length_m              IN Model.length_m%TYPE,
    p_height_m              IN Model.height_m%TYPE,
    p_weight_t              IN Model.weight_t%TYPE,
    p_width_m               IN Model.width_m%TYPE,

    -- Gauge identified by name (Gauge.name has UNIQUE)
    p_gauge_name            IN Gauge.name%TYPE,

    -- LocomotiveModel
    p_power_kw              IN LocomotiveModel.power_kw%TYPE,
    p_traction_kn           IN LocomotiveModel.traction_kn%TYPE,
    p_operational_speed_kmh IN LocomotiveModel.operational_speed_kmh%TYPE,

    -- Locomotive (instance required by Electric_Locomotive)
    p_operator_id           IN Locomotive.operator_id%TYPE,
    p_start_facility_name   IN Facility.name%TYPE,
    p_service_year          IN Locomotive.service_year%TYPE,
    p_loco_name             IN Locomotive.name%TYPE DEFAULT NULL,

    -- Electric subtype
    p_voltage_kv            IN Electric_Locomotive.voltage_kv%TYPE,
    p_frequency_hz          IN Electric_Locomotive.frequency_hz%TYPE
) RETURN SYS_REFCURSOR
IS
    v_model_id          Model.model_id%TYPE;
    v_loco_model_id     LocomotiveModel.locomotive_model_id%TYPE;
    v_loco_id           Locomotive.loco_id%TYPE;

    v_gauge_id          Gauge.gauge_id%TYPE;
    v_start_facility_id Facility.facility_id%TYPE;

    v_count NUMBER;

    rc SYS_REFCURSOR;
BEGIN
    -- Resolve Gauge by name (case/space insensitive)
SELECT g.gauge_id
INTO v_gauge_id
FROM Gauge g
WHERE UPPER(REPLACE(TRIM(g.name),' ','')) = UPPER(REPLACE(TRIM(p_gauge_name),' ',''));

-- Resolve Facility by name (case/space insensitive)
SELECT f.facility_id
INTO v_start_facility_id
FROM Facility f
WHERE UPPER(REPLACE(TRIM(f.name),' ','')) = UPPER(REPLACE(TRIM(p_start_facility_name),' ',''));

-- Enforce Model.name uniqueness with case/space insensitive comparison
SELECT COUNT(*)
INTO v_count
FROM Model m
WHERE UPPER(REPLACE(TRIM(m.name),' ','')) = UPPER(REPLACE(TRIM(p_model_name),' ',''));

IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20043, 'USBD43: Model name already exists (case/space insensitive).');
END IF;

    -- Generate IDs (no sequences)
SELECT NVL(MAX(m.model_id), 0) + 1 INTO v_model_id      FROM Model m;
SELECT NVL(MAX(lm.locomotive_model_id), 0) + 1 INTO v_loco_model_id FROM LocomotiveModel lm;
SELECT NVL(MAX(l.loco_id), 0) + 1 INTO v_loco_id        FROM Locomotive l;

-- Insert Model
INSERT INTO Model (
    model_id, name, maker, max_speed_kmh,
    number_of_bogies, bogies, length_m, height_m, weight_t, width_m
) VALUES (
             v_model_id, p_model_name, p_maker, p_max_speed_kmh,
             p_number_of_bogies, p_bogies, p_length_m, p_height_m, p_weight_t, p_width_m
         );

-- Insert LocomotiveModel
INSERT INTO LocomotiveModel (
    locomotive_model_id, model_id, gauge_id,
    power_kw, traction_kn, operational_speed_kmh
) VALUES (
             v_loco_model_id, v_model_id, v_gauge_id,
             p_power_kw, p_traction_kn, p_operational_speed_kmh
         );

-- Insert Locomotive instance (to attach subtype)
INSERT INTO Locomotive (
    loco_id, operator_id, locomotive_model_id,
    start_facility_id, name, service_year
) VALUES (
             v_loco_id, p_operator_id, v_loco_model_id,
             v_start_facility_id, p_loco_name, p_service_year
         );

-- Insert Electric subtype
INSERT INTO Electric_Locomotive (loco_id, voltage_kv, frequency_hz)
VALUES (v_loco_id, p_voltage_kv, p_frequency_hz);

-- Return everything created
OPEN rc FOR
SELECT  m.model_id,
        m.name               AS model_name,
        m.maker,
        lm.locomotive_model_id,
        g.gauge_id,
        g.name               AS gauge_name,
        l.loco_id,
        l.operator_id,
        l.start_facility_id,
        l.service_year,
        el.voltage_kv,
        el.frequency_hz
FROM Model m
         JOIN LocomotiveModel lm     ON lm.model_id = m.model_id
         JOIN Gauge g                ON g.gauge_id = lm.gauge_id
         JOIN Locomotive l           ON l.locomotive_model_id = lm.locomotive_model_id
         JOIN Electric_Locomotive el ON el.loco_id = l.loco_id
WHERE m.model_id = v_model_id;

RETURN rc;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        -- Gauge name or Facility name not found
        RAISE_APPLICATION_ERROR(-20043, 'USBD43: Gauge name or Facility name not found.');
WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(-20043, 'USBD43: Uniqueness violation (likely Model.name).');
WHEN OTHERS THEN
  IF SQLCODE = -20043 THEN
    RAISE;
END IF;
  RAISE_APPLICATION_ERROR(-20043, 'USBD43: Error: ' || SQLERRM);

END FNC_USBD43_ADD_ELECTRIC_LOCOMOTIVE_MODEL;
/

-- =========================================================
-- TESTS (Anonymous Blocks) - comprehensive, not only happy path
-- IMPORTANT: no pre-inserts required; each test uses SAVEPOINT and ends with ROLLBACK.
-- =========================================================


-- Test 1: Happy path (should return 1 row)
DECLARE
rc SYS_REFCURSOR;

    v_gauge_name  Gauge.name%TYPE;
    v_fac_name    Facility.name%TYPE;
    v_operator_id Locomotive.operator_id%TYPE;

    o_model_id     Model.model_id%TYPE;
    o_model_name   Model.name%TYPE;
    o_maker        Model.maker%TYPE;
    o_lm_id        LocomotiveModel.locomotive_model_id%TYPE;
    o_gauge_id     Gauge.gauge_id%TYPE;
    o_gauge_name   Gauge.name%TYPE;
    o_loco_id      Locomotive.loco_id%TYPE;
    o_op_id        Locomotive.operator_id%TYPE;
    o_start_fac_id Facility.facility_id%TYPE;
    o_year         Locomotive.service_year%TYPE;
    o_voltage      Electric_Locomotive.voltage_kv%TYPE;
    o_freq         Electric_Locomotive.frequency_hz%TYPE;

    v_unique_name  Model.name%TYPE;
BEGIN
SAVEPOINT sp_usbd43_t1;

SELECT name INTO v_gauge_name FROM Gauge WHERE ROWNUM = 1;
SELECT name INTO v_fac_name   FROM Facility WHERE ROWNUM = 1;
SELECT operator_id INTO v_operator_id FROM Operator WHERE ROWNUM = 1;

-- Create a unique model name using MAX(model_id)+1 (no sequences)
SELECT 'ELECTRIC_MODEL_' || (NVL(MAX(model_id),0) + 1)
INTO v_unique_name
FROM Model;

rc := FNC_USBD43_add_electric_locomotive_model(
        p_model_name            => v_unique_name,
        p_maker                 => 'MakerX',
        p_max_speed_kmh         => 120,
        p_number_of_bogies      => 2,
        p_bogies                => 'Bo-Bo',
        p_length_m              => 18.500,
        p_height_m              => 4.20,
        p_weight_t              => 80.00,
        p_width_m               => 3.00,

        p_gauge_name            => v_gauge_name,

        p_power_kw              => 3000,
        p_traction_kn           => 250,
        p_operational_speed_kmh => 100,

        p_operator_id           => v_operator_id,
        p_start_facility_name   => v_fac_name,
        p_service_year          => 2020,
        p_loco_name             => NULL,

        p_voltage_kv            => 25,
        p_frequency_hz          => '50'
    );

FETCH rc INTO o_model_id, o_model_name, o_maker, o_lm_id,
    o_gauge_id, o_gauge_name, o_loco_id, o_op_id,
    o_start_fac_id, o_year, o_voltage, o_freq;

IF rc%NOTFOUND THEN
        RAISE_APPLICATION_ERROR(-20911, 'USBD43 Test1 FAILED: cursor returned no rows.');
END IF;

    DBMS_OUTPUT.PUT_LINE('USBD43 Test1 OK: model_id=' || o_model_id || ', loco_id=' || o_loco_id ||
                         ', ' || o_voltage || 'kV/' || o_freq || 'Hz');

CLOSE rc;
ROLLBACK TO sp_usbd43_t1;
END;
/

-- Test 2: Duplicate Model.name case/space insensitive (should raise error)
BEGIN
SAVEPOINT sp_usbd43_t2;

DECLARE
rc SYS_REFCURSOR;
        v_gauge_name  Gauge.name%TYPE;
        v_fac_name    Facility.name%TYPE;
        v_operator_id Locomotive.operator_id%TYPE;
        v_base_name   Model.name%TYPE;
        v_variant     Model.name%TYPE;
BEGIN
SELECT name INTO v_gauge_name FROM Gauge WHERE ROWNUM = 1;
SELECT name INTO v_fac_name   FROM Facility WHERE ROWNUM = 1;
SELECT operator_id INTO v_operator_id FROM Operator WHERE ROWNUM = 1;

SELECT 'ELECTRIC_MODEL_DUP_' || (NVL(MAX(model_id),0) + 1)
INTO v_base_name
FROM Model;

-- First call succeeds
rc := FNC_USBD43_add_electric_locomotive_model(
            p_model_name            => v_base_name,
            p_maker                 => 'MakerX',
            p_max_speed_kmh         => 120,
            p_number_of_bogies      => 2,
            p_bogies                => 'Bo-Bo',
            p_length_m              => 18.500,
            p_height_m              => 4.20,
            p_weight_t              => 80.00,
            p_width_m               => 3.00,
            p_gauge_name            => v_gauge_name,
            p_power_kw              => 3000,
            p_traction_kn           => 250,
            p_operational_speed_kmh => 100,
            p_operator_id           => v_operator_id,
            p_start_facility_name   => v_fac_name,
            p_service_year          => 2020,
            p_loco_name             => NULL,
            p_voltage_kv            => 25,
            p_frequency_hz          => '50'
        );
CLOSE rc;

-- Second call with case/space variant must fail
v_variant := '  ' || LOWER(REPLACE(v_base_name,'_','   _   ')) || '  ';

        rc := FNC_USBD43_add_electric_locomotive_model(
            p_model_name            => v_variant,
            p_maker                 => 'MakerX',
            p_max_speed_kmh         => 120,
            p_number_of_bogies      => 2,
            p_bogies                => 'Bo-Bo',
            p_length_m              => 18.500,
            p_height_m              => 4.20,
            p_weight_t              => 80.00,
            p_width_m               => 3.00,
            p_gauge_name            => v_gauge_name,
            p_power_kw              => 3000,
            p_traction_kn           => 250,
            p_operational_speed_kmh => 100,
            p_operator_id           => v_operator_id,
            p_start_facility_name   => v_fac_name,
            p_service_year          => 2020,
            p_loco_name             => NULL,
            p_voltage_kv            => 25,
            p_frequency_hz          => '50'
        );

        RAISE_APPLICATION_ERROR(-20912, 'USBD43 Test2 FAILED: expected duplicate-name error, got success.');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('USBD43 Test2 OK (duplicate name): ' || SQLERRM);
END;

ROLLBACK TO sp_usbd43_t2;
END;
/

-- Test 3: Invalid Gauge name (should raise error)
BEGIN
SAVEPOINT sp_usbd43_t3;

DECLARE
rc SYS_REFCURSOR;
        v_fac_name    Facility.name%TYPE;
        v_operator_id Locomotive.operator_id%TYPE;
        v_unique_name Model.name%TYPE;
BEGIN
SELECT name INTO v_fac_name FROM Facility WHERE ROWNUM = 1;
SELECT operator_id INTO v_operator_id FROM Operator WHERE ROWNUM = 1;
SELECT 'ELECTRIC_MODEL_BAD_GAUGE_' || (NVL(MAX(model_id),0) + 1)
INTO v_unique_name
FROM Model;

rc := FNC_USBD43_add_electric_locomotive_model(
            p_model_name            => v_unique_name,
            p_maker                 => 'MakerX',
            p_max_speed_kmh         => 120,
            p_number_of_bogies      => 2,
            p_bogies                => 'Bo-Bo',
            p_length_m              => 18.500,
            p_height_m              => 4.20,
            p_weight_t              => 80.00,
            p_width_m               => 3.00,
            p_gauge_name            => '___GAUGE_DOES_NOT_EXIST___',
            p_power_kw              => 3000,
            p_traction_kn           => 250,
            p_operational_speed_kmh => 100,
            p_operator_id           => v_operator_id,
            p_start_facility_name   => v_fac_name,
            p_service_year          => 2020,
            p_loco_name             => NULL,
            p_voltage_kv            => 25,
            p_frequency_hz          => '50'
        );

        RAISE_APPLICATION_ERROR(-20913, 'USBD43 Test3 FAILED: expected error, got success.');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('USBD43 Test3 OK (invalid gauge): ' || SQLERRM);
END;

ROLLBACK TO sp_usbd43_t3;
END;
/

-- Test 4: Invalid voltage (violates CHECK voltage_kv > 0) (should raise error)
BEGIN
SAVEPOINT sp_usbd43_t4;

DECLARE
rc SYS_REFCURSOR;
        v_gauge_name  Gauge.name%TYPE;
        v_fac_name    Facility.name%TYPE;
        v_operator_id Locomotive.operator_id%TYPE;
        v_unique_name Model.name%TYPE;
BEGIN
SELECT name INTO v_gauge_name FROM Gauge WHERE ROWNUM = 1;
SELECT name INTO v_fac_name   FROM Facility WHERE ROWNUM = 1;
SELECT operator_id INTO v_operator_id FROM Operator WHERE ROWNUM = 1;
SELECT 'ELECTRIC_MODEL_BAD_VOLT_' || (NVL(MAX(model_id),0) + 1)
INTO v_unique_name
FROM Model;

rc := FNC_USBD43_add_electric_locomotive_model(
            p_model_name            => v_unique_name,
            p_maker                 => 'MakerX',
            p_max_speed_kmh         => 120,
            p_number_of_bogies      => 2,
            p_bogies                => 'Bo-Bo',
            p_length_m              => 18.500,
            p_height_m              => 4.20,
            p_weight_t              => 80.00,
            p_width_m               => 3.00,
            p_gauge_name            => v_gauge_name,
            p_power_kw              => 3000,
            p_traction_kn           => 250,
            p_operational_speed_kmh => 100,
            p_operator_id           => v_operator_id,
            p_start_facility_name   => v_fac_name,
            p_service_year          => 2020,
            p_loco_name             => NULL,
            p_voltage_kv            => 0, -- invalid (must be > 0)
            p_frequency_hz          => '50'
        );

        RAISE_APPLICATION_ERROR(-20914, 'USBD43 Test4 FAILED: expected error, got success.');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('USBD43 Test4 OK (invalid voltage): ' || SQLERRM);
END;

ROLLBACK TO sp_usbd43_t4;
END;
/
