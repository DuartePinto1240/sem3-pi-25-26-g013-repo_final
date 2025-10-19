WITH params AS (
    SELECT 'Ramal Barcelos - Viana' AS line_name FROM dual  -- <== altera aqui o nome da linha
)
SELECT
    rl.name       AS line_name,
    ls.track_type,
    COUNT(*)      AS segments
FROM params p
         JOIN RailwayLine rl ON rl.name   = p.line_name
         JOIN LineSegment ls ON ls.line_id = rl.line_id
GROUP BY rl.name, ls.track_type
ORDER BY ls.track_type;
