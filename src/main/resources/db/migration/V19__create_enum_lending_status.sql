-- 1. Remover coluna antiga de status (se existir)
ALTER TABLE lending
DROP COLUMN IF EXISTS status CASCADE;

ALTER TABLE lending
DROP COLUMN IF EXISTS status_id CASCADE;

-- 2. Remover tabela antiga de status (se existir)
DROP TABLE IF EXISTS lending_status CASCADE;

-- 3. Remover enum antigo, se existir
DROP TYPE IF EXISTS lending_status CASCADE;
DROP TYPE IF EXISTS lending_status_enum CASCADE;

-- 4. Criar o novo enum definitivo
CREATE TYPE lending_status_enum AS ENUM (
    'PENDING',
    'RETURNED',
    'OVERDUE',
    'APPROVED',
    'REJECTED'
);

-- 5. Criar a nova coluna usando o enum novo
ALTER TABLE lending
ADD COLUMN status_enum lending_status_enum NOT NULL;
