package org.openwis.factorytests.admin.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.junit.Test;
import org.openwis.factorytests.admin.AdminFactoryTest;
import org.openwis.usermanagement.PopulateUser;
import org.openwis.usermanagement.model.group.OpenWISGroup;
import org.openwis.usermanagement.model.user.ClassOfService;
import org.openwis.usermanagement.model.user.OpenWISAddress;
import org.openwis.usermanagement.model.user.OpenWISUser;
import org.openwis.usermanagement.util.GroupUtils;
import org.openwis.usermanagement.util.LdapUtils;
import org.openwis.usermanagement.util.OpenWiSBackupUtils;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

/**
 * Factory test: VT-OPENWIS-ADM-SES-02
 */
public class Test_VT_OPENWIS_ADM_SES_02_UserManagement extends AdminFactoryTest {
   public static final String ID_USER_GRID = "userGrid";

   public static final String LABEL_USER_NAME = "User Name: ";

   public static final String LABEL_LAST_NAME = "Last Name: ";

   public static final String LABEL_FIRST_NAME = "First Name: ";

   public static final String LABEL_PASSWORD = "Password: ";

   public static final String LABEL_NEW_PASSWORD = "New Password: ";

   public static final String LABEL_EMAIL = "Contact Email: ";

   public static final String LABEL_ADDRESS = "Address: ";

   public static final String LABEL_CITY = "City: ";

   public static final String LABEL_COUNTRY = "Country: ";

   public static final String LABEL_STATE = "State: ";

   public static final String LABEL_ZIP = "Zip: ";

   public static final String MSG_VALIDATION = "Manage User Validation Form";

   public static final String MSG_ALREADY_EXISTS = "already exists";

   public static final String TAB_PRIVILEGES = "Privileges";

   public static final String LABEL_PROFILE = "Profile: ";

   public static final String LABEL_CLASS_OF_SERVICE = "Class of Service: ";

   public static final String LABEL_NEED_USER_ACCOUNT = "Need User Account: ";

   public static final String LABEL_GROUPS = "Groups: ";

   public static final String LABEL_BACKUP = "Backup: ";

   public static final String BTN_IMPORT_USER = "Import User...";

   public static final String ID_IMPORT_USER_GRID = "importUserGrid";

   public static final String BTN_IMPORT = "Import";

   //
   //   public static final String LABEL_GLOBAL = "Global ?:";
   //
   //   public static final String DEPLOYMENT_NAME = "GiscA";
   //
   //   public static final String GLOBAL_NAME = "GLOBAL";

