-- ============================================================
-- fn_get_available_routes - Retorna rotas sem train atribuído
-- ============================================================
CREATE OR REPLACE FUNCTION fn_get_available_routes
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    r.route_id,
    r.route_type,
    r.created_date,
    COUNT(DISTINCT f.freight_id) AS num_freights,
    COUNT(DISTINCT rs.facility_id) AS num_stops,
    MIN(rs_first.facility_id) AS start_facility_id,
    f_start.name AS start_facility_name,
    MAX(rs_last.facility_id) AS end_facility_id,
    f_end.name AS end_facility_name
FROM Route r
         LEFT JOIN Freight f ON r.route_id = f.route_id
         LEFT JOIN RouteStop rs ON r.route_id = rs.route_id
         LEFT JOIN RouteStop rs_first ON r.route_id = rs_first.route_id AND rs_first.stop_order = 1
         LEFT JOIN Facility f_start ON rs_first.facility_id = f_start.facility_id
         LEFT JOIN RouteStop rs_last ON r.route_id = rs_last.route_id
    AND rs_last.stop_order = (SELECT MAX(stop_order) FROM RouteStop WHERE route_id = r.route_id)
         LEFT JOIN Facility f_end ON rs_last.facility_id = f_end.facility_id
WHERE NOT EXISTS (SELECT 1 FROM Train t WHERE t.route_id = r.route_id)
GROUP BY r.route_id, r.route_type, r.created_date, f_start.name, f_end.name
ORDER BY r.created_date DESC;

RETURN rc;
END;
/