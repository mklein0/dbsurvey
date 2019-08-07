DROP TABLE IF EXISTS STOCK_SYMBOL;
CREATE TABLE IF NOT EXISTS STOCK_SYMBOL (
  SYMBOL VARCHAR NOT NULL PRIMARY KEY,
  COMPANY VARCHAR
);
UPSERT INTO STOCK_SYMBOL VALUES ('CRM','SalesForce.com');
SELECT * FROM STOCK_SYMBOL;

