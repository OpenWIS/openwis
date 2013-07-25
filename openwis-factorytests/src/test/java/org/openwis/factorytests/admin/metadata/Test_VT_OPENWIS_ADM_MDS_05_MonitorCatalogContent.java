package org.openwis.factorytests.admin.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.Arrays;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.openwis.factorytests.admin.AdminFactoryTest;

/**
 * Factory test: VT-OPENWIS-ADM-MDS-05
 */
public class Test_VT_OPENWIS_ADM_MDS_05_MonitorCatalogContent extends AdminFactoryTest {
   public static final String ID_METADATA_GRID = "metadataGrid";

   public static final String LABEL_TEXT_SEARCH = "Text search:";

   public static final String BTN_SEARCH = "Search";

   public static final String BTN_RESET = "Reset";

   public static final String BTN_DUPLICATE = "Duplicate...";

   public static final String BTN_EDIT_METAINFO = "Edit MetaInfo...";

   public static final String BTN_EDIT_CATEGORY = "Edit category...";

   public static final String LABEL_NEW_METADATA_URN = "Please enter a valid URN :";

   public static final String LABEL_METADATA_URN = "Metadata URN:";

   public static final String LABEL_METADATA_TITLE = "Title:";

   public static final String LABEL_ORIGINATOR = "Originator:";

   public static final String LABEL_GTS_CATEGORY = "GTS Category:";

   public static final String LABEL_DATA_POLICY = "Data Policy:";

   public static final String LABEL_OVERRIDDEN_DATA_POLICY = "Overridden Data Policy:";

   public static final String LABEL_FNC_PATTERN = "Overriden FNC Pattern:";

   public static final String LABEL_GTS_PRIORITY = "GTS Priority:";

   public static final String LABEL_FILE_EXTENSION = "File extension:";

   public static final String LABEL_OVERRIDEN_FILE_EXTENSION = "Overriden file extension:";

