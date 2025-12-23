CREATE OR REPLACE FUNCTION remove_freight_from_train (
    p_freight_id   IN Freight.freight_id%TYPE,
    p_ref_date     IN DATE
) RETURN NUMBER
IS
    v_train_id   Freight.train_id%TYPE;
    v_depart     Freight.depart_time%TYPE;
    v_count      NUMBER;
BEGIN

-- Validate freight existence
    SELECT train_id, depart_time
    INTO v_train_id, v_depart
    FROM Freight
    WHERE freight_id = p_freight_id;

-- Validate freight is associated with a train
    IF v_train_id IS NULL THEN
        RAISE_APPLICATION_ERROR(-20041,'Freight is not associated with any train.');
    END IF;

-- Validate freight is still planned
    IF v_depart <= p_ref_date THEN
        RAISE_APPLICATION_ERROR(-20042,'Freight has already departed.');
    END IF;

-- Remove wagons from the train
    DELETE FROM TrainWagon
    WHERE wagon_id IN (
        SELECT wagon_id
        FROM FreightWagon
        WHERE freight_id = p_freight_id
    );

    v_count := SQL%ROWCOUNT;

-- Remove freight from train
    UPDATE Freight
    SET train_id = NULL
    WHERE freight_id = p_freight_id;

    RETURN v_count;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20040,'Freight does not exist.');
END;
/

-- Tests - anonymous block
SET SERVEROUTPUT ON;

DECLARE
    v_removed NUMBER;
BEGIN
    SAVEPOINT test_start;

-- Test: Happy path (planned freight with wagons)
    v_removed := remove_freight_from_train(2001,TO_DATE('2025-10-01', 'YYYY-MM-DD'));
    DBMS_OUTPUT.PUT_LINE('Test 1 OK - Wagons removed from train: ' || v_removed);

-- Test: Freight without train (train_id is NULL)
    BEGIN
        v_removed := remove_freight_from_train(2007,TO_DATE('2025-10-01', 'YYYY-MM-DD'));
        DBMS_OUTPUT.PUT_LINE('Test 2 FAILED');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 2 OK - Freight without train rejected');
    END;

-- Test: Freight already departed (past date)
    BEGIN
        v_removed := remove_freight_from_train(9001, TO_DATE('2025-10-10', 'YYYY-MM-DD'));
        DBMS_OUTPUT.PUT_LINE('Test 3 FAILED');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 3 OK - Departed freight rejected');
    END;

-- Test: Non-existent freight
    BEGIN
        v_removed := remove_freight_from_train(-1,TO_DATE('2025-10-01', 'YYYY-MM-DD'));
        DBMS_OUTPUT.PUT_LINE('Test 4 FAILED');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 4 OK - Non-existent freight rejected');
    END;

-- Cleanup
    ROLLBACK TO test_start;
    DBMS_OUTPUT.PUT_LINE('All USBD41 tests executed successfully.');

END;
/