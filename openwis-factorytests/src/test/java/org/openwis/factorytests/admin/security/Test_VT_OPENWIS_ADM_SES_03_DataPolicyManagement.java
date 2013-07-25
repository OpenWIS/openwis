package org.openwis.factorytests.admin.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.openwis.factorytests.admin.AdminFactoryTest;

/**
 * Factory test: VT-OPENWIS-ADM-SES-03
 */
public class Test_VT_OPENWIS_ADM_SES_03_DataPolicyManagement extends AdminFactoryTest {
   public static final String ID_DP_GRID = "dataPolicyGrid";

   public static final String ID_OP_ALLOWED_GRID = "operationsAllowedGrid";

   public static final String LABEL_DP_NAME = "Name of the data policy (as found in MD):";
   
   @Override
   public void setUp() throws Exception {
      super.setUp();
      initLDAPConnection();
   }

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      dataSet.addTable("groups");
      dataSet.addTable("usergroups");
      dataSet.addTable("datapolicy",
            "select * from datapolicy where name<>'public' and name<>'additional-default' and name<>'unknown'");
      dataSet.addTable("operationallowed");
      dataSet.addTable("datapolicyalias");

      dataSet.addTable("metadata", "select * from metadata where istemplate='n'");
      dataSet.addTable("openwis_update_frequency");
      dataSet.addTable("openwis_product_metadata");
      dataSet.addTable("openwis_pattern_metadata_mapping");

      dataSet.addTable("openwis_request");
      dataSet.addTable("openwis_parameter");
      dataSet.addTable("openwis_requests_parameters");
      dataSet.addTable("openwis_processed_request");
      dataSet.addTable("openwis_staging_post_entry");
      dataSet.addTable("openwis_value");
      dataSet.addTable("openwis_parameter_values");
      dataSet.addTable("openwis_cached_file");

