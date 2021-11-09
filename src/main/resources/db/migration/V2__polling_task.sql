-- Task
CREATE TABLE trade_service.task
(
    chain_id     BIGINT PRIMARY KEY,
    block_number BIGINT NOT NULL,
    timestamp    BIGINT NOT NULL
);

CREATE TYPE trade_service.task_status AS ENUM ('PENDING', 'SUCCESSFUL', 'FAILED');
CREATE TYPE trade_service.task_type AS ENUM ('BUY', 'SELL');

CREATE TABLE trade_service.blockchain_task
(
    id            SERIAL UNIQUE,
    wallet        VARCHAR                   NOT NULL,
    chain_id      BIGINT                    NOT NULL,
    amount_usd    BIGINT                    NOT NULL,
    order_id      BIGINT                    NOT NULL,
    type          trade_service.task_type   NOT NULL,
    status        trade_service.task_status NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE  NOT NULL,
    hash          VARCHAR,
    order_task_id BIGINT,
    updated_at    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uc_bt_wallet_order UNIQUE (wallet, order_id)
);

CREATE TABLE trade_service.order_task
(
    id                 SERIAL UNIQUE,
    wallet             VARCHAR                   NOT NULL,
    amount_usd         BIGINT                    NOT NULL,
    order_id           BIGINT                    NOT NULL,
    stock_id           BIGINT                    NOT NULL,
    type               trade_service.task_type   NOT NULL,
    status             trade_service.task_status NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE  NOT NULL,
    confirmation_id    VARCHAR,
    blockchain_task_id BIGINT,
    updated_at         TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uc_ot_wallet_order UNIQUE (wallet, order_id)
);
