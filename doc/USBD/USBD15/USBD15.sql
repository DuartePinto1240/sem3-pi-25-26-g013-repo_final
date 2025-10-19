WITH params AS (
    SELECT 'Porto São Bento' AS endpoint_name FROM dual
)
SELECT
    p.endpoint_name AS endpoint,
    COUNT(DISTINCT rl.line_id) AS lines_departing
FROM params p
         LEFT JOIN Facility    f  ON f.name = p.endpoint_name
         LEFT JOIN RailwayLine rl ON rl.start_station_id = f.facility_id
GROUP BY p.endpoint_name;
