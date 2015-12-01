package org.openwis.usermanagement;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 *  The Monitoring Component is used for :
 *  - test if the security service is available. <P>
 * 
 */
@WebService(targetNamespace = "http://securityservice.openwis.org/", name = "MonitoringService", portName = "MonitoringServicePort", serviceName = "MonitoringService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface MonitoringService {

   /**
    * Is Security Service Available.
    * Test the security service and the connection with the LDAP.
    * @return true if the security service is available
    */
   public boolean isSecurityServiceAvailable();
}
