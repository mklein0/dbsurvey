# Phoenix With HBase

Apache Phoenix docker image based on alpine

## Small setup

```
# hbase+phoenix startup
docker-compose up -d

# tail logs for a while
docker-compose logs -f

# check ps
docker-compose ps
     Name                   Command               State                  Ports
---------------------------------------------------------------------------------------------
datanode-1       entrypoint.sh datanode           Up      50010/tcp, 50020/tcp, 50075/tcp
hmaster-1        entrypoint.sh hmaster-1          Up      16000/tcp, 0.0.0.0:32781->16010/tcp
namenode-1       entrypoint.sh namenode-1         Up      0.0.0.0:32780->50070/tcp, 8020/tcp
queryserver-1    entrypoint.sh bin/queryser ...   Up      8765/tcp
regionserver-1   entrypoint.sh regionserver       Up      16020/tcp, 16030/tcp
zookeeper-1      entrypoint.sh -server 1 1 vnet   Up      2181/tcp, 2888/tcp, 3888/tcp

# Try Getting Started (http://phoenix.apache.org/installation.html)
```

```
$ docker-compose exec regionserver-1 bash
> psql.py zookeeper-1.vnet /root/meta_data/stock_symbol/STOCK_SYMBOL.sql /root/data/stock_symbol/STOCK_SYMBOL.csv
no rows upserted
Time: 0.01 sec(s)

1 row upserted
Time: 0.044 sec(s)

SYMBOL                                   COMPANY
---------------------------------------- ----------------------------------------
CRM                                      SalesForce.com
Time: 0.044 sec(s)

csv columns from database.
CSV Upsert complete. 9 rows upserted
Time: 0.02 sec(s)

-- Bulk update second parameter to command line above

> sqlline.py zookeeper-1.vnet
Connected to: Phoenix (version 5.0)
Driver: PhoenixEmbeddedDriver (version 5.0)
Autocommit status: true
Transaction isolation: TRANSACTION_READ_COMMITTED
Building list of tables and columns for tab-completion (set fastconnect to true to skip)...
88/88 (100%) Done
Done
sqlline version 1.2.0
0: jdbc:phoenix:zookeeper-1.vnet>
0: jdbc:phoenix:zookeeper-1.vnet> !table
0: jdbc:phoenix:zookeeper-1.vnet> select * from STOCK_SYMBOL;
+---------+-----------------------+
| SYMBOL  |        COMPANY        |
+---------+-----------------------+
| AAPL    | APPLE Inc.            |
| CRM     | SALESFORCE            |
| GOOG    | Google                |
| HOG     | Harlet-Davidson Inc.  |
| HPQ     | Hewlett Packard       |
| INTC    | Intel                 |
| MSFT    | Microsoft             |
| WAG     | Walgreens             |
| WMT     | Walmart               |
+---------+-----------------------+
9 rows selected (0.067 seconds)

0: jdbc:phoenix:zookeeper-1.vnet> !exit
```

## Use queryserver client
```
$ docker run -it --rm --net vnet smizy/apache-phoenix:5.0.0-alpine sh
> bin/sqlline-thin.py http://queryserver-1.vnet:8765
Setting property: [incremental, false]
Setting property: [isolation, TRANSACTION_READ_COMMITTED]
issuing: !connect jdbc:phoenix:thin:url=http://queryserver-1.vnet:8765;serialization=PROTOBUF none none org.apache.phoenix.queryserver.client.Driver
Connecting to jdbc:phoenix:thin:url=http://queryserver-1.vnet:8765;serialization=PROTOBUF
Connected to: Apache Phoenix (version unknown version)
Driver: Phoenix Remote JDBC Driver (version unknown version)
Autocommit status: true
Transaction isolation: TRANSACTION_READ_COMMITTED
Building list of tables and columns for tab-completion (set fastconnect to true to skip)...
101/101 (100%) Done
Done
sqlline version 1.2.0
0: jdbc:phoenix:thin:url=http://queryserver-1>
0: jdbc:phoenix:thin:url=http://queryserver-1> !table
0: jdbc:phoenix:thin:url=http://queryserver-1> select * from STOCK_SYMBOL;
+---------+-----------------------+
| SYMBOL  |        COMPANY        |
+---------+-----------------------+
| AAPL    | APPLE Inc.            |
| CRM     | SALESFORCE            |
| GOOG    | Google                |
| HOG     | Harlet-Davidson Inc.  |
| HPQ     | Hewlett Packard       |
| INTC    | Intel                 |
| MSFT    | Microsoft             |
| WAG     | Walgreens             |
| WMT     | Walmart               |
+---------+-----------------------+
9 rows selected (0.04 seconds)
0: jdbc:phoenix:thin:url=http://queryserver-1> !exit

```

