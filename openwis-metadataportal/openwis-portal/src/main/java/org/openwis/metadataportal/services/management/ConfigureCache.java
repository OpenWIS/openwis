/**
 *
 */
package org.openwis.metadataportal.services.management;

import org.jdom.Element;
import org.openwis.management.control.ControlService;
import org.openwis.management.control.ManagedServiceIdentifier;
import org.openwis.management.control.ManagedServiceStatus;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import jeeves.utils.Log;


/**
 * Abstract <code>Service</code> class. Base class for all <code>ConfigureCache</code> service classes. <P>
 * Explanation goes here. <P>
 */
abstract public class ConfigureCache implements Service {
   
   protected static final String REQUEST_ADD_FILTER = "ADD_FILTER";
   protected static final String REQUEST_REMOVE_FILTER = "REMOVE_FILTER";
   protected static final String REQUEST_SET_SERVICE_STATUS = "SET_SERVICE_STATUS";
   protected static final String REQUEST_GET_SERVICE_STATUS = "GET_SERVICE_STATUS";
   
   protected static final String PARAM_REQUEST_TYPE = "requestType";
   protected static final String PARAM_FILTER_REGEX = "regex";
   protected static final String PARAM_FILTER_DESCR = "description";
   protected static final String PARAM_SERVICE_STATUS = "checked";
   
   protected static final String RESPONSE_NAME = "result"; 
   protected static final String RESPONSE_ATTRIB_REQUESTID = "requestID"; 
   protected static final String RESPONSE_ATTRIB_SUCCESS = "success"; 
   protected static final String RESPONSE_ATTRIB_ERROR = "error"; 
   protected static final String RESPONSE_ATTRIB_FILTER = "filter"; 
   protected static final String RESPONSE_ATTRIB_NEW_STATUS = "new_status"; 
   protected static final String RESPONSE_ATTRIB_CURRENT_STATUS = "status"; 
   protected static final String RESPONSE_ATTRIB_TARGET = "target"; 
   
   protected static String LOGMODULE = "openwis.service.management";

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
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
         Log.info(LOGMODULE, "Request service type: " + requestType);
         
         if (requestType.equals(REQUEST_ADD_FILTER)) {
            result = addFilter(params);
         }
         else if (requestType.equals(REQUEST_REMOVE_FILTER)) {
            result = removeFilter(params);            
         }
         else if (requestType.equals(REQUEST_SET_SERVICE_STATUS)) {
            result = setServiceStatus(params);
         }
         else if (requestType.equals(REQUEST_GET_SERVICE_STATUS)) {
            result = getServiceStatus(params);
         }
         else {
            result = null;
         }
      }
      else {
         result = getFilters(params);
      }
      return result;
   }

   /**
    * Retrieves the desires filters from the <code>ControlService</code>
    * @param params parameters passed from the JS
    * @return
    * @throws Exception
    */
   protected abstract Element getFilters(final Element params) throws Exception;
   
   /**
    * Invokes the adding of a filter via the the <code>ControlService</code>
    * @param params contains filter specifications
    * @return
    */
   protected abstract Element addFilter(final Element params);

   /**
    * Invokes the deleting of a filter via the the <code>ControlService</code>
    * @param params contains filter specifications
    * @return
    * @throws Exception
    */
   protected abstract Element removeFilter(final Element params) throws Exception;

   /**
    * Invokes the switching of the filter status
    * @param params contains new filter status
    * @return
    */
   protected abstract Element setServiceStatus(final Element params);

   /**
    * Invokes the query of the current filter status
    * @param params
    * @return
    */
   protected abstract Element getServiceStatus(final Element params);
   
   /**
    * Creates a XML result object used to provide for the calling JS.
    * Sets the request identification as an response attribute
    * @param requestID request identification
    * @return <code>Element</code>
    */
   protected Element getResult(final String requestID) {
      Element result = new Element(RESPONSE_NAME);
      result.setAttribute(RESPONSE_ATTRIB_REQUESTID, requestID);
      return result;
   }
   
   /**
    * Sets the error attribute to the result object.
    * @param result result XML object
    * @param msg error message
    */
   protected void setResultError(final Element result, final String msg) {
      if (result != null) {
         result.setAttribute(RESPONSE_ATTRIB_ERROR, msg);                              
         Log.error(LOGMODULE, msg);
      }
   }
   
   /**
    * Sets the success attribute to the result object.
    * @param result result XML object
    * @param status result status
    */
   protected void setResultSuccess(final Element result, final boolean status) {
      if (result != null) {
         result.setAttribute(RESPONSE_ATTRIB_SUCCESS, String.valueOf(status));                              
      }
   }
   
   
   /**
    * Gets the status for a given service.
    * @param serviceId service identifier
    * @return response element
    */
   protected Element getServiceStatus(final ManagedServiceIdentifier serviceId) {
      Element result = getResult(REQUEST_GET_SERVICE_STATUS);
      
      result.setAttribute(RESPONSE_ATTRIB_TARGET, serviceId.name());
      
      ControlService service = ManagementServiceProvider.getControlService();
      if (service != null) {
         Log.info(LOGMODULE, "Get service status for " + serviceId);
         String serviceStatus = service.getServiceStatus(serviceId);         
         Log.info(LOGMODULE, "Service status for " + serviceId + " is " + serviceStatus);
         
         boolean success = serviceStatus != null;
         setResultSuccess(result, success);
         
         if (success) {
            result.setAttribute(RESPONSE_ATTRIB_CURRENT_STATUS, serviceStatus);
         }
         else {
            setResultError(result, "Could not get status from control service");                     
         }
      }
      else {
         setResultError(result, "No control service available");                     
      }
      return result;
   }
   
   /**
    * Sets the status for a given service.
    * @param serviceId service identifier
    * @return response element
    * @return
    */
   protected Element setServiceStatus(final ManagedServiceIdentifier serviceId, final boolean status) {
      Element result = getResult(REQUEST_SET_SERVICE_STATUS);

      result.setAttribute(RESPONSE_ATTRIB_NEW_STATUS, String.valueOf(status));
      result.setAttribute(RESPONSE_ATTRIB_TARGET, serviceId.name());
      
      ControlService service = ManagementServiceProvider.getControlService();
      
      if (service != null) {
         ManagedServiceStatus serviceStatus = status ? ManagedServiceStatus.ENABLED : ManagedServiceStatus.DISABLED; 
         Log.info(LOGMODULE, "Set service status for " + serviceId + ": " + status);
         
         boolean success = service.setServiceStatus(serviceId, serviceStatus);
         setResultSuccess(result, success);
      }
      else {
         setResultError(result, "No control service available");                     
      }
      return result;
   }
   
   protected abstract String filter2String(final Object filter);
}
