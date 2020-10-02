package com.sightmachine.hbase;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils {

  private static final Logger logger = LogManager.getLogger(Utils.class);

  public static Put constructPut(ResultSet rs, Map<Integer, byte[]> columnsMap, int columnCount)
      throws SQLException {

    byte[] rowKey =
        Bytes.add(
            rs.getBytes(Constants.machine__source),
            rs.getBytes(Constants.production_date),
            rs.getBytes(Constants.endtime_epoch));

    Put put = new Put(rowKey);

    for (int i = 24; i <= columnCount; i++) {
      put.addColumn(Constants.DEFAULT_COLUMN_FAMILY, columnsMap.get(i), rs.getBytes(i));
    }

    return put;
  }

  /**
   * Outputs from given HBase table into System.out. Uses an HBase representation of the same
   * SQL query used in outputFromPostgres.
   *
   * @param table Fully instantiated, readable HBase Table object
   * @throws Exception
   */
  public static void outputFromHBase(Table table) throws Exception {

    String[] machineSources = {
      "ORO_ROCK_010_Lacquer_1",
      "ORO_ROCK_010_Lacquer_2",
      "ORO_ROCK_010_Lacquer_3",
      "ORO_ROCK_010_Lacquer_4",
      "ORO_ROCK_010_Lacquer_5",
      "ORO_ROCK_010_Lacquer_6",
      "ORO_ROCK_010_Lacquer_7",
      "ORO_ROCK_010_Lacquer_8",
      "ORO_ROCK_020_Lacquer_2",
      "ORO_ROCK_020_Lacquer_3",
      "ORO_ROCK_020_Lacquer_4",
      "ORO_ROCK_020_Lacquer_5",
      "ORO_ROCK_020_Lacquer_6",
      "ORO_ROCK_020_Lacquer_7"
    };

    byte[][] columns = {
      Bytes.toBytes("stats__COUT__val"),
      Bytes.toBytes("stats__LAC_IC_BOT_AVG__val"),
      Bytes.toBytes("stats__LAC_IC_MID1_AVG__val"),
      Bytes.toBytes("stats__LAC_IC_MID2_AVG__val"),
      Bytes.toBytes("stats__LAC_IC_TOP_AVG__val")
    };

    byte[] zeroPadding = new byte[128];
    Arrays.fill(zeroPadding, (byte) 0);

    Arrays.sort(machineSources);

    Scan scan = new Scan();

    FilterList list = new FilterList(Operator.MUST_PASS_ONE);

    for (String machineSource : machineSources) {
      byte[] startRow =
          Bytes.add(Bytes.toBytes(machineSource), Bytes.toBytes(Constants.startProdDate));
      byte[] stopRow =
          Bytes.add(Bytes.toBytes(machineSource), Bytes.toBytes(Constants.endProdDate));

      FilterList shortList = new FilterList(Operator.MUST_PASS_ALL);
      shortList.addFilter(
          new RowFilter(CompareOp.GREATER_OR_EQUAL, new BinaryPrefixComparator(startRow)));
      shortList.addFilter(
          new RowFilter(CompareOp.LESS_OR_EQUAL, new BinaryPrefixComparator(stopRow)));

      list.addFilter(shortList);
    }

    scan.setFilter(list);

    for (byte[] column : columns) {
      scan = scan.addColumn(Constants.DEFAULT_COLUMN_FAMILY, column);
    }

    final int scanFetch = 1000;
    scan = scan.setCaching(scanFetch); // .setCacheBlocks(false);

    StringBuilder sb = new StringBuilder();

    ResultScanner scanner = table.getScanner(scan);

    Iterator<Result> resultIterator = scanner.iterator();
    while (resultIterator.hasNext()) {
      Result result = resultIterator.next();
      byte[] rowKey = result.getRow();
      sb.setLength(0);
      sb.append(Bytes.toString(rowKey).substring(0, 32));
      sb.append(" | ");

      long epoch = Bytes.toLong(rowKey, rowKey.length - 8, 8);
      sb.append(epoch);

      for (byte[] qualifier : columns) {
        byte[] val = result.getValue(Constants.DEFAULT_COLUMN_FAMILY, qualifier);
        sb.append('\t');
        sb.append(Bytes.toString(val).trim());
      }
      System.out.println(sb.toString());
    }

    scanner.close();
  }

  /**
   * Outputs a hard-coded query from Postgres to System.out
   *
   * @throws Exception
   */
  public static void outputFromPostgres() throws Exception {

    try (java.sql.Connection conn = DriverManager.getConnection(
        Constants.pg_url,
        Constants.pg_user,
        Constants.pg_password))
    {
      Statement st = conn.createStatement();
      // Turn use of the cursor on.
      st.setFetchSize(1000);

      String originalQuery =
          "SELECT analytics_lacquer.machine__source AS x_1,\n"
              + " analytics_lacquer.production_date AS x_2,\n"
              + " analytics_lacquer.endtime_epoch AS x_3,\n"
              + " analytics_lacquer.\"stats__COUT__val\" AS y_0,\n"
              + " analytics_lacquer.\"stats__LAC_IC_BOT_AVG__val\" AS y_1,\n"
              + " analytics_lacquer.\"stats__LAC_IC_MID1_AVG__val\" AS y_2,\n"
              + " analytics_lacquer.\"stats__LAC_IC_MID2_AVG__val\" AS y_3,\n"
              + " analytics_lacquer.\"stats__LAC_IC_TOP_AVG__val\" AS y_4\n"
              + "FROM analytics_lacquer\n"
              + "WHERE analytics_lacquer.production_date >= '2020-07-01' AND analytics_lacquer.production_date <= '2020-07-15'\n"
              + " AND analytics_lacquer.machine__source IN ('ORO_ROCK_010_Lacquer_1', 'ORO_ROCK_010_Lacquer_2', 'ORO_ROCK_010_Lacquer_3', 'ORO_ROCK_010_Lacquer_4', 'ORO_ROCK_010_Lacquer_5', 'ORO_ROCK_010_Lacquer_6', 'ORO_ROCK_010_Lacquer_7', 'ORO_ROCK_010_Lacquer_8', 'ORO_ROCK_020_Lacquer_2', 'ORO_ROCK_020_Lacquer_3', 'ORO_ROCK_020_Lacquer_4', 'ORO_ROCK_020_Lacquer_5', 'ORO_ROCK_020_Lacquer_6', 'ORO_ROCK_020_Lacquer_7')\n"
              + " AND analytics_lacquer.machine__source_type IN ('Lacquer');\n";

      ResultSet rs = st.executeQuery(originalQuery);
      StringBuilder sb = new StringBuilder();
      while (rs.next()) {
        sb.setLength(0);
        sb.append(rs.getString(1));
        sb.append(rs.getString(2));
        sb.append(" | ");
        sb.append(rs.getLong(3));
        for (int i = 4; i < 9; i++) {
          sb.append('\t');
          sb.append(rs.getString(i));
        }
        System.out.println(sb.toString());
      }
    }
  }

  public static void fillFromDB(Table table) throws Exception {
    String url = "jdbc:postgresql://hbpostgres:5432/postgres";
    String user = "postgres";
    String password = "postgres";

    try (java.sql.Connection conn = DriverManager.getConnection(url, user, password)) {
      Statement st = conn.createStatement();
      // Turn use of the cursor on.
      st.setFetchSize(100);

      String originalQuery = "SELECT * FROM analytics_lacquer LIMIT 10000 OFFSET ";

      long rowOffset = 0;
      long offsetStep = 10000;

      Map<Integer, byte[]> columnsMap = Maps.newHashMap();
      int columnCount = 0;

      while (rowOffset + offsetStep < 3600000) {
        ResultSet rs = st.executeQuery(originalQuery + rowOffset);
        rowOffset += offsetStep;
        System.out.print(".");
        List<Put> allPuts = Lists.newArrayList();
        if (columnsMap.isEmpty()) {
          ResultSetMetaData metaData = rs.getMetaData();
          columnCount = metaData.getColumnCount();
          for (int i = 24; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            columnsMap.put(i, Bytes.toBytes(columnName));
          }
        }

        while (rs.next()) {
          allPuts.add(constructPut(rs, columnsMap, columnCount));
        }

        try {
          table.put(allPuts);
        } catch (Exception e) {
          System.err.println("While putting: " + e.toString());
        }
      }

      System.out.println();

    } catch (SQLException ex) {
      logger.error(ex);
    }
  }

  public static Table prepareTable() throws Exception {
    Configuration config = HBaseConfiguration.create();

    App app = new App();

    String path = app.getClass().getClassLoader().getResource("hbase-site.xml").getPath();
    config.addResource(new Path(path));

    HBaseAdmin.checkHBaseAvailable(config);

    Connection connection = ConnectionFactory.createConnection(config);
    HTableDescriptor tableDescriptor = new HTableDescriptor(Constants.ANALYTICS_LACQUER_TABLE);
    HColumnDescriptor dataColfamDescriptor = new HColumnDescriptor(Constants.DEFAULT_COLUMN_FAMILY);
    dataColfamDescriptor.setScope(HConstants.REPLICATION_SCOPE_LOCAL);
    dataColfamDescriptor.setCompressionType(Algorithm.SNAPPY);
    tableDescriptor.addFamily(dataColfamDescriptor);

    boolean found = false;
    for (TableName tname : connection.getAdmin().listTableNames()) {
      logger.error("Table name " + tname.getNameAsString());
      if (tname.getNameAsString().equals(Constants.ANALYTICS_LACQUER_TABLE.getNameAsString())) {
        found = true;
      }
    }
    if (!found) {
      connection.getAdmin().createTable(tableDescriptor);
    }

    // Retrieve the table we just created so we can do some reads and writes
    return connection.getTable(Constants.ANALYTICS_LACQUER_TABLE);
  }
}
