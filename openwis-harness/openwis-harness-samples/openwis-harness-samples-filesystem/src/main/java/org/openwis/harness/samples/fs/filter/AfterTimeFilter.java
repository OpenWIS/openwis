/**
 * 
 */
package org.openwis.harness.samples.fs.filter;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.regex.Matcher;

import org.openwis.harness.samples.fs.localdatasource.FsExtractionRunnable;

/**
 * The Class BeforeFieldFilter. <P>
 * Explanation goes here. <P>
 */
public class AfterTimeFilter extends AfterFieldFilter {

   /**
    * Instantiates a new before field filter.
    *
    * @param timestamp the timestamp
    */
   public AfterTimeFilter(Calendar timestamp) {
      super(timestamp, -1);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("hh:mm > {0}", getField());
   }

   /**
    * Gets the field.
    * @return the field.
    */
   @Override
   protected int getField() {
      return 60 * getTimestamp().get(Calendar.HOUR) + getTimestamp().get(Calendar.MINUTE);
   }

   /**
    * Gets the file value.
    *
    * @param file the file
    * @return the file value
    */
   @Override
   protected int getFileValue(File file) {
      Matcher matcher = FsExtractionRunnable.TIME_PATTERN.matcher(file.getName());
      if (!matcher.matches()) {
         throw new NumberFormatException(MessageFormat.format(
               "File name {0} does not match pattern {1}", file.getName(), matcher.pattern()));
      }
      // Retrieve value
      int h = Integer.valueOf(matcher.group(1));
      int m = Integer.valueOf(matcher.group(2));

      return 60 * h + m;
   }
}
