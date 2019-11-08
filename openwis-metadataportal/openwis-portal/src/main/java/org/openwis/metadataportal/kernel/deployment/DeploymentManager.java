/**
 * 
 */
package org.openwis.metadataportal.kernel.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.model.deployment.Deployment;
import org.openwis.securityservice.OpenWISUser;
import org.openwis.securityservice.UserManagementService;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DeploymentManager {

    /**
     * Default constructor.
     * Builds a DeploymentManager.
     */
    public DeploymentManager() {
        super();
    }

    /**
     * Description goes here.
     * @param userName the user name.
     * @return a list of backup centres.
     * @throws Exception if an error occurs.
     */
    @SuppressWarnings("unchecked")
    public List<Deployment> getUserBackupCentres(String userName) throws Exception {
        //Gets the list of referenced deployments from configuration file.
        Collection<String> localReferencedBackups = OpenwisDeploymentsConfig.getBackUps();
        
        //Gets the list of backup centres for the user from security service.
        OpenWISUser user = getUserManagementService().getUserInfo(userName);
        Collection<String> userBackups = user.getBackUps();

        //Compute an intersection of collections (not-referenced deployments are useless). 
        Collection<String> referencedBackups = CollectionUtils.intersection(userBackups,
                localReferencedBackups);

        //Creates a list of deployments. 
        List<Deployment> backupCentres = new ArrayList<Deployment>();
        for (String backup : referencedBackups) {
            backupCentres.add(getDeploymentByName(backup));
        }
        Collections.sort(backupCentres);
        return backupCentres;
    }

    /**
     * Description goes here.
     * @return
     */
    public List<Deployment> getAllCotDeployments() {
       Collection<String> deploymentNames = OpenwisDeploymentsConfig.getDeployments();
       
       //Remove local deployment.
       deploymentNames.remove(OpenwisMetadataPortalConfig
                .getString(ConfigurationConstants.DEPLOY_NAME));
       
       List<Deployment> deployments = new ArrayList<Deployment>();
       for(String s : deploymentNames) {
          deployments.add(new Deployment(s));
       }
       
       //Sort.
       Collections.sort(deployments);
       return deployments;
    }

    /**
     * Returns the local deployment.
     * @return the local deployment.
     */
    public Deployment getLocalDeployment() {
        return getDeploymentByName(OpenwisMetadataPortalConfig
                .getString(ConfigurationConstants.DEPLOY_NAME));
    }

    /**
     * Gets the deployment by its name.
     * @param name the name of the deployment.
     * @return the deployment (name and url).
     */
    public Deployment getDeploymentByName(String name) {
        String url = OpenwisDeploymentsConfig.getURLByDeploymentName(name);
        String adminMail = OpenwisDeploymentsConfig.getAdminMailByDeploymentName(name);
        return new Deployment(name, url, adminMail);
    }

    /**
     * The user management service.
     * @return the user management service.
     */
    public UserManagementService getUserManagementService() {
        return SecurityServiceProvider.getUserManagementService();
    }
    
    /**
    * Get All backup Centres
    * @return all backup centres.
    */
   public List<Deployment> getAllBackupCentres() {
         Collection<String> deploymentNames = OpenwisDeploymentsConfig.getBackUps();

       List<Deployment> deployments = new ArrayList<Deployment>();
       for(String s : deploymentNames) {
          deployments.add(new Deployment(s));
       }
       
       //Sort.
       Collections.sort(deployments);
       return deployments;
    }
}
