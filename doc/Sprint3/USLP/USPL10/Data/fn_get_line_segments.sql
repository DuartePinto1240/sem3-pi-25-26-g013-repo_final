-- ============================================================
-- fn_get_line_segments
-- Returns all line segments in the path between two facilities
-- ============================================================
CREATE OR REPLACE FUNCTION fn_get_line_segments(
    p_from_facility_id IN NUMBER,
    p_to_facility_id IN NUMBER
)
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
    -- Simplified version: returns all segments of lines connecting the facilities
    -- TODO: Implement proper pathfinding algorithm
OPEN rc FOR
SELECT
    ls.line_segment_id,
    ls.line_id,
    rl.name AS line_name,
    rl.start_facility_id,
    f1.name AS start_facility_name,
    rl.end_facility_id,
    f2.name AS end_facility_name,
    ls.segment_order,
    ls.length_m,
    ls.speed_limit,
    ls.max_weight,
    ls.track_type,
    ls.electrified,
    ls.siding_position,
    ls.siding_length,
    g.gauge_id,
    g.name AS gauge_name,
    g.width_mm AS gauge_width,
    o.owner_id,
    c.short_name AS owner_name
FROM LineSegment ls
         JOIN RailwayLine rl ON ls.line_id = rl.line_id
         JOIN Facility f1 ON rl.start_facility_id = f1.facility_id
         JOIN Facility f2 ON rl.end_facility_id = f2.facility_id
         JOIN Gauge g ON rl.gauge_id = g.gauge_id
         JOIN Owner o ON rl.owner_id = o.owner_id
         JOIN Company c ON o.owner_id = c.company_id
WHERE (rl.start_facility_id = p_from_facility_id AND rl.end_facility_id = p_to_facility_id)
   OR (rl.start_facility_id = p_to_facility_id AND rl.end_facility_id = p_from_facility_id)
ORDER BY ls.line_id, ls.segment_order;

RETURN rc;
END;
/

-- Test
VAR rc REFCURSOR;
EXEC :rc := fn_get_line_segments(50, 48);
PRINT rc;