-- src/main/resources/postgre/schema.sql

CREATE TABLE IF NOT EXISTS transfers (
    id          BIGSERIAL,
    origin      VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    amount      NUMERIC(15, 2) NOT NULL,
    status      VARCHAR(20) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT pk_transfers PRIMARY KEY (id),
    CONSTRAINT chk_transfer_amount CHECK (amount > 0)
);

-- Índice para optimizar búsquedas operativas por estado (ej: transacciones PENDING)
CREATE INDEX IF NOT EXISTS idx_transfers_status ON transfers(status);

-- Índice para acelerar conciliaciones bancarias por cuenta de origen
CREATE INDEX IF NOT EXISTS idx_transfers_origin ON transfers(origin);