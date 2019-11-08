package org.openwis.factorytests.admin.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.Arrays;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.openwis.factorytests.admin.AdminFactoryTest;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

/**
 * Factory test: VT-OPENWIS-ADM-SES-01
 */
public class Test_VT_OPENWIS_ADM_SES_01_GroupManagement extends AdminFactoryTest {
   public static final String ID_GROUP_GRID = "groupGrid";

   public static final String LABEL_GROUP_NAME = "Name of the group:";

   public static final String LABEL_GLOBAL = "Global ?:";

   public static final String DEPLOYMENT_NAME = "GiscA";
   public static final String GLOBAL_NAME = "GLOBAL";
   
   @Override
   public void setUp() throws Exception {
      super.setUp();
      initLDAPConnection();
   }

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      dataSet.addTable("groups");
      dataSet.addTable("groupsdes");
      dataSet.addTable("usergroups");
      dataSet.addTable("operationallowed");
      return dataSet;
   }

   protected boolean ldapContainsGroup(String centreName, String localGroup)
         throws LDAPException {
      String entryDN = "ou=" + centreName + ",ou=groups,dc=opensso,dc=java,dc=net";
      int searchScope = LDAPConnection.SCOPE_SUB;
      String searchFilter = "cn=" + localGroup;
      LDAPSearchResults searchResults = ldapConnection.search(entryDN, searchScope, searchFilter,
            null, false);
      searchResults.hasMore();
      System.out.println("entryDN=" + entryDN + ", searchFilter=" +searchFilter +":" +searchResults.getCount());
      return searchResults.getCount() == 1;
   }

   /**
    * Open group management section with or without performing LDAP synchro.
    */
   private void openGroupManagementSection(boolean synchronize) throws Exception {
      loginAsAdmin();

      if (synchronize) {
         synchronizeGroupsWithLDAP();
      }
      openAdminHomePage();
      selectAdminMenu(SECTION_SECURITY_SERVICE, SECTION_GROUP_MANAGEMENT);
      waitForLoadingVanish(5);
   }

   private void openGroupManagementSection() throws Exception {
      openGroupManagementSection(true);
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_01_01() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-01.xml");
      openGroupManagementSection();
      assertEquals(2, countGridRows(ID_GROUP_GRID));
      assertEquals("DEFAULT", getGridCellText(ID_GROUP_GRID, 1, 1));
      assertEquals("false", getGridCellText(ID_GROUP_GRID, 1, 2));
      assertEquals("Institutional", getGridCellText(ID_GROUP_GRID, 2, 1));
      assertEquals("true", getGridCellText(ID_GROUP_GRID, 2, 2));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_01_02() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-02.xml");
      openGroupManagementSection();
      assertTrue(isButtonEnabled(BTN_NEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertFalse(isButtonEnabled(BTN_REMOVE));

      selectGridCell(ID_GROUP_GRID, 1, 1);
      assertTrue(isButtonEnabled(BTN_NEW));
      assertTrue(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_REMOVE));
      pause(500);

      Point p1 = new Point(1, 2);
      Point p2 = new Point(1, 3);
      selectGridCells(ID_GROUP_GRID, Arrays.asList(p1, p2));
      pause(500);
      assertTrue(isButtonEnabled(BTN_NEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_REMOVE));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_01_03() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-01.xml");
      openGroupManagementSection();

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setTextFieldValue(LABEL_GROUP_NAME, "mygroup");
      clickOnButton(BTN_CANCEL);

      assertEquals(2, countGridRows(ID_GROUP_GRID));

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setTextFieldValue(LABEL_GROUP_NAME, "mygroup");
      clickOnButton(BTN_SAVE);

      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_GROUP_GRID));
      assertEquals("mygroup", getGridCellText(ID_GROUP_GRID, 3, 1));
      assertCurrentDataSet("/admin/security/group/final-dataset_03.xml");
      assertTrue(ldapContainsGroup(DEPLOYMENT_NAME, "mygroup"));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_01_04() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-01.xml");
      openGroupManagementSection();

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setTextFieldValue(LABEL_GROUP_NAME, "myglobalgroup");
      setCheckboxValue(LABEL_GLOBAL, true);
      clickOnButton(BTN_SAVE);

      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_GROUP_GRID));
      assertEquals("myglobalgroup", getGridCellText(ID_GROUP_GRID, 3, 1));
      assertCurrentDataSet("/admin/security/group/final-dataset_04.xml");
      assertTrue(ldapContainsGroup(GLOBAL_NAME, "myglobalgroup"));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_01_05() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-05.xml");
      openGroupManagementSection();

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setTextFieldValue(LABEL_GROUP_NAME, "mygroup");
      setCheckboxValue(LABEL_GLOBAL, false);
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertTrue(selenium.isTextPresent("The group mygroup already exists"));
      clickOnButton(BTN_OK);

      setTextFieldValue(LABEL_GROUP_NAME, "mygroup2");
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertEquals(4, countGridRows(ID_GROUP_GRID));
      assertEquals("mygroup2", getGridCellText(ID_GROUP_GRID, 4, 1));
      assertTrue(ldapContainsGroup(DEPLOYMENT_NAME, "mygroup2"));

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setTextFieldValue(LABEL_GROUP_NAME, "mygroup");
      setCheckboxValue(LABEL_GLOBAL, true);
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);
      //pause(5000);
      assertTrue(ldapContainsGroup(GLOBAL_NAME, "mygroup"));

      assertCurrentDataSet("/admin/security/group/final-dataset_05.xml");
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_01_06() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-06.xml");
      IDataSet dataSet = loadDataSet("/admin/security/group/init-dataset_06.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      openGroupManagementSection(false);

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setTextFieldValue(LABEL_GROUP_NAME, "myglobalgroup");
      setCheckboxValue(LABEL_GLOBAL, true);
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);
      pause(500);

      assertTrue(selenium.isTextPresent("problem of synchronization"));
      clickOnButton(BTN_OK);

      setTextFieldValue(LABEL_GROUP_NAME, "myglobalgroup2");
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertEquals(4, countGridRows(ID_GROUP_GRID));
      assertEquals("myglobalgroup2", getGridCellText(ID_GROUP_GRID, 3, 1));
      assertCurrentDataSet("/admin/security/group/final-dataset_06.xml");
      assertTrue(ldapContainsGroup(GLOBAL_NAME, "myglobalgroup2"));
   }
   
   @Test
   public void test_VT_OPENWIS_ADM_SES_01_07() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-05.xml");
      openGroupManagementSection();

      selectGridCell(ID_GROUP_GRID, 3, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setTextFieldValue(LABEL_GROUP_NAME, "mygroup3");
      clickOnButton(BTN_CANCEL);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_GROUP_GRID));
      assertEquals("mygroup", getGridCellText(ID_GROUP_GRID, 3, 1));

      selectGridCell(ID_GROUP_GRID, 3, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setTextFieldValue(LABEL_GROUP_NAME, "mygroup3");
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_GROUP_GRID));
      assertEquals("mygroup3", getGridCellText(ID_GROUP_GRID, 3, 1));
      assertCurrentDataSet("/admin/security/group/final-dataset_07.xml");
      assertTrue(ldapContainsGroup(DEPLOYMENT_NAME, "mygroup3"));
      assertFalse(ldapContainsGroup(DEPLOYMENT_NAME, "mygroup"));
   }
   
   /* discarded @Test TODO to check  test_VT_OPENWIS_ADM_SES_01_08 */
   public void test_VT_OPENWIS_ADM_SES_01_08() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-05.xml");
      openGroupManagementSection();

      selectGridCell(ID_GROUP_GRID, 3, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_GROUP_NAME, 5);
      setCheckboxValue(LABEL_GLOBAL, true);
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_GROUP_GRID));
      assertEquals("mygroup", getGridCellText(ID_GROUP_GRID, 3, 1));
      assertCurrentDataSet("/admin/security/group/final-dataset_08.xml");
      assertTrue(ldapContainsGroup(GLOBAL_NAME, "mygroup"));
      assertFalse(ldapContainsGroup(DEPLOYMENT_NAME, "mygroup"));
   }
   
   @Test
   public void test_VT_OPENWIS_ADM_SES_01_09() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-06.xml");
      openGroupManagementSection();

      selectGridCell(ID_GROUP_GRID, 4, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_NO);

      assertEquals(4, countGridRows(ID_GROUP_GRID));
      assertEquals("mygroup", getGridCellText(ID_GROUP_GRID, 4, 1));
      
      selectGridCell(ID_GROUP_GRID, 4, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_GROUP_GRID));
      assertCurrentDataSet("/admin/security/group/final-dataset_09.xml");
      assertFalse(ldapContainsGroup(DEPLOYMENT_NAME, "mygroup"));
   }
   
   @Test
   public void test_VT_OPENWIS_ADM_SES_01_10() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-06.xml");
      openGroupManagementSection();

      selectGridCell(ID_GROUP_GRID, 3, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_GROUP_GRID));
      assertCurrentDataSet("/admin/security/group/final-dataset_10.xml");
      assertFalse(ldapContainsGroup(GLOBAL_NAME, "myglobalgroup"));
      assertFalse(ldapContainsGroup(DEPLOYMENT_NAME, "myglobalgroup"));
   }
   
   @Test
   public void test_VT_OPENWIS_ADM_SES_01_11() throws Exception {
      initLDAP("/admin/security/group/init-userAndGroup-06.xml");
      openGroupManagementSection();

      Point p1 = new Point(1, 3);
      Point p2 = new Point(1, 4);
      selectGridCells(ID_GROUP_GRID, Arrays.asList(p1, p2));
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);

      assertEquals(2, countGridRows(ID_GROUP_GRID));
      assertCurrentDataSet("/admin/security/group/final-dataset_11.xml");
      assertFalse(ldapContainsGroup(DEPLOYMENT_NAME, "mygroup"));
      assertFalse(ldapContainsGroup(GLOBAL_NAME, "myglobalgroup"));
      assertFalse(ldapContainsGroup(DEPLOYMENT_NAME, "myglobalgroup"));
   }
}
