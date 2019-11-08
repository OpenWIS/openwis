package org.openwis.factorytests;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;

public class DatabaseExport {
   public static void main(String[] args) throws Exception {
      // database connection
      Class.forName("org.postgresql.Driver");
      Connection jdbcConnection = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/OpenWIS", "postgres", "postgres");
      IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

      // partial database export
      //      QueryDataSet partialDataSet = new QueryDataSet(connection);
      //      partialDataSet.addTable("FOO", "SELECT * FROM TABLE WHERE COL='VALUE'");
      //      partialDataSet.addTable("BAR");
      //      FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));

      // full database export
      //IDataSet fullDataSet = connection.createDataSet();
      ITableFilter filter = new DatabaseSequenceFilter(connection);
      IDataSet fullDataSet = new FilteredDataSet(filter, connection.createDataSet());
      FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));

      // dependent tables database export: export table X and all tables that
      // have a PK which is a FK on X, in the right order for insertion
//            String[] depTableNames = TablesDependencyHelper.getAllDependentTables(connection, "operationallowed");
//            IDataSet depDataset = connection.createDataSet(depTableNames);
//            FlatXmlDataSet.write(depDataset, new FileOutputStream("dependents.xml"));
   }

}
