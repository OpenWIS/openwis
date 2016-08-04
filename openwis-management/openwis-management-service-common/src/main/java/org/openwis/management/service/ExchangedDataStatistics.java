/**
 *
 */
package org.openwis.management.service;

import java.util.List;

import org.openwis.management.entity.ExchangedData;
import org.openwis.management.entity.ExchangedDataColumn;
import org.openwis.management.entity.SortDirection;
import org.openwis.management.service.bean.ExchangedDataResult;

/**
 * Defines the management interface to monitor the volume of extracted data.
 */
public interface ExchangedDataStatistics {

   // -------------------------------------------------------------------------
   // Update Statistics
   // -------------------------------------------------------------------------

   /**
    * Add a volume of extracted data for a given date.
    *
    * @param date the date.
    * @param nbMetadata the number of metadata
    * @param source the source
    * @param totalSize the total size
    */
   void updateExchangedData(String date, String source, long nbMetadata, long totalSize);

   // -------------------------------------------------------------------------
   // Query Reports
   // -------------------------------------------------------------------------

   /**
    * Retrieve the overall volume of data disseminated and extracted per day.
    *
    * @param date the date.
    * @return the exchanged data.
    */
   ExchangedData getExchangedData(String date, String source);

   /**
    * Gets the exchanged data in interval by source.
    *
    * @param from the date from
    * @param to the date to
    * @param firstResult the starting index
    * @param maxCount the max number of result
    * @param column the column to sort
    * @param dir the sort direction
    * @return the exchanged data in interval by source
    */
   ExchangedDataResult getExchangedDataInIntervalForAllSources(String from, String to,
         int firstResult, int maxCount, ExchangedDataColumn column, SortDirection dir);

   /**
    * Gets the exchanged data in interval by source.
    *
    * @param source the source prefix
    * @param from the date from
    * @param to the date to
    * @param firstResult the starting index
    * @param maxCount the max number of result
    * @param column the column to sort
    * @param dir the sort direction
    * @return the exchanged data in interval by source
    */
   ExchangedDataResult getExchangedDataInIntervalBySources(String source, int firstResult,
         int maxCount, ExchangedDataColumn column, SortDirection dir);

   /**
    * Retrieve the overall volume of data disseminated and extracted per day
    * applying a filter from a given date to a given date.
    *
    * @param from the from date.
    * @param to the to date.
    * @return the exchanged data.
    */
   ExchangedData getTotalExchangedDataInInterval(String from, String to);

   /**
    * Retrieves the overall volume of data disseminated and extracted per day.
    *
    * @param maxItemsCount specifies the maximum number of items to return
    * @return the list of statistical records.
    */
   List<ExchangedData> getExchangedDataStatistics(int maxItemsCount);

}
