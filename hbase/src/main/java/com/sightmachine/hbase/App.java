package com.sightmachine.hbase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.hadoop.hbase.client.Table;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command
public class App implements Runnable {

    private static final Logger logger = LogManager.getLogger(App.class);

    @Command(name = "transfer")
    public void transferFromPGSQLToHbase() throws Exception {
        logger.info("Transferring data from Postgres to HBase");
        try (Table table = Utils.prepareTable()) {
            Utils.fillFromDB(table);
        }
    }

    @Command(name = "postgres")
    public void outputFromPGSQL() throws Exception {
        logger.info("Outputting data from Postgres");
        Utils.outputFromPostgres();
    }

    @Command(name = "hbase")
    public void outputFromHBase() throws Exception {
        logger.info("Outputting data from HBase");
        try (Table table = Utils.prepareTable()) {
            Utils.outputFromHBase(table);
        }
    }

    public static void main(String[] args) throws Exception {
        CommandLine.run(new App(), args);
    }

    @Override
    public void run() {
        CommandLine.usage(new App(), System.out);
    }
}