## Cleanup
```
# hbase/phoenix shutdown
docker-compose stop

# cleanup container
docker-compose rm -v
```

## WEB STAT
```

> sqlline.py zookeeper-1.vnet /root/meta_data/web_stat/WEB_STAT.sql
Setting property: [incremental, false]
Setting property: [isolation, TRANSACTION_READ_COMMITTED]
Setting property: [run, /root/meta_data/web_stat/WEB_STAT.sql]
issuing: !connect jdbc:phoenix:zookeeper-1.vnet none none org.apache.phoenix.jdbc.PhoenixDriver
Connecting to jdbc:phoenix:zookeeper-1.vnet
19/03/22 22:31:08 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Connected to: Phoenix (version 5.0)
Driver: PhoenixEmbeddedDriver (version 5.0)
Autocommit status: true
Transaction isolation: TRANSACTION_READ_COMMITTED
Building list of tables and columns for tab-completion (set fastconnect to true to skip)...
135/135 (100%) Done
Done
1/1          CREATE TABLE IF NOT EXISTS WEB_STAT (
HOST CHAR(2) NOT NULL,
DOMAIN VARCHAR NOT NULL,
FEATURE VARCHAR NOT NULL,
DATE DATE NOT NULL,
USAGE.CORE BIGINT,
USAGE.DB BIGINT,
STATS.ACTIVE_VISITOR INTEGER
CONSTRAINT PK PRIMARY KEY (HOST, DOMAIN, FEATURE, DATE)
);
No rows affected (1.563 seconds)
Closing: org.apache.phoenix.jdbc.PhoenixConnection
sqlline version 1.2.0
> psql.py -t WEB_STAT zookeeper-1.vnet /root/data/web_stat/WEB_STAT.csv
19/03/22 22:32:07 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
csv columns from database.
CSV Upsert complete. 39 rows upserted
Time: 0.245 sec(s)


> sqlline.py zookeeper-1.vnet
Setting property: [incremental, false]
Setting property: [isolation, TRANSACTION_READ_COMMITTED]
issuing: !connect jdbc:phoenix:zookeeper-1.vnet none none org.apache.phoenix.jdbc.PhoenixDriver
Connecting to jdbc:phoenix:zookeeper-1.vnet
19/03/22 22:33:03 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Connected to: Phoenix (version 5.0)
Driver: PhoenixEmbeddedDriver (version 5.0)
Autocommit status: true
Transaction isolation: TRANSACTION_READ_COMMITTED
Building list of tables and columns for tab-completion (set fastconnect to true to skip)...
142/142 (100%) Done
Done
sqlline version 1.2.0
0: jdbc:phoenix:zookeeper-1.vnet> SELECT DOMAIN, AVG(CORE) Average_CPU_Usage, AVG(DB) Average_DB_Usage
. . . . . . . . . . . . . . . . > FROM WEB_STAT
. . . . . . . . . . . . . . . . > GROUP BY DOMAIN
. . . . . . . . . . . . . . . . > ORDER BY DOMAIN DESC;
+-----------------+--------------------+-------------------+
|     DOMAIN      | AVERAGE_CPU_USAGE  | AVERAGE_DB_USAGE  |
+-----------------+--------------------+-------------------+
| Salesforce.com  | 260.7272           | 257.6363          |
| Google.com      | 212.875            | 213.75            |
| Apple.com       | 114.1111           | 119.5555          |
+-----------------+--------------------+-------------------+
3 rows selected (0.295 seconds)
0: jdbc:phoenix:zookeeper-1.vnet> SELECT TRUNC(DATE,'DAY') DAY, SUM(CORE) TOTAL_CPU_Usage, MIN(CORE) MIN_CPU_Usage, MAX(CORE) MAX_CPU_Usage
. . . . . . . . . . . . . . . . > FROM WEB_STAT
. . . . . . . . . . . . . . . . > WHERE DOMAIN LIKE 'Salesforce%'
. . . . . . . . . . . . . . . . > GROUP BY TRUNC(DATE,'DAY');
+--------------------------+------------------+----------------+----------------+
|           DAY            | TOTAL_CPU_USAGE  | MIN_CPU_USAGE  | MAX_CPU_USAGE  |
+--------------------------+------------------+----------------+----------------+
| 2013-01-01 00:00:00.000  | 35               | 35             | 35             |
| 2013-01-02 00:00:00.000  | 150              | 25             | 125            |
| 2013-01-03 00:00:00.000  | 88               | 88             | 88             |
| 2013-01-04 00:00:00.000  | 26               | 3              | 23             |
| 2013-01-05 00:00:00.000  | 550              | 75             | 475            |
| 2013-01-06 00:00:00.000  | 12               | 12             | 12             |
| 2013-01-08 00:00:00.000  | 345              | 345            | 345            |
| 2013-01-09 00:00:00.000  | 390              | 35             | 355            |
| 2013-01-10 00:00:00.000  | 345              | 345            | 345            |
| 2013-01-11 00:00:00.000  | 335              | 335            | 335            |
| 2013-01-12 00:00:00.000  | 5                | 5              | 5              |
| 2013-01-13 00:00:00.000  | 355              | 355            | 355            |
| 2013-01-14 00:00:00.000  | 5                | 5              | 5              |
| 2013-01-15 00:00:00.000  | 720              | 65             | 655            |
| 2013-01-16 00:00:00.000  | 785              | 785            | 785            |
| 2013-01-17 00:00:00.000  | 1590             | 355            | 1235           |
+--------------------------+------------------+----------------+----------------+
16 rows selected (0.244 seconds)
0: jdbc:phoenix:zookeeper-1.vnet> SELECT HOST, SUM(ACTIVE_VISITOR) TOTAL_ACTIVE_VISITORS
. . . . . . . . . . . . . . . . > FROM WEB_STAT
. . . . . . . . . . . . . . . . > WHERE DB > (CORE * 10)
. . . . . . . . . . . . . . . . > GROUP BY HOST;
+-------+------------------------+
| HOST  | TOTAL_ACTIVE_VISITORS  |
+-------+------------------------+
| EU    | 150                    |
| NA    | 1                      |
+-------+------------------------+
2 rows selected (0.13 seconds)
0: jdbc:phoenix:zookeeper-1.vnet>
```

