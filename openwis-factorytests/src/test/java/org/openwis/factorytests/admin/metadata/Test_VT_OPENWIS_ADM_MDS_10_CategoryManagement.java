package org.openwis.factorytests.admin.metadata;

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

/**
 * Factory test: VT-OPENWIS-ADM-MDS-10
 */
public class Test_VT_OPENWIS_ADM_MDS_10_CategoryManagement extends AdminFactoryTest {
   public static final String ID_CATEGORY_GRID = "categoryGrid";

   public static final String LABEL_CATEGORY_NAME = "Name of the category:";

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      // leave existing categories
      dataSet.addTable("categories", "select * from categories where name<>'datasets' AND name<>'draft'");
      // leave existing categories
      dataSet.addTable("metadata", "select * from metadata where istemplate='n'");
      return dataSet;
   }

   private void openCategoryManagementSection() throws Exception {
      loginAsAdmin();
      openAdminHomePage();
      selectAdminMenu(SECTION_METADATA_SERVICE, SECTION_CATEGORY_MANAGEMENT);
      waitForLoadingVanish(5);
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_10_01() throws Exception {
      openCategoryManagementSection();
      assertEquals(2, countGridRows(ID_CATEGORY_GRID));
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_10_02() throws Exception {
      IDataSet dataSet = loadDataSet("/admin/metadata/category/init-dataset_02.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      openCategoryManagementSection();

      assertEquals(6, countGridRows(ID_CATEGORY_GRID));
      assertEquals("cat2", getGridCellText(ID_CATEGORY_GRID, 1, 1));

      assertTrue(isButtonEnabled(BTN_NEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertFalse(isButtonEnabled(BTN_REMOVE));
      
      selectGridCell(ID_CATEGORY_GRID, 1, 1);
      assertTrue(isButtonEnabled(BTN_NEW));
      assertTrue(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_REMOVE));
      pause(500);
      
      Point p1 = new Point(1, 2);
      Point p2 = new Point(1, 3);
      selectGridCells(ID_CATEGORY_GRID, Arrays.asList(p1, p2));
      pause(500);
      assertTrue(isButtonEnabled(BTN_NEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_REMOVE));
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_10_03() throws Exception {
      openCategoryManagementSection();

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_CATEGORY_NAME, 5);
      setTextFieldValue(LABEL_CATEGORY_NAME, "mycategory");
      clickOnButton(BTN_CANCEL);

      assertEquals(2, countGridRows(ID_CATEGORY_GRID));

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_CATEGORY_NAME, 5);
      setTextFieldValue(LABEL_CATEGORY_NAME, "mycategory");
      clickOnButton(BTN_SAVE);

      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_CATEGORY_GRID));
      assertEquals("mycategory", getGridCellText(ID_CATEGORY_GRID, 3, 1));
      assertCurrentDataSet("/admin/metadata/category/final-dataset_03.xml");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_10_04() throws Exception {
      IDataSet dataSet = loadDataSet("/admin/metadata/category/init-dataset_04.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      openCategoryManagementSection();

      clickOnButton(BTN_NEW);
      waitForTextPresent(LABEL_CATEGORY_NAME, 5);
      setTextFieldValue(LABEL_CATEGORY_NAME, "mycategory");
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertTrue(selenium.isTextPresent("The category mycategory already exists"));
      clickOnButton(BTN_OK);

      setTextFieldValue(LABEL_CATEGORY_NAME, "mycategory2");
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertEquals(4, countGridRows(ID_CATEGORY_GRID));
      assertEquals("mycategory2", getGridCellText(ID_CATEGORY_GRID, 4, 1));
      assertCurrentDataSet("/admin/metadata/category/final-dataset_04.xml");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_10_05() throws Exception {
      IDataSet dataSet = loadDataSet("/admin/metadata/category/init-dataset_04.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      openCategoryManagementSection();

      selectGridCell(ID_CATEGORY_GRID, 3, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_CATEGORY_NAME, 5);
      setTextFieldValue(LABEL_CATEGORY_NAME, "mycategory3");
      clickOnButton(BTN_CANCEL);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_CATEGORY_GRID));
      assertEquals("mycategory", getGridCellText(ID_CATEGORY_GRID, 3, 1));

      selectGridCell(ID_CATEGORY_GRID, 3, 1);
      clickOnButton(BTN_EDIT);
      waitForTextPresent(LABEL_CATEGORY_NAME, 5);
      setTextFieldValue(LABEL_CATEGORY_NAME, "mycategory3");
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_CATEGORY_GRID));
      assertEquals("mycategory3", getGridCellText(ID_CATEGORY_GRID, 3, 1));
      assertCurrentDataSet("/admin/metadata/category/final-dataset_05.xml");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_10_06() throws Exception {
      IDataSet dataSet = loadDataSet("/admin/metadata/category/init-dataset_04.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      openCategoryManagementSection();

      selectGridCell(ID_CATEGORY_GRID, 3, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_NO);

      assertEquals(3, countGridRows(ID_CATEGORY_GRID));
      assertEquals("mycategory", getGridCellText(ID_CATEGORY_GRID, 3, 1));

      selectGridCell(ID_CATEGORY_GRID, 3, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);

      assertEquals(2, countGridRows(ID_CATEGORY_GRID));
      assertCurrentDataSet("/admin/metadata/category/final-dataset_06.xml");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_10_07() throws Exception {
      IDataSet dataSet = loadDataSet("/admin/metadata/category/init-dataset_07.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      openCategoryManagementSection();

      Point p1 = new Point(1, 3);
      Point p2 = new Point(1, 4);
      selectGridCells(ID_CATEGORY_GRID, Arrays.asList(p1, p2));
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_CATEGORY_GRID));
      assertCurrentDataSet("/admin/metadata/category/final-dataset_07.xml");
   }
}
