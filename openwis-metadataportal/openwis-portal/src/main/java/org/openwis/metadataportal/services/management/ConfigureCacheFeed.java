/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.util.ArrayList;
import java.util.List;

import jeeves.exceptions.BadInputEx;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.management.control.ControlService;
import org.openwis.management.control.FeedingFilter;
import org.openwis.management.control.ManagedServiceIdentifier;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockConfigurationFilters;
import org.openwis.metadataportal.services.mock.MockMode;

/**
 * Extension of <code>ConfigureCache</code> to establish a service for the configure cache feeding filter. <P>
 */
public class ConfigureCacheFeed extends ConfigureCache {

   private static final String REQUEST_RESET_TO_DEFAULT = "RESET_TO_DEFAULT";
   
   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(final Element params, final ServiceContext context) throws Exception {
      Element result = null;
      String requestType = Util.getParam(params, PARAM_REQUEST_TYPE, null);
      
      if (requestType != null) {         
         if (requestType.equals(REQUEST_RESET_TO_DEFAULT)) {
            Log.info(LOGMODULE, "Request service type: " + requestType);
            result = resetToDefault(params);
         }
      }
      if (result == null) {
         result = super.exec(params, context);
      }
      return result;
   }
   
   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#getFilters(org.jdom.Element)
    */
   @Override
   protected Element getFilters(Element params) throws Exception {
      List<FeedingFilter> elements = new ArrayList<FeedingFilter>();
      // Test mode
      if (MockMode.isMockModeControlService()) {
         elements = MockConfigurationFilters.getFeedingFilters();
      }
      // Operation mode
      else {
         // Delegate to the MC service
         ControlService service = ManagementServiceProvider.getControlService();
         if (service != null) {
            // query...
            List<FeedingFilter> filters = service.getFeedingFilters();
            if (filters != null) {
               elements.addAll(filters);
            }
            else {
               Log.error(LOGMODULE, "Invalid feeding filter result list from ControlService (null)");
            }
         }
         else {
            Log.error(LOGMODULE, "No control service available from the ManagementServiceProvider");
         }
      }
      SearchResultWrapper<FeedingFilter> wrapper =
         new SearchResultWrapper<FeedingFilter>(elements.size(), elements);

      return JeevesJsonWrapper.send(wrapper);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#addFilter(org.jdom.Element)
    */
   @Override
   protected Element addFilter(final Element params) {
      Element result = getResult(REQUEST_ADD_FILTER);
      
      FeedingFilter filter = this.getFilter(params);
      if (filter == null) {
         setResultError(result, "Invalid filter passed [null]");                     
         return result;
      }
      
      result.setAttribute(RESPONSE_ATTRIB_FILTER, filter2String(filter));
      
      ControlService service = ManagementServiceProvider.getControlService();      
      if (service != null) {
         Log.info(LOGMODULE, "Add feeding filter: " + filter2String(filter));
         boolean success = service.addFeedingFilter(filter.getRegex(), filter.getDescription());
         setResultSuccess(result, success);
      }
      else {
         setResultError(result, "No control service available");                     
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#removeFilter(org.jdom.Element)
    */
   @Override
   protected Element removeFilter(Element params) throws Exception {
      Element result = getResult(REQUEST_REMOVE_FILTER);

      FeedingFilter filter = this.getFilter(params);
      if (filter == null) {
         setResultError(result, "Invalid filter passed [null]");                     
         return result;
      }
      
      result.setAttribute(RESPONSE_ATTRIB_FILTER, filter2String(filter));
      
      ControlService service = ManagementServiceProvider.getControlService();      
      if (service != null) {
         Log.info(LOGMODULE, "Remove feeding filter: " + filter2String(filter));
         boolean success = service.removeFeedingFilter(filter.getRegex());
         setResultSuccess(result, success);
      }      
      else {
         setResultError(result, "No control service available");                     
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#toogleFilterStatus(org.jdom.Element)
    */
   @Override
   protected Element setServiceStatus(final Element params) {
      Element result = null;
      try {
         String checkedValue = Util.getParam(params, PARAM_SERVICE_STATUS);
         boolean status = Boolean.valueOf(checkedValue);
         result = setServiceStatus(ManagedServiceIdentifier.FEEDING_SERVICE, status);
      }      
      catch (BadInputEx ex) {         
         result = getResult(REQUEST_SET_SERVICE_STATUS);
         setResultError(result, ex.getMessage());                     
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#getFilterStatus()
    */
   @Override
   protected Element getServiceStatus(final Element params) {
      return getServiceStatus(ManagedServiceIdentifier.FEEDING_SERVICE);
   }
   
   /**
    * Request a default feeding filter list.
    * @param params
    * @return
    * @throws Exception
    */
   private Element resetToDefault(final Element params) throws Exception {
      List<FeedingFilter> defaultFilters = new ArrayList<FeedingFilter>();

      ControlService service = ManagementServiceProvider.getControlService();
      if (service != null) {
         // query...
         List<FeedingFilter> filters = service.resetFeedingFilters();
         if (filters != null) {
            defaultFilters.addAll(filters);
         }
      }
      else {
         Log.error(LOGMODULE, "No control service available from the ManagementServiceProvider");
      }
      SearchResultWrapper<FeedingFilter> wrapper =
         new SearchResultWrapper<FeedingFilter>(defaultFilters.size(), defaultFilters);

      return JeevesJsonWrapper.send(wrapper);
   }
   
   private FeedingFilter getFilter(final Element params) {
      FeedingFilter filter = null;
      try {
         String regex = Util.getParam(params, PARAM_FILTER_REGEX);
         String description = Util.getParam(params, PARAM_FILTER_DESCR);
         
         filter = new FeedingFilter();
         filter.setRegex(regex);
         filter.setDescription(description);
      }
      catch (BadInputEx ex) {
         Log.error(LOGMODULE, "Invalid filter parameters " + ex.toString());
         filter = null;
      }
      return filter;
   }
   
   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#filter2String(java.lang.Object)
    */
   @Override
   protected String filter2String(final Object filter) {
      FeedingFilter f = (FeedingFilter) filter;
      return "{regex:" + f.getRegex() + ",description:" + f.getDescription() + "}";
   }
   
}
