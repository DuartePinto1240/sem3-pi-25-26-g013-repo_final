CREATE OR REPLACE FUNCTION fn_uslp08_get_freight_cargos(p_freight_id IN NUMBER)
RETURN SYS_REFCURSOR
IS
  rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    c.cargo_id,
    c.wagon_id,
    c.description,
    c.type,
    c.quantity,
    c.volume,
    c.weight,
    c.expiry_date
FROM FreightWagon fw
         JOIN Cargo c ON c.wagon_id = fw.wagon_id
WHERE fw.freight_id = p_freight_id
ORDER BY c.wagon_id, c.cargo_id;

RETURN rc;
END;
/

SELECT freight_id
FROM Freight
WHERE train_id IS NULL
    FETCH FIRST 1 ROWS ONLY;

VAR rc REFCURSOR;
EXEC :rc := fn_uslp08_get_freight_cargos(<FREIGHT_ID_AQUI>);
PRINT rc;
