package org.openwis.factorytests.admin.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openwis.factorytests.admin.AdminFactoryTest;

/**
 * Factory test: VT_OPENWIS_ADM_MDS_03_OAI
 */
public class Test_VT_OPENWIS_ADM_MDS_03_OAI_ConfigureHarvesting extends AdminFactoryTest {

   public static final String ID_HARVESTING_GRID = "harvestingGrid";

   public static final String BTN_OAIPMH = "OAIPMH Harvester";

   public static final String LABEL_NAME = "Name:";

   public static final String LABEL_URL = "URL:";

   public static final String LABEL_CATEGORY = "Categories:";

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      dataSet.addTable("harvestingtask");
      dataSet.addTable("harvestingtaskconfiguration");
      dataSet.addTable("harvestingtaskresult");
      dataSet.addTable("metadata", "select * from metadata where istemplate='n'");
      return dataSet;
   }

   private void openConfigureHarvestingSection() throws Exception {
      loginAsAdmin();
      openAdminHomePage();
      pause(1000);
      selectAdminMenu(SECTION_METADATA_SERVICE, SECTION_CONFIGURE_HARVESTING);
   }

   private void setOaiHarvestingTask(String name, String url, String category) throws Exception {
      waitForTextPresent("Create OAIPMH Harvester", 2);

      setTextFieldValue(LABEL_NAME, name);
      setTextFieldValue(LABEL_URL, url);
      if (category != null) {
         setComboValue(LABEL_CATEGORY, category);
      }

      clickOnButton(BTN_SAVE);
      pause(1000);
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_03_OAI_01() throws Exception {
      openConfigureHarvestingSection();
      pause(1000);

      assertEquals(0, countGridRows(ID_HARVESTING_GRID));

      clickOnButton(BTN_NEW);
      pause(200);
      selenium.click("//span[contains(@class,'x-menu-item-text') and text()='" + BTN_OAIPMH + "']");
      
      // check mandatory fields
      setOaiHarvestingTask("MyHarvesting", "http://localhost:8080/oai", null);
      pause(200);
      // the form is still displayed (combo in red)
      assertTrue(selenium.isTextPresent("Create OAIPMH Harvester"));
      
      setOaiHarvestingTask("MyHarvesting", "http://localhost:8080/oai", "datasets");

      assertEquals(1, countGridRows(ID_HARVESTING_GRID));
      assertEquals("MyHarvesting", getGridCellText(ID_HARVESTING_GRID, 1, 3));
      assertEquals("oaipmh", getGridCellText(ID_HARVESTING_GRID, 1, 4));

      // Check results in DB
      ITable harvestingtaskTable = databaseTester.getConnection().createTable("harvestingtask");
      ITable harvestingtaskconfigurationTable = databaseTester.getConnection().createTable(
            "harvestingtaskconfiguration");

      // Load expected data from an XML dataset
      IDataSet expectedDataSet = loadDataSet("/admin/metadata/harvesting/final-dataset_01.xml");

      assertFilteredTable(expectedDataSet, harvestingtaskTable, "harvestingtask");
      assertFilteredTable(expectedDataSet, harvestingtaskconfigurationTable,
            "harvestingtaskconfiguration");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_03_OAI_02() throws Exception {
      openConfigureHarvestingSection();
      pause(1000);

      clickOnButton(BTN_NEW);
      pause(200);
      selenium.click("//span[contains(@class,'x-menu-item-text') and text()='" + BTN_OAIPMH + "']");
      setOaiHarvestingTask("MyHarvesting", "http://localhost:8080/oai", "datasets");

      selectGridCell(ID_HARVESTING_GRID, 1, 1);
      pause(200);
      clickOnButton(BTN_EDIT);
      setOaiHarvestingTask("MyHarvesting2", "http://localhost:8080/oai2", "datasets");

      assertEquals(1, countGridRows(ID_HARVESTING_GRID));
      assertEquals("MyHarvesting2", getGridCellText(ID_HARVESTING_GRID, 1, 3));
      assertEquals("oaipmh", getGridCellText(ID_HARVESTING_GRID, 1, 4));

      // Check results in DB
      ITable harvestingtaskTable = databaseTester.getConnection().createTable("harvestingtask");
      ITable harvestingtaskconfigurationTable = databaseTester.getConnection().createTable(
            "harvestingtaskconfiguration");

      // Load expected data from an XML dataset
      IDataSet expectedDataSet = loadDataSet("/admin/metadata/harvesting/final-dataset_02.xml");

      assertFilteredTable(expectedDataSet, harvestingtaskTable, "harvestingtask");
      assertFilteredTable(expectedDataSet, harvestingtaskconfigurationTable,
            "harvestingtaskconfiguration");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_03_OAI_03() throws Exception {
      openConfigureHarvestingSection();
      pause(1000);

      clickOnButton(BTN_NEW);
      pause(200);
      selenium.click("//span[contains(@class,'x-menu-item-text') and text()='" + BTN_OAIPMH + "']");
      setOaiHarvestingTask("MyHarvesting", "http://localhost:8080/oai", "datasets");

      assertEquals(1, countGridRows(ID_HARVESTING_GRID));

      selectGridCell(ID_HARVESTING_GRID, 1, 1);
      pause(200);
      clickOnButton(BTN_REMOVE);
      waitForTextPresent(CONFIRM_DELETE_MSG, 2);
      clickOnButton(BTN_YES);
      pause(200);
      clickOnButton(BTN_OK);
      pause(1000);

      assertEquals(0, countGridRows(ID_HARVESTING_GRID));

      // Check results in DB
      ITable harvestingtaskTable = databaseTester.getConnection().createTable("harvestingtask");
      ITable harvestingtaskconfigurationTable = databaseTester.getConnection().createTable(
            "harvestingtaskconfiguration");

      assertEquals(0, harvestingtaskTable.getRowCount());
      assertEquals(0, harvestingtaskconfigurationTable.getRowCount());
   }
}
