SELECT DOMAIN, AVG("USAGE.CORE") Average_CPU_Usage, AVG("USAGE.DB") Average_DB_Usage
FROM WEB_STAT 
GROUP BY DOMAIN 
ORDER BY DOMAIN DESC;

-- Sum, Min and Max CPU usage by Salesforce grouped by day
SELECT formatDateTime(DATE, '%Y-%m-%d') DAY, SUM("USAGE.CORE") TOTAL_CPU_Usage, MIN("USAGE.CORE") MIN_CPU_Usage, MAX("USAGE.CORE") MAX_CPU_Usage
FROM WEB_STAT 
WHERE DOMAIN LIKE 'Salesforce%' 
GROUP BY formatDateTime(DATE, '%Y-%m-%d');

-- list host and total active users when core CPU usage is 10X greater than DB usage
SELECT HOST, SUM("STATS.ACTIVE_VISITOR") TOTAL_ACTIVE_VISITORS
FROM WEB_STAT 
WHERE "USAGE.DB" > ("USAGE.CORE" * 10)
GROUP BY HOST;
