/**
 * 
 */
package org.openwis.usermanagement;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

import org.openwis.securityservice.GroupManagementService;
import org.openwis.securityservice.GroupManagementService_Service;
import org.openwis.securityservice.UserManagementService;
import org.openwis.securityservice.UserManagementService_Service;

/**
 * Service Provider Class.
 * 
 */
public final class ServiceProvider {

   private static String userWsdl = "http://localhost:8080/openwis-securityservice/services/UserManagementService?wsdl";

   private static String groupWsdl = "http://localhost:8080/openwis-securityservice/services/GroupManagementService?wsdl";

   public static final String centreName = "DcpcDemo";

   private static UserManagementService userManagementService;

   private static GroupManagementService groupManagementService;

   /**
    * Default constructor.
    * Builds a ServiceProvider.
    */
   private ServiceProvider() {

   }

   /**
    * Gets the User Management Service.
    * @return the User Management Service.
    */
   public static UserManagementService getUserManagementSrv() {
      try {
         if (userManagementService == null) {
            if (System.getProperty("userManagementServiceWsdl") != null) {
               userWsdl = System.getProperty("userManagementServiceWsdl");
            }
            UserManagementService_Service service = new UserManagementService_Service(new URL(
                  userWsdl));
            userManagementService = service.getUserManagementServicePort();
            ((BindingProvider) userManagementService).getRequestContext().put(
                  BindingProvider.ENDPOINT_ADDRESS_PROPERTY, userWsdl.replace("?wsdl", ""));
         }
         return userManagementService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Gets the Group Management Service.
    * @return the Group Management Service.
    */
   public static GroupManagementService getGroupManagementSrv() {
      try {
         if (groupManagementService == null) {
            if (System.getProperty("groupManagementServiceWsdl") != null) {
               groupWsdl = System.getProperty("groupManagementServiceWsdl");
            }
            GroupManagementService_Service service = new GroupManagementService_Service(new URL(
                  groupWsdl));
            groupManagementService = service.getGroupManagementServicePort();
            ((BindingProvider) groupManagementService).getRequestContext().put(
                  BindingProvider.ENDPOINT_ADDRESS_PROPERTY, groupWsdl);
            ((BindingProvider) groupManagementService).getRequestContext().put(
                  BindingProvider.ENDPOINT_ADDRESS_PROPERTY, groupWsdl.replace("?wsdl", ""));
         }
         return groupManagementService;
      } catch (MalformedURLException e) {
         return null;
      }
   }
}
