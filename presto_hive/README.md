# Hive with Presto

## Setup
```
# run containers
docker-compose up -d

docker-compose exec hive-server bash
# hdfs dfs -mkdir /root
# hdfs dfs -put /root/data /root
# hdfs dfs -rm /root/data/wide_table/fields.py
# hdfs dfs -rm /root/data/wide_table/WIDE_TABLE2.csv  
```

## Hive
https://hortonworks.com/blog/hive-cheat-sheet-for-sql-users/

https://www.tutorialspoint.com/hive/hive_create_table.htm
### Stock Symbol
```
# cat /root/meta_data/stock_symbol/STOCK_SYMBOL.hql | /opt/hive/bin/beeline -u jdbc:hive2://localhost:10000
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/hive/lib/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/hadoop-2.7.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Connecting to jdbc:hive2://localhost:10000
Connected to: Apache Hive (version 2.3.2)
Driver: Hive JDBC (version 2.3.2)
Transaction isolation: TRANSACTION_REPEATABLE_READ
Beeline version 2.3.2 by Apache Hive
0: jdbc:hive2://localhost:10000> CREATE TABLE IF NOT EXISTS STOCK_SYMBOL (SYMBOL STRING, COMPANY STRING);
No rows affected (0.09 seconds)
0: jdbc:hive2://localhost:10000> INSERT OVERWRITE TABLE STOCK_SYMBOL VALUES ('CRM','SalesForce.com');
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
No rows affected (1.843 seconds)
0: jdbc:hive2://localhost:10000> SELECT * FROM STOCK_SYMBOL;
+----------------------+-----------------------+
| stock_symbol.symbol  | stock_symbol.company  |
+----------------------+-----------------------+
| CRM                  | SalesForce.com        |
+----------------------+-----------------------+
1 row selected (0.257 seconds)
0: jdbc:hive2://localhost:10000> -- newline to allow script to echo selectClosing: 0: jdbc:hive2://localhost:10000
#
# /opt/hive/bin/beeline -u jdbc:hive2://localhost:10000
> CREATE EXTERNAL TABLE csv_file (SYMBOL STRING, COMPANY STRING)
 COMMENT 'from csv file'
 ROW FORMAT DELIMITED FIELDS TERMINATED BY '\054'
 STORED AS TEXTFILE
 LOCATION '/root/data/stock_symbol';
No rows affected (0.076 seconds)
> SELECT * FROM csv_file LIMIT 5;
+------------------+-----------------------+
| csv_file.symbol  |   csv_file.company    |
+------------------+-----------------------+
| AAPL             | APPLE Inc.            |
| CRM              | SALESFORCE            |
| GOOG             | Google                |
| HOG              | Harlet-Davidson Inc.  |
| HPQ              | Hewlett Packard       |
+------------------+-----------------------+
5 rows selected (0.175 seconds)
> SELECT * FROM stock_symbol;
+----------------------+-----------------------+
| stock_symbol.symbol  | stock_symbol.company  |
+----------------------+-----------------------+
| CRM                  | SalesForce.com        |
+----------------------+-----------------------+
1 row selected (0.26 seconds)
> INSERT INTO TABLE stock_symbol SELECT * FROM csv_file;
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
No rows affected (1.943 seconds)
> SELECT * FROM stock_symbol;
+----------------------+-----------------------+
| stock_symbol.symbol  | stock_symbol.company  |
+----------------------+-----------------------+
| CRM                  | SalesForce.com        |
| AAPL                 | APPLE Inc.            |
| CRM                  | SALESFORCE            |
| GOOG                 | Google                |
| HOG                  | Harlet-Davidson Inc.  |
| HPQ                  | Hewlett Packard       |
| INTC                 | Intel                 |
| MSFT                 | Microsoft             |
| WAG                  | Walgreens             |
| WMT                  | Walmart               |
+----------------------+-----------------------+
10 rows selected (0.196 seconds)
>
```

