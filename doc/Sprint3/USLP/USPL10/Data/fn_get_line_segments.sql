CREATE OR REPLACE FUNCTION fn_get_line_segments(
    p_from_facility_id IN NUMBER,
    p_to_facility_id   IN NUMBER
)
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    ls.line_segment_id,
    ls.line_id,
    ls.start_facility_id,
    f1.name AS start_facility_name,
    ls.end_facility_id,
    f2.name AS end_facility_name,
    ls.length_m,
    ls.speed_limit,
    ls.electrified,
    ls.track_type,
    rl.gauge_id
FROM LineSegment ls
         JOIN RailwayLine rl ON rl.line_id = ls.line_id
         JOIN Facility f1 ON f1.facility_id = ls.start_facility_id
         JOIN Facility f2 ON f2.facility_id = ls.end_facility_id
WHERE (ls.start_facility_id = p_from_facility_id AND ls.end_facility_id = p_to_facility_id)
   OR (ls.start_facility_id = p_to_facility_id AND ls.end_facility_id = p_from_facility_id)
ORDER BY ls.line_id, ls.segment_order;

RETURN rc;
END;
/
