/**
 *
 */
package org.openwis.management.service;

import java.util.List;
import java.util.Set;

import org.openwis.management.entity.SortDirection;
import org.openwis.management.entity.UserDisseminatedData;
import org.openwis.management.entity.UserDisseminatedDataColumn;
import org.openwis.management.service.bean.UserDisseminatedDataResult;

/**
 * Defines the management interface to monitor the volume of disseminated data.
 */
public interface DisseminatedDataStatistics {

   // -------------------------------------------------------------------------
   // Update Methods
   // -------------------------------------------------------------------------

   /**
    * Add a volume of disseminated data for a given date and a given user.
    *
    * @param date the date.
    * @param userId the user id.
    * @param size the data size.
    */
   void updateUserExtractedData(String userId, String date, int nbFiles, long totalSize);

   /**
    * Update user disseminated by tool data.
    *
    * @param userId the user id
    * @param date the date
    * @param nbFiles the nb files
    * @param totalSize the total size
    */
   void updateUserDisseminatedByToolData(String userId, String date, int nbFiles, long totalSize);

   // -------------------------------------------------------------------------
   // Report Methods
   // -------------------------------------------------------------------------

   /**
    * Retrieve the overall volume of data disseminated per day for a given user.
    *
    * @param userId the user id.
    * @param date the date.
    * @return the user disseminated data.
    */
   UserDisseminatedData getUserDisseminatedData(String userId, String date);

   /**
    * Gets the user disseminated data.
    *
    * @param users the users list
    * @param date the date
    * @return the user disseminated data
    */
   List<UserDisseminatedData> getUsersDisseminatedData(Set<String> users, String date);

   /**
    * Gets the users disseminated data by user.
    *
    * @param user the user
    * @param date the date
    * @param firstResults the first result index
    * @param maxResults the max results
    * @param column the column
    * @param sortDirection the sort direction
    * @return the users disseminated data by user
    */
   UserDisseminatedDataResult getUsersDisseminatedDataByUser(String user, int firstResults,
         int maxResults, UserDisseminatedDataColumn column, SortDirection sortDirection);

   /**
    * Retrieve the overall volume of data disseminated per day for a given user
    * applying a filter from a given date to a given date.
    *
    * @param userId the user id.
    * @param from the from date.
    * @param to the to date.
    * @return the user disseminated data.
    */
   UserDisseminatedData getUserDisseminatedDataInInterval(String userId, String from, String to);

   /**
    * Retrieve the overall volume of data disseminated per day and per user
    *
    * @param date the date.
    * @return the user disseminated data.
    */
   UserDisseminatedData getDisseminatedData(String date);

   /**
    * Retrieve the overall volume of data disseminated per day and per user
    * applying a filter from a given date to a given date
    *
    * @param from the from date.
    * @param to the to date.
    * @return the user disseminated data.
    */
   UserDisseminatedDataResult getDisseminatedDataInInterval(String from, String to,
         int firstResults, int maxResults, UserDisseminatedDataColumn column,
         SortDirection sortDirection);

   /**
    * Retrieves the overall volume of data disseminated per day and per user.
    *
    * @param maxItemsCount specifies the maximum number of items to return
    * @return the list of statistical records.
    */
   List<UserDisseminatedData> getDisseminatedDataStatistics(int maxItemsCount);

}
