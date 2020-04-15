package org.openwis.usermanagement;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.SecurityServiceAlerts;
import org.openwis.usermanagement.util.JNDIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogWatcherTask {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(LogWatcherTask.class);

   /** Date/time format used in OpenAM logs */
   private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

   /**
    * Column separator
    */
   private static final String COLUMN_SEPARATOR = ";";

   /**
    * Keyword for failed login
    */
   private static final String FAILED_LOGIN = "FAILED";

   /**
    * Field position of what we are looking for: date of login and the username
    */
   private static final Integer[] FIELD_POSITIONS = new Integer[]{1, 7};

   /**
    * Method called by the scheduler.
    */
   public void timeout() {
      // Check OpenAM Logs
      logger.debug("Check OpenAM logs");

      // Get the amAuthentication.error file
      File amAuthenticationErrorFile = new File(JNDIUtils.getInstance().getLogTimerFile());

      File lastDateFile = new File(amAuthenticationErrorFile.getAbsoluteFile().getParent(),
            "openWisLogDate.txt");

      try {
         Calendar newLastDate = null;
         Calendar lastDate = null;
         if (lastDateFile.exists()) {
            lastDate = getLastDate(lastDateFile);
         }
         if (lastDate != null && logger.isDebugEnabled()) {
            logger.debug("Checking logs from date {}", DATE_TIME_FORMAT.format(lastDate.getTime()));
         }

         FileInputStream fstream = new FileInputStream(amAuthenticationErrorFile);
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String strLine;

         ParseLogEntry parseLogEntry = new ParseLogEntry(COLUMN_SEPARATOR, FIELD_POSITIONS);

         //Read File Line By Line
         while ((strLine = br.readLine()) != null) {
            if (!strLine.contains(FAILED_LOGIN)) {
               continue;
            }

            logger.debug("Line contains {}: {}",FAILED_LOGIN, strLine);

            List<String> parsedFields = parseLogEntry.parse(strLine);
            if (parsedFields.size() == 0) {
               continue;
            }
            String dateToUse = parsedFields.get(0);
            logger.debug("New date : {}", dateToUse);
            try {
               newLastDate = Calendar.getInstance();
               newLastDate.setTime(DATE_TIME_FORMAT.parse(dateToUse));
            } catch (ParseException e) {
               // can't parse date, skip line
               logger.debug("can't parse date, skip line");
              continue;
            }

            if (lastDate == null || !newLastDate.after(lastDate)) {
               logger.debug("-> date ignored");
               continue;
            }

            String loginItem = parsedFields.get(1);
            logger.debug("loginItem: {}", loginItem);
            String login = loginItem.split("\t")[0];
            logger.debug("Extracted login: {}", login);
            sendAlarm(login);
         }
         //Close the input stream
         in.close();

         setActualDate(lastDateFile, newLastDate);
      } catch (FileNotFoundException e) {//Catch exception if any
         logger.warn("Error during LogTimerService: " + e);
      } catch (Exception e) {//Catch exception if any
         logger.error("Error during LogTimerService: ", e);
      }
   }
   
   private Calendar getLastDate(File lastDateFile) throws IOException, ParseException {
      FileInputStream fstream = new FileInputStream(lastDateFile);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      Calendar c;
      try {
         String result = br.readLine();
         c = Calendar.getInstance();
         c.setTime(DATE_TIME_FORMAT.parse(result));
      } catch (ParseException e) {
         // can't parse date, skip line
         c = null;
      }
      in.close();
      return c;
   }

   private void setActualDate(File lastDateFile, Calendar date) throws IOException {
      if (date != null) {
         FileWriter fw = new FileWriter(lastDateFile);
         fw.write(DATE_TIME_FORMAT.format(date.getTime()));
         fw.close();
      }
   }

   /**
    * Send authentication failed for operator/administrator alarm.
    * 
    * @param user user login
    */
   private void sendAlarm(String user) {
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null) {
         logger.error("Could not get hold of the AlertService. No alert was passed!");
      } else {
         String source = "Security Service";
         String location = "Log Timer Service";

         String eventId = SecurityServiceAlerts.AUTHENTICATION_FAILED.getKey();

         List<Object> arguments = new ArrayList<Object>();
         arguments.add(user);

         alertService.raiseEvent(source, location, null, eventId, arguments);
      }
   }

   private class ParseLogEntry {

      /*
      Column seperator
       */
      private final String columnSeparator;

      /**
       * Position of captured fields
       */
      private final Integer[] positions;

      private final String[] stripChar = new String[]{"\"", "[", "]"};

      private ParseLogEntry(String columnSeparator, Integer[] positions) {
         this.columnSeparator = columnSeparator;
         this.positions = positions;
      }

      private List<String> parse(String line) {
         List<String> capturedFields = new ArrayList<>();
         String[] fields = line.split(this.columnSeparator);
         for (Integer pos : this.positions) {
            if (pos < fields.length) {
               capturedFields.add(sanitize(fields[pos]));
            } else {
               logger.warn("Cannot capture field at position {}. Position is greater than number of columns", pos);
            }
         }
         return capturedFields;
      }

      /**
       * Strip any unwanted char from string
       * @param field string to sanitize
       * @return sanitized string
       */
      private String sanitize(String field) {
         String sanitizedString = field;

         for (String c: this.stripChar) {
            if (sanitizedString.contains(c)) {
               sanitizedString = sanitizedString.replace(c, "");
            }
         }
         return sanitizedString;
      }
   }
}
