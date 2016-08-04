package org.openwis.dataservice.common.service;

import java.util.Collection;
import java.util.List;

import org.openwis.dataservice.common.domain.entity.enumeration.RequestColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;

/**
 * The request service. <P>
 */
public interface RequestService {

   /**
    * Create a request.
    * @param request the request to create.
    * @param metadataURN
    * @return the request id.
    */
   public Long createRequest(AdHoc request, String metadataURN);

   /**
    * Delete a request using its id.
    * @param id the id.
    */
   public void deleteRequest(Long id);

   /**
    *
    * Get the request using its id.
    * @param id the id.
    * @return the request.
    */
   public AdHoc getRequest(Long id);

   /**
    * Get the requests by users.
    * @param users the users
    * @return a list of adHoc
    */
   List<ProcessedRequest> getRequestsByUsers(Collection<String> users, int firstResult,
         int maxResults, RequestColumn column, SortDirection sortDirection);

   /**
    * Get the requests by users.
    * @param users the users
    * @return a list of adHoc
    */
   int getRequestsByUsersCount(Collection<String> users);

   /**
    * Retrieve last request for a given user.
    *
    * @param user user id
    * @param maxRequest the max request
    * @return a collection of request
    */
   public List<ProcessedRequest> getLastProcessedRequest(String user, int maxRequest);

   /**
    * Removes request by user.
    *
    * @param user the user
    * @return the number of deleted request
    */
   public int deleteRequestByUser(String user);

}
