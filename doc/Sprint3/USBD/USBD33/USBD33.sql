CREATE OR REPLACE TRIGGER TRG_USBD33_TRAIN_MAX_LENGTH
BEFORE INSERT OR UPDATE OF train_id, wagon_id ON TrainWagon
    FOR EACH ROW
DECLARE
v_max_len_m   Train.max_length_m%TYPE;
    v_current_len NUMBER;
    v_new_len     NUMBER;
BEGIN

SELECT t.max_length_m
INTO v_max_len_m
FROM Train t
WHERE t.train_id = :NEW.train_id;

IF v_max_len_m IS NULL THEN
        RAISE_APPLICATION_ERROR(-20033, 'USBD33: Train max_length_m is NULL.');
END IF;


SELECT NVL(m.length_m, 0)
INTO v_new_len
FROM Wagon w
         JOIN WagonModel wm ON wm.wagonmodel_id = w.wagonmodel_id
         JOIN Model m       ON m.model_id       = wm.model_id
WHERE w.wagon_id = :NEW.wagon_id;

IF v_new_len <= 0 THEN
        RAISE_APPLICATION_ERROR(-20033, 'USBD33: Wagon length_m is NULL/0, cannot validate train length.');
END IF;


SELECT NVL(SUM(NVL(m.length_m, 0)), 0)
INTO v_current_len
FROM TrainWagon tw
         JOIN Wagon w       ON w.wagon_id        = tw.wagon_id
         JOIN WagonModel wm ON wm.wagonmodel_id  = w.wagonmodel_id
         JOIN Model m       ON m.model_id        = wm.model_id
WHERE tw.train_id = :NEW.train_id
  AND tw.wagon_id <> :NEW.wagon_id;

IF (v_current_len + v_new_len) > v_max_len_m THEN
        RAISE_APPLICATION_ERROR(-20033, 'USBD33: Train maximum length exceeded.');
END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20033, 'USBD33: Train or Wagon not found.');
END;
/
-- =========================================================
-- TESTS
-- =========================================================

-- ---------------------------------------------------------
-- Test 1: Happy path -> 1 wagon fits
-- ---------------------------------------------------------
DECLARE
v_train_id       Train.train_id%TYPE;
    v_operator_id    Operator.operator_id%TYPE;

    v_wagonmodel_id  WagonModel.wagonmodel_id%TYPE;
    v_len_m          Model.length_m%TYPE;

    v_facility_id    Facility.facility_id%TYPE;
    v_wagon_id       Wagon.wagon_id%TYPE;

    v_train_name     Train.name%TYPE;
BEGIN
SAVEPOINT sp_usbd33_t1;

SELECT MIN(operator_id) INTO v_operator_id FROM Operator;
SELECT MIN(facility_id) INTO v_facility_id FROM Facility;


SELECT wm.wagonmodel_id, m.length_m
INTO v_wagonmodel_id, v_len_m
FROM WagonModel wm
         JOIN Model m ON m.model_id = wm.model_id
WHERE m.length_m IS NOT NULL
  AND m.length_m > 0
  AND ROWNUM = 1;

SELECT NVL(MAX(train_id), 0) + 1 INTO v_train_id FROM Train;
v_train_name := 'T33A' || LPAD(v_train_id, 6, '0'); -- 10 chars

INSERT INTO Train (train_id, operator_id, name, max_length_m)
VALUES (v_train_id, v_operator_id, v_train_name, CEIL(v_len_m));


SELECT NVL(MAX(wagon_id), 0) + 1 INTO v_wagon_id FROM Wagon;

INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (v_wagon_id, v_wagonmodel_id, v_operator_id, v_facility_id, v_facility_id, 2020);


INSERT INTO TrainWagon (train_id, wagon_id)
VALUES (v_train_id, v_wagon_id);

DBMS_OUTPUT.PUT_LINE('USBD33 Test1 OK (1 wagon fits): train_id=' || v_train_id || ', wagon_id=' || v_wagon_id);

