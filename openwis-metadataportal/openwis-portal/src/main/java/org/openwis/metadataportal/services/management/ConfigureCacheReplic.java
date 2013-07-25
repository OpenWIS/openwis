/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.util.List;

import jeeves.exceptions.BadInputEx;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.management.control.ControlService;
import org.openwis.management.control.ManagedServiceIdentifier;
import org.openwis.management.control.ReplicationFilter;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockConfigurationFilters;
import org.openwis.metadataportal.services.mock.MockMode;

/**
 * Extension of <code>ConfigureCache</code> to establish a service for the configure cache replication filter. <P>
 * Explanation goes here. <P>
 */
public class ConfigureCacheReplic extends ConfigureCache {
   private static final String PARAM_FILTER_SOURCE = "source";
   private static final String PARAM_FILTER_TYPE = "type";
   private static final String PARAM_FILTER_ACTIVE = "active";
   private static final String PARAM_FILTER_STATUS = "checked";

   private static final String PARAM_FILTER_EDIT_SOURCE = "editSource";
   private static final String PARAM_FILTER_EDIT_REGEX = "editRegex";
   
   private static final String REQUEST_SET_FILTER_STATUS = "SET_FILTER_STATUS";
   private static final String REQUEST_UPDATE_FILTER = "UPDATE_FILTER";