      return dataSet;
   }

   private void openDataPolicyManagementSection() throws Exception {
      loginAsAdmin();
      synchronizeGroupsWithLDAP();
      openAdminHomePage();
      selectAdminMenu(SECTION_SECURITY_SERVICE, SECTION_DATA_POLICY_MANAGEMENT);
      waitForLoadingVanish(5);
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_03_01() throws Exception {
      initLDAP("/admin/security/datapolicy/init-userAndGroup-01.xml");
      openDataPolicyManagementSection();
      assertEquals(3, countGridRows(ID_DP_GRID));
      assertEquals("additional-default", getGridCellText(ID_DP_GRID, 1, 1));
      assertEquals("public", getGridCellText(ID_DP_GRID, 2, 1));
      assertEquals("unknown", getGridCellText(ID_DP_GRID, 3, 1));

      assertTrue(isButtonEnabled(BTN_NEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertFalse(isButtonEnabled(BTN_REMOVE));
      // TODO check import / export

      selectGridCell(ID_DP_GRID, 1, 1);
      assertTrue(isButtonEnabled(BTN_NEW));
      assertTrue(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_REMOVE));
      pause(500);

      Point p2 = new Point(1, 2);
      selectGridCells(ID_DP_GRID, Arrays.asList(p2));
      pause(500);
      assertTrue(isButtonEnabled(BTN_NEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_REMOVE));
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_03_02() throws Exception {
      initLDAP("/admin/security/datapolicy/init-userAndGroup-01.xml");
      openDataPolicyManagementSection();

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_DP_NAME, 5);
      setTextFieldValue(LABEL_DP_NAME, "public");
      clickOnButton(BTN_CANCEL);

      assertEquals(3, countGridRows(ID_DP_GRID));

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_DP_NAME, 5);
      setTextFieldValue(LABEL_DP_NAME, "public");
      clickOnButton(BTN_SAVE);

      waitForTextPresent("Invalid name", 5);
      clickOnButton(BTN_OK);

      setTextFieldValue(LABEL_DP_NAME, "mydp");
      ArrayList<Point> ops = new ArrayList<Point>();
      ops.add(new Point(2, 1));
      ops.add(new Point(2, 2));
      ops.add(new Point(2, 3));
      ops.add(new Point(2, 4));
      ops.add(new Point(3, 1));
      ops.add(new Point(3, 2));
      ops.add(new Point(3, 3));
      ops.add(new Point(3, 4));
      ops.add(new Point(4, 1));
      ops.add(new Point(4, 2));
      ops.add(new Point(4, 3));
      ops.add(new Point(4, 4));
      selectGridCells(ID_OP_ALLOWED_GRID, ops);
      clickOnButton(BTN_SAVE);

      waitForPleaseWaitVanish(5);

      assertEquals(4, countGridRows(ID_DP_GRID));
      assertEquals("mydp", getGridCellText(ID_DP_GRID, 2, 1));
      assertCurrentDataSet("/admin/security/datapolicy/final-dataset_02.xml");

      assertOperationsAllowed("/admin/security/datapolicy/op-dataset_02.xml");
   }
   
   private void assertOperationsAllowed(String file) throws Exception {
      QueryDataSet opDataSet = new QueryDataSet(databaseTester.getConnection());
      opDataSet.addTable("operationNames",
            "select operations.name from operations, operationallowed, groups, datapolicy "
                  + "where groups.name='local1' and groups.id=operationallowed.groupid "
                  + "and operationallowed.operationid=operations.id "
                  + "and datapolicy.name='mydp' and datapolicy.id=operationallowed.datapolicyid");

      IDataSet expectedDataSet = loadDataSet(file);
      assertFilteredDataSet(expectedDataSet, opDataSet);
   }

   @Test
   public void test_VT_OPENWIS_ADM_SES_03_03() throws Exception {
      initLDAP("/admin/security/datapolicy/init-userAndGroup-01.xml");
      IDataSet dataSet = loadDataSet("/admin/security/datapolicy/init-dataset_03.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);
      openDataPolicyManagementSection();

      selectGridCell(ID_DP_GRID, 2, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_DP_NAME, 5);
      setTextFieldValue(LABEL_DP_NAME, "mydp2");
      clickOnButton(BTN_CANCEL);
      waitForPleaseWaitVanish(5);

      assertEquals(4, countGridRows(ID_DP_GRID));
      assertEquals("mydp", getGridCellText(ID_DP_GRID, 2, 1));

      selectGridCell(ID_DP_GRID, 2, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_DP_NAME, 5);
      
      ArrayList<Point> ops = new ArrayList<Point>();
      ops.add(new Point(3, 1));
      ops.add(new Point(3, 2));
      ops.add(new Point(3, 3));
      ops.add(new Point(3, 4));
      ops.add(new Point(4, 1));
      ops.add(new Point(4, 2));
      ops.add(new Point(4, 3));
      ops.add(new Point(4, 4));
      ops.add(new Point(5, 1));
      ops.add(new Point(5, 2));
      ops.add(new Point(5, 3));
      ops.add(new Point(5, 4));
      selectGridCells(ID_OP_ALLOWED_GRID, ops);
      clickOnButton(BTN_SAVE);

      waitForPleaseWaitVanish(5);

      assertEquals(4, countGridRows(ID_DP_GRID));
      assertEquals("mydp", getGridCellText(ID_DP_GRID, 2, 1));
      assertCurrentDataSet("/admin/security/datapolicy/final-dataset_03.xml");

      assertOperationsAllowed("/admin/security/datapolicy/op-dataset_03.xml");
   }
   
   @Test
   public void test_VT_OPENWIS_ADM_SES_03_04() throws Exception {
      initLDAP("/admin/security/datapolicy/init-userAndGroup-01.xml");
      IDataSet dataSet = loadDataSet("/admin/security/datapolicy/init-dataset_03.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);
      openDataPolicyManagementSection();

      selectGridCell(ID_DP_GRID, 2, 1);
      clickOnButton(BTN_REMOVE);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      clickOnButton(BTN_OK);
      pause(500);
      assertEquals(3, countGridRows(ID_DP_GRID));
      assertEquals("additional-default", getGridCellText(ID_DP_GRID, 1, 1));
      assertEquals("public", getGridCellText(ID_DP_GRID, 2, 1));
      assertEquals("unknown", getGridCellText(ID_DP_GRID, 3, 1));
   }

}
