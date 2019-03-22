
-- https://clickhouse.yandex/docs/en/operations/table_engines/mergetree/
CREATE TABLE STOCK_SYMBOL
(
    SYMBOL String,
    COMPANY String
)
ENGINE = MergeTree()
ORDER BY SYMBOL
PRIMARY KEY SYMBOL
;

-- No UPSERT
INSERT INTO STOCK_SYMBOL VALUES ('CRM','SalesForce.com');

SELECT * FROM STOCK_SYMBOL;

