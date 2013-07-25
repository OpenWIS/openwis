package org.openwis.usermanagement;

/**
 *  The Monitoring Component is used for :
 *  - test if the security service is available. <P>
 * 
 */
public interface MonitoringService {

   /**
    * Is Security Service Available.
    * Test the security service and the connection with the LDAP.
    * @return true if the security service is available
    */
   public boolean isSecurityServiceAvailable();
}
