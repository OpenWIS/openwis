/**
 *
 */
package org.openwis.management.service;

import java.util.List;

import org.openwis.management.entity.ReplicatedData;
import org.openwis.management.entity.ReplicatedDataColumn;
import org.openwis.management.entity.SortDirection;

/**
 * Defines the management interface to monitor the volume of replicated data.
 */
public interface ReplicatedDataStatistics {

   // -------------------------------------------------------------------------
   // Update Statistics
   // -------------------------------------------------------------------------

   /**
    * Add a volume of replicated data for a given date and a given source.
    * 
    * @param source the source.
    * @param date the date.
    * @param size the date size.
    */
   void updateReplicatedData(String source, String date, long size);

   // -------------------------------------------------------------------------
   // Query Reports
   // -------------------------------------------------------------------------

   /**
    * Retrieve the overall volume of data replicated per day and for a given
    * source.
    * 
    * @param source the source.
    * @param date the date.
    * @return the replicated data.
    */
   ReplicatedData getReplicatedDataFromSource(String source, String date);

   /**
    * Retrieve the overall volume of data replicated per day and for a given
    * source applying a filter from a given date to a given date.
    * 
    * @param source the source.
    * @param from the from date.
    * @param to the to date.
    * @return the replicated data.
    */
   ReplicatedData getReplicatedDataFromSourceInInterval(String source, String from, String to);

   /**
    * Retrieve the overall volume of data replicated per day and per source.
    * 
    * @param date the date.
    * @return the replicated data.
    */
   ReplicatedData getReplicatedData(String date);

   /**
    * Retrieve the overall volume of data replicated per day and per source
    * applying a filter from a given date to a given date.
    * 
    * @param from the from date.
    * @param to the to date.
    * @return the replicated data.
    */
   ReplicatedData getReplicatedDataInInterval(String from, String to);

   /**
    * Retrieves the overall volume of data replicated per day.
    * 
    * @param firstResult the starting index
    * @param maxItemsCount specifies the maximum number of items to return
    * @param column the column to sort
    * @param dir the sort direction
    * @return the list of statistical records.
    */
   List<ReplicatedData> getReplicatedDataStatistics(int firstResult, int maxItemsCount,
		   ReplicatedDataColumn column, SortDirection dir);

}
