-- This is here only to test that jOOQ and Flyway work as intended - replace this with actual first migration
CREATE TABLE trade_service.dummy (
    dummy_column INT PRIMARY KEY
);

INSERT INTO trade_service.dummy(dummy_column) VALUES (1);
