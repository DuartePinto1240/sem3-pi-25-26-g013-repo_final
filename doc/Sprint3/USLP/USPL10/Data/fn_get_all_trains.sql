-- Adicionar ao Oracle
CREATE OR REPLACE FUNCTION fn_get_all_trains
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    t.train_id,
    t.name AS train_name,
    t.max_length_m,
    COUNT(DISTINCT tl.loco_id) AS num_locomotives,
    COUNT(DISTINCT tw.wagon_id) AS num_wagons,
    o.operator_id,
    c.short_name AS operator_name
FROM Train t
         JOIN Operator o ON t.operator_id = o.operator_id
         JOIN Company c ON o.operator_id = c.company_id
         LEFT JOIN TrainLocomotive tl ON t.train_id = tl.train_id
         LEFT JOIN TrainWagon tw ON t.train_id = tw.train_id
GROUP BY t.train_id, t.name, t.max_length_m, o.operator_id, c.short_name
ORDER BY t.train_id;

RETURN rc;
END;
/