### WEB STAT
```
# cat /root/meta_data/web_stat/WEB_STAT.hql | /opt/hive/bin/beeline -u jdbc:hive2://localhost:10000
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/hive/lib/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/hadoop-2.7.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Connecting to jdbc:hive2://localhost:10000
Connected to: Apache Hive (version 2.3.2)
Driver: Hive JDBC (version 2.3.2)
Transaction isolation: TRANSACTION_REPEATABLE_READ
Beeline version 2.3.2 by Apache Hive
0: jdbc:hive2://localhost:10000> CREATE EXTERNAL TABLE WEB_STAT (
. . . . . . . . . . . . . . . .>      HOST STRING,
. . . . . . . . . . . . . . . .>      DOMAIN STRING,
. . . . . . . . . . . . . . . .>      FEATURE STRING,
. . . . . . . . . . . . . . . .>      `DATE` TIMESTAMP,
. . . . . . . . . . . . . . . .>      USAGE__CORE BIGINT,
. . . . . . . . . . . . . . . .>      USAGE__DB BIGINT,
. . . . . . . . . . . . . . . .>      STATS__ACTIVE_VISITOR INTEGER
. . . . . . . . . . . . . . . .> )
. . . . . . . . . . . . . . . .> COMMENT 'from csv file'
. . . . . . . . . . . . . . . .> ROW FORMAT DELIMITED FIELDS TERMINATED BY '\054'
. . . . . . . . . . . . . . . .> STORED AS TEXTFILE
. . . . . . . . . . . . . . . .> LOCATION '/root/data/web_stat'
. . . . . . . . . . . . . . . .> ;
No rows affected (0.151 seconds)
0: jdbc:hive2://localhost:10000> SELECT * FROM web_stat LIMIT 5;
+----------------+------------------+-------------------+------------------------+-----------------------+---------------------+---------------------------------+
| web_stat.host  | web_stat.domain  | web_stat.feature  |     web_stat.date      | web_stat.usage__core  | web_stat.usage__db  | web_stat.stats__active_visitor  |
+----------------+------------------+-------------------+------------------------+-----------------------+---------------------+---------------------------------+
| NA             | Salesforce.com   | Login             | 2013-01-01 01:01:01.0  | 35                    | 42                  | 10                              |
| EU             | Salesforce.com   | Reports           | 2013-01-02 12:02:01.0  | 25                    | 11                  | 2                               |
| EU             | Salesforce.com   | Reports           | 2013-01-02 14:32:01.0  | 125                   | 131                 | 42                              |
| NA             | Apple.com        | Login             | 2013-01-01 01:01:01.0  | 35                    | 22                  | 40                              |
| NA             | Salesforce.com   | Dashboard         | 2013-01-03 11:01:01.0  | 88                    | 66                  | 44                              |
+----------------+------------------+-------------------+------------------------+-----------------------+---------------------+---------------------------------+
5 rows selected (0.195 seconds)
0: jdbc:hive2://localhost:10000> Closing: 0: jdbc:hive2://localhost:10000
#
# cat /root/meta_data/web_stat/WEB_STAT_QUERIES.hql | /opt/hive/bin/beeline -u jdbc:hive2://localhost:10000
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/hive/lib/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/hadoop-2.7.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Connecting to jdbc:hive2://localhost:10000
Connected to: Apache Hive (version 2.3.2)
Driver: Hive JDBC (version 2.3.2)
Transaction isolation: TRANSACTION_REPEATABLE_READ
Beeline version 2.3.2 by Apache Hive
0: jdbc:hive2://localhost:10000> SELECT DOMAIN, AVG(USAGE__CORE) Average_CPU_Usage, AVG(USAGE__DB) Average_DB_Usage
. . . . . . . . . . . . . . . .> FROM WEB_STAT 
. . . . . . . . . . . . . . . .> GROUP BY DOMAIN 
. . . . . . . . . . . . . . . .> ORDER BY DOMAIN DESC;
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
+-----------------+---------------------+---------------------+
|     domain      |  average_cpu_usage  |  average_db_usage   |
+-----------------+---------------------+---------------------+
| Salesforce.com  | 260.72727272727275  | 257.6363636363636   |
| Google.com      | 212.875             | 213.75              |
| Apple.com       | 114.11111111111111  | 119.55555555555556  |
+-----------------+---------------------+---------------------+
3 rows selected (2.801 seconds)
0: jdbc:hive2://localhost:10000> 
0: jdbc:hive2://localhost:10000> -- Sum, Min and Max CPU usage by Salesforce grouped by day
0: jdbc:hive2://localhost:10000> SELECT TO_DATE(`DATE`) DAY,
. . . . . . . . . . . . . . . .>  SUM(USAGE__CORE) TOTAL_CPU_Usage, MIN(USAGE__CORE) MIN_CPU_Usage, MAX(USAGE__CORE) MAX_CPU_Usage
. . . . . . . . . . . . . . . .> FROM WEB_STAT 
. . . . . . . . . . . . . . . .> WHERE DOMAIN LIKE 'Salesforce%' 
. . . . . . . . . . . . . . . .> GROUP BY TO_DATE(`DATE`);
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
+-------------+------------------+----------------+----------------+
|     day     | total_cpu_usage  | min_cpu_usage  | max_cpu_usage  |
+-------------+------------------+----------------+----------------+
| 2013-01-01  | 35               | 35             | 35             |
| 2013-01-02  | 150              | 25             | 125            |
| 2013-01-03  | 88               | 88             | 88             |
| 2013-01-04  | 26               | 3              | 23             |
| 2013-01-05  | 550              | 75             | 475            |
| 2013-01-06  | 12               | 12             | 12             |
| 2013-01-08  | 345              | 345            | 345            |
| 2013-01-09  | 390              | 35             | 355            |
| 2013-01-10  | 345              | 345            | 345            |
| 2013-01-11  | 335              | 335            | 335            |
| 2013-01-12  | 5                | 5              | 5              |
| 2013-01-13  | 355              | 355            | 355            |
| 2013-01-14  | 5                | 5              | 5              |
| 2013-01-15  | 720              | 65             | 655            |
| 2013-01-16  | 785              | 785            | 785            |
| 2013-01-17  | 1590             | 355            | 1235           |
+-------------+------------------+----------------+----------------+
16 rows selected (1.408 seconds)
0: jdbc:hive2://localhost:10000> 
0: jdbc:hive2://localhost:10000> -- list host and total active users when core CPU usage is 10X greater than DB usage
0: jdbc:hive2://localhost:10000> SELECT HOST, SUM(STATS__ACTIVE_VISITOR) TOTAL_ACTIVE_VISITORS
. . . . . . . . . . . . . . . .> FROM WEB_STAT 
. . . . . . . . . . . . . . . .> WHERE USAGE__DB > (USAGE__CORE * 10)
. . . . . . . . . . . . . . . .> GROUP BY HOST;
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
+-------+------------------------+
| host  | total_active_visitors  |
+-------+------------------------+
| EU    | 150                    |
| NA    | 1                      |
+-------+------------------------+
2 rows selected (1.369 seconds)
0: jdbc:hive2://localhost:10000> Closing: 0: jdbc:hive2://localhost:10000
#
```

