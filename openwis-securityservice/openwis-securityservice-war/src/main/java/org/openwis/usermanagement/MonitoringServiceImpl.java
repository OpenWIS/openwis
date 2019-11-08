package org.openwis.usermanagement;

import javax.jws.WebService;

/**
 * Implements the monitoring component interface.
 * The Monitoring Component is used for : <P>
 * -  test if the security service is available
 */
@WebService(endpointInterface = "org.openwis.usermanagement.MonitoringService", targetNamespace = "http://securityservice.openwis.org/", portName = "MonitoringServicePort", serviceName = "MonitoringService")
public class MonitoringServiceImpl implements MonitoringService {

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.MonitoringService#isSecurityServiceAvailable()
    */
   @Override
   public boolean isSecurityServiceAvailable() {
      boolean result = true;
      try {
      // Test LDAP Connection
         UtilEntry.createLDAPConnection();
      } catch (Exception e) {
         result = false;
      }
      return result;
   }

}
