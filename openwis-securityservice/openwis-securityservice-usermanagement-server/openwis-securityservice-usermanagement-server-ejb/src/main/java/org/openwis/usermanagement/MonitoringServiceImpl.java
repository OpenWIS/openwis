package org.openwis.usermanagement;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Implements the monitoring component interface.
 * The Monitoring Component is used for : <P>
 * -  test if the security service is available
 */
@WebService(targetNamespace = "http://securityservice.openwis.org/", name = "MonitoringService", portName = "MonitoringServicePort", serviceName = "MonitoringService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Remote(MonitoringService.class)
@Stateless(name = "MonitoringService")
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
