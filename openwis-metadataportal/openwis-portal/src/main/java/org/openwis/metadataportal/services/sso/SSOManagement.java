/**
 * 
 */
package org.openwis.metadataportal.services.sso;

import org.jdom.Element;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

/**
 * SSO Management service. <P>
 * Return OpenAM link. <P>
 */
public class SSOManagement implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      String sso = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SSO_MANAGEMENT);
      return JeevesJsonWrapper.send(sso);
   }

}