   /**
    * {@inheritDoc}
    */
   @Override
   public void init(final String appPath, final ServiceConfig params) throws Exception {
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(final Element params, final ServiceContext context) throws Exception {
      Element result = null;
      String requestType = Util.getParam(params, PARAM_REQUEST_TYPE, null);
      
      if (requestType != null) {         
         if (requestType.equals(REQUEST_SET_FILTER_STATUS)) {
            Log.info(LOGMODULE, "Request service type: " + requestType);
            result = setFilterStatus(params);
         }
         else if (requestType.equals(REQUEST_UPDATE_FILTER)) {
            Log.info(LOGMODULE, "Request service type: " + requestType);
            result = updateFilter(params);
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
   protected Element getFilters(final Element params) throws Exception {
      List<ReplicationFilter> elements = null;
      // Test mode
      if (MockMode.isMockModeControlService()) {
         elements = MockConfigurationFilters.getReplicationFilters();
      }
      // Operation mode
      else {
         // Delegate to the MC service
         ControlService service = ManagementServiceProvider.getControlService();
         if (service != null) {
            // query...
            elements = service.getReplicationFilters();
         }
      }
      SearchResultWrapper<ReplicationFilter> wrapper =
         new SearchResultWrapper<ReplicationFilter>(elements.size(), elements);

      return JeevesJsonWrapper.send(wrapper);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#addFilter(org.jdom.Element)
    */
   @Override
   protected Element addFilter(final Element params) {
      Element result = getResult(REQUEST_ADD_FILTER);
      
      ReplicationFilter filter = this.getFilter(params);
      if (filter == null) {
         setResultError(result, "Invalid filter passed [null]");                     
         return result;
      }
      
      result.setAttribute(RESPONSE_ATTRIB_FILTER, filter2String(filter));

      ControlService service = ManagementServiceProvider.getControlService();
      if (service != null) {
         Log.info(LOGMODULE, "Add replication filter: " + filter2String(filter));
         boolean success = service.addReplicationFilter(filter.getType(),
                                                        filter.getSource(),
                                                        filter.getRegex(), 
                                                        filter.getDescription(),                                                        
                                                        filter.isActive());
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

      ReplicationFilter filter = this.getFilter(params);
      if (filter == null) {
         setResultError(result, "Invalid filter passed [null]");                     
         return result;
      }
      
      result.setAttribute(RESPONSE_ATTRIB_FILTER, filter2String(filter));
      ControlService service = ManagementServiceProvider.getControlService();
      if (service != null) {
         Log.info(LOGMODULE, "Remove replication filter: " + filter2String(filter));
         boolean success = service.removeReplicationFilter(filter.getSource(), filter.getRegex());         
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
         result = setServiceStatus(ManagedServiceIdentifier.REPLICATION_SERVICE, status);
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
      return getServiceStatus(ManagedServiceIdentifier.REPLICATION_SERVICE);
   }

   /**
    * Request the setting of the activation status of a given replication filter. 
    * @param params
    * @return
    */
   private Element setFilterStatus(final Element params) {
      Element result = getResult(REQUEST_SET_FILTER_STATUS);

      ReplicationFilter filter = this.getFilter(params);
      if (filter == null) {
         setResultError(result, "Invalid filter passed [null]");                     
         return result;
      }
      
      ControlService service = ManagementServiceProvider.getControlService();
      if (service != null) {
         boolean newStatus = false;
         
         try {
            String checkedValue = Util.getParam(params, PARAM_FILTER_STATUS);
            newStatus = Boolean.valueOf(checkedValue);
         }
         catch (BadInputEx ex) {
            Log.error(LOGMODULE, "Invalid filter parameters " + ex.toString());
            newStatus = !filter.isActive();
         }
         
         // newStatus = !filter.isActive();
         boolean success = 
            service.setReplicationFilterStatus(filter.getSource(), 
                                               filter.getRegex(), 
                                               newStatus);
         setResultSuccess(result, success);
         result.setAttribute(RESPONSE_ATTRIB_NEW_STATUS, String.valueOf(newStatus));
      }
      else {
         setResultError(result, "No control service available");                     
      }
      return result;
   }
   
   private ReplicationFilter getFilter(final Element params) {
      ReplicationFilter filter = null;
      try {
         String regex = Util.getParam(params, PARAM_FILTER_REGEX);
         String description = Util.getParam(params, PARAM_FILTER_DESCR);
         String source = Util.getParam(params, PARAM_FILTER_SOURCE);
         String type = Util.getParam(params, PARAM_FILTER_TYPE, null);
         boolean active = Util.getParam(params, PARAM_FILTER_ACTIVE, false);

         filter = new ReplicationFilter();
         filter.setRegex(regex);
         filter.setDescription(description);
         filter.setActive(active);
         filter.setSource(source);
         filter.setType(type);
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
      ReplicationFilter f = (ReplicationFilter) filter;
      return "{active:" + f.isActive() + 
             ",source:" + f.getSource() + 
             ",type:" + f.getType() + 
             ",regex:" + f.getRegex() + 
             ",description:" + f.getDescription() + "}";
   }
   
   /**
    * Updates an existing replication filter.
    * @param params service parameter
    * @return XML result
    */
   private Element updateFilter(final Element params) {
      Element result = getResult(REQUEST_UPDATE_FILTER);

      ReplicationFilter filter = this.getFilter(params);
      if (filter == null) {
         setResultError(result, "Invalid filter passed [null]");                     
         return result;
      }
      String editSource = null;
      String editRegex = null;
      try {
         editSource = Util.getParam(params, PARAM_FILTER_EDIT_SOURCE);
         editRegex = Util.getParam(params, PARAM_FILTER_EDIT_REGEX);
      }
      catch (BadInputEx ex) {
         setResultError(result, "Invalid filter passed [" + ex.toString() + "]");                     
         return result;
      }
      
      ControlService service = ManagementServiceProvider.getControlService();
      if (service != null) {
         Log.info(LOGMODULE, "Update replication filter: " + filter2String(filter));
         
         boolean success = service.editReplicationFilter(editSource,
                                                         editRegex,
                                                         filter.getType(),
                                                         filter.getSource(),
                                                         filter.getRegex(), 
                                                         filter.getDescription(),                                                        
                                                         filter.isActive());
         setResultSuccess(result, success);
      }
      else {
         setResultError(result, "No control service available");                              
      }
      return result;
   }
}
