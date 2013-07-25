/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jdom.Element;
import org.openwis.management.monitoring.SortDirection;

import jeeves.exceptions.BadInputEx;
import jeeves.interfaces.Service;
import jeeves.utils.Log;
import jeeves.utils.Util;


/**
 * Common abstract base class used for report services.
 */
public abstract class FilterReports implements Service {
   
   protected static final String PARAM_FILTER_PERIOD = "period";
   private static final String PARAM_SORT_COLUMN = "sort";
   private static final String PARAM_SORT_DIR = "dir";   
   private static final String PARAM_EXPORT = "xml";   
   
   protected static final String LOG_MODULE = "openwis.service.management";
   
   protected static final int MAX_COUNT = 1000;
   protected static final int DEF_PERIOD = 1; // day
   
   protected int maxRowCount = MAX_COUNT;   
   private int filterPeriod = DEF_PERIOD;
   
   // filter period in time stamps
   protected long fromTime;
   protected long toTime;
   
   // sort directions
   private static final Map<String, SortDirection> sortDirections = 
      new HashMap<String, SortDirection>();
   
   static {
      sortDirections.put("ASC", SortDirection.ASC);
      sortDirections.put("DESC", SortDirection.DESC);
   };
   
   // xml export
   private boolean export = false;
   
   /**
    * Lists all parameters called for this service execution.
    * @param params
    * @return
    */
   protected static String listParams(final Element params) {
      StringBuffer list = new StringBuffer();
      for (Object o: params.getChildren()) {
         Element e = (Element) o;
         if (list.length() > 0) {
            list.append(",");
         }
         list.append(e.getName());
         list.append("=");
         list.append(e.getTextTrim());
      }
      list.insert(0, "Params: ");
      return list.toString();
   }
   
   /**
    * Gets the filter period from the parameters.
    * @param params
    * @return filter period in days
    */
   private int readFilterPeriod(final Element params, boolean useDefault) {
      // get filter parameter
      int period = -1;
      try {
         String paramPeriod = Util.getParam(params, PARAM_FILTER_PERIOD);
         period = Integer.parseInt(paramPeriod);
      }
      catch (BadInputEx e) {
         Log.info(LOG_MODULE, "No filter period passed - " + e.getMessage());
         period = -1;         
      }
      catch (NumberFormatException ex) {
         Log.info(LOG_MODULE, "Invalid filter period used - " + ex.getMessage());
         period = -1;
      }
      if (period < 0 && useDefault) {
         period = DEF_PERIOD;
      }
      return period;
   }
   
   /**
    * Gets the start date: (current day - filter period) at 00:00:00.000.
    * @param current time in milliseconds
    * @return milliseconds
    */
   private long getFromDate(long currentTime) {
      long millis = currentTime - (filterPeriod * 86400000L);
      // set day to 00:00:00.000
      Calendar calendar = new GregorianCalendar();
      calendar.setTimeInMillis(millis);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      return calendar.getTimeInMillis();
   }

   /**
    * Gets the end date: current day at 23:59:59.999.
    * @param current time in milliseconds
    * @return milliseconds
    */
   private long getToDate(long currentTime) {
      // set day to 23:59:59.999
      Calendar calendar = new GregorianCalendar();
      calendar.setTimeInMillis(currentTime);
      calendar.set(Calendar.HOUR_OF_DAY, 23);
      calendar.set(Calendar.MINUTE, 59);
      calendar.set(Calendar.SECOND, 59);
      calendar.set(Calendar.MILLISECOND, 999);
      return calendar.getTimeInMillis();
   }
   
   /**
    * Refreshes the filter period.
    * @param params
    */
   protected void setFilterPeriod(final Element params) {
      setFilterPeriod(params, true);
   }
   
   /**
    * Refreshes the filter period.
    * @param params
    * @param useDefault
    */
   protected void setFilterPeriod(final Element params, boolean useDefault) {
      filterPeriod = readFilterPeriod(params, useDefault);
      
      if (filterPeriod < 0) {
         fromTime = 0;
         toTime = 0;
      }
      else {
         long currentTime = System.currentTimeMillis();
         fromTime = getFromDate(currentTime); // current day - filter period
         toTime = getToDate(currentTime);         
      }
      Log.info(LOG_MODULE, "Filter period: " + 
            getFilterPeriod() + " days from " + formatDate(fromTime) + " to " + formatDate(toTime) + 
            " (" + fromTime + " - " + toTime + ")");      
   }

   /**
    * Checks whether the given time is in between the filter period.
    * @param date time
    * @return true/false
    */
   protected boolean isInPeriod(final XMLGregorianCalendar date) {
      boolean result = false;
      if (date != null) {
         long time = date.toGregorianCalendar().getTimeInMillis();
         result = isInPeriod(time);
      }
      return result;
   }
   
   /**
    * Checks whether the given time is in between the filter period.
    * @param time milliseconds
    * @return true/false
    */
   protected boolean isInPeriod(final long time) {
      return fromTime <= time && time <= toTime;      
   }

   /**
    * Gets the current filter period.
    * @return
    */
   protected int getFilterPeriod() {
      return filterPeriod;
   }
   
   /**
    * Checks whether a valid filter parameter was passed into this service.
    * @return
    */
   protected boolean hasFilterPeriod() {
      return getFilterPeriod() > 0 && fromTime > 0 && toTime > 0;
   }
   
   /**
    * Converts milliseconds to a <code>SimpleDateFormat</code> object using 'yyyy-MM-dd HH:mm:ss' pattern.
    * @param time milliseconds
    * @return date format
    */
   protected static String formatDate(final long time) {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(time));
   }

   /**
    * Gets a parameter by its name (if any). Returns default value if no parameter has been found.
    * @param params
    * @param name
    * @param defaultValue
    * @return
    */
   protected static String getNamedParam(final Element params, final String name, final String defaultValue) {
      return Util.getParam(params, name, defaultValue);
   }
   
   /**
    * Gets the sorting column (if any) from the service parameters.
    * @param params
    * @return
    */
   protected static String getSortColumnParam(final Element params) {
      return getNamedParam(params, PARAM_SORT_COLUMN, "date");
   }
   
   /**
    * Gets the sorting direction (if any) from the service parameters.
    * @param params
    * @return
    */
   protected static String getSortDirectionParam(final Element params) {
      return getNamedParam(params, PARAM_SORT_DIR, "ASC");
   }   

   /**
    * Gets the sort direction enumeration value for a proper key value.
    * @param dirKey
    * @return
    */
   protected static SortDirection getSortDirection(final String dirKey) {
      SortDirection sortDir = sortDirections.get(dirKey);
      if (sortDir == null) {
         sortDir = SortDirection.DESC;
      }
      return sortDir;
   }
   
   /**
    * Accesses the export flag.
    * @return true/false
    */
   protected boolean isExport() {
      return export;
   }

   /**
    * Checks whether the export parameter is used. 
    * @param params
    */
   protected void setExport(final Element params) {
      try {
         export = Util.getParam(params, PARAM_EXPORT, false);
      }
      catch (BadInputEx e) {
         export = false;
      }
      Log.info(LOG_MODULE, "XML data eport: " + isExport());      
   }

}
