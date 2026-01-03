-- ============================================================
-- sp_assign_train_to_route - Atribui train a uma rota
-- ============================================================
CREATE OR REPLACE PROCEDURE sp_assign_train_to_route(
    p_train_id IN NUMBER,
    p_route_id IN NUMBER
)
IS
BEGIN
    -- Update train with route
UPDATE Train
SET route_id = p_route_id
WHERE train_id = p_train_id;

-- Update all freights of the route with this train
UPDATE Freight
SET train_id = p_train_id
WHERE route_id = p_route_id;

COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/