-- =========================================================
-- RESET TOTAL (USLP10 + USLP09 + desfazer planeamento)
-- Mantém dados base de USLP08: ROUTE e ROUTESTOP
-- =========================================================

-- 0) (Opcional) ver em que schema estás
-- SELECT USER FROM dual;

BEGIN
  -- 1) USLP10: apagar execuções/movimentos/schedules gerados
  -- (filhos primeiro)
DELETE FROM TRAINMOVEMENT;
DELETE FROM ROUTEENDPOINT;

-- 2) USLP09: apagar associações do train (wagons e locomotives)
DELETE FROM TRAINWAGON;
DELETE FROM TRAINLOCOMOTIVE;

-- 3) USLP09: desassociar rota do train
UPDATE TRAIN
SET ROUTE_ID = NULL
WHERE ROUTE_ID IS NOT NULL;

-- 4) USLP10/09: desfazer dispatch/associação de freights a train
UPDATE FREIGHT
SET TRAIN_ID = NULL
WHERE TRAIN_ID IS NOT NULL;

-- 5) USLP08: desfazer planeamento (voltar tudo a "pending")
UPDATE FREIGHT
SET ROUTE_ID = NULL
WHERE ROUTE_ID IS NOT NULL;

COMMIT;
END;
/
-- =========================================================
-- CHECKS (opcional)
-- =========================================================
SELECT COUNT(*) AS trainmovement_rows FROM TRAINMOVEMENT;
SELECT COUNT(*) AS routeendpoint_rows  FROM ROUTEENDPOINT;
SELECT COUNT(*) AS trainwagon_rows     FROM TRAINWAGON;
SELECT COUNT(*) AS trainloco_rows      FROM TRAINLOCOMOTIVE;

SELECT COUNT(*) AS trains_with_route   FROM TRAIN WHERE ROUTE_ID IS NOT NULL;
SELECT COUNT(*) AS freights_with_train FROM FREIGHT WHERE TRAIN_ID IS NOT NULL;
SELECT COUNT(*) AS freights_with_route FROM FREIGHT WHERE ROUTE_ID IS NOT NULL;

-- ROTAS BASE (USLP08) continuam
SELECT COUNT(*) AS routes_rows     FROM ROUTE;
SELECT COUNT(*) AS routestop_rows  FROM ROUTESTOP;
