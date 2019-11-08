/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.util.ArrayList;
import java.util.List;

import jeeves.exceptions.BadInputEx;
import jeeves.utils.Log;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.management.control.ControlService;
import org.openwis.management.control.IngestionFilter;
import org.openwis.management.control.ManagedServiceIdentifier;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockConfigurationFilters;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;

/**
 * Extension of <code>ConfigureCache</code> to establish a service for the configure cache ingestion filter. <P>
 * Explanation goes here. <P>
 */
public class ConfigureCacheIngest extends ConfigureCache {
   
   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#getFilters(org.jdom.Element)
    */
   @Override
   protected Element getFilters(final Element params) throws Exception {
      List<IngestionFilter> elements = new ArrayList<IngestionFilter>();
      // Test mode
      if (MockMode.isMockModeControlService()) {
         elements = MockConfigurationFilters.getIngestionFilters();
      }
      // Operation mode
      else {
         // Delegate to the MC service
         ControlService service = ManagementServiceProvider.getControlService();
         if (service != null) {
            // query...
            List<IngestionFilter> filters = service.getIngestionFilters();            
            if (filters != null) {
               elements.addAll(filters);
            }
            else {
               Log.error(LOGMODULE, "Invalid ingestion filter result list from ControlService (null)");
            }
         }
         else {
            Log.error(LOGMODULE, "No control service available from the ManagementServiceProvider");
         }
      }
      SearchResultWrapper<IngestionFilter> wrapper =
         new SearchResultWrapper<IngestionFilter>(elements.size(), elements);

      return JeevesJsonWrapper.send(wrapper);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#addFilter(org.jdom.Element)
    */
   @Override
   protected Element addFilter(final Element params) {
      Element result = getResult(REQUEST_ADD_FILTER);
      
      IngestionFilter filter = this.getFilter(params);
      if (filter == null) {
         setResultError(result, "Invalid filter passed [null]");                     
         return result;
      }
      
      result.setAttribute(RESPONSE_ATTRIB_FILTER, filter2String(filter));
      
      ControlService service = ManagementServiceProvider.getControlService();
      if (service != null) {
         Log.info(LOGMODULE, "Add ingestion filter: " + filter2String(filter));
         boolean success = 
            service.addIngestionFilter(filter.getRegex(), filter.getDescription());
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
   protected Element removeFilter(final Element params) throws Exception {
      Element result = getResult(REQUEST_REMOVE_FILTER);

      IngestionFilter filter = getFilter(params);
      if (filter == null) {
         setResultError(result, "Invalid filter passed [null]");                     
         return result;
      }
      
      result.setAttribute(RESPONSE_ATTRIB_FILTER, filter2String(filter));
      
      ControlService service = ManagementServiceProvider.getControlService();
      if (service != null) {
         Log.info(LOGMODULE, "Remove ingestion filter: " + filter2String(filter));
         boolean success = service.removeIngestionFilter(filter.getRegex());
         setResultSuccess(result, success);
      }      
      else {
         Log.error(LOGMODULE, "No control service available");
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
         result = setServiceStatus(ManagedServiceIdentifier.INGESTION_SERVICE, status);
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
      return getServiceStatus(ManagedServiceIdentifier.INGESTION_SERVICE);
   }
   
   private IngestionFilter getFilter(final Element params) {
      IngestionFilter filter = null;
      try {
         String regex = Util.getParam(params, PARAM_FILTER_REGEX);
         String description = Util.getParam(params, PARAM_FILTER_DESCR);
         
         filter = new IngestionFilter();
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
      IngestionFilter f = (IngestionFilter) filter;
      return "{regex:" + f.getRegex() + ",description:" + f.getDescription() + "}";
   }
}
