-- TESTE USBD27 — adicionar 2 vagões CER ao Comboio 2
INSERT INTO TrainWagon (train_id, wagon_id)
VALUES (2, 17);
INSERT INTO TrainWagon (train_id, wagon_id)
VALUES (2, 18);

COMMIT;