-- Task
CREATE TABLE task(
    chain_id     BIGINT PRIMARY KEY,
    block_number BIGINT NOT NULL,
    timestamp    BIGINT NOT NULL
);
