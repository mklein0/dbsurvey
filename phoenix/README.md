# Phoenix With HBase

Apache Phoenix docker image based on alpine

## Small setup

```
# load default env as needed
eval $(docker-machine env default)

# network 
docker network create vnet

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
