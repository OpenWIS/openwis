/**
 * 
 */
package org.openwis.metadataportal.services.deployment;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.metadataportal.kernel.deployment.DeploymentManager;
import org.openwis.metadataportal.model.deployment.Deployment;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Get All Backup Centres. <P>
 * 
 */
public class GetAllBackupCentres implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
       DeploymentManager deploymentManager = new DeploymentManager();
       List<Deployment> userBackupCentres = deploymentManager.getAllBackupCentres();

       return JeevesJsonWrapper.send(userBackupCentres);
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String arg0, ServiceConfig arg1) throws Exception {

   }

}