ROLLBACK TO sp_usbd33_t1;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('USBD33 Test1 FAIL: ' || SQLERRM);
ROLLBACK TO sp_usbd33_t1;
END;
/
-- ---------------------------------------------------------
-- Test 2: Exceed max length -> 2nd wagon should fail
-- ---------------------------------------------------------
DECLARE
v_train_id       Train.train_id%TYPE;
    v_operator_id    Operator.operator_id%TYPE;

    v_wagonmodel_id  WagonModel.wagonmodel_id%TYPE;
    v_len_m          Model.length_m%TYPE;

    v_facility_id    Facility.facility_id%TYPE;
    v_wagon_id1      Wagon.wagon_id%TYPE;
    v_wagon_id2      Wagon.wagon_id%TYPE;

    v_train_name     Train.name%TYPE;
BEGIN
SAVEPOINT sp_usbd33_t2;

SELECT MIN(operator_id) INTO v_operator_id FROM Operator;
SELECT MIN(facility_id) INTO v_facility_id FROM Facility;


SELECT wm.wagonmodel_id, m.length_m
INTO v_wagonmodel_id, v_len_m
FROM WagonModel wm
         JOIN Model m ON m.model_id = wm.model_id
WHERE m.length_m IS NOT NULL
  AND m.length_m > 0
  AND ROWNUM = 1;


SELECT NVL(MAX(train_id), 0) + 1 INTO v_train_id FROM Train;
v_train_name := 'T33B' || LPAD(v_train_id, 6, '0'); -- 10 chars

INSERT INTO Train (train_id, operator_id, name, max_length_m)
VALUES (v_train_id, v_operator_id, v_train_name, CEIL(v_len_m));


SELECT NVL(MAX(wagon_id), 0) + 1 INTO v_wagon_id1 FROM Wagon;

INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (v_wagon_id1, v_wagonmodel_id, v_operator_id, v_facility_id, v_facility_id, 2020);

v_wagon_id2 := v_wagon_id1 + 1;

INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (v_wagon_id2, v_wagonmodel_id, v_operator_id, v_facility_id, v_facility_id, 2020);

-- first wagon OK
INSERT INTO TrainWagon (train_id, wagon_id)
VALUES (v_train_id, v_wagon_id1);

-- second wagon must fail
BEGIN
INSERT INTO TrainWagon (train_id, wagon_id)
VALUES (v_train_id, v_wagon_id2);

DBMS_OUTPUT.PUT_LINE('USBD33 Test2 FAIL: expected max-length exception but insert succeeded.');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('USBD33 Test2 OK (exceed max length): ' || SQLERRM);
END;

ROLLBACK TO sp_usbd33_t2;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('USBD33 Test2 FAIL (setup): ' || SQLERRM);
ROLLBACK TO sp_usbd33_t2;
END;
/
-- ---------------------------------------------------------
-- Test 3: Invalid train -> should fail (FK or -20033)
-- ---------------------------------------------------------
DECLARE
v_operator_id    Operator.operator_id%TYPE;
    v_wagonmodel_id  WagonModel.wagonmodel_id%TYPE;
    v_facility_id    Facility.facility_id%TYPE;
    v_wagon_id       Wagon.wagon_id%TYPE;
    v_bad_train_id   Train.train_id%TYPE;
BEGIN
SAVEPOINT sp_usbd33_t3;

SELECT MIN(operator_id) INTO v_operator_id FROM Operator;
SELECT MIN(facility_id) INTO v_facility_id FROM Facility;

SELECT wm.wagonmodel_id
INTO v_wagonmodel_id
FROM WagonModel wm
         JOIN Model m ON m.model_id = wm.model_id
WHERE m.length_m IS NOT NULL
  AND m.length_m > 0
  AND ROWNUM = 1;

SELECT NVL(MAX(wagon_id), 0) + 1 INTO v_wagon_id FROM Wagon;

INSERT INTO Wagon (wagon_id, wagonmodel_id, operator_id, start_facility_id, current_facility_id, service_year)
VALUES (v_wagon_id, v_wagonmodel_id, v_operator_id, v_facility_id, v_facility_id, 2020);

v_bad_train_id := 999999999; -- should not exist

BEGIN
INSERT INTO TrainWagon (train_id, wagon_id)
VALUES (v_bad_train_id, v_wagon_id);

DBMS_OUTPUT.PUT_LINE('USBD33 Test3 FAIL: expected error but insert succeeded.');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('USBD33 Test3 OK (invalid train): ' || SQLERRM);
END;

ROLLBACK TO sp_usbd33_t3;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('USBD33 Test3 FAIL (setup): ' || SQLERRM);
ROLLBACK TO sp_usbd33_t3;
END;
/
