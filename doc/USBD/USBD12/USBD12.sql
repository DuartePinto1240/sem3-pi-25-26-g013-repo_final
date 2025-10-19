WITH params AS (
    SELECT
        'Medway'                               AS operator_short,      -- <== operador
        'Container wagon (max 40'' HC)'        AS wagon_type,          -- <== tipo (texto exato)
        1668                                   AS gauge_width_mm       -- <== bitola (mm)
    FROM dual
)
SELECT
    w.wagon_id,
    o.short_name      AS operator,
    wt.type           AS wagon_type,
    g.width_mm        AS gauge_width_mm,
    wm.wagonmodel_id,
    wm.name           AS wagon_model,
    w.service_year
FROM params p
         JOIN Operator    o  ON o.short_name      = p.operator_short
         JOIN Wagon       w  ON w.operator_id     = o.operator_id
         JOIN WagonModel  wm ON wm.wagonmodel_id  = w.wagonmodel_id
         JOIN WagonType   wt ON wt.wagontype_id   = wm.wagontype_id
         JOIN Gauge       g  ON g.gauge_id        = wm.gauge_id
WHERE wt.type = p.wagon_type
  AND g.width_mm = p.gauge_width_mm
ORDER BY w.wagon_id;
