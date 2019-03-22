-- https://clickhouse.yandex/docs/en/operations/table_engines/mergetree/
-- https://clickhouse.yandex/docs/en/data_types/
-- Case-Sensitive syntax
CREATE TABLE WEB_STAT (
     HOST FixedString(2),
     DOMAIN String,
     FEATURE String,
     DATE DateTime,
     "USAGE.CORE" UInt64,
     "USAGE.DB" UInt64,
     "STATS.ACTIVE_VISITOR" Int32
)
ENGINE = MergeTree()
ORDER BY (HOST, DOMAIN, FEATURE, DATE)
;
