SELECT
    f.name   AS endpoint_name,
    COUNT(*) AS num_lines_departing
FROM RailwayLine rl
         INNER JOIN Facility f ON f.facility_id = rl.start_station_id
WHERE f.name = 'Porto Campanhã'   -- <== EDIT
GROUP BY f.name;