## Wide Table
### 1500 Column

```
$ docker exec -it regionserver-1 bash

# Load Schema + data
> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_1500.sql /root/data/wide_table/WIDE_TABLE_1500.csv
20/09/21 20:47:09 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
  no rows upserted
  Time: 1.84 sec(s)

  csv columns from database.
  CSV Upsert complete. 39 rows upserted
  Time: 0.541 sec(s)

>

> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_1500_QUERIES.sql
...
244.889 Apple.com
  Time: 0.546 sec(s)

>
> sqlline.py zookeeper-1.vnet
Setting property: [incremental, false]
Setting property: [isolation, TRANSACTION_READ_COMMITTED]
issuing: !connect jdbc:phoenix:zookeeper-1.vnet none none org.apache.phoenix.jdbc.PhoenixDriver
Connecting to jdbc:phoenix:zookeeper-1.vnet
20/09/21 20:48:06 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Connected to: Phoenix (version 5.0)
Driver: PhoenixEmbeddedDriver (version 5.0)
Autocommit status: true
Transaction isolation: TRANSACTION_READ_COMMITTED
Building list of tables and columns for tab-completion (set fastconnect to true to skip)...
1634/1634 (100%) Done
Done
sqlline version 1.2.0
0: jdbc:phoenix:zookeeper-1.vnet> !table
+------------+--------------+------------------+---------------+----------+------------+----------------------------+-----------------+--------------+-----------------+---------------+------------+
| TABLE_CAT  | TABLE_SCHEM  |    TABLE_NAME    |  TABLE_TYPE   | REMARKS  | TYPE_NAME  | SELF_REFERENCING_COL_NAME  | REF_GENERATION  | INDEX_STATE  | IMMUTABLE_ROWS  | SALT_BUCKETS  | MULTI_TENA |
+------------+--------------+------------------+---------------+----------+------------+----------------------------+-----------------+--------------+-----------------+---------------+------------+
|            | SYSTEM       | CATALOG          | SYSTEM TABLE  |          |            |                            |                 |              | false           | null          | false      |
|            | SYSTEM       | FUNCTION         | SYSTEM TABLE  |          |            |                            |                 |              | false           | null          | false      |
|            | SYSTEM       | LOG              | SYSTEM TABLE  |          |            |                            |                 |              | true            | 32            | false      |
|            | SYSTEM       | SEQUENCE         | SYSTEM TABLE  |          |            |                            |                 |              | false           | null          | false      |
|            | SYSTEM       | STATS            | SYSTEM TABLE  |          |            |                            |                 |              | false           | null          | false      |
|            |              | WIDE_TABLE_1500  | TABLE         |          |            |                            |                 |              | false           | null          | false      |
+------------+--------------+------------------+---------------+----------+------------+----------------------------+-----------------+--------------+-----------------+---------------+------------+
0: jdbc:phoenix:zookeeper-1.vnet>

```

