CREATE OR REPLACE FUNCTION assign_freight_to_train (
    p_freight_id IN Freight.freight_id%TYPE,
    p_train_id   IN Train.train_id%TYPE
) RETURN NUMBER IS
    v_count NUMBER;
BEGIN

-- Validate Freight exists
    SELECT COUNT(*)
    INTO v_count
    FROM Freight
    WHERE freight_id = p_freight_id;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Freight does not exist.');
    END IF;

-- Validate Train exists
    SELECT COUNT(*)
    INTO v_count
    FROM Train
    WHERE train_id = p_train_id;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Train does not exist.');
    END IF;

-- Freight must not already be assigned to a train
    SELECT COUNT(*)
    INTO v_count
    FROM Freight
    WHERE freight_id = p_freight_id
      AND train_id IS NOT NULL;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Freight already assigned to a train.');
    END IF;

-- Freight must have wagons
    SELECT COUNT(*)
    INTO v_count
    FROM FreightWagon
    WHERE freight_id = p_freight_id;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Freight has no wagons.');
    END IF;

-- Check wagons are not already in another train
    SELECT COUNT(*)
    INTO v_count
    FROM FreightWagon fw
    JOIN TrainWagon tw ON tw.wagon_id = fw.wagon_id
    WHERE fw.freight_id = p_freight_id;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'One or more wagons already assigned to a train.');
    END IF;

-- Assign train to freight
    UPDATE Freight
    SET train_id = p_train_id
    WHERE freight_id = p_freight_id;

-- Assign all wagons to train
    INSERT INTO TrainWagon (train_id, wagon_id)
    SELECT p_train_id, wagon_id
    FROM FreightWagon
    WHERE freight_id = p_freight_id;

    RETURN SQL%ROWCOUNT;
END;
/

--tests: anonymous blocks
SET SERVEROUTPUT ON;

DECLARE
    v_result NUMBER;
BEGIN
    SAVEPOINT test_start;

-- Test: Happy path
-- Freight 2007 has no train and has wagons
    v_result := assign_freight_to_train(2007, 5437);

    DBMS_OUTPUT.PUT_LINE( 'Test 1 OK - Wagons associated: ' || v_result );

-- Test: Freight does not exist
    BEGIN
        v_result := assign_freight_to_train(-1, 5437);
        DBMS_OUTPUT.PUT_LINE('Test 2 FAILED');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 2 OK - Invalid freight rejected');
    END;

-- Test: Train does not exist
    BEGIN
        v_result := assign_freight_to_train(2007, -1);
        DBMS_OUTPUT.PUT_LINE('Test 3 FAILED');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 3 OK - Invalid train rejected');
    END;

--Test: Freight already assigned
    BEGIN
        v_result := assign_freight_to_train(2001, 5437);
        DBMS_OUTPUT.PUT_LINE('Test 4 FAILED');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test 4 OK - Already assigned freight rejected');
    END;

-- Cleanup
    ROLLBACK TO test_start;
    DBMS_OUTPUT.PUT_LINE('All tests executed successfully.');

END;
/