### WIDE TABLE

```
# cat /root/meta_data/wide_table/WIDE_TABLE.hql | /opt/hive/bin/beeline -u jdbc:hive2://localhost:10000
-- TLDR;
# cat /root/meta_data/wide_table/WIDE_TABLE_QUERIES.hql | /opt/hive/bin/beeline -u jdbc:hive2://localhost:10000
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/hive/lib/log4j-slf4j-impl-2.6.2.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/hadoop-2.7.4/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Connecting to jdbc:hive2://localhost:10000
Connected to: Apache Hive (version 2.3.2)
Driver: Hive JDBC (version 2.3.2)
Transaction isolation: TRANSACTION_REPEATABLE_READ
Beeline version 2.3.2 by Apache Hive
0: jdbc:hive2://localhost:10000> SELECT DOMAIN, AVG(USAGE__CORE) Average_CPU_Usage, AVG(USAGE__DB) Average_DB_Usage 
. . . . . . . . . . . . . . . .> FROM WIDE_TABLE 
. . . . . . . . . . . . . . . .> GROUP BY DOMAIN 
. . . . . . . . . . . . . . . .> ORDER BY DOMAIN DESC;
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
+-----------------+---------------------+---------------------+
|     domain      |  average_cpu_usage  |  average_db_usage   |
+-----------------+---------------------+---------------------+
| Salesforce.com  | 260.72727272727275  | 257.6363636363636   |
| Google.com      | 212.875             | 213.75              |
| Apple.com       | 114.11111111111111  | 119.55555555555556  |
+-----------------+---------------------+---------------------+
3 rows selected (3.192 seconds)
0: jdbc:hive2://localhost:10000> 
0: jdbc:hive2://localhost:10000> -- Sum, Min and Max CPU usage by Salesforce grouped by day
0: jdbc:hive2://localhost:10000> SELECT TO_DATE(`DATE`) DAY,
. . . . . . . . . . . . . . . .>   SUM(USAGE__CORE) TOTAL_CPU_Usage, MIN(USAGE__CORE) MIN_CPU_Usage, MAX(USAGE__CORE) MAX_CPU_Usage,
. . . . . . . . . . . . . . . .>   MIN(FILLER__INT2404) RANDOM_INT_MIN, MAX(FILLER__INT2404) RANDOM_INT_MAX,
. . . . . . . . . . . . . . . .>   MIN(FILLER__FLOAT0804) RANDOM_FLOAT_MIN, MAX(FILLER__FLOAT0804) RANDOM_FLOAT_MAX
. . . . . . . . . . . . . . . .> FROM WIDE_TABLE 
. . . . . . . . . . . . . . . .> WHERE DOMAIN LIKE 'Salesforce%' 
. . . . . . . . . . . . . . . .> GROUP BY TO_DATE(`DATE`);
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
+-------------+------------------+----------------+----------------+-----------------+-----------------+-------------------+-------------------+
|     day     | total_cpu_usage  | min_cpu_usage  | max_cpu_usage  | random_int_min  | random_int_max  | random_float_min  | random_float_max  |
+-------------+------------------+----------------+----------------+-----------------+-----------------+-------------------+-------------------+
| 2013-01-01  | 35               | 35             | 35             | 183             | 183             | 407.99854         | 407.99854         |
| 2013-01-02  | 150              | 25             | 125            | 170             | 447             | 200.22644         | 369.62488         |
| 2013-01-03  | 88               | 88             | 88             | 846             | 846             | 143.28621         | 143.28621         |
| 2013-01-04  | 26               | 3              | 23             | 457             | 791             | 37.24987          | 249.11401         |
| 2013-01-05  | 550              | 75             | 475            | 758             | 816             | 336.26785         | 389.8751          |
| 2013-01-06  | 12               | 12             | 12             | 71              | 71              | 346.00516         | 346.00516         |
| 2013-01-08  | 345              | 345            | 345            | 803             | 803             | 228.41173         | 228.41173         |
| 2013-01-09  | 390              | 35             | 355            | 142             | 913             | 25.955435         | 89.91394          |
| 2013-01-10  | 345              | 345            | 345            | 529             | 529             | 155.28105         | 155.28105         |
| 2013-01-11  | 335              | 335            | 335            | 870             | 870             | 197.71368         | 197.71368         |
| 2013-01-12  | 5                | 5              | 5              | 574             | 574             | 481.31473         | 481.31473         |
| 2013-01-13  | 355              | 355            | 355            | 437             | 437             | 416.02887         | 416.02887         |
| 2013-01-14  | 5                | 5              | 5              | 843             | 843             | 372.89236         | 372.89236         |
| 2013-01-15  | 720              | 65             | 655            | 781             | 804             | 266.4943          | 404.2953          |
| 2013-01-16  | 785              | 785            | 785            | 444             | 444             | 97.9207           | 97.9207           |
| 2013-01-17  | 1590             | 355            | 1235           | 49              | 745             | 80.901794         | 479.1267          |
+-------------+------------------+----------------+----------------+-----------------+-----------------+-------------------+-------------------+
16 rows selected (1.881 seconds)
0: jdbc:hive2://localhost:10000> 
0: jdbc:hive2://localhost:10000> -- list host and total active users when core CPU usage is 10X greater than DB usage
0: jdbc:hive2://localhost:10000> SELECT HOST, SUM(STATS__ACTIVE_VISITOR) TOTAL_ACTIVE_VISITORS,
. . . . . . . . . . . . . . . .>  SUM(FILLER__INT1404) RANDOM_INT_SUM, SUM(FILLER__FLOAT2804) RANDOM_FLOAT_SUM
. . . . . . . . . . . . . . . .> FROM WIDE_TABLE 
. . . . . . . . . . . . . . . .> WHERE USAGE__DB > (USAGE__CORE * 10) 
. . . . . . . . . . . . . . . .> GROUP BY HOST;
WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
+-------+------------------------+-----------------+---------------------+
| host  | total_active_visitors  | random_int_sum  |  random_float_sum   |
+-------+------------------------+-----------------+---------------------+
| EU    | 150                    | 135             | 167.39828491210938  |
| NA    | 1                      | 887             | 432.9884948730469   |
+-------+------------------------+-----------------+---------------------+
2 rows selected (1.858 seconds)
0: jdbc:hive2://localhost:10000> Closing: 0: jdbc:hive2://localhost:10000
#
```


