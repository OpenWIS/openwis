/**
 *
 */
package org.openwis.management.service;

import java.util.List;

import org.openwis.management.entity.IngestedData;
import org.openwis.management.entity.IngestedDataColumn;
import org.openwis.management.entity.SortDirection;

/**
 * Defines the management interface to monitor the volume of ingested data.
 */
public interface IngestedDataStatistics {

   // -------------------------------------------------------------------------
   // Update Statistics
   // -------------------------------------------------------------------------

   /**
    * Add a volume of ingested data for a given date.
    *
    * @param date the date.
    * @param size the data size.
    */
   void updateIngestedData(String date, long size);

   // -------------------------------------------------------------------------
   // Query Report
   // -------------------------------------------------------------------------

   /**
    * Retrieve the overall volume of data ingested per day.
    * 
    * @param date the date.
    * @return the exchanged data.
    */
   IngestedData getIngestedData(String date);

   /**
    * Retrieve the overall volume of data ingested per day applying a filter
    * from a given date to a given date.
    *
    * @param from the from date.
    * @param to the to date.
    * @return the exchanged data.
    */
   IngestedData getIngestedDataInInterval(String from, String to);

   /**
    * Retrieves the overall volume of data ingested per day.
    * 
    * @param firstResult the starting index
    * @param maxItemsCount specifies the maximum number of items to return
    * @param column the column to sort
    * @param dir the sort direction
    * @return the list of statistical records.
    */
   List<IngestedData> getIngestedDataStatistics(int firstResult, int maxItemsCount,
		   IngestedDataColumn column, SortDirection dir);

}
