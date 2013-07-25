package org.openwis.factorytests.admin.security;

import static org.openwis.usermanagement.util.LdapUtils.CN;
import static org.openwis.usermanagement.util.LdapUtils.CONTACT_EMAIL;
import static org.openwis.usermanagement.util.LdapUtils.EQUAL;
import static org.openwis.usermanagement.util.LdapUtils.INET_USER_STATUS;
import static org.openwis.usermanagement.util.LdapUtils.INET_USER_STATUS_ACTIVE;
import static org.openwis.usermanagement.util.LdapUtils.NAME;
import static org.openwis.usermanagement.util.LdapUtils.NEEDUSERACCOUNT;
import static org.openwis.usermanagement.util.LdapUtils.OBJECT_CLASS;
import static org.openwis.usermanagement.util.LdapUtils.OPEN_WIS_USER;
import static org.openwis.usermanagement.util.LdapUtils.PASSWORD;
import static org.openwis.usermanagement.util.LdapUtils.PROFILE;
import static org.openwis.usermanagement.util.LdapUtils.SURNAME;
import static org.openwis.usermanagement.util.LdapUtils.UID;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import org.openwis.securityservice.OpenWISUser;
import org.openwis.securityservice.UserManagementService;
import org.openwis.usermanagement.ServiceProvider;
import org.openwis.usermanagement.util.GroupUtils;
import org.openwis.usermanagement.util.LdapUtils;
import org.openwis.usermanagement.util.UserUtils;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.controls.LDAPSortControl;
import com.novell.ldap.controls.LDAPSortKey;

public class LoadTestLdap {
   private LDAPConnection ldapConnection;

   private static String userWsdl = "http://localhost:8180/openwis-securityservice-openwis-securityservice-usermanagement-server-ejb-1.0-SNAPSHOT/UserManagementService?wsdl";

   private static String groupWsdl = "http://localhost:8180/openwis-securityservice-openwis-securityservice-usermanagement-server-ejb-1.0-SNAPSHOT/GroupManagementService?wsdl";

   private UserManagementService ums = ServiceProvider.getUserManagementSrv();

   //private GroupManagementService gms = ServiceProvider.getGroupManagementSrv();

