SELECT
    op.short_name    AS operator_short,
    wt.type          AS wagon_type,
    g.width_mm       AS gauge_mm,
    w.wagon_id,
    wm.name          AS model_name,
    w.service_year
FROM Wagon w
         INNER JOIN WagonModel wm ON wm.wagonmodel_id = w.wagonmodel_id
         INNER JOIN WagonType  wt ON wt.wagontype_id  = wm.wagontype_id
         INNER JOIN Gauge       g ON g.gauge_id       = wm.gauge_id
         INNER JOIN Operator   op ON op.operator_id   = w.operator_id
WHERE (op.short_name = 'Medway')                        -- <== EDIT
  AND wt.type       = 'Cereal wagon'                    -- <== EDIT
  AND g.width_mm    = 1668                              -- <== EDIT
ORDER BY w.wagon_id;