### 3K Column

```
$ docker exec -it regionserver-1 bash

# Load Schema + data
> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_3K.sql /root/data/wide_table/WIDE_TABLE_3K.csv
20/09/21 21:23:22 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
no rows upserted
Time: 1.918 sec(s)

csv columns from database.
CSV Upsert complete. 39 rows upserted
Time: 0.88 sec(s)

>

> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_3K_QUERIES.sql
...
190.667 Apple.com
Time: 0.714 sec(s)

>
```

### 10K Column

```
$ docker exec -it regionserver-1 bash

# Load Schema + data
> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_10K.sql /root/data/wide_table/WIDE_TABLE_10K.csv
20/09/21 21:51:18 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
no rows upserted
Time: 2.938 sec(s)

csv columns from database.
20/09/21 21:51:24 ERROR util.CSVCommonsLoader: Error upserting record [EU, Salesforce.com, Login, 2013-01-12 01:01:01, 5, 62, 314, wkKKFLDpVYaxfbnd, 49.5, 451,
...
JtBVwqcFdjbwabE, 192.0, 150]
CSV Upsert complete. 38 rows upserted
Time: 1.839 sec(s)

>

> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_10K_QUERIES.sql
...
7 Apple.com
Time: 1.874 sec(s)

>
```

Issue is batch memory buffer too small. Need to enlarge it to load CSV file via updates to hbase-site.xml properties.

```
    <property>
      <name>phoenix.mutate.maxSizeBytes</name>
      <value>2147483648</value>
    </property>
```

### 15K Column

```
$ docker exec -it regionserver-1 bash

# Load Schema + data
> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_15K.sql /root/data/wide_table/WIDE_TABLE_15K.csv
20/09/21 21:58:48 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
no rows upserted
Time: 3.565 sec(s)

csv columns from database.
...
kvGMnvIfWWmZ, 27.5, 645, HRbwrwZBuSsNFlkK, 32.5, 572, mmuYGVjTrCirxoUX, 172.0, 6]
CSV Upsert complete. 37 rows upserted
Time: 1.621 sec(s)

>

> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_15K_QUERIES.sql
...
7 Apple.com
Time: 1.651 sec(s)

>
```

### 40K Column

```
$ docker exec -it regionserver-1 bash

# Load Schema + data
> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_40K.sql /root/data/wide_table/WIDE_TABLE_40K.csv
20/09/21 21:58:48 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
no rows upserted
Time: 3.565 sec(s)

csv columns from database.
...
kvGMnvIfWWmZ, 27.5, 645, HRbwrwZBuSsNFlkK, 32.5, 572, mmuYGVjTrCirxoUX, 172.0, 6]
CSV Upsert complete. 33 rows upserted
Time: 2.641 sec(s)


>

> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_40K_QUERIES.sql
...
7 Googlecom
Time: 2.746 sec(s)

>
```

### 100K Column