   @Override
   public void setUp() throws Exception {
      super.setUp();
      initLDAPConnection();
   }

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      dataSet.addTable("users");
      dataSet.addTable("usergroups");
      return dataSet;
   }

   protected boolean ldapContainsUser(String username) throws LDAPException {
      String entryDN = "ou=people,dc=opensso,dc=java,dc=net";
      int searchScope = LDAPConnection.SCOPE_SUB;
      String searchFilter = "cn=" + username;
      LDAPSearchResults searchResults = ldapConnection.search(entryDN, searchScope, searchFilter,
            null, false);
      searchResults.hasMore();
      return searchResults.getCount() == 1;
   }

   protected boolean userBelongsToCentre(String username, String centre) throws Exception {
      // get groups
      boolean contained = false;
      List<OpenWISGroup> groups = getUserGroups(username);
      for (OpenWISGroup ldapGroup : groups) {
         if (centre.equals(ldapGroup.getCentreName()) && ldapGroup.getGroupIds().contains("DEFAULT")) {
            contained = true;
            break;
         }

      }
      return contained;
   }

   /**
    * Assert given user values with ldap content for this user.
    */
   protected void assertLdapContainsUser(OpenWISUser user) throws Exception {
      String entryDN = "uid=" + user.getUserName() + ",ou=people,dc=opensso,dc=java,dc=net";
      int searchScope = LDAPConnection.SCOPE_SUB;
      String searchFilter = "objectclass=*";

      LDAPSearchResults searchResults = ldapConnection.search(entryDN, searchScope, searchFilter,
            null, false);
      LDAPEntry ldapEntry = searchResults.next();
      assertNotNull(ldapEntry);

      LDAPAttributeSet attributeSet = ldapEntry.getAttributeSet();

      @SuppressWarnings("unchecked")
      Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();
      while (allAttributes.hasNext()) {

         LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();

         if (LdapUtils.NAME.equals(attribute.getName())) {
            assertEquals(user.getName(), attribute.getStringValue());
         } else if (LdapUtils.CN.equals(attribute.getName())) {
            assertEquals(user.getUserName(), attribute.getStringValue());
         } else if (LdapUtils.SURNAME.equals(attribute.getName())) {
            assertEquals(user.getSurName(), attribute.getStringValue());
         } else if (LdapUtils.PROFILE.equals(attribute.getName())) {
            assertEquals(user.getProfile(), attribute.getStringValue());
         } else if (LdapUtils.CONTACT_EMAIL.equals(attribute.getName())) {
            assertEquals(user.getEmailContact(), attribute.getStringValue());
         } else if (LdapUtils.ADDRESS_ADDRESS.equals(attribute.getName())) {
            assertEquals(user.getAddress().getAddress(), attribute.getStringValue());
         } else if (LdapUtils.ADDRESS_COUNTRY.equals(attribute.getName())) {
            assertEquals(user.getAddress().getCountry(), attribute.getStringValue());
         } else if (LdapUtils.ADDRESS_STATE.equals(attribute.getName())) {
            assertEquals(user.getAddress().getState(), attribute.getStringValue());
         } else if (LdapUtils.ADDRESS_CITY.equals(attribute.getName())) {
            assertEquals(user.getAddress().getCity(), attribute.getStringValue());
         } else if (LdapUtils.ADDRESS_ZIP.equals(attribute.getName())) {
            assertEquals(user.getAddress().getZip(), attribute.getStringValue());
         } else if (LdapUtils.CLASSOFSERVICE.equals(attribute.getName())) {
            assertEquals(user.getClassOfService(),
                  ClassOfService.valueOf(attribute.getStringValue()));
         } else if (LdapUtils.NEEDUSERACCOUNT.equals(attribute.getName())) {
            assertEquals(user.isNeedUserAccount(), Boolean.valueOf(attribute.getStringValue()));
         } else if (LdapUtils.BACKUPS.equals(attribute.getName())) {
            assertEquals(user.getBackUps(),
                  OpenWiSBackupUtils.convertStringToBackUpList(attribute.getStringValue()));
         }

         //         else if (LdapUtils.EMAILS.equals(attribute.getName())) {
         //            List<OpenWISEmail> emails = OpenWISEmailUtils.convertToOpenWISEmails(attribute
         //                  .getStringValue());
         //            assertEquals(user.getEmails(), emails);
         //         } else if (LdapUtils.FTPS.equals(attribute.getName())) {
         //            List<OpenWISFTP> ftps = OpenWISFTPUtils
         //                  .convertToOpenWISFTPs(attribute.getStringValue());
         //            assertEquals(user.getFtps(), ftps);

      }

      // check password
      assertTrue(checkLdapPassword(entryDN, user.getPassword()));

      // get groups
      List<OpenWISGroup> groups = getUserGroups(user.getUserName());

      // assert groups
      ArrayList<OpenWISGroup> groupsToTest = new ArrayList<OpenWISGroup>(user.getGroups());
      for (OpenWISGroup userGroup : user.getGroups()) {
         for (OpenWISGroup ldapGroup : groups) {
            if (userGroup.getCentreName().equals(ldapGroup.getCentreName())
                  && userGroup.getGroupIds().equals(ldapGroup.getGroupIds())) {
               groupsToTest.remove(userGroup);
               break;
            }
         }
      }
      assertEquals(0, groupsToTest.size());
   }

   private List<OpenWISGroup> getUserGroups(String username) throws Exception {
      String entryDN = "uid=" + username + ",ou=people,dc=opensso,dc=java,dc=net";
      int searchScope = LDAPConnection.SCOPE_SUB;

      String[] attrs = new String[] {"ismemberof"};
      LDAPSearchResults searchResults = ldapConnection.search(entryDN, searchScope, null, attrs,
            false);
      LDAPEntry ldapEntry = searchResults.next();
      assertNotNull(ldapEntry);
      List<OpenWISGroup> groups = new ArrayList<OpenWISGroup>();
      if (ldapEntry.getAttributeSet().size() == 0) {
         return groups;
      }
      LDAPAttribute attribute = (LDAPAttribute) ldapEntry.getAttributeSet().iterator().next();
      @SuppressWarnings("unchecked")
      Enumeration<String> memberOf = attribute.getStringValues();
      while (memberOf.hasMoreElements()) {
         String member = (String) memberOf.nextElement();
         OpenWISGroup openWISGroup = GroupUtils.getOpenWisGroupByDn(member);
         groups.add(openWISGroup);
      }
      return groups;
   }

   private boolean checkLdapPassword(String entryDn, String password) {
      try {
         LDAPConnection testConn = new LDAPConnection();
         testConn.connect(ldapConnection.getHost(), ldapConnection.getPort());
         testConn.bind(LDAPConnection.LDAP_V3, entryDn, password.getBytes("UTF8"));
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   private void openUserManagementSection() throws Exception {
      loginAsAdmin();
      synchronizeGroupsWithLDAP();
      openAdminHomePage();
      selectAdminMenu(SECTION_SECURITY_SERVICE, SECTION_USER_MANAGEMENT);
      waitForLoadingVanish(5);
   }

   protected void setTextFieldValue(String textFieldLabel, String value) {
      selenium
            .type("//td[div[text()='" + textFieldLabel + "']]/following-sibling::td/input", value);
   }

   private void fillUserForm(OpenWISUser user, boolean editMode) {
      if (!editMode) {
         setTextFieldValue(LABEL_USER_NAME, user.getUserName());
      }
      setTextFieldValue(LABEL_LAST_NAME, user.getSurName());
      setTextFieldValue(LABEL_FIRST_NAME, user.getName());
      if (!editMode) {
         setTextFieldValue(LABEL_PASSWORD, user.getPassword());
      } else {
         setTextFieldValue(LABEL_NEW_PASSWORD, user.getPassword());
      }
      setTextFieldValue(LABEL_EMAIL, user.getEmailContact());
      setTextFieldValue(LABEL_ADDRESS, user.getAddress().getAddress());
      setTextFieldValue(LABEL_CITY, user.getAddress().getCity());
      setTextFieldValue(LABEL_STATE, user.getAddress().getState());
      setTextFieldValue(LABEL_ZIP, user.getAddress().getZip());
      setTextFieldValue(LABEL_COUNTRY, user.getAddress().getCountry());

      // select privileges tab
      selectUserFormTab(TAB_PRIVILEGES);
      pause(500);
      setComboValue(LABEL_PROFILE, user.getProfile());
      setComboValue(LABEL_CLASS_OF_SERVICE, user.getClassOfService().name());
      setCheckboxValue(LABEL_NEED_USER_ACCOUNT, user.isNeedUserAccount());

      if (editMode) {
         unselectAllFromList(LABEL_GROUPS);
         unselectAllFromList(LABEL_BACKUP);
      }

      for (OpenWISGroup group : user.getGroups()) {
         for (String groupId : group.getGroupIds()) {
            if (!groupId.equals("DEFAULT")) {
               selectFromList(LABEL_GROUPS, groupId);
            }
         }
      }
      if (user.getBackUps() != null) {
         for (String backup : user.getBackUps()) {
            selectFromList(LABEL_BACKUP, backup);
         }
      }

      // TODO set diss favourites
   }

   protected void setComboValue(String comboLabel, String value) {
      selenium
            .click("//td[div/text()='"
                  + comboLabel
                  + "']/following-sibling::td/div/descendant::img[contains(@class, 'x-form-arrow-trigger')]");
      // wait for a drop down list of options to be visible
      pause(500);
      selenium
            .click("//div[contains(@class, 'x-combo-list')]/descendant::div[contains(@class, 'x-combo-list-item')][text()='"
                  + value + "']");
      pause(500);
   }

   protected void setCheckboxValue(String checkboxLabel, boolean value) {
      String locator = "//td[div/text()='" + checkboxLabel + "']/following-sibling::td/div/input";
      if (value) {
         selenium.check(locator);
      } else {
         selenium.uncheck(locator);
      }
   }

   protected void selectFromList(String listLabel, String value) {
      selenium.mouseDown("//td[div/text()='" + listLabel
            + "']/following-sibling::td/div//fieldset[1]//dl[dt/em/text()='" + value + "']");
      selenium.click("//td[div/text()='" + listLabel
            + "']/following-sibling::td/div/div/div/div/table/tbody/tr/td[2]/div/img[3]");
   }

   protected void unselectAllFromList(String listLabel) {
      int countSelected = selenium
            .getXpathCount(
                  "//td[div/text()='"
                        + listLabel
                        + "']/following-sibling::td/div/div/div/div/table/tbody/tr/td[3]/div/fieldset//dl")
            .intValue();
      for (int i = 1; i <= countSelected; i++) {
         selenium
               .mouseDown("//td[div/text()='"
                     + listLabel
                     + "']/following-sibling::td/div/div/div/div/table/tbody/tr/td[3]/div/fieldset//dl[1]");
         selenium.click("//td[div/text()='" + listLabel
               + "']/following-sibling::td/div/div/div/div/table/tbody/tr/td[2]/div/img[4]");
      }
   }

   private void selectUserFormTab(String tab) {
      selenium.mouseDown("//li[descendant::span/text()='" + tab + "']");
   }

   private OpenWISUser createTestUser(int index) {
      OpenWISUser user = new OpenWISUser();
      user.setEmailContact("myuser" + index + "@akka.eu");
      user.setUserName("myuser" + index);
      user.setName("myfirstname" + index);
      user.setSurName("mylastname" + index);
      user.setPassword("mypassword" + index);
      OpenWISAddress address = new OpenWISAddress();
      address.setAddress("myaddress" + index);
      address.setCity("mycity" + index);
      address.setState("mystate" + index);
      address.setZip("myzip" + index);
      address.setCountry("mycountry" + index);
      user.setAddress(address);
      user.setProfile("Editor");
      user.setClassOfService(ClassOfService.SILVER);
      user.setNeedUserAccount(true);

      ArrayList<OpenWISGroup> groups = new ArrayList<OpenWISGroup>();
      OpenWISGroup g = new OpenWISGroup();
      g.setCentreName("GLOBAL");
      g.getGroupIds().add("Institutional");
      groups.add(g);

      g = new OpenWISGroup();
      g.setCentreName("GiscA");
      g.getGroupIds().add("DEFAULT");
      groups.add(g);

      g = new OpenWISGroup();
      g.setCentreName("GiscA");
      g.getGroupIds().add("local1");
      groups.add(g);

      ArrayList<String> backups = new ArrayList<String>();
      backups.add("GiscAkka");
      if (index % 2 == 0) {
         g = new OpenWISGroup();
         g.setCentreName("GiscA");
         g.getGroupIds().add("local2");
         groups.add(g);
      }

      user.setGroups(groups);
      user.setBackUps(backups);

      return user;
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_02_01() throws Exception {
      initLDAP("/admin/security/user/init-userAndGroup-01.xml");
      openUserManagementSection();
      assertEquals(0, countGridRows(ID_USER_GRID));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_02_02() throws Exception {
      initLDAP("/admin/security/user/init-userAndGroup-02.xml");
      openUserManagementSection();
      assertTrue(isButtonEnabled(BTN_NEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertFalse(isButtonEnabled(BTN_REMOVE));

      selectGridCell(ID_USER_GRID, 1, 1);
      assertTrue(isButtonEnabled(BTN_NEW));
      assertTrue(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_REMOVE));
      pause(500);

      Point p1 = new Point(1, 2);
      Point p2 = new Point(1, 3);
      selectGridCells(ID_USER_GRID, Arrays.asList(p1, p2));
      pause(500);
      assertTrue(isButtonEnabled(BTN_NEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_REMOVE));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_02_03() throws Exception {
      initLDAP("/admin/security/user/init-userAndGroup-02.xml");
      openUserManagementSection();

      OpenWISUser user = createTestUser(0);

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_USER_NAME, 5);
      fillUserForm(user, false);
      clickOnButton(BTN_CANCEL);

      assertEquals(3, countGridRows(ID_USER_GRID));

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_USER_NAME, 5);
      fillUserForm(user, false);
      clickOnButton(BTN_SAVE);

      waitForPleaseWaitVanish(5);
      pause(500);

      assertEquals(4, countGridRows(ID_USER_GRID));
      assertEquals("myuser0", getGridCellText(ID_USER_GRID, 4, 1));
      assertLdapContainsUser(user);

      // test mandatory values
      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_USER_NAME, 5);

      user.setUserName("");
      fillAndCheckValidation(user);

      user = createTestUser(1);
      user.setPassword("");
      fillAndCheckValidation(user);

      user = createTestUser(1);
      user.setName("");
      fillAndCheckValidation(user);

      user = createTestUser(1);
      user.setSurName("");
      fillAndCheckValidation(user);

      user = createTestUser(1);
      user.setEmailContact("");
      fillAndCheckValidation(user);

      // address not mandatory
      clickOnButton(BTN_CANCEL);
      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_USER_NAME, 5);

      user = createTestUser(1);
      user.getAddress().setAddress("");
      user.getAddress().setCity("");
      user.getAddress().setCountry("");
      user.getAddress().setZip("");
      user.getAddress().setState("");
      fillUserForm(user, false);
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertEquals(5, countGridRows(ID_USER_GRID));
      assertEquals("myuser1", getGridCellText(ID_USER_GRID, 5, 1));
      assertLdapContainsUser(user);

      // duplicate user
      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_USER_NAME, 5);
      user = createTestUser(0);
      fillUserForm(user, false);
      clickOnButton(BTN_SAVE);
      waitForTextPresent(MSG_ALREADY_EXISTS, 5);
   }

   private void fillAndCheckValidation(OpenWISUser user) {
      fillUserForm(user, false);
      clickOnButton(BTN_SAVE);
      waitForTextPresent(MSG_VALIDATION, 1);
      clickOnButton(BTN_OK);
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_02_04() throws Exception {
      initLDAP("/admin/security/user/init-userAndGroup-02.xml");
      openUserManagementSection();

      OpenWISUser user = createTestUser(0);
      user.setUserName("user3");

      selectGridCell(ID_USER_GRID, 3, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_USER_NAME, 5);
      fillUserForm(user, true);
      clickOnButton(BTN_CANCEL);

      assertEquals(3, countGridRows(ID_USER_GRID));
      assertEquals("user3", getGridCellText(ID_USER_GRID, 3, 1));

      selectGridCell(ID_USER_GRID, 3, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_USER_NAME, 5);
      fillUserForm(user, true);
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);
      pause(1000);

      assertEquals(3, countGridRows(ID_USER_GRID));
      assertEquals("user3", getGridCellText(ID_USER_GRID, 3, 1));
      assertEquals("mylastname0", getGridCellText(ID_USER_GRID, 3, 2));
      assertLdapContainsUser(user);
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_02_05() throws Exception {
      initLDAP("/admin/security/user/init-userAndGroup-02.xml");
      openUserManagementSection();

      selectGridCell(ID_USER_GRID, 3, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_NO);

      assertEquals(3, countGridRows(ID_USER_GRID));
      assertEquals("user3", getGridCellText(ID_USER_GRID, 3, 1));

      selectGridCell(ID_USER_GRID, 3, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_OK);
      waitForPleaseWaitVanish(5);

      assertEquals(2, countGridRows(ID_USER_GRID));
      assertFalse(ldapContainsUser("user3"));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_02_06() throws Exception {
      initLDAP("/admin/security/user/init-userAndGroup-02.xml");
      openUserManagementSection();

      Point p1 = new Point(1, 2);
      Point p2 = new Point(1, 3);
      selectGridCells(ID_USER_GRID, Arrays.asList(p1, p2));
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_OK);
      waitForPleaseWaitVanish(5);

      assertEquals(1, countGridRows(ID_USER_GRID));
      assertTrue(ldapContainsUser("user1"));
      assertFalse(ldapContainsUser("user2"));
      assertFalse(ldapContainsUser("user3"));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_02_07() throws Exception {
      initLDAP("/admin/security/user/init-userAndGroup-07.xml");
      // populate for dcpc
      PopulateUser populateUser = new PopulateUser();
      URL fileUrl = getClass().getResource("/admin/security/user/init-userAndGroup-07-dcpc.xml");
      populateUser.populate(fileUrl.getFile(), "DcpcA");

      openUserManagementSection();

      assertEquals(2, countGridRows(ID_USER_GRID));

      clickOnButton(BTN_IMPORT_USER);
      waitForPleaseWaitVanish(5);
      selectGridCell(ID_IMPORT_USER_GRID, 1, 1);

      selenium.click("//button[text()='" + BTN_IMPORT + "']");
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_USER_GRID));
      assertTrue(ldapContainsUser("user3"));

      selectGridCell(ID_USER_GRID, 3, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_OK);
      waitForPleaseWaitVanish(5);

      assertEquals(2, countGridRows(ID_USER_GRID));
      // user3 still in LDAP as belonging to an external centre
      assertFalse(userBelongsToCentre("user3", "GiscA"));
      assertTrue(userBelongsToCentre("user3", "DcpcA"));
   }

}
