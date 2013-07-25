package org.openwis.metadataportal.search.solr.config;

/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class
 * from the iBATIS Apache project. Only removed dependency on Resource class
 * and a constructor
 */
/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tool to run database scripts.
 */
public class ScriptRunner {
   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ScriptRunner.class);

   /** The Constant DEFAULT_DELIMITER. */
   private static final String DEFAULT_DELIMITER = ";";

   /** The connection. */
   private final Connection connection;

   /** The stop on error. */
   private final boolean stopOnError;

   /** The auto commit. */
   private final boolean autoCommit;

   /** The delimiter. */
   private String delimiter = DEFAULT_DELIMITER;

   /** The full line delimiter. */
   private boolean fullLineDelimiter = false;

   /**
    * Default constructor.
    *
    * @param connection the connection
    * @param autoCommit the auto commit
    * @param stopOnError the stop on error
    */
   public ScriptRunner(Connection connection, boolean autoCommit, boolean stopOnError) {
      this.connection = connection;
      this.autoCommit = autoCommit;
      this.stopOnError = stopOnError;
   }

   /**
    * Sets the delimiter.
    *
    * @param delimiter the delimiter
    * @param fullLineDelimiter the full line delimiter
    */
   public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
      this.delimiter = delimiter;
      this.fullLineDelimiter = fullLineDelimiter;
   }

   /**
    * Runs an SQL script (read in using the Reader parameter).
    *
    * @param reader - the source of the script
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws SQLException the sQL exception
    */
   public void runScript(Reader reader) throws IOException, SQLException {
      try {
         boolean originalAutoCommit = connection.getAutoCommit();
         try {
            if (originalAutoCommit != autoCommit) {
               connection.setAutoCommit(autoCommit);
            }
            runScript(connection, reader);
         } finally {
            connection.setAutoCommit(originalAutoCommit);
         }
      } catch (IOException e) {
         throw e;
      } catch (SQLException e) {
         throw e;
      } catch (Exception e) {
         throw new RuntimeException("Error running script.  Cause: " + e, e);
      }
   }

   /**
    * Runs an SQL script (read in using the Reader parameter) using the
    * connection passed in.
    *
    * @param conn - the connection to use for the script
    * @param reader - the source of the script
    * @throws IOException if there is an error reading from the Reader
    * @throws SQLException if any SQL errors occur
    */
   private void runScript(Connection conn, Reader reader) throws IOException, SQLException {
      StringBuilder info = new StringBuilder();
      StringBuffer command = null;
      try {
         LineNumberReader lineReader = new LineNumberReader(reader);
         String line = null;
         while ((line = lineReader.readLine()) != null) {
            if (command == null) {
               command = new StringBuffer();
            }
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("--")) {
               info.append(trimmedLine);
               info.append('\n');
            } else if (trimmedLine.length() < 1 || trimmedLine.startsWith("//")) {
               // Do nothing
            } else if (trimmedLine.length() < 1 || trimmedLine.startsWith("--")) {
               // Do nothing
            } else if (!fullLineDelimiter && trimmedLine.endsWith(getDelimiter())
                  || fullLineDelimiter && trimmedLine.equals(getDelimiter())) {
               command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
               command.append(" ");
               Statement statement = conn.createStatement();

               info.append(command);
               info.append('\n');

               boolean hasResults = false;
               if (stopOnError) {
                  hasResults = statement.execute(command.toString());
               } else {
                  try {
                     statement.execute(command.toString());
                  } catch (SQLException e) {
                     logger.error("Error executing: " + command, e);
                  }
               }

               if (autoCommit && !conn.getAutoCommit()) {
                  conn.commit();
               }

               ResultSet rs = statement.getResultSet();
               if (hasResults && rs != null) {
                  ResultSetMetaData md = rs.getMetaData();
                  int cols = md.getColumnCount();
                  for (int i = 1; i < (cols + 1); i++) {
                     String name = md.getColumnLabel(i);
                     info.append(name + "\t");
                  }
                  info.append('\n');
                  while (rs.next()) {
                     for (int i = 1; i < (cols + 1); i++) {
                        String value = rs.getString(i);
                        info.append(value + "\t");
                     }
                     info.append('\n');
                  }
               }

               command = null;
               try {
                  statement.close();
               } catch (Exception e) {
                  // Ignore to workaround a bug in Jakarta DBCP
               }
               Thread.yield();
            } else {
               command.append(line);
               command.append(" ");
            }
         }
         if (!autoCommit) {
            conn.commit();
         }
      } catch (SQLException e) {
         logger.error("Error executing: " + command, e);
         throw e;
      } catch (IOException e) {
         logger.error("Error executing: " + command, e);
         throw e;
      } finally {
         logger.info(info.toString());
         conn.rollback();
      }
   }

   /**
    * Gets the delimiter.
    *
    * @return the delimiter
    */
   private String getDelimiter() {
      return delimiter;
   }
}