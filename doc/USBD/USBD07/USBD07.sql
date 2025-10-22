SELECT
    o.name          AS owner_name,
    rl.name         AS line_name,
    ls.line_segment_id,
    ls.segment_order,
    ls.length_m,
    ls.speed_limit,
    ls.max_weight,
    ls.track_type,
    ls.electrified
FROM LineSegment ls
         INNER JOIN RailwayLine rl ON rl.line_id = ls.line_id
         INNER JOIN Owner o       ON o.owner_id  = rl.owner_id
WHERE o.short_name = 'IP'  -- <== EDIT
ORDER BY rl.name, ls.segment_order;
