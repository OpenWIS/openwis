package org.openwis.dataservice.common.service;

import org.openwis.dataservice.common.domain.bean.BlacklistInfoResult;
import org.openwis.dataservice.common.domain.entity.blacklist.BlacklistInfo;
import org.openwis.dataservice.common.domain.entity.enumeration.BlacklistInfoColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;

/**
 * The Interface BlackListService.
 */
public interface BlacklistService {

   /**
    * Checks if the user is blacklisted.
    *
    * @param user the user
    * @return true, if is blacklisted
    */
   boolean isUserBlacklisted(String user);

   /**
    * Sets the user blacklisted status.
    *
    * @param user the user
    * @param blacklisted the blacklisted status
    */
   void setUserBlacklisted(String user, boolean blacklisted);

   /**
    * Update user black list info (threshold).
    *
    * @param blacklistInfo the blacklist info
    * @return the black list info
    */
   BlacklistInfo updateUserBlackListInfo(BlacklistInfo blacklistInfo);

   /**
    * Gets the user black list info (threshold).
    *
    * @param user the user
    * @return the user black list info
    */
   BlacklistInfo getUserBlackListInfoIfExists(String user);

   /**
    * Gets the user black list info (threshold).
    *
    * @param user the user
    * @param create create the blacklist info if true
    * @return the user black list info
    */
   BlacklistInfo getUserBlackListInfo(String user, boolean create);

   /**
    * Gets the user black list info.
    *
    * @param firstResult the first result
    * @param maxResults the max results
    * @param column the column
    * @param sortDirection the sort direction
    * @return the user black list info
    */
   BlacklistInfoResult getUsersBlackListInfo(int firstResult, int maxResults,
         BlacklistInfoColumn column, SortDirection sortDirection);

   /**
    * Gets the user black list info.
    *
    * @param startWith the start with
    * @param firstResult the first result
    * @param maxResults the max results
    * @param column the column
    * @param sortDirection the sort direction
    * @return the user black list info
    */
   BlacklistInfoResult getUsersBlackListInfoByUser(String startWith, int firstResult,
         int maxResults, BlacklistInfoColumn column, SortDirection sortDirection);

   /**
    * Check and update disseminated data.
    *
    * @param user the user
    * @param email the email
    * @param date the date
    * @param nbFiles the number of files
    * @param totalSize the total size
    * @return true, if successful
    */
   boolean checkAndUpdateDisseminatedData(String user,String email, String date, int nbFiles, long totalSize);

}
