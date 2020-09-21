package com.sightmachine.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.util.Bytes;

public class Constants {

  public static final int _id   = 1;
  public static final int shift   = 2;
  public static final int shiftid   = 3;
  public static final int machine__source   = 4;
  public static final int machine__source_type   = 5;
  public static final int machine__factory_location   = 6;
  public static final int machine__factory_partner   = 7;
  public static final int capturetime   = 8;
  public static final int starttime   = 9;
  public static final int starttime_local  = 10;
  public static final int starttime_epoch  = 11;
  public static final int endtime  = 12;
  public static final int endtime_local  = 13;
  public static final int endtime_epoch  = 14;
  public static final int total  = 15;
  public static final int idealcycle  = 16;
  public static final int shift_date  = 17;
  public static final int production_date  = 18;
  public static final int record_time  = 19;
  public static final int output  = 20;
  public static final int cycleindex  = 21;
  public static final int timezone  = 22;
  public static final int NG  = 23;
  public static final int stats__PART_NAME__val  = 24;

  public static TableName ANALYTICS_LACQUER_TABLE = TableName.valueOf("analytics_lacquer");
  public static byte[] DEFAULT_COLUMN_FAMILY = Bytes.toBytes("cf");

  public static final String startProdDate = "2020-07-01";
  public static final String endProdDate = "2020-07-15";

  public static final String pg_url = "jdbc:postgresql://hbpostgres:5432/postgres";
  public static final String pg_user = "postgres";
  public static final String pg_password = "postgres";
}
