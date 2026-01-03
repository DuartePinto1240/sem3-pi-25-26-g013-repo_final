-- ============================================================
-- fn_get_trains_with_routes
-- Retorna trains que têm route_id atribuído
-- ============================================================

CREATE OR REPLACE FUNCTION fn_get_trains_with_routes
RETURN SYS_REFCURSOR
IS
    rc SYS_REFCURSOR;
BEGIN
OPEN rc FOR
SELECT
    t.train_id,
    t.name AS train_name,
    t.route_id,
    r.route_type,
    (SELECT COUNT(*) FROM TrainLocomotive tl WHERE tl.train_id = t.train_id) AS num_locos,
    (SELECT COUNT(*) FROM TrainWagon tw WHERE tw.train_id = t.train_id) AS num_wagons
FROM Train t
         JOIN Route r ON t.route_id = r.route_id
WHERE t.route_id IS NOT NULL
ORDER BY t.train_id DESC;

RETURN rc;
END;
/