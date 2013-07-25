/**
 * 
 */
package org.openwis.metadataportal.services.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.constants.Jeeves;
import jeeves.interfaces.Service;
import jeeves.interfaces.ServiceWithJsp;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ProfileManager;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.apache.commons.collections.CollectionUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.kernel.availability.IAvailabilityManager;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.services.mock.MockGetDisseminationParameters;
import org.openwis.metadataportal.services.mock.MockMode;

/**
 * Get service. <P>
 * Get all services list + add service if MSS FSS service is allowed.
 */
public class Get implements Service, ServiceWithJsp {
   
   /**
    * True if the portal is the portal user.
    * @member: isUserPortal
    */
   private boolean isUserPortal;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   public void init(String appPath, ServiceConfig params) throws Exception {
      isUserPortal = "user".equals(params.getValue("portal"));
   }

   //--------------------------------------------------------------------------
   //---
   //--- Service
   //---
   //--------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   public Element exec(Element params, ServiceContext context) throws Exception {
      String profile = ProfileManager.GUEST;

      if (context.getUserSession().isAuthenticated()) {
         profile = context.getUserSession().getProfile();
      }
      
      Element servicesElement = context.getProfileManager().getAccessibleServices(profile);
      
      if (isUserPortal) {
       //Compute operations allowed.
         boolean isMssFssSupported = OpenwisMetadataPortalConfig
               .getBoolean(ConfigurationConstants.MSSFSS_SUPPORT);
         List<String> channels = null;
         if (isMssFssSupported) {
            if (MockMode.isMockModeHarnessMSSFSS()) {
               channels = MockGetDisseminationParameters.getMSSFSSChannelsMock();
            } else {
               MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
               channels = mssFssService.getChannelsForUser(context.getUserSession().getUsername());
            }
         }

         boolean isAllowedMSSFSS = isMssFssSupported && CollectionUtils.isNotEmpty(channels);
         if (isAllowedMSSFSS) {
         // Add allowedMSSFSS service to the service list.
            Element allowedMSSFSS = new Element(Jeeves.Elem.SERVICE);
            allowedMSSFSS.setAttribute(Jeeves.Attr.NAME, "allowedMSSFSS");
            servicesElement.addContent(allowedMSSFSS);
         }
      }
         
      return servicesElement;
   }
   
   public Map<String, Object> execWithJsp(Element params, ServiceContext context) throws Exception {
      Element servicesElement = exec(params, context);
      ArrayList<String> services = new ArrayList<String>();
      for (Object element : servicesElement.getChildren()) {
         Element el = (Element) element;
         String value = el.getAttributeValue(Jeeves.Attr.NAME);
         services.add(value);
      }
      
      Map<String, Object> attrMap = new HashMap<String, Object>();
      attrMap.put("availableServices", services);
      
      if (isUserPortal) {
         Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         IAvailabilityManager availabilityManager = new AvailabilityManager(dbms);
         boolean isUserPortalEnabled = availabilityManager.isUserPortalEnable();
         attrMap.put("isUserPortalEnabled", isUserPortalEnabled);
      }
      
      return attrMap;
   }
}
