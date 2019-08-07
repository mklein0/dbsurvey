
Clickhouse docker environment

## Small setup

```
# server + client setup
docker-compose up -d

# tail logs for a while
docker-compose logs -f


# https://clickhouse.yandex/docs/en/interfaces/cli/
$ docker-compose exec client bash

> cat /root/meta_data/stock_symbol/STOCK_SYMBOL.sql | clickhouse-client --host server --multiquery
CRM	SalesForce.com
> cat /root/data/stock_symbol/STOCK_SYMBOL.csv | clickhouse-client --host server --query="INSERT INTO STOCK_SYMBOL FORMAT CSV"


> clickhouse-client --host server --multiline
ClickHouse client version 19.1.14.
Connecting to server:9000.
Connected to ClickHouse server version 19.1.14 revision 54413.

clickhouse-server :) SELECT * FROM STOCK_SYMBOL;

SELECT *
FROM STOCK_SYMBOL 

┌─SYMBOL─┬─COMPANY────────┐
│ CRM    │ SalesForce.com │
└────────┴────────────────┘
┌─SYMBOL─┬─COMPANY──────────────┐
│ AAPL   │ APPLE Inc.           │
│ CRM    │ SALESFORCE           │
│ GOOG   │ Google               │
│ HOG    │ Harlet-Davidson Inc. │
│ HPQ    │ Hewlett Packard      │
│ INTC   │ Intel                │
│ MSFT   │ Microsoft            │
│ WAG    │ Walgreens            │
│ WMT    │ Walmart              │
└────────┴──────────────────────┘

10 rows in set. Elapsed: 0.008 sec. 

clickhouse-server :) 
```

## Cleanup
```
# hbase/phoenix shutdown  
docker-compose stop

# cleanup container
docker-compose rm -v
```

# WEB STAT
```

> cat /root/meta_data/web_stat/WEB_STAT.sql | clickhouse-client --host server --multiquery
> cat cat /root/data/web_stat/WEB_STAT.csv | clickhouse-client --host server --query="INSERT INTO WEB_STAT FORMAT CSV"

> clickhouse-client --host server --multiline
ClickHouse client version 19.1.14.
Connecting to server:9000.
Connected to ClickHouse server version 19.1.14 revision 54413.

clickhouse-server :) SELECT DOMAIN, AVG("USAGE.CORE") Average_CPU_Usage, AVG("USAGE.DB") Average_DB_Usage
:-] FROM WEB_STAT 
:-] GROUP BY DOMAIN 
:-] ORDER BY DOMAIN DESC;

SELECT 
    DOMAIN, 
    AVG(`USAGE.CORE`) AS Average_CPU_Usage, 
    AVG(`USAGE.DB`) AS Average_DB_Usage
FROM WEB_STAT 
GROUP BY DOMAIN
ORDER BY DOMAIN DESC

┌─DOMAIN─────────┬──Average_CPU_Usage─┬───Average_DB_Usage─┐
│ Salesforce.com │ 260.72727272727275 │  257.6363636363636 │
│ Google.com     │            212.875 │             213.75 │
│ Apple.com      │ 114.11111111111111 │ 119.55555555555556 │
└────────────────┴────────────────────┴────────────────────┘

3 rows in set. Elapsed: 0.010 sec. 

clickhouse-server :) SELECT formatDateTime(DATE, '%Y-%m-%d') DAY, SUM("USAGE.CORE") TOTAL_CPU_Usage, MIN("USAGE.CORE") MIN_CPU_Usage, MAX("USAGE.CORE") MAX_CPU_Usage
:-] FROM WEB_STAT 
:-] WHERE DOMAIN LIKE 'Salesforce%' 
:-] GROUP BY formatDateTime(DATE, '%Y-%m-%d');

SELECT 
    formatDateTime(DATE, '%Y-%m-%d') AS DAY, 
    SUM(`USAGE.CORE`) AS TOTAL_CPU_Usage, 
    MIN(`USAGE.CORE`) AS MIN_CPU_Usage, 
    MAX(`USAGE.CORE`) AS MAX_CPU_Usage
FROM WEB_STAT 
WHERE DOMAIN LIKE 'Salesforce%'
GROUP BY formatDateTime(DATE, '%Y-%m-%d')

┌─DAY────────┬─TOTAL_CPU_Usage─┬─MIN_CPU_Usage─┬─MAX_CPU_Usage─┐
│ 2013-01-08 │             345 │           345 │           345 │
│ 2013-01-09 │             390 │            35 │           355 │
│ 2013-01-02 │             150 │            25 │           125 │
│ 2013-01-03 │              88 │            88 │            88 │
│ 2013-01-06 │              12 │            12 │            12 │
│ 2013-01-16 │             785 │           785 │           785 │
│ 2013-01-17 │            1590 │           355 │          1235 │
│ 2013-01-13 │             355 │           355 │           355 │
│ 2013-01-12 │               5 │             5 │             5 │
│ 2013-01-10 │             345 │           345 │           345 │
│ 2013-01-11 │             335 │           335 │           335 │
│ 2013-01-15 │             720 │            65 │           655 │
│ 2013-01-14 │               5 │             5 │             5 │
│ 2013-01-04 │              26 │             3 │            23 │
│ 2013-01-05 │             550 │            75 │           475 │
│ 2013-01-01 │              35 │            35 │            35 │
└────────────┴─────────────────┴───────────────┴───────────────┘

16 rows in set. Elapsed: 0.013 sec. 

clickhouse-server :) SELECT HOST, SUM("STATS.ACTIVE_VISITOR") TOTAL_ACTIVE_VISITORS
:-] FROM WEB_STAT 
:-] WHERE "USAGE.DB" > ("USAGE.CORE" * 10)
:-] GROUP BY HOST;

SELECT 
    HOST, 
    SUM(`STATS.ACTIVE_VISITOR`) AS TOTAL_ACTIVE_VISITORS
FROM WEB_STAT 
WHERE `USAGE.DB` > (`USAGE.CORE` * 10)
GROUP BY HOST

┌─HOST─┬─TOTAL_ACTIVE_VISITORS─┐
│ NA   │                     1 │
│ EU   │                   150 │
└──────┴───────────────────────┘

2 rows in set. Elapsed: 0.015 sec. 

clickhouse-server :) 
```
