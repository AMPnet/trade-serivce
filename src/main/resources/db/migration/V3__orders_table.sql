DROP TABLE trade_service.order_task;

CREATE TYPE trade_service.ORDER_TYPE AS ENUM ('BUY', 'SELL');
CREATE TYPE trade_service.ORDER_STATUS AS ENUM ('PREPARED', 'PENDING', 'SUCCESSFUL', 'FAILED');

CREATE TABLE trade_service.order (
    id                           SERIAL UNIQUE,
    interactive_brokers_order_id INTEGER                    NOT NULL,
    blockchain_order_id          INTEGER                    NOT NULL,
    chain_id                     BIGINT                     NOT NULL,
    wallet                       VARCHAR                    NOT NULL,
    stock_id                     INTEGER                    NOT NULL,
    amount_usd                   DECIMAL(28, 18)            NOT NULL,
    min_or_max_price             DOUBLE PRECISION           NOT NULL,
    num_shares                   INTEGER                    NOT NULL,
    order_type                   trade_service.ORDER_TYPE   NOT NULL,
    order_status                 trade_service.ORDER_STATUS NOT NULL,
    created_at                   TIMESTAMP WITH TIME ZONE   NOT NULL,
    submitted_at                 TIMESTAMP WITH TIME ZONE,
    completed_at                 TIMESTAMP WITH TIME ZONE
);

CREATE INDEX order_type_idx ON trade_service.order(order_type);
CREATE INDEX order_status_idx ON trade_service.order(order_status);
CREATE INDEX order_submitted_at_idx ON trade_service.order(submitted_at);
