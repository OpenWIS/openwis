package org.openwis.usermanagement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openwis.securityservice.OpenWISGroup;
import org.openwis.securityservice.OpenWISUser;
import org.openwis.securityservice.UserManagementException_Exception;
import org.openwis.usermanagement.ldap.model.UserAndGroup;

import com.thoughtworks.xstream.XStream;

/**
 * Populate Users and Groups. <P>
 * Possible properties:
 * <ul>
 * <li>userManagementServiceWsdl</li>
 * <li>groupManagementServiceWsdl</li>
 * </ul>
 */
public class PopulateUser {

   /**
    * Display menu.
    */
   public void displayMenu() {

      System.out.println("------ Populate LDAP ------ ");
      System.out.println("[1] Initialize Deployment");
      System.out.println("[2] Populate LDAP");
      System.out.println("[3] Reset LDAP Users");
      System.out.println("[4] Reset LDAP Groups");
      System.out.println("[5] Reset LDAP Users And Groups");
      System.out.println("Choose one of the above values. (any other value to exit)");

      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         String choice = br.readLine();
         int value = Integer.parseInt(choice);

         switch (value) {
         case 1:
            initializeDeployment();
            break;
         case 2:
            populate();
            break;
         case 3:
            resetUsers();
            break;
         case 4:
            resetGroups();
            break;
         case 5:
            resetUsers();
            resetGroups();
            break;

         default:
            break;
         }
      } catch (NumberFormatException e) {
         // ignore and exit
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   /**
    * Reset Users.
    */
   public void resetUsers() {
      try {
         ServiceProvider.getUserManagementSrv().resetUsers();
      } catch (UserManagementException_Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Reset Groups.
    */
   public void resetGroups() {
      try {
         ServiceProvider.getGroupManagementSrv().resetGroups();
      } catch (UserManagementException_Exception e) {
         e.printStackTrace();
      }
   }
   
   /**
    * Initialize Deployment.
    * @throws IOException if an error occurs.
    */
   public void initializeDeployment() throws IOException {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Initialize Deployment:");
      
      // Initialize on security service
      try {
         
         System.out.println("Deployment Name:");
         String deploymentName = br.readLine();
         while (deploymentName.isEmpty()) {
            System.err.println("This deployment name is empty...");
            System.out.println("Please, re-enter the deployment name:");
            deploymentName = br.readLine();
         }
         
         System.out.println("Administrator Login:");
         String adminLogin = br.readLine();
         
         while (adminLogin.isEmpty() || ServiceProvider.getUserManagementSrv().checkUserNameExists(adminLogin)) {
            System.err.println("This user name is already used or is empty...");
            System.out.println("Please, re-enter the Administrator Login:");
            adminLogin = br.readLine();
         }
         System.out.println("Administrator Password:");
         String adminPassword = br.readLine();
         
         while (adminPassword.isEmpty()) {
            System.err.println("The password is empty...");
            System.out.println("Please, re-enter the Administrator Password:");
            adminPassword = br.readLine();
         }
         
         System.out.println("Administrator Email:");
         String adminEmail = br.readLine();
         
         while (adminEmail.isEmpty()) {
            System.err.println("The email is empty...");
            System.out.println("Please, re-enter the Administrator Email:");
            adminEmail = br.readLine();
         }
         
         System.out.println("Administrator First name:");
         String adminFirstName = br.readLine();
         
         while (adminFirstName.isEmpty()) {
            System.err.println("The First Name is empty...");
            System.out.println("Please, re-enter the First Name:");
            adminFirstName = br.readLine();
         }
         
         System.out.println("Administrator Last name:");
         String adminLastName = br.readLine();
         
         while (adminLastName.isEmpty()) {
            System.err.println("The Last Name is empty...");
            System.out.println("Please, re-enter the Last Name:");
            adminLastName = br.readLine();
         }
         
         ServiceProvider.getUserManagementSrv().initialize(adminLogin, adminPassword, adminEmail, deploymentName, adminFirstName, adminLastName);
      } catch (UserManagementException_Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Populate LDAP
    * @param file The file to load LDAP Data
    * @param centreName The centre name
    */
   public void populate(String file, String centreName) {
      System.out.println("Parse usersAndgroups.xml File");
      XStream xStream = new XStream();

      UserAndGroup userAndGroup;
      try {
         userAndGroup = (UserAndGroup) xStream.fromXML(new FileInputStream(file));
         for (OpenWISGroup openWISGroup : userAndGroup.getGroups()) {
            System.out.println("Add group centre : " + openWISGroup.getCentreName());
            try {
               if (openWISGroup.isIsGlobal()) {
                  ServiceProvider.getGroupManagementSrv().createGlobalGroup();
               } else {
                  ServiceProvider.getGroupManagementSrv().createLocalGroup(
                        openWISGroup.getCentreName());
               }

            } catch (UserManagementException_Exception e) {
               if (!e.getMessage().equals("Entry Already Exists")) {
                  e.printStackTrace();
               }
            }

            try {
               for (String groupId : openWISGroup.getGroupIds()) {
                  System.out.println("Add group : " + groupId);
                  if (openWISGroup.isIsGlobal()) {
                     ServiceProvider.getGroupManagementSrv().createGlobalGroupId(groupId);
                  } else {
                     ServiceProvider.getGroupManagementSrv().createLocalGroupId(
                           openWISGroup.getCentreName(), groupId);
                  }

               }
            } catch (UserManagementException_Exception e) {
               if (!e.getMessage().equals("Entry Already Exists")) {
                  e.printStackTrace();
               }
            }
         }

         for (OpenWISUser openWISUser : userAndGroup.getUsers()) {
            System.out.println("Add user : " + openWISUser.getUserName());
            try {
               ServiceProvider.getUserManagementSrv().createUser(openWISUser, centreName);
            } catch (UserManagementException_Exception e) {
               if (!e.getMessage().equals("Entry Already Exists")) {
                  e.printStackTrace();
               }
            }
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

   }

   /**
    * Populate LDAP
    */
   public void populate() {
      populate("userAndGroup.xml", ServiceProvider.centreName);
   }

   /**
    * Populate or Reset LDAP.
    * @param args Args
    */
   public static void main(String[] args) {
      PopulateUser populateUser = new PopulateUser();
      populateUser.displayMenu();
   }
}
