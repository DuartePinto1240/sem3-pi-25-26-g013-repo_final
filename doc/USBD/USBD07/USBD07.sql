WITH params AS (
    SELECT 'IP' AS owner_short FROM dual   -- <== altera aqui o short_name do owner
)
SELECT
    ow.short_name        AS owner,
    rl.line_id,
    rl.name              AS line_name,
    ls.line_segment_id,
    ls.segment_order,
    ls.length_m,
    ls.speed_limit,
    ls.max_weight,
    ls.track_type,
    ls.electrified
FROM params p
         JOIN Owner        ow ON ow.short_name = p.owner_short
         JOIN RailwayLine  rl ON rl.owner_id   = ow.owner_id
         JOIN LineSegment  ls ON ls.line_id    = rl.line_id
ORDER BY rl.line_id, ls.segment_order;