```
$ docker exec -it regionserver-1 bash

# Load Schema + data
> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_100K.sql
20/09/21 22:41:15 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
no rows upserted
Time: 13.27 sec(s)

> psql.py -t WIDE_TABLE_100K zookeeper-1.vnet /root/data/wide_table/WIDE_TABLE_100K.csv
20/09/21 22:43:02 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
csv columns from database.
20/09/21 22:43:02 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
csv columns from database.
20/09/21 22:43:14 ERROR util.CSVCommonsLoader: Error upserting record [EU, Salesforce.com, Reports, 2013-01-02 14:32:01, 125, 131, 311, VVoqkkxbeMEsxRRt, 111.5, 325, CGTtOmozXhXotG...
20/09/21 22:43:14 ERROR util.CSVCommonsLoader: Error upserting record [NA, Salesforce.com, Login, 2013-01-04 06:01:21, 3, 52, 166, LFlhyRjeaUMgOqrv, 96.0, 440, SnNZsyTgkBKMaBaZ, 0....
20/09/21 22:43:16 ERROR util.CSVCommonsLoader: Error upserting record [EU, Salesforce.com, Reports, 2013-01-05 03:11:12, 75, 22, 937, xgByPeZHoULRPpZu, 198.0, 385, esMPBtTdXpMRSrAm...
20/09/21 22:43:16 ERROR util.CSVCommonsLoader: Error upserting record [NA, Google.com, Analytics, 2013-01-07 06:01:01, 23, 1, 774, yHGreSLfUmPnILiw, 309.0, 89, ZOEvUhDwfNCUINxO, 35...
20/09/21 22:43:16 ERROR util.CSVCommonsLoader: Error upserting record [NA, Salesforce.com, Login, 2013-01-08 14:11:01, 345, 242, 126, CFOzXbMkQgpDoWUi, 373.0, 921, hgPXoMZfPcOorfha...
20/09/21 22:43:17 ERROR util.CSVCommonsLoader: Error upserting record [EU, Apple.com, Store, 2013-01-03 01:01:01, 345, 722, 284, bfeJAwKIylyjYZtu, 368.5, 139, brLNuWRsFKqaMevm, 272...
20/09/21 22:43:17 ERROR util.CSVCommonsLoader: Error upserting record [NA, Apple.com, Login, 2013-01-04 01:01:01, 135, 2, 677, yhfeUSxQLKwzoHUN, 158.5, 381, yEbXEUmQmvKgNTve, 353.5...
20/09/21 22:43:17 ERROR util.CSVCommonsLoader: Error upserting record [EU, Salesforce.com, Login, 2013-01-12 01:01:01, 5, 62, 78, hxQztcWhkKcWHuyd, 490.5, 527, kKCubooEfmHhnLJR, 40...
20/09/21 22:43:18 ERROR util.CSVCommonsLoader: Error upserting record [EU, Salesforce.com, Reports, 2013-01-13 08:04:04, 355, 52, 302, uQlqczFknafLPXKA, 327.5, 20, pNSVWOXCPZdzynem...
20/09/21 22:43:18 ERROR util.CSVCommonsLoader: Error upserting record [NA, Apple.com, iPad, 2013-01-07 01:01:01, 9, 27, 329, aekBxjEGSBRkQGMM, 107.0, 49, bfWhipnVDuHEXwYZ, 99.0, 52...
20/09/21 22:43:18 ERROR util.CSVCommonsLoader: Error upserting record [NA, Salesforce.com, Reports, 2013-01-15 07:09:01, 655, 426, 581, ZLxbOwiGDPzpCOvy, 423.0, 675, lvkwTJLLbOTvrE...
20/09/21 22:43:19 ERROR util.CSVCommonsLoader: Error upserting record [NA, Salesforce.com, Login, 2013-01-16 01:01:01, 785, 782, 597, vwUTSFeaOjALdiPS, 329.0, 690, tPOrcVJrLlESSyrE...
20/09/21 22:43:19 ERROR util.CSVCommonsLoader: Error upserting record [NA, Salesforce.com, Login, 2013-01-17 02:20:01, 1235, 2422, 737, YzJRicLpzOlqdyRi, 255.0, 914, PRcTKCaxZCNvGU...
CSV Upsert complete. 26 rows upserted
Time: 10.334 sec(s)

> psql.py zookeeper-1.vnet /root/meta_data/wide_table/WIDE_TABLE_100K_QUERIES.sql
...
7 Googlecom
Time: 2.746 sec(s)

>
```
