package org.openwis.metadataportal.services.util;

import jeeves.exceptions.BadInputEx;
import jeeves.utils.Util;

import org.jdom.Element;

/**
 * Wrapper to access the transmitted parameter of a <code>Service</code>. <P>
 * Provides access to the parameter
 * 'start' ... start index for the search result
 * 'limit' ... number of row to be returned for the search result
 * 
 */
public class ServiceParameter {
   
   private static final String PARAM_START = "start";
   private static final String PARAM_LIMIT = "limit";
   
   private Element serviceParams;

   /**
    * Default constructor.
    * Builds a ParameterUtils.
    * @param params contains all transmitted parameters of the service
    */
   public ServiceParameter(final Element params) {
      serviceParams = params;
   }
   
   /**
    * Gets the value for the 'start' parameter (if any).
    * @return integer
    */
   public int getStart() {
      return getStart(getParams());
   }
   
   /**
    * Gets the value for the 'start' parameter (if any).
    * @param params service parameter
    * @return integer
    */
   private int getStart(final Element params) {
      return getIntParam(params, PARAM_START, 0);
   }
   
   /**
    * Gets the value for the 'limit' parameter (if any).
    * @return integer
    */
   public int getLimit() {
      return getLimit(getParams());
   }

   /**
    * Gets the value for the 'limit' parameter (if any).
    * @param params service parameter
    * @return integer
    */
   private int getLimit(final Element params) {
      return getIntParam(params, PARAM_LIMIT, 25);
   }
   
   /**
    * Checks the existence of the <code>PARAM_LIMIT</code>.
    * @return true/false;
    */
   public boolean hasLimit() {
      return hasParam(PARAM_LIMIT);
   }

   /**
    * Checks the existence of the <code>PARAM_START</code>.
    * @return true/false;
    */
   public boolean hasStart() {
      return hasParam(PARAM_START);
   }
   
   protected Element getParams() {
      return serviceParams;
   }
   
   protected boolean hasParam(final String paramName) {
      boolean hasParameter = false;
      if (getParams() != null) {
         try {
            Util.getParam(getParams(), paramName);
            hasParameter = true;
         }
         catch (BadInputEx ex) {                     
         }
      }
      return hasParameter;      
   }
   
   protected int getIntParam(final Element params, final String name, final int defaultValue) {
      int value = 0;
      
      if (params != null) {
         try {
            value = Util.getParamAsInt(params, name);
         }
         catch (BadInputEx ex) {
            value = defaultValue;
         }
      }
      return value;      
   }
}
