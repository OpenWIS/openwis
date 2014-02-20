/**
 *
 */
package org.openwis.dataservice.common.service;

import java.util.Collection;
import java.util.List;

import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.dto.LightProcessedRequestDTO;
import org.openwis.dataservice.common.domain.entity.enumeration.ProcessedRequestColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.ProcessedRequestFilter;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.service.bean.ProcessedRequestListResult;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public interface ProcessedRequestService {

    /**
     * Add a new processed request to a given subscription.
     *
     * @param subscription subscription
     * @param processedRequest processed request
     * @return the id of the persisted processed request
     */
    Long addProcessedRequestToSubscription(Subscription subscription,
            ProcessedRequest processedRequest);

    /**
     * Description goes here.
     *
     * @param id the id
     * @return true, if successful
     */
    boolean monitorExtraction(Long id);

    /**
     * Description goes here.
     *
     * @param processedRequest the processed request
     * @param productDate the product date
     * @param productId the product id
     * @return true, if successful
     */
   Status extract(ProcessedRequest processedRequest, String productDate, String productId);

    /**
     * Gets the processed request.
     *
     * @param id the id
     * @return the processed request
     */
    ProcessedRequest getProcessedRequest(Long id);

    /**
     * A method to return all processed request of a request.
     *
     * @param requestID the subscription ID.
     * @param firstResult the first result
     * @param maxResults the max results
     * @param column the column
     * @param sortDirection the sort direction
     * @return the processed request of a subscription.
     */
    List<LightProcessedRequestDTO> getAllProcessedRequestsByRequest(Long requestID,
            Integer firstResult, Integer maxResults, ProcessedRequestColumn column,
            SortDirection sortDirection);

    /**
     * A method to return the number of processed requests for a request.
     * @param requestID the request ID.
     * @return the number of processed requests for a request.
     */
    int getAllProcessedRequestsByRequestCount(Long requestID);

    /**
    * A method to return all processed request for a list of users.
    * @param userNames the user names.
    * @param prFilter the {@link ProcessedRequestFilter}
    * @param firstResult the first result.
    * @param maxResults the count.
    * @param column the column to sort.
    * @param sortDirection the sort order.
    * @return a {@link ProcessedRequestListResult}
    */
    ProcessedRequestListResult getAllProcessedRequestsByUsers(Collection<String> userNames, ProcessedRequestFilter prfilter,
            Integer firstResult, Integer maxResults, RequestColumn column,
            SortDirection sortDirection);

    /**
     * A method to return all processed request for a list of users.
     * @param userNames the user names.
     * @return a list of processed requests for a list of users.
     */
    int getAllProcessedRequestsByUsersCount(Collection<String> userNames);

    /**
     * A method to return all processed request of a request.
     * @param adhocId the subscription ID.
     * @return the processed request of a adhoc request.
     */
    ProcessedRequest getProcessedRequestForAdhoc(Long adhocId);

    /**
     * A method to return a processed request with the adhoc fully loaded.
     * @param adhocId the subscription ID.
     * @return the processed request of a adhoc request.
     */
    ProcessedRequest getFullProcessedRequestForAdhoc(Long adhocId);

    /**
     * A method to return a processed request with the corresponding request and metadata information.
     * Similar to {@link #getFullProcessedRequestForAdhoc(Long)} but uses the processed request ID.
     *
     * @param processedRequestID the processed request id.
     * @return the processed request
     */
    ProcessedRequest getFullProcessedRequest(Long processedRequestID);

    /**
    * Deletes the processed requests associated to a request.<p>
    * Operates as a Cascade deletion when deleting a request.
    * @param requestID the request id.
    */
    void deleteProcessedRequestsByRequest(Long requestID);
    
    /**
     * Delete processed requests.
     *
     * @param processedRequestIDs the processed request i ds
     */
    void deleteProcessedRequests(List<Long> processedRequestIDs);

    /**
     * Deletes the processed requests associated to a request.
     * Also delete the request if it is an AdHoc <p>
     * @param processedRequestId the processed request id
     */
    void deleteProcessedRequestWithAdHoc(Long processedRequestId);

    /**
     * Updates a processed request.
     * @param processedRequest the processed request to update.
     */
    void updateProcessedRequest(ProcessedRequest processedRequest);

   /**
    * Update processed request on staging post cleaning.
    */
   void clearProcessedRequestStagingPost();

   /**
    * Update processed request on staging post cleaning.
    *
    * @param uri the uri the removed uri
    */
   void clearProcessedRequestStagingPostByUri(String uri);

}
