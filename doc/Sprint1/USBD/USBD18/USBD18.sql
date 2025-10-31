SELECT
    ls.track_type,
    COUNT(*) AS num_segments
FROM LineSegment ls
         INNER JOIN RailwayLine rl ON rl.line_id = ls.line_id
WHERE rl.name = 'Ramal Barcelos - Viana'   -- <== EDIT
GROUP BY ls.track_type
ORDER BY ls.track_type;
