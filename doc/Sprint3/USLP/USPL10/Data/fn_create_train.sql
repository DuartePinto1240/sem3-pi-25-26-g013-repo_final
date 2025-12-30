-- ============================================================
-- fn_create_train
-- Creates a new train and assigns locomotives and wagons
-- Returns the new train_id
-- ============================================================
CREATE OR REPLACE FUNCTION fn_create_train(
    p_operator_id IN NUMBER,
    p_train_name IN VARCHAR2,
    p_max_length_m IN NUMBER,
    p_locomotive_ids IN VARCHAR2,  -- comma-separated: '5621,5623'
    p_wagon_ids IN VARCHAR2         -- comma-separated: '3330001,3330002'
)
RETURN NUMBER
IS
    v_train_id NUMBER;
    v_loco_id NUMBER;
    v_wagon_id NUMBER;
    v_pos NUMBER;
    v_next_pos NUMBER;
    v_ids VARCHAR2(4000);
BEGIN
    -- Generate new train_id
SELECT NVL(MAX(train_id), 0) + 1 INTO v_train_id FROM Train;

-- Insert train
INSERT INTO Train (train_id, operator_id, name, max_length_m)
VALUES (v_train_id, p_operator_id, p_train_name, p_max_length_m);

-- Add locomotives
IF p_locomotive_ids IS NOT NULL THEN
        v_ids := p_locomotive_ids || ',';
        v_pos := 1;

        LOOP
v_next_pos := INSTR(v_ids, ',', v_pos);
            EXIT WHEN v_next_pos = 0;

            v_loco_id := TO_NUMBER(TRIM(SUBSTR(v_ids, v_pos, v_next_pos - v_pos)));

INSERT INTO TrainLocomotive (train_id, loco_id)
VALUES (v_train_id, v_loco_id);

v_pos := v_next_pos + 1;
END LOOP;
END IF;

    -- Add wagons
    IF p_wagon_ids IS NOT NULL THEN
        v_ids := p_wagon_ids || ',';
        v_pos := 1;

        LOOP
v_next_pos := INSTR(v_ids, ',', v_pos);
            EXIT WHEN v_next_pos = 0;

            v_wagon_id := TO_NUMBER(TRIM(SUBSTR(v_ids, v_pos, v_next_pos - v_pos)));

INSERT INTO TrainWagon (train_id, wagon_id)
VALUES (v_train_id, v_wagon_id);

v_pos := v_next_pos + 1;
END LOOP;
END IF;

COMMIT;
RETURN v_train_id;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Test
DECLARE
v_train_id NUMBER;
BEGIN
    v_train_id := fn_create_train(
        p_operator_id => 2,
        p_train_name => 'TEST9999',
        p_max_length_m => 200,
        p_locomotive_ids => '9001',
        p_wagon_ids => '9001001,9001002'
    );
    DBMS_OUTPUT.PUT_LINE('Created train_id: ' || v_train_id);
END;
/