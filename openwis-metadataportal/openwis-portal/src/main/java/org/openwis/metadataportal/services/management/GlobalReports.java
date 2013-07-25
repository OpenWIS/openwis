package org.openwis.metadataportal.services.management;

import java.util.HashMap;
import java.util.Map;

import org.openwis.management.monitoring.UserDisseminatedDataColumn_0020;

/**
 * Common abstract used base class for disseminated and extracted report service classes.
 * Explanation goes here. <P>
 * 
 */
public abstract class GlobalReports extends FilterReports {

   private static final Map<String, UserDisseminatedDataColumn_0020> sortColumns = 
      new HashMap<String, UserDisseminatedDataColumn_0020>();

   static {
      sortColumns.put("date", UserDisseminatedDataColumn_0020.DATE);
      sortColumns.put("dissToolSize", UserDisseminatedDataColumn_0020.DISS_TOOL_TOTAL_SIZE);
      sortColumns.put("size", UserDisseminatedDataColumn_0020.TOTAL_SIZE);
      sortColumns.put("userId", UserDisseminatedDataColumn_0020.USER);      
   };
   
   /**
    * Gets the sort column enumeration value for a proper key value.
    * @param sortKey
    * @return
    */
   protected static UserDisseminatedDataColumn_0020 getSortColumn(final String sortKey) {
      UserDisseminatedDataColumn_0020 sortColumn = sortColumns.get(sortKey);
      if (sortColumn == null) {
         sortColumn = UserDisseminatedDataColumn_0020.DATE;;
      }
      return sortColumn;
   }
   
}