## Presto
```
docker-compose exec presto-coordinator bash
$ wget https://repo1.maven.org/maven2/com/facebook/presto/presto-cli/0.183/presto-cli-0.183-executable.jar
$ mv presto-cli-0.183-executable.jar presto.jar
$ chmod +x presto.jar
$ ./presto.jar --server localhost:8080 --catalog hive --schema default
presto> SELECT * FROM stock_symbol;
```

### WEB STAT
```
# cat /root/meta_data/web_stat/WEB_STAT_QUERIES.sql | ./presto.jar --server localhost:8080 --catalog hive --schema default
presto:default> SELECT DOMAIN, AVG(USAGE__CORE) Average_CPU_Usage, AVG(USAGE__DB) Average_DB_Usage
             -> FROM WEB_STAT 
             -> GROUP BY DOMAIN 
             -> ORDER BY DOMAIN DESC;
ERROR: failed to open pager: Cannot run program "less": error=2, No such file or directory
     DOMAIN     | Average_CPU_Usage  |  Average_DB_Usage  
----------------+--------------------+--------------------
 Salesforce.com | 260.72727272727275 |  257.6363636363636 
 Google.com     |            212.875 |             213.75 
 Apple.com      | 114.11111111111111 | 119.55555555555556 
(3 rows)

Query 20190409_225405_00006_cm8dj, FINISHED, 1 node
Splits: 50 total, 50 done (100.00%)
0:01 [39 rows, 2.04KB] [36 rows/s, 1.89KB/s]

presto:default> 
presto:default> -- Sum, Min and Max CPU usage by Salesforce grouped by day
             -> SELECT DATE_FORMAT(DATE_TRUNC('DAY', DATE), '%Y-%d-%m') DAY,
             ->  SUM(USAGE__CORE) TOTAL_CPU_Usage, MIN(USAGE__CORE) MIN_CPU_Usage, MAX(USAGE__CORE) MAX_CPU_Usage
             -> FROM WEB_STAT 
             -> WHERE DOMAIN LIKE 'Salesforce%' 
             -> GROUP BY DATE_TRUNC('DAY', DATE);
ERROR: failed to open pager: Cannot run program "less": error=2, No such file or directory
    DAY     | TOTAL_CPU_Usage | MIN_CPU_Usage | MAX_CPU_Usage 
------------+-----------------+---------------+---------------
 2013-10-01 |             345 |           345 |           345 
 2013-02-01 |             150 |            25 |           125 
 2013-13-01 |             355 |           355 |           355 
 2013-16-01 |             785 |           785 |           785 
 2013-14-01 |               5 |             5 |             5 
 2013-09-01 |             390 |            35 |           355 
 2013-01-01 |              35 |            35 |            35 
 2013-06-01 |              12 |            12 |            12 
 2013-15-01 |             720 |            65 |           655 
 2013-05-01 |             550 |            75 |           475 
 2013-03-01 |              88 |            88 |            88 
 2013-17-01 |            1590 |           355 |          1235 
 2013-04-01 |              26 |             3 |            23 
 2013-08-01 |             345 |           345 |           345 
 2013-12-01 |               5 |             5 |             5 
 2013-11-01 |             335 |           335 |           335 
(16 rows)

Query 20190409_225408_00008_cm8dj, FINISHED, 1 node
Splits: 49 total, 49 done (100.00%)
0:01 [39 rows, 2.04KB] [52 rows/s, 2.74KB/s]

presto:default> 
presto:default> -- list host and total active users when core CPU usage is 10X greater than DB usage
             -> SELECT HOST, SUM(STATS__ACTIVE_VISITOR) TOTAL_ACTIVE_VISITORS
             -> FROM WEB_STAT 
             -> WHERE USAGE__DB > (USAGE__CORE * 10)
             -> GROUP BY HOST;
ERROR: failed to open pager: Cannot run program "less": error=2, No such file or directory
 HOST | TOTAL_ACTIVE_VISITORS 
------+-----------------------
 EU   |                   150 
 NA   |                     1 
(2 rows)

Query 20190409_225409_00009_cm8dj, FINISHED, 1 node
Splits: 49 total, 49 done (100.00%)
0:01 [39 rows, 2.04KB] [41 rows/s, 2.14KB/s]

presto:default> 
#
```