   public static final String LABEL_CATEGORIES = "Categories:";

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      dataSet.addTable("openwis_update_frequency");
      dataSet.addTable("openwis_product_metadata");
      dataSet.addTable("openwis_pattern_metadata_mapping");
      dataSet.addTable("categories", "select * from categories where name<>'datasets'");
      dataSet.addTable("metadata", "select * from metadata where istemplate='n'");
      dataSet.addTable("datapolicy",
            "select * from datapolicy where name<>'public' and name<>'additional-default'");
      dataSet.addTable("operationallowed");
      dataSet.addTable("datapolicyalias");
      return dataSet;
   }

   private void openMonitorCatalogContentSection(String mdDirToLoad) throws Exception {
      loginAsAdmin();
      rebuildIndex();

      if (mdDirToLoad != null) {
         loadMetadata(mdDirToLoad);
      }

      pause(1000);
      openAdminHomePage();
      pause(1000);
      selectAdminMenu(SECTION_METADATA_SERVICE, SECTION_MONITOR_CATALOG_CONTENT);
      waitForLoadingVanish(5);
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_01() throws Exception {
      openMonitorCatalogContentSection(null);
      assertEquals(0, countGridRows(ID_METADATA_GRID));
   }
   
   protected void clickSearchOrResetButton(String buttonName) {
      selenium.click("//div[contains(@class, 'administrationTitle1') and text()='Monitor Catalogue Content']/following-sibling::div//button[text()='"
            + buttonName + "']");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_02() throws Exception {
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");
      assertEquals(4, countGridRows(ID_METADATA_GRID));
      // Check MD columns
      assertEquals("urn:x-wmo:md:int.wmo.wis::ECEU82LFRO", getGridCellText(ID_METADATA_GRID, 1, 1));
      assertEquals("urn:x-wmo:md:int.wmo.wis::ECEU83LFRO", getGridCellText(ID_METADATA_GRID, 2, 1));
      assertEquals("urn:x-wmo:md:int.wmo.wis::ECEU84LFRO", getGridCellText(ID_METADATA_GRID, 3, 1));
      assertEquals("urn:x-wmo:md:int.wmo.wis::md1", getGridCellText(ID_METADATA_GRID, 4, 1));

      assertEquals(
            "ECEU82 bulletin available from LFRO at 01, 02, 04, 05, 07, 08, 10, 11, 13, 14, 16, 17, 19, 20, 22 and 23 UTC",
            getGridCellText(ID_METADATA_GRID, 1, 2));
      assertEquals("ECEU83 bulletin available from LFRO at 03, 09, 15 and 21 UTC",
            getGridCellText(ID_METADATA_GRID, 2, 2));
      assertEquals("ECEU84 bulletin available from LFRO at 00, 06, 12 and 18 UTC",
            getGridCellText(ID_METADATA_GRID, 3, 2));
      assertEquals("MD_1", getGridCellText(ID_METADATA_GRID, 4, 2));

      // originator
      assertEquals("RTH Toulouse", getGridCellText(ID_METADATA_GRID, 1, 4));
      assertEquals("RTH focal point", getGridCellText(ID_METADATA_GRID, 4, 4));

      // assert process = import
      assertEquals("LOCAL", getGridCellText(ID_METADATA_GRID, 1, 5));
      assertEquals("LOCAL", getGridCellText(ID_METADATA_GRID, 2, 5));
      assertEquals("LOCAL", getGridCellText(ID_METADATA_GRID, 3, 5));
      assertEquals("LOCAL", getGridCellText(ID_METADATA_GRID, 4, 5));

      // gts category for non essential product
      assertEquals("WMO Essential", getGridCellText(ID_METADATA_GRID, 1, 6));
      assertEquals("WMO Essential", getGridCellText(ID_METADATA_GRID, 2, 6));
      assertEquals("WMO Additional", getGridCellText(ID_METADATA_GRID, 3, 6));
      assertEquals("WMO Essential", getGridCellText(ID_METADATA_GRID, 4, 6));

      // fnc pattern
      assertEquals("", getGridCellText(ID_METADATA_GRID, 1, 7));
      assertEquals("", getGridCellText(ID_METADATA_GRID, 2, 7));
      assertEquals("", getGridCellText(ID_METADATA_GRID, 3, 7));
      assertEquals("PATTERN", getGridCellText(ID_METADATA_GRID, 4, 7));

      assertEquals("2", getGridCellText(ID_METADATA_GRID, 1, 8));
      assertEquals("3", getGridCellText(ID_METADATA_GRID, 2, 8));
      assertEquals("1", getGridCellText(ID_METADATA_GRID, 3, 8));
      assertEquals("3", getGridCellText(ID_METADATA_GRID, 4, 8));

      assertEquals("public", getGridCellText(ID_METADATA_GRID, 1, 9));
      assertEquals("public", getGridCellText(ID_METADATA_GRID, 2, 9));
      assertEquals("additional-default", getGridCellText(ID_METADATA_GRID, 3, 9));
      assertEquals("public", getGridCellText(ID_METADATA_GRID, 4, 9));

      assertEquals("", getGridCellText(ID_METADATA_GRID, 1, 10));
      assertEquals("DCPC Toulouse 1", getGridCellText(ID_METADATA_GRID, 4, 10));

      // Assert enabled/disabled actions
      assertFalse(isButtonEnabled(BTN_DUPLICATE));
      assertFalse(isButtonEnabled(BTN_VIEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertFalse(isButtonEnabled(BTN_EDIT_METAINFO));
      assertFalse(isButtonEnabled(BTN_EDIT_CATEGORY));
      assertFalse(isButtonEnabled(BTN_REMOVE));
      //assertFalse(isButtonEnabled(BTN_EXPORT));

      selectGridCell(ID_METADATA_GRID, 1, 1);
      pause(500);
      assertTrue(isButtonEnabled(BTN_DUPLICATE));
      assertTrue(isButtonEnabled(BTN_VIEW));
      assertTrue(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_EDIT_METAINFO));
      assertTrue(isButtonEnabled(BTN_EDIT_CATEGORY));
      assertTrue(isButtonEnabled(BTN_REMOVE));
      //assertTrue(isButtonEnabled(BTN_EXPORT));

      Point p1 = new Point(1, 2);
      Point p2 = new Point(1, 3);
      selectGridCells(ID_METADATA_GRID, Arrays.asList(p1, p2));
      pause(500);
      assertFalse(isButtonEnabled(BTN_DUPLICATE));
      assertFalse(isButtonEnabled(BTN_VIEW));
      assertFalse(isButtonEnabled(BTN_EDIT));
      assertTrue(isButtonEnabled(BTN_EDIT_METAINFO));
      assertTrue(isButtonEnabled(BTN_EDIT_CATEGORY));
      assertTrue(isButtonEnabled(BTN_REMOVE));
      //assertTrue(isButtonEnabled(BTN_EXPORT));
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_03() throws Exception {
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");

      setTextFieldValue(LABEL_TEXT_SEARCH, "ECEU82LFRO");
      selenium.keyUp("//label[text()='" + LABEL_TEXT_SEARCH + "']/following-sibling::div/input",
            "O");
      clickSearchOrResetButton(BTN_SEARCH);
      pause(1000);
      assertEquals(1, countGridRows(ID_METADATA_GRID));
      assertEquals("urn:x-wmo:md:int.wmo.wis::ECEU82LFRO", getGridCellText(ID_METADATA_GRID, 1, 1));

      clickSearchOrResetButton(BTN_RESET);
      pause(1000);
      // Check MD columns
      assertEquals(4, countGridRows(ID_METADATA_GRID));
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_04() throws Exception {
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");
      // Duplicate...
      selectGridCell(ID_METADATA_GRID, 4, 1);
      clickOnButton(BTN_DUPLICATE);
      waitForTextPresent(LABEL_NEW_METADATA_URN, 5);
      selenium.type("//span[text()='" + LABEL_NEW_METADATA_URN + "']/following-sibling::div/input",
            "urn:x-wmo:md:int.wmo.wis::md1");

      clickOnButton(BTN_CANCEL);
      pause(500);

      selectGridCell(ID_METADATA_GRID, 4, 1);
      clickOnButton(BTN_DUPLICATE);
      waitForTextPresent(LABEL_NEW_METADATA_URN, 5);
      selenium.type("//span[text()='" + LABEL_NEW_METADATA_URN + "']/following-sibling::div/input",
            "urn:x-wmo:md:int.wmo.wis::md1");
      clickOnButton(BTN_OK);
      pause(500);

      waitForTextPresent("URN already exists", 5);
      clickOnButton(BTN_OK);

      selectGridCell(ID_METADATA_GRID, 4, 1);
      clickOnButton(BTN_DUPLICATE);
      waitForTextPresent(LABEL_NEW_METADATA_URN, 5);
      selenium.type("//span[text()='" + LABEL_NEW_METADATA_URN + "']/following-sibling::div/input",
            "urn:x-wmo:md:int.wmo.wis::md2");
      clickOnButton(BTN_OK);

      waitForMetadataViewer("MD_1", 10);
      waitForLoadingVanish(10);
      closeMetadataViewer(true);

      // Check MD columns
      assertEquals(5, countGridRows(ID_METADATA_GRID));
      assertEquals("urn:x-wmo:md:int.wmo.wis::md2", getGridCellText(ID_METADATA_GRID, 5, 1));
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_05() throws Exception {
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");
      selectGridCell(ID_METADATA_GRID, 4, 1);
      clickOnButton(BTN_VIEW);
      waitForMetadataViewer("MD_1", 5);
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_06() throws Exception {
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");
      selectGridCell(ID_METADATA_GRID, 4, 1);
      clickOnButton(BTN_EDIT);
      waitForMetadataViewer("MD_1", 10);
      waitForLoadingVanish(10);
      selenium.type("//input[@value='MD_1']", "MD_2");
      clickOnButton("Save");
      pause(1000);
      closeMetadataViewer(true);
      assertEquals("MD_1", getGridCellText(ID_METADATA_GRID, 4, 2));
   }

   protected String getReadonlyValue(String label) {
      return selenium.getText("//td[div/text()='" + label + "']/following-sibling::td/div/text()");
   }

   protected void setComboValue(String comboLabel, String value) throws Exception {
      // click the down arrow image on the right of the ComboBox and assumes
      // that there is a label before the component
      selenium
            .click("//td[div/text()='"
                  + comboLabel
                  + "']/following-sibling::td/div/descendant::img[contains(@class, 'x-form-arrow-trigger')]");
      // wait for a drop down list of options to be visible
      pause(1000);

      // click the required drop down item based on the text of the target
      // item
      selenium
            .click("//div[contains(@class, 'x-combo-list')]/descendant::div[contains(@class, 'x-combo-list-item')][text()='"
                  + value + "']");
      pause(1000);
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_07() throws Exception {
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");

      selectGridCell(ID_METADATA_GRID, 4, 1);
      clickOnButton(BTN_EDIT_METAINFO);
      waitForTextPresent(LABEL_METADATA_URN, 5);

      assertEquals("urn:x-wmo:md:int.wmo.wis::md1", getReadonlyValue(LABEL_METADATA_URN));
      assertEquals("MD_1", getReadonlyValue(LABEL_METADATA_TITLE));

      assertEquals("RTH focal point", getReadonlyValue(LABEL_ORIGINATOR));

      // TODO assert process = import
      //assertEquals("import", getReadonlyValue(ID_METADATA_GRID));

      // TODO category for non essential product
      //assertEquals("WMO Essential", getReadonlyValue(ID_METADATA_GRID));

      // TODO fnc pattern
      //assertEquals("", getGridCellText(ID_METADATA_GRID, 1, 6));

      // TODO priority
      //assertEquals("", getGridCellText(ID_METADATA_GRID, 1, 7));

      // TODO data policy
      //assertEquals("", getGridCellText(ID_METADATA_GRID, 1, 8));

      // TODO local data source
      //assertEquals("", getGridCellText(ID_METADATA_GRID, 1, 9));

   }

   @Test
   public void test_OWT_1() throws Exception {
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");

      // check dp for md_1
      ITable metadataTable = databaseTester.getConnection().createQueryTable("metadata",
            "select * from metadata where title='MD_1'");
      Integer dp1 = (Integer) metadataTable.getValue(0, "datapolicy");
      assertEquals(1, dp1.intValue());

      selectGridCell(ID_METADATA_GRID, 4, 1);
      clickOnButton(BTN_EDIT_METAINFO);
      waitForTextPresent(LABEL_METADATA_URN, 5);

      // Override data policy
      setComboValue(LABEL_OVERRIDDEN_DATA_POLICY, "additional-default");
      clickOnButton(BTN_SAVE);
      waitForPleaseWaitVanish(5);

      metadataTable = databaseTester.getConnection().createQueryTable("metadata",
            "select * from metadata where title='MD_1'");
      Integer dp2 = (Integer) metadataTable.getValue(0, "datapolicy");
      assertEquals(2, dp2.intValue());
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_08() throws Exception {
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");

      selectGridCell(ID_METADATA_GRID, 1, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_NO);

      assertEquals(4, countGridRows(ID_METADATA_GRID));

      selectGridCell(ID_METADATA_GRID, 1, 1);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);

      assertEquals(3, countGridRows(ID_METADATA_GRID));

      Point p1 = new Point(1, 1);
      Point p2 = new Point(1, 2);
      selectGridCells(ID_METADATA_GRID, Arrays.asList(p1, p2));
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);
      // confirm deletion ok
      clickOnButton(BTN_YES);
      waitForPleaseWaitVanish(5);

      assertEquals(1, countGridRows(ID_METADATA_GRID));

      ITable metadataTable = databaseTester.getConnection().createQueryTable("metadata",
            "select * from metadata where istemplate='n'");
      assertEquals(1, metadataTable.getRowCount());
      ITable productMetadataTable = databaseTester.getConnection().createTable("openwis_product_metadata");
      assertEquals(1, productMetadataTable.getRowCount());
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_05_09() throws Exception {
      IDataSet dataSet = loadDataSet("/admin/metadata/catalogcontent/init-dataset_09.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);
      openMonitorCatalogContentSection("src/test/resources/admin/metadata/catalogcontent/md_02");

      selectGridCell(ID_METADATA_GRID, 1, 1);
      clickOnButton(BTN_EDIT_CATEGORY);
      waitForTextPresent(LABEL_CATEGORIES, 2);
      super.setComboValue(LABEL_CATEGORIES, "mycategory");
      clickOnButton(BTN_SAVE);
      waitForTextPresent("Update category was successful.", 5);
      clickOnButton(BTN_OK);

      assertEquals("mycategory", getGridCellText(ID_METADATA_GRID, 1, 3));
   }
}
