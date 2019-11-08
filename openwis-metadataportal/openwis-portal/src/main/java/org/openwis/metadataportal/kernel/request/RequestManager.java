/**
 * 
 */
package org.openwis.metadataportal.kernel.request;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.openwis.dataservice.DataPolicyOperations;
import org.openwis.dataservice.LightProcessedRequestDTO;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.ProcessedRequestColumn;
import org.openwis.dataservice.ProcessedRequestFilter;
import org.openwis.dataservice.ProcessedRequestListResult;
import org.openwis.dataservice.ProcessedRequestService;
import org.openwis.dataservice.RequestColumn;
import org.openwis.dataservice.RequestService;
import org.openwis.dataservice.SortDirection;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionColumn;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.dataservice.UserDataPolicyOperations;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class RequestManager {

   /** The executor. */
   private static final ExecutorService executor = Executors.newSingleThreadExecutor();

   public SearchResultWrapper<ProcessedRequest> getAllAdhocsByUsers(
         SearchCriteriaWrapper<List<String>, RequestColumn> searchCriteriaWrapper) throws Exception {
      RequestService requestService = DataServiceProvider.getRequestService();

      SearchResultWrapper<ProcessedRequest> result = new SearchResultWrapper<ProcessedRequest>();

      result.setRows(requestService.getRequestsByUsers(searchCriteriaWrapper.getCriteria(),
            searchCriteriaWrapper.getStart(), searchCriteriaWrapper.getLimit(),
            searchCriteriaWrapper.getSort(),
            SortDirection.valueOf(searchCriteriaWrapper.getDir().toString())));
      result.setTotal(requestService.getRequestsByUsersCount(searchCriteriaWrapper.getCriteria()));
      return result;
   }

   /**
    * Description goes here.
    * @param wrapper
    * @return
    */
   public SearchResultWrapper<Subscription> getAllSubscriptionsByUsers(
         SearchCriteriaWrapper<List<String>, SubscriptionColumn> searchCriteriaWrapper) {
      SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();

      SearchResultWrapper<Subscription> result = new SearchResultWrapper<Subscription>();

      result.setRows(subscriptionService.getSubscriptionsByUsers(
            searchCriteriaWrapper.getCriteria(), searchCriteriaWrapper.getStart(),
            searchCriteriaWrapper.getLimit(), searchCriteriaWrapper.getSort(),
            SortDirection.valueOf(searchCriteriaWrapper.getDir().toString())));
      result.setTotal(subscriptionService.getSubscriptionsByUsersCount(searchCriteriaWrapper
            .getCriteria()));
      return result;
   }

   public SearchResultWrapper<ProcessedRequest> getAllProcessedRequestsByUsers(
         SearchCriteriaWrapper<List<String>, RequestColumn> searchCriteriaWrapper,
         ProcessedRequestFilter prFilter) throws Exception {
      ProcessedRequestService processedRequestService = DataServiceProvider
            .getProcessedRequestService();

      SearchResultWrapper<ProcessedRequest> result = new SearchResultWrapper<ProcessedRequest>();

      ProcessedRequestListResult allProcessedRequestsByUsers = processedRequestService
            .getAllProcessedRequestsByUsers(searchCriteriaWrapper.getCriteria(), prFilter,
                  searchCriteriaWrapper.getStart(), searchCriteriaWrapper.getLimit(),
                  searchCriteriaWrapper.getSort(),
                  SortDirection.valueOf(searchCriteriaWrapper.getDir().toString()));

      result.setRows(allProcessedRequestsByUsers.getList());
      result.setTotal(allProcessedRequestsByUsers.getCount());

      return result;
   }

   public SearchResultWrapper<LightProcessedRequestDTO> getAllProcessedRequestsByRequest(
         SearchCriteriaWrapper<Long, ProcessedRequestColumn> searchCriteriaWrapper)
         throws Exception {
      ProcessedRequestService processedRequestService = DataServiceProvider
            .getProcessedRequestService();

      SearchResultWrapper<LightProcessedRequestDTO> result = new SearchResultWrapper<LightProcessedRequestDTO>();

      result.setRows(processedRequestService.getAllProcessedRequestsByRequest(
            searchCriteriaWrapper.getCriteria(), searchCriteriaWrapper.getStart(),
            searchCriteriaWrapper.getLimit(), searchCriteriaWrapper.getSort(),
            SortDirection.valueOf(searchCriteriaWrapper.getDir().toString())));
      result.setTotal(processedRequestService
            .getAllProcessedRequestsByRequestCount(searchCriteriaWrapper.getCriteria()));
      //result.setTotal(result.getRows().size());
      return result;
   }

   /**
    * Description goes here.
    * @param columnName
    * @return
    */
   public RequestColumn getRequestColumnAttribute(String columnName) {
      Map<String, RequestColumn> pmcEnumMap = new HashMap<String, RequestColumn>();
      pmcEnumMap.put("urn", RequestColumn.URN);
      pmcEnumMap.put("title", RequestColumn.TITLE);
      pmcEnumMap.put("creationDate", RequestColumn.CREATION_DATE);
      pmcEnumMap.put("id", RequestColumn.ID);
      pmcEnumMap.put("localDataSource", RequestColumn.LOCAL_DATASOURCE);
      pmcEnumMap.put("status", RequestColumn.STATUS);
      pmcEnumMap.put("user", RequestColumn.USER);
      pmcEnumMap.put("size", RequestColumn.VOLUME);

      return pmcEnumMap.get(columnName);
   }

   /**
    * Description goes here.
    * @param sortColumn
    * @return
    */
   public SubscriptionColumn getSubscriptionColumnAttribute(String columnName) {
      Map<String, SubscriptionColumn> pmcEnumMap = new HashMap<String, SubscriptionColumn>();
      pmcEnumMap.put("urn", SubscriptionColumn.URN);
      pmcEnumMap.put("title", SubscriptionColumn.TITLE);
      pmcEnumMap.put("id", SubscriptionColumn.ID);
      pmcEnumMap.put("status", SubscriptionColumn.STATUS);
      pmcEnumMap.put("user", SubscriptionColumn.USER);
      //      pmcEnumMap.put("startingDate", SubscriptionColumn.STARTING_DATE);
      //      pmcEnumMap.put("backup", SubscriptionColumn.BACKUP);

      return pmcEnumMap.get(columnName);
   }

   /**
    * Description goes here.
    * @param sortColumn
    * @return
    */
   public ProcessedRequestColumn getProcessedRequestColumnAttribute(String columnName) {
      Map<String, ProcessedRequestColumn> pmcEnumMap = new HashMap<String, ProcessedRequestColumn>();
      pmcEnumMap.put("creationDate", ProcessedRequestColumn.CREATION_DATE);
      pmcEnumMap.put("status", ProcessedRequestColumn.STATUS);
      pmcEnumMap.put("size", ProcessedRequestColumn.VOLUME);

      return pmcEnumMap.get(columnName);
   }

   /**
    * Check Subscription on Data Service for a user.
    * @param userName The user name.
    * @param dbms The dbms.
    * @throws Exception if an error occurs.
    */
   public void checkUserSubscription(String userName, Dbms dbms) throws Exception {

      DataPolicyManager dataPolicyManager = new DataPolicyManager(dbms);
      List<DataPolicyOperations> dataPolicyOperations = dataPolicyManager
            .getAllUserDataPoliciesOperations(userName);

      SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
      subscriptionService.checkUserSubscription(userName, dataPolicyOperations);
   }

   /**
    * Check Subscription on Data Service.
    * @param context The service context.
    * @throws Exception if an error occurs.
    */
   public void checkUsersSubscription(final ServiceContext context) throws Exception {
      Log.info(Geonet.DATA_MANAGER, "Scheduling Check of Users Subscription");
      executor.execute(new Runnable() {
         @Override
         public void run() {
            // Open a dbms connection
            Dbms dbms = null;
            try {
               dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
               checkUsersSubscription(dbms);
            } catch (Exception e) {
               Log.error(Geonet.DATA_MANAGER,
                     "Exception while checking Users Subscription: " + e.getMessage(), e);
            } finally {
               if (dbms != null) {
                  try {
                     context.getResourceManager().close();
                  } catch (Exception e) {
                     Log.error(Geonet.DATA_MANAGER, "Exception while closing the dbms connection: "
                           + e.getMessage(), e);
                  }
               }
            }
         }
      });
   }

   /**
    * Check Subscription on Data Service.
    * @param dbms The dbms.
    * @throws Exception if an error occurs.
    */
   private void checkUsersSubscription(Dbms dbms) throws Exception {
      Log.info(Geonet.DATA_MANAGER, "Checking Users Subscription, getting all operations to check");
      
      DataPolicyManager dataPolicyManager = new DataPolicyManager(dbms);
      List<UserDataPolicyOperations> dataPolicyOperations = dataPolicyManager
            .getAllDataPoliciesOperations();
      
      Log.info(
            Geonet.DATA_MANAGER,
            MessageFormat.format("Perform {0} checks on DataService",
                  dataPolicyOperations.size()));

      SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
      subscriptionService.checkUsersSubscription(dataPolicyOperations);

      Log.info(Geonet.DATA_MANAGER, "Check Users Subscription completed");
   }

   /**
    * Remove all request/subscriptions of a user.
    * 
    * @param userName the username
    * @throws Exception if an error occurs.
    */
   public void removeUserRequests(String userName) throws Exception {
      RequestService requestService = DataServiceProvider.getRequestService();
      requestService.deleteRequestByUser(userName);
   }

}
