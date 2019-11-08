package org.openwis.usermanagement;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

import org.openwis.management.alert.AlertService;
import org.openwis.management.alert.AlertService_Service;
import org.openwis.management.utils.ServiceProviderUtil;
import org.openwis.usermanagement.util.JNDIConnectionUtils;

/**
 * Provides access to the remote WS providing support for the management of the OpenWIS system.
 * <p>
 * Explanation goes here.
 */
public final class ManagementServiceProvider {
   
   // -------------------------------------------------------------------------
   // Management Client Settings
   // -------------------------------------------------------------------------
   private static final String MANAGEMENT_ALERTSERVICE_WSDL = "openwis.management.alertservice.wsdl";
  
   // Management services
   private static AlertService alertService;

   /**
    * Default constructor.
    * Builds a ManagementServiceProvider.
    */
   private ManagementServiceProvider() {
   }

   /**
    * Returns the shared instance of the {@code AlertService}.
    *
    * @return the {@code AlertService}.
    */
   public static AlertService getAlertService() {
      try {
         if (alertService == null) {
            JNDIConnectionUtils ldapDataSourceConnection = new JNDIConnectionUtils();
            String wsdl = ldapDataSourceConnection.getString(MANAGEMENT_ALERTSERVICE_WSDL);
            AlertService_Service service = new AlertService_Service(new URL(wsdl));
            alertService = service.getAlertServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) alertService, wsdl);
         }
         return alertService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

}