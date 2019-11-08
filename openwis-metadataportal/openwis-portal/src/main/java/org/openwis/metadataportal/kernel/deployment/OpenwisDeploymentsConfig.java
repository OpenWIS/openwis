/**
 * 
 */
package org.openwis.metadataportal.kernel.deployment;

import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * The configuration. <P>
 * This utility class is used to access the properties in the external properties file. <P>
 * 
 */
public final class OpenwisDeploymentsConfig {

    private static final String DEPLOYMENTS = "openwis.cots";

    private static final String DEPLOYMENT_URL = "openwis.deployment.url.";
    
    private static final String BACKUPS= "openwis.backups";
    
    private static final String DEPLOYMENT_ADMIN = ".admin";

    private static final String BACKUP_WARN_RATE = "openwis.backup.warn.rate";
    
    /**
     * The resource bundle.
     */
    private static ResourceBundle ressourceBundle = ResourceBundle.getBundle("openwis-deployments");

    /**
     * Default constructor.
     * Builds a OpenwisMetadataPortalConfig.
     */
    private OpenwisDeploymentsConfig() {
        super();
    }

    /**
     * Get the value for the given key. Try first in system properties then in vod resource bundle
     * @param key the key.
     * @return the String value.
     */
    public static String getString(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = ressourceBundle.getString(key);
        }
        return value;
    }
    
    /**
    * Get the backup warn rate : the rate of available function (in %) bellow which we consider that the deployment is in error
    * @return the backup warn rate
    */
   public static double getBackupWarnRate() {
       return Double.valueOf(getString(BACKUP_WARN_RATE));
    }

    /**
     * Gets the list of deployments.
     * @return a list of logical names referenced.
     */
    public static Collection<String> getDeployments() {
        String deployments = getString(DEPLOYMENTS);
        StringTokenizer st = new StringTokenizer(deployments, ",");
        Collection<String> deploymentNames = new HashSet<String>();
        while (st.hasMoreTokens()) {
            deploymentNames.add(st.nextToken());
        }
        return deploymentNames;
    }

    /**
     * Gets the URL of the given deployment.
     * @param deploymentName the logical name of the deployment.
     * @return the URL of the deployment.
     */
    public static String getURLByDeploymentName(String deploymentName) {
        return getString(DEPLOYMENT_URL + deploymentName);
    }
    
    /**
     * Gets the admin contact email of the given deployment.
     * @param deploymentName the logical name of the deployment.
     * @return the admin contact email of the deployment.
    */
   public static String getAdminMailByDeploymentName(String deploymentName) {
        return getString(DEPLOYMENT_URL + deploymentName + DEPLOYMENT_ADMIN);
    }
    
    /**
     * Gets the list of backups.
     * @return a list of logical names referenced.
     */
    public static Collection<String> getBackUps() {
        String backups = getString(BACKUPS);
        StringTokenizer st = new StringTokenizer(backups, ",");
        Collection<String> deploymentNames = new HashSet<String>();
        while (st.hasMoreTokens()) {
            deploymentNames.add(st.nextToken());
        }
        return deploymentNames;
    }
}
