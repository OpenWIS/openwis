/**
 * 
 */
package org.openwis.harness.samples.fs.filter;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BeforeFieldFilter. <P>
 * Explanation goes here. <P>
 */
public class AfterFieldFilter implements FsFileFilter {

   /** The Constant INVALID_FILE_FOR_A_LOCAL_DATASOURCE. */
   private static final String INVALID_FILE_FOR_A_LOCAL_DATASOURCE = "Invalid file for a LocalDatasource: ";

   /**  The logger. */
   private static Logger logger = LoggerFactory.getLogger(AfterFieldFilter.class);

   /** The field. */
   private final int field;

   /** The timestamp. */
   private final Calendar timestamp;

   /** The calendar field. */
   private final int calendarField;

   /**
    * Instantiates a new before field filter.
    *
    * @param timestamp the timestamp
    * @param calendarField the calendar field
    */
   public AfterFieldFilter(Calendar timestamp, int calendarField) {
      super();
      this.timestamp = timestamp;
      this.calendarField = calendarField;
      field = getField();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      String fieldName;
      switch (calendarField) {
      case Calendar.YEAR:
         fieldName = "year";
         break;
      case Calendar.MONTH:
         fieldName = "month";
         break;
      case Calendar.DAY_OF_MONTH:
         fieldName = "day";
         break;
      default:
         fieldName = "";
         break;
      }
      return MessageFormat.format("{0} > {1}", fieldName, field);
   }

   /**
    * Gets the field.
    * @return the field.
    */
   protected int getField() {
      return timestamp.get(calendarField);
   }

   /**
    * Accept.
    *
    * @param file the file
    * @return true, if successful
    * {@inheritDoc}
    * @see java.io.FileFilter#accept(java.io.File)
    */
   @Override
   public boolean accept(File file) {
      assert file != null;
      // Accept file if the FS contains a product after the timestamp
      return file.isDirectory() && checkField(file);
   }

   /**
    * Accept.
    *
    * @param file the file
    * @return true, if successful
    * {@inheritDoc}
    * @see java.io.FileFilter#accept(java.io.File)
    */
   @Override
   public boolean acceptStrict(File file) {
      assert file != null;
      // Accept file if the FS contains a product after the timestamp
      return file.isDirectory() && checkStrictField(file);
   }

   /**
    * Check field.
    *
    * @param file the file
    * @return true, if successful
    */
   private boolean checkField(File file) {
      boolean result = false;
      try {
         int y = getFileValue(file);
         result = file.isDirectory() && (y >= field);
      } catch (NumberFormatException nfe) {
         logger.warn(INVALID_FILE_FOR_A_LOCAL_DATASOURCE + file, nfe);
      }
      return result;
   }

   /**
    * Check field.
    *
    * @param file the file
    * @return true, if successful
    */
   private boolean checkStrictField(File file) {
      boolean result = false;
      try {
         int y = getFileValue(file);
         result = file.isDirectory() && (y > field);
      } catch (NumberFormatException nfe) {
         logger.warn(INVALID_FILE_FOR_A_LOCAL_DATASOURCE + file, nfe);
      }
      return result;
   }

   /**
    * Gets the file value.
    *
    * @param file the file
    * @return the file value
    */
   protected int getFileValue(File file) {
      return Integer.valueOf(file.getName());
   }

   /**
    * Gets the timestamp.
    * @return the timestamp.
    */
   protected Calendar getTimestamp() {
      return timestamp;
   }
}