/**
 * 
 */
package org.openwis.metadataportal.services.deployment;

import org.jdom.Element;
import org.openwis.metadataportal.kernel.deployment.DeploymentManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class GetAllCotDeployments implements Service {

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
      DeploymentManager dm = new DeploymentManager();
      return JeevesJsonWrapper.send(dm.getAllCotDeployments());
   }

}
