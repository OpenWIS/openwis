package org.openwis.metadataportal.services.management;

import jeeves.exceptions.BadInputEx;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.management.control.ManagedServiceIdentifier;

/**
 * Extension of <code>ConfigureCache</code> to establish a service for the configure cache dissemination. <P>
 */
public class ConfigureCacheDiss extends ConfigureCache {

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#setServiceStatus(org.jdom.Element)
    */
   @Override
   protected Element setServiceStatus(final Element params) {
      Element result = null;
      try {
         String checkedValue = Util.getParam(params, PARAM_SERVICE_STATUS);
         boolean status = Boolean.valueOf(checkedValue);
         result = setServiceStatus(ManagedServiceIdentifier.DISSEMINATION_SERVICE, status);
      }      
      catch (BadInputEx ex) {         
         result = getResult(REQUEST_SET_SERVICE_STATUS);
         setResultError(result, ex.getMessage());                     
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#getServiceStatus(org.jdom.Element)
    */
   @Override
   protected Element getServiceStatus(final Element params) {
      return getServiceStatus(ManagedServiceIdentifier.DISSEMINATION_SERVICE);
   }

   /**
    * Not used
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#filter2String(java.lang.Object)
    */
   @Override
   protected String filter2String(Object filter) {
      return null;
   }

   /**
    * Not used
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#getFilters(org.jdom.Element)
    */
   @Override
   protected Element getFilters(Element params) throws Exception {
      return null;
   }

   /**
    * Not used
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#addFilter(org.jdom.Element)
    */
   @Override
   protected Element addFilter(Element params) {
      return null;
   }

   /**
    * Not used
    * {@inheritDoc}
    * @see org.openwis.metadataportal.services.management.ConfigureCache#removeFilter(org.jdom.Element)
    */
   @Override
   protected Element removeFilter(Element params) throws Exception {
      return null;
   }

}