   public void resetUsers() {
      System.out.println("Resetting users");
      try {
         ums.resetUsers();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void resetUsersLdap() throws Exception {
      initLDAPConnection();

      int searchScope = LDAPConnection.SCOPE_SUB;
      String searchFilter = OBJECT_CLASS + EQUAL + OPEN_WIS_USER;
      String[] attrs = new String[] {CN};
      LDAPSearchConstraints constraints = new LDAPSearchConstraints();
      constraints.setMaxResults(0);
      constraints.setBatchSize(0);
      LDAPSearchResults results = ldapConnection.search("ou=people,dc=opensso,dc=java,dc=net",
            searchScope, searchFilter, attrs, false, constraints);
      int i = 0;
      System.out.println("resetting users");
      while (results.hasMore()) {
         try {
            LDAPEntry result = results.next();
            ldapConnection.delete(result.getDN());
            i++;
            if (i % 50 == 0) {
               System.out.print(".");
            }
         } catch (LDAPException e) {
            e.printStackTrace();
         }
      }
      System.out.println();
   }

   public void loadUsers() throws Exception {
      //initLDAPConnection();

      System.out.println("Creating users");
      for (int i = 0; i < 1200; i++) {
         if (i % 50 == 0) {
            System.out.print(".");
         }
         OpenWISUser u = new OpenWISUser();
         u.setUserName("user" + i);
         u.setName("name" + i);
         u.setSurName("surname" + i);
         u.setEmailContact("email" + i);
         u.setProfile("Editor");
         u.setPassword("p" + i);

         try {
            ums.createUser(u, "GiscYannick");
            //createTestUser(u);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public void createTestUser(OpenWISUser user) throws Exception {
      //System.out.println("creating users");

      //initLDAPConnection();

      LDAPAttributeSet attributeSet = new LDAPAttributeSet();
      attributeSet.add(new LDAPAttribute(OBJECT_CLASS, new String[] {"OpenWisUser",
            "inetOrgPerson", "iPlanetPreferences", "inetuser",
            "iplanet-am-auth-configuration-service", "iplanet-am-managed-person",
            "iplanet-am-user-service", "sunAMAuthAccountLockout", "sunFMSAML2NameIdentifier",
            "sunFederationManagerDataStore", "sunIdentityServerLibertyPPService"}));
      attributeSet.add(new LDAPAttribute(UID, new String[] {user.getUserName()}));
      attributeSet.add(new LDAPAttribute(INET_USER_STATUS, INET_USER_STATUS_ACTIVE));
      attributeSet.add(new LDAPAttribute(CN, new String[] {user.getUserName()}));
      attributeSet.add(new LDAPAttribute(NAME, user.getName()));
      attributeSet.add(new LDAPAttribute(SURNAME, user.getSurName()));
      attributeSet.add(new LDAPAttribute(PASSWORD, user.getPassword()));
      attributeSet.add(new LDAPAttribute(CONTACT_EMAIL, user.getEmailContact()));
      attributeSet.add(new LDAPAttribute(NEEDUSERACCOUNT, Boolean.valueOf(user.isNeedUserAccount())
            .toString()));
      // profile
      attributeSet.add(new LDAPAttribute(PROFILE, user.getProfile()));

      String dn = UserUtils.getDn(user.getUserName());
      LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
      ldapConnection.add(newEntry);

      String groupdn = GroupUtils.getGroupDn("GiscYannick", "DEFAULT");

      LDAPAttribute ldapMember = new LDAPAttribute("uniqueMember", dn);
      // modifications for group and user
      LDAPModification[] modGroup = new LDAPModification[1];

      // Add modifications to modGroup
      modGroup[0] = new LDAPModification(LDAPModification.ADD, ldapMember);

      // Modify the group's attributes
      ldapConnection.modify(groupdn, modGroup);

      //ldapConnection.disconnect();
   }

   public void addUsersToGroup() {
      System.out.println("Adding users to group");
      for (int i = 0; i < 10000; i++) {
         if (i % 50 == 0) {
            System.out.print(".");
         }

         try {
            ums.addUserToLocalGroup("user" + i, "GiscA", "DEFAULT");
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   protected void initLDAPConnection() throws LDAPException, UnsupportedEncodingException {
      ldapConnection = new LDAPConnection();

      String ldapHost = System.getProperty("ldapHost");
      if (ldapHost == null || ldapHost.length() == 0) {
         ldapHost = "localhost";
      }
      String ldapPortStr = System.getProperty("ldapPort");
      if (ldapPortStr == null || ldapPortStr.length() == 0) {
         ldapPortStr = "1389";
      }
      int ldapPort = Integer.parseInt(ldapPortStr);
      String ldapUser = System.getProperty("ldapUser");
      if (ldapUser == null || ldapUser.length() == 0) {
         ldapUser = "cn=Directory Manager";
      }
      String ldapPassword = System.getProperty("ldapPassword");
      if (ldapPassword == null || ldapPassword.length() == 0) {
         ldapPassword = "toulouse";
      }

      // connect to the server
      ldapConnection.connect(ldapHost, ldapPort);

      // authenticate to the server
      ldapConnection.bind(LDAPConnection.LDAP_V3, ldapUser, ldapPassword.getBytes("UTF8"));
   }

   public void retrieveAllLocalUsers() {
      try {
         System.out.println("retrieving users");
         String entryDN = "cn=DEFAULT,ou=GiscA,ou=groups,dc=opensso,dc=java,dc=net";
         int searchScope = LDAPConnection.SCOPE_BASE;
         //Enumeration<String> memberOf = null;
         String[] attrs = {"uniqueMember"};

         initLDAPConnection();
         long start = System.currentTimeMillis();

         LDAPSortKey[] keys = new LDAPSortKey[1];
         keys[0] = new LDAPSortKey("mail");

         // Create a LDAPSortControl object - Fail if cannot sort
         LDAPSortControl sort = new LDAPSortControl(keys, true);
         // Set the Sort control to be sent as part of search request
         LDAPSearchConstraints cons = ldapConnection.getSearchConstraints();
         cons.setControls(sort);
         ldapConnection.setConstraints(cons);

         LDAPSearchResults searchResults = ldapConnection.search(entryDN, searchScope, null, attrs,
               false, cons);
         LDAPEntry ldapEntry = searchResults.next();
         LDAPAttribute ldapAttr = (LDAPAttribute) ldapEntry.getAttributeSet().iterator().next();
         System.out.println("count=" + ldapAttr.getStringValueArray().length);
         for (int i = 0; i < 10; i++) {
            System.out.println(ldapAttr.getStringValueArray()[i]);
         }
         long ellapsed = System.currentTimeMillis() - start;
         System.out.println("ellapsed time=" + ellapsed);

      } catch (Exception e) {
         e.printStackTrace();
      }

      //      if (ldapEntry != null) {
      //         LDAPAttributeSet attributeSet = ldapEntry.getAttributeSet();
      //         Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();
      //
      //         while (allAttributes.hasNext()) {
      //            LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
      //            memberOf = attribute.getStringValues();
      //            while (memberOf.hasMoreElements()) {
      //               String member = (String) memberOf.nextElement();
      //               users.add(member);
      //            }
      //         }
      //      }
   }

   public void getUsersByGroup(String centreName, String localGroup, int pageSize, int page,
         String sortColumn) {

   }

   public void getUsers() {
      try {
         initLDAPConnection();
         String entryDN = "dc=opensso,dc=java,dc=net"; //"(|(cn=user0,ou=people,dc=opensso,dc=java,dc=net)(cn=user0,ou=people,dc=opensso,dc=java,dc=net))";
         int searchScope = LDAPConnection.SCOPE_SUB;
         //String searchFilter = "(|(cn=user0)(cn=user1))";
         //String searchFilter = "(isMemberOf=cn=default,ou=gisca,ou=groups,dc=opensso,dc=java,dc=net)";
         String searchFilter = "(isMemberOf=cn=OACI,ou=GLOBAL,ou=groups,dc=opensso,dc=java,dc=net)";

         LDAPSearchResults searchResults = ldapConnection.search(entryDN, searchScope,
               searchFilter, null, false);
         searchResults.hasMore();
         System.out.println(searchResults.getCount());
         //         LDAPEntry e = searchResults.next();
         //         System.out.println(e);
         //         e = searchResults.next();
         //         System.out.println(e);
         //      System.out.println("entryDN=" + entryDN + ", searchFilter=" +searchFilter +":" +searchResults.getCount());
      } catch (Exception e) {
         e.printStackTrace();
      }
      //      
      //     
      //(|(cn=user0)(cn=user1))

   }

   public void countUsers() {
      try {
         initLDAPConnection();

         LDAPSearchConstraints constraints = new LDAPSearchConstraints();
         constraints.setBatchSize(0);
         constraints.setMaxResults(0);
         //ldapConnection.setConstraints(constraints);

         //String entryDN = GroupUtils.getGroupDn("GiscYannick", "DEFAULT");
         //int searchScope = LDAPConnection.SCOPE_BASE;
         String[] attrs = new String[] {UID};
         int searchScope = LDAPConnection.SCOPE_SUB;
         String entryDN = LdapUtils.PEOPLE;
         String groupDn = GroupUtils.getGroupDn("GiscYannick", "DEFAULT");

         String searchFilter = "(ismemberof=" + groupDn + ")";

         LDAPSearchResults searchResults = ldapConnection.search(entryDN, searchScope,
               searchFilter, attrs, false, constraints);
         searchResults.hasMore();
         //searchResults.next();
         System.out.println(searchResults.getCount());

         //String searchFilter = "(|(cn=user0)(cn=user1))";
         //String searchFilter = "(isMemberOf=cn=default,ou=gisca,ou=groups,dc=opensso,dc=java,dc=net)";
         //String searchFilter = "(isMemberOf=cn=OACI,ou=GLOBAL,ou=groups,dc=opensso,dc=java,dc=net)";

         //         LDAPSearchResults searchResults = ldapConnection.search(entryDN, searchScope,
         //               searchFilter, null, false);
         //         LDAPEntry e = searchResults.next();
         //         System.out.println(e);
         //         e = searchResults.next();
         //         System.out.println(e);
         //      System.out.println("entryDN=" + entryDN + ", searchFilter=" +searchFilter +":" +searchResults.getCount());
      } catch (Exception e) {
         e.printStackTrace();
      }
      //      
      //     
      //(|(cn=user0)(cn=user1))

   }

   public static void main(String[] args) {
      System.setProperty("userManagementServiceWsdl", userWsdl);
      System.setProperty("groupManagementServiceWsdl", groupWsdl);
      LoadTestLdap t = new LoadTestLdap();
      try {
         //t.resetUsersLdap();
         //t.resetUsers();
         //t.loadUsers();
         t.countUsers();
      } catch (Exception e) {
         e.printStackTrace();
      }
      //t.retrieveAllLocalUsers();
      //t.getUsers();
      //t.getUsers("GiscA", "DEFAULT", null);

      System.out.println(Pattern.matches("Z_PINGUINS\\d\\d\\d\\d\\d\\d_C_FAKE_.*",
            "Z_PINGUINS301101_C_FAKE_20110830110100.jpg"));
   }

}
