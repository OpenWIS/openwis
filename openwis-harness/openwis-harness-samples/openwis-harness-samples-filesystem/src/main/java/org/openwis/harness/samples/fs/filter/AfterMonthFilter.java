/**
 * 
 */
package org.openwis.harness.samples.fs.filter;

import java.util.Calendar;

/**
 * Explanation goes here. <P>
 */
public class AfterMonthFilter extends AfterFieldFilter {

   /**
    * Instantiates a new before field filter.
    *
    * @param timestamp the timestamp
    */
   public AfterMonthFilter(Calendar timestamp) {
      super(timestamp, Calendar.MONTH);
   }

   /**
    * Gets the field.
    * @return the field.
    */
   @Override
   protected int getField() {
      // XXX Calendar month start at 0 for January
      return super.getField() + 1;
   }

}