### WIDE TABLE
```
cat /root/meta_data/wide_table/WIDE_TABLE_QUERIES.sql | ./presto.jar --server localhost:8080 --catalog hive --schema default
presto:default> SELECT DOMAIN, AVG(USAGE__CORE) Average_CPU_Usage, AVG(USAGE__DB) Average_DB_Usage 
             -> FROM WIDE_TABLE 
             -> GROUP BY DOMAIN 
             -> ORDER BY DOMAIN DESC;
ERROR: failed to open pager: Cannot run program "less": error=2, No such file or directory
     DOMAIN     | Average_CPU_Usage  |  Average_DB_Usage  
----------------+--------------------+--------------------
 Salesforce.com | 260.72727272727275 |  257.6363636363636 
 Google.com     |            212.875 |             213.75 
 Apple.com      | 114.11111111111111 | 119.55555555555556 
(3 rows)

Query 20190409_225155_00001_cm8dj, FINISHED, 1 node
Splits: 50 total, 50 done (100.00%)
0:06 [39 rows, 4.39MB] [6 rows/s, 781KB/s]

presto:default> 
presto:default> -- Sum, Min and Max CPU usage by Salesforce grouped by day
             -> SELECT DATE_FORMAT(DATE_TRUNC('DAY', DATE), '%Y-%d-%m') DAY,
             ->   SUM(USAGE__CORE) TOTAL_CPU_Usage, MIN(USAGE__CORE) MIN_CPU_Usage, MAX(USAGE__CORE) MAX_CPU_Usage,
             ->   MIN(FILLER__INT2404) RANDOM_INT_MIN, MAX(FILLER__INT2404) RANDOM_INT_MAX,
             ->   MIN(FILLER__FLOAT0804) RANDOM_FLOAT_MIN, MAX(FILLER__FLOAT0804) RANDOM_FLOAT_MAX
             -> FROM WIDE_TABLE 
             -> WHERE DOMAIN LIKE 'Salesforce%' 
             -> GROUP BY DATE_TRUNC('DAY', DATE);
ERROR: failed to open pager: Cannot run program "less": error=2, No such file or directory
    DAY     | TOTAL_CPU_Usage | MIN_CPU_Usage | MAX_CPU_Usage | RANDOM_INT_MIN | RANDOM_INT_MAX | RANDOM_FLOAT_MIN | RANDOM_FLOAT_MAX 
------------+-----------------+---------------+---------------+----------------+----------------+------------------+------------------
 2013-09-01 |             390 |            35 |           355 |            142 |            913 |        25.955435 |         89.91394 
 2013-02-01 |             150 |            25 |           125 |            170 |            447 |        200.22644 |        369.62488 
 2013-13-01 |             355 |           355 |           355 |            437 |            437 |        416.02887 |        416.02887 
 2013-16-01 |             785 |           785 |           785 |            444 |            444 |          97.9207 |          97.9207 
 2013-11-01 |             335 |           335 |           335 |            870 |            870 |        197.71368 |        197.71368 
 2013-08-01 |             345 |           345 |           345 |            803 |            803 |        228.41173 |        228.41173 
 2013-01-01 |              35 |            35 |            35 |            183 |            183 |        407.99854 |        407.99854 
 2013-06-01 |              12 |            12 |            12 |             71 |             71 |        346.00516 |        346.00516 
 2013-15-01 |             720 |            65 |           655 |            781 |            804 |         266.4943 |         404.2953 
 2013-03-01 |              88 |            88 |            88 |            846 |            846 |        143.28621 |        143.28621 
 2013-17-01 |            1590 |           355 |          1235 |             49 |            745 |        80.901794 |         479.1267 
 2013-10-01 |             345 |           345 |           345 |            529 |            529 |        155.28105 |        155.28105 
 2013-14-01 |               5 |             5 |             5 |            843 |            843 |        372.89236 |        372.89236 
 2013-04-01 |              26 |             3 |            23 |            457 |            791 |         37.24987 |        249.11401 
 2013-05-01 |             550 |            75 |           475 |            758 |            816 |        336.26785 |         389.8751 
 2013-12-01 |               5 |             5 |             5 |            574 |            574 |        481.31473 |        481.31473 
(16 rows)

Query 20190409_225202_00003_cm8dj, FINISHED, 1 node
Splits: 49 total, 49 done (100.00%)
0:02 [39 rows, 4.39MB] [18 rows/s, 2.08MB/s]

presto:default> 
presto:default> -- list host and total active users when core CPU usage is 10X greater than DB usage
             -> SELECT HOST, SUM(STATS__ACTIVE_VISITOR) TOTAL_ACTIVE_VISITORS,
             ->  SUM(FILLER__INT1404) RANDOM_INT_SUM, SUM(FILLER__FLOAT2804) RANDOM_FLOAT_SUM
             -> FROM WIDE_TABLE 
             -> WHERE USAGE__DB > (USAGE__CORE * 10) 
             -> GROUP BY HOST;
ERROR: failed to open pager: Cannot run program "less": error=2, No such file or directory
 HOST | TOTAL_ACTIVE_VISITORS | RANDOM_INT_SUM | RANDOM_FLOAT_SUM 
------+-----------------------+----------------+------------------
 NA   |                     1 |            887 |         432.9885 
 EU   |                   150 |            135 |        167.39828 
(2 rows)

Query 20190409_225204_00004_cm8dj, FINISHED, 1 node
Splits: 49 total, 49 done (100.00%)
0:02 [39 rows, 4.39MB] [21 rows/s, 2.47MB/s]

presto:default> 
#
```

## Cleanup
```

# cleanup
docker-compose stop
docker-compose rm -fv
```

