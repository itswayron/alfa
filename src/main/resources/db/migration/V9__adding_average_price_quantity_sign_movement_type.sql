ALTER TABLE movement_type
ADD COLUMN affects_average_price BOOLEAN DEFAULT TRUE,
ADD COLUMN quantity_sign INT;

UPDATE movement_type
SET affects_average_price = TRUE,
    quantity_sign = 1
WHERE name = 'ENTRADA';

UPDATE movement_type
SET affects_average_price = FALSE,
    quantity_sign = -1
WHERE name = 'SAÍDA';

UPDATE movement_type
SET affects_average_price = FALSE,
    quantity_sign = NULL
WHERE name = 'MOVIMENTAÇÃO INTERNA';

UPDATE movement_type
SET affects_average_price = FALSE,
    quantity_sign = -1
WHERE name = 'CONSUMO';
