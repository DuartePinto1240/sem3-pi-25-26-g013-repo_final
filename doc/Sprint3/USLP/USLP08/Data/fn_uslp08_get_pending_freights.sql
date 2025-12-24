CREATE OR REPLACE FUNCTION fn_uslp08_get_pending_freights
RETURN SYS_REFCURSOR
IS
  rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    f.freight_id,
    f.origin_facility_id,
    fo.name  AS origin_name,
    f.destination_facility_id,
    fd.name  AS destination_name,
    f.depart_time,
    f.arrival_time
FROM Freight f
         JOIN Facility fo ON fo.facility_id = f.origin_facility_id
         JOIN Facility fd ON fd.facility_id = f.destination_facility_id
WHERE f.train_id IS NULL
ORDER BY f.depart_time NULLS LAST, f.freight_id;

RETURN rc;
END;
/

VAR rc REFCURSOR;
EXEC :rc := fn_uslp08_get_pending_freights;
PRINT rc;

