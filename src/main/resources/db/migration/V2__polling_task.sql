-- Task
CREATE TABLE task
(
    chain_id     BIGINT PRIMARY KEY,
    block_number BIGINT NOT NULL,
    timestamp    BIGINT NOT NULL
);

CREATE TYPE task_status AS ENUM ('PENDING', 'SUCCESSFUL', 'FAILED');
CREATE TYPE task_type AS ENUM ('BUY', 'SELL');

CREATE TABLE blockchain_task
(
    id            SERIAL UNIQUE,
    wallet        VARCHAR                  NOT NULL,
    chain_id      BIGINT                   NOT NULL,
    amount_usd    BIGINT                   NOT NULL,
    order_id      BIGINT                   NOT NULL,
    type          task_type                NOT NULL,
    status        task_status              NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    hash          VARCHAR,
    order_task_id BIGINT,
    updated_at    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uc_bt_wallet_order UNIQUE (wallet, order_id)
);

CREATE TABLE order_task
(
    id                 SERIAL UNIQUE,
    wallet             VARCHAR                  NOT NULL,
    amount_usd         BIGINT                   NOT NULL,
    order_id           BIGINT                   NOT NULL,
    stock_id           BIGINT                   NOT NULL,
    type               task_type                NOT NULL,
    status             task_status              NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmation_id    VARCHAR,
    blockchain_task_id BIGINT,
    updated_at         TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uc_ot_wallet_order UNIQUE (wallet, order_id)
);
