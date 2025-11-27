CREATE OR REPLACE FUNCTION fn_usbd29_get_electric_perc(p_operator_id IN NUMBER)
RETURN SYS_REFCURSOR
IS
    v_cursor          SYS_REFCURSOR;
    v_total_locos     NUMBER := 0;
    v_electric_locos  NUMBER := 0;
    v_percentage      NUMBER := 0;
    v_company_name    VARCHAR2(255);
BEGIN
    -- 1. Obter o nome da empresa
BEGIN
SELECT c.short_name
INTO v_company_name
FROM Operator o
         INNER JOIN Company c ON o.operator_id = c.company_id
WHERE o.operator_id = p_operator_id;
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            OPEN v_cursor FOR SELECT 'Operador não encontrado' AS status FROM DUAL;
RETURN v_cursor;
END;

    -- 2. Contar o TOTAL de locomotivas deste operador
SELECT COUNT(*)
INTO v_total_locos
FROM Locomotive
WHERE operator_id = p_operator_id;

-- 3. Contar APENAS as locomotivas ELÉTRICAS

SELECT COUNT(*)
INTO v_electric_locos
FROM Locomotive l
         INNER JOIN Electric_Locomotive el ON l.loco_id = el.loco_id
WHERE l.operator_id = p_operator_id;

-- 4. Calcular a percentagem
IF v_total_locos > 0 THEN
        v_percentage := (v_electric_locos / v_total_locos) * 100;
ELSE
        v_percentage := 0;
END IF;

    -- 5. Retornar o cursor
OPEN v_cursor FOR
SELECT
    p_operator_id          AS operator_id,
    v_company_name         AS operator_name,
    v_total_locos          AS total_fleet,
    v_electric_locos       AS electric_fleet,
    ROUND(v_percentage, 2) AS electric_percentage_val
FROM DUAL;

RETURN v_cursor;
END;
/

SET SERVEROUTPUT ON;

DECLARE
v_cursor        SYS_REFCURSOR;
    v_oper_id       NUMBER;
    v_oper_name     VARCHAR2(255);
    v_total         NUMBER;
    v_electric      NUMBER;
    v_perc          NUMBER;
BEGIN
    -- Testar com o Operador 2 (Medway)
    v_cursor := fn_usbd29_get_electric_perc(2);

    DBMS_OUTPUT.PUT_LINE('===========================================');
    DBMS_OUTPUT.PUT_LINE('       USBD29 - Electric Fleet Percentage  ');
    DBMS_OUTPUT.PUT_LINE('===========================================');

    LOOP
FETCH v_cursor INTO v_oper_id, v_oper_name, v_total, v_electric, v_perc;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Operator ID:    ' || v_oper_id);
        DBMS_OUTPUT.PUT_LINE('Operator Name:  ' || v_oper_name);
        DBMS_OUTPUT.PUT_LINE('Total Locos:    ' || v_total);
        DBMS_OUTPUT.PUT_LINE('Electric Locos: ' || v_electric);
        DBMS_OUTPUT.PUT_LINE('Percentage:     ' || v_perc || '%');
END LOOP;

CLOSE v_cursor;
DBMS_OUTPUT.PUT_LINE('===========================================');
END;
/