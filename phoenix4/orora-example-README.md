### Testing Orora Datavis ###
1. Extract the data and clean it up. (We reorder the `_id` to the end to the file because the index we 
create ends with `_id` and that requires the correct order.) 

```
# Extract schema
pg_dump -h 127.0.0.1 -U postgres -p 5433 --schema-only -d tenant_storage -t analytics_lacquer > /opt/sightmachine/dbsurvey/data/orora-roc27.sql

# Extract the rows of data and reorder that id. 
\copy (SELECT * FROM analytics_lacquer WHERE analytics_lacquer.shift_date >= '2019-11-10' AND analytics_lacquer.shift_date < '2019-11-11') TO '/opt/sightmachine/dbsurvey/data/orora_lacquer_one_day.csv' WITH CSV;
python phoenix4/reorder.py --src=/opt/sightmachine/_backup/phoenix/orora_lacquer_one_day.csv --dest=/opt/sightmachine/_backup/phoenix/orora_lacquer_one_day_reordered.csv
```

2. Load the data into hbase
```
docker exec -it regionserver-1 bash
psql.py zookeeper-1.vnet /root/data/local/orora-roc27.sql
# Table is case sensitive
psql.py zookeeper-1.vnet /root/data/local/orora_lacquer_example.csv -t PUBLIC.ANALYTICS_LACQUER
```


### Guidelines for porting the data over ###
* Postgres data needs to be altered after pulling in from postgres. 
* While running psql to pull data from the source, you could easily run into an error upserting the record. Use `-s` to see why the parsing failed. 