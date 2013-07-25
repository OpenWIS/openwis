/**
 * 
 */
package org.openwis.harness.samples.common.parameters;

import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.samples.common.time.DateTimeUtils;
import org.openwis.harness.subselectionparameters.DatePeriodSelection;
import org.openwis.harness.subselectionparameters.TimePeriodSelection;

/**
 * The Class ParameterUtils. <P>
 * Explanation goes here. <P>
 */
public final class ParameterUtils {
   /** The Constant DATE_PARAMETER. */
   public static final String DATE_PARAMETER = "parameter.date.period";

   /** The Constant TIME_PARAMETER. */
   public static final String TIME_PARAMETER = "parameter.time.period";

   /**
    * Instantiates a new parameter utils.
    */
   private ParameterUtils() {
      super();
   }

   /**
    * Parameters to string.
    *
    * @param parameters the parameters
    * @return the string
    */
   public static String parametersToString(Parameter... parameters) {
      StringBuffer sb = new StringBuffer();
      if (parameters == null || parameters.length == 0) {
         sb.append("<No parameters>");
      } else {
         boolean isFirst = true;
         for (Parameter parameter : parameters) {
            if (isFirst) {
               isFirst = false;
            } else {
               sb.append(", ");
            }
            sb.append(parameter.getCode());
            sb.append('=');
            sb.append(parameter.getValues());
         }
      }
      return sb.toString();
   }

   /**
    * Parameters to string.
    *
    * @param parameters the parameters
    * @return the string
    */
   public static String parametersToString(List<Parameter> parameters) {
      return parametersToString(parameters.toArray(new Parameter[parameters.size()]));
   }

   /**
    * Gets the time period selection.
    *
    * @param bundle the bundle
    * @return the time period selection
    */
   public static TimePeriodSelection getTimePeriodSelection(ResourceBundle bundle) {
      TimePeriodSelection tps = new TimePeriodSelection();
      tps.setCode(TIME_PARAMETER);
      tps.setLabel(bundle.getString(TIME_PARAMETER));

      tps.setPeriodMinExtent("");
      tps.setPeriodMaxExtent("");

      Calendar now = Calendar.getInstance();
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.HOUR, -1);
      cal.set(Calendar.MINUTE, 0);
      tps.setFrom(DateTimeUtils.formatTime(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE)));
      tps.setTo(DateTimeUtils.formatTime(now.get(Calendar.HOUR), now.get(Calendar.MINUTE)));

      return tps;
   }

   /**
    * Gets the date period selection.
    *
    * @param bundle the bundle
    * @return the date period selection
    */
   public static DatePeriodSelection getDatePeriodSelection(ResourceBundle bundle) {
      DatePeriodSelection dps = new DatePeriodSelection();
      dps.setCode(DATE_PARAMETER);
      dps.setLabel(bundle.getString(DATE_PARAMETER));

      Calendar now = Calendar.getInstance();
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.YEAR, 2010);
      cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      dps.setPeriodMinExtent(DateTimeUtils.formatDate(cal));
      dps.setPeriodMaxExtent("");

      dps.setFrom(DateTimeUtils.formatDate(now));
      dps.setTo(DateTimeUtils.formatDate(now));
      return dps;
   }

}
