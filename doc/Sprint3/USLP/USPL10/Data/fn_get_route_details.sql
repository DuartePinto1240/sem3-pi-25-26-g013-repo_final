-- ============================================================
-- fn_get_route_details - Detalhes completos de uma rota
-- ============================================================
CREATE OR REPLACE FUNCTION fn_get_route_details(
    p_route_id IN NUMBER
)
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    rs.route_id,
    rs.stop_order,
    rs.facility_id,
    f.name AS facility_name,
    f.location,
    -- Freights to load at this stop
    (SELECT COUNT(*) FROM Freight fr WHERE fr.route_id = rs.route_id AND fr.origin_facility_id = rs.facility_id) AS freights_to_load,
    -- Freights to unload at this stop
    (SELECT COUNT(*) FROM Freight fr WHERE fr.route_id = rs.route_id AND fr.destination_facility_id = rs.facility_id) AS freights_to_unload
FROM RouteStop rs
         JOIN Facility f ON rs.facility_id = f.facility_id
WHERE rs.route_id = p_route_id
ORDER BY rs.stop_order;

RETURN rc;
END;
/