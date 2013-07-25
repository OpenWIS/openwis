package org.openwis.factorytests.admin.metadata;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openwis.factorytests.admin.AdminFactoryTest;

/**
 * Factory test: VT-OPENWIS-ADM-MDS-01
 */
public class Test_VT_OPENWIS_ADM_MDS_01_CreateMetadata extends AdminFactoryTest {

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      dataSet.addTable("openwis_update_frequency");
      dataSet.addTable("openwis_product_metadata");
      dataSet.addTable("openwis_pattern_metadata_mapping");
      dataSet.addTable("openwis_request");
      dataSet.addTable("openwis_value");
      dataSet.addTable("openwis_parameter");
      dataSet.addTable("openwis_parameter_values");
      dataSet.addTable("openwis_requests_parameters");
      dataSet.addTable("openwis_processed_request");
      dataSet.addTable("metadata", "select * from metadata where istemplate='n'");
      return dataSet;
   }

   protected void setTextFieldValue(String textFieldLabel, String value) {
      selenium.type("//label[text()='" + textFieldLabel + "']/following-sibling::input", value);
   }

   private void openCreateMDSection() throws Exception {
      loginAsAdmin();
      rebuildIndex();
      addDefaultTemplates();
      openAdminHomePage();
      pause(1000);
      selectAdminMenu(SECTION_METADATA_SERVICE, SECTION_CREATE_METADATA);      
   }
   
   private void createValidMD() throws Exception {
      // Fill the form
      setTextFieldValue("urn:x-wmo:md:", "int.wmo.wis");
      setTextFieldValue("::", "FCSN32ESWI");
      //setComboValue("Data policy:", "public");
      setComboValue("Template:", "Template for OpenWIS");
      setComboValue("Categories:", "datasets");

      // submit
      clickOnButton("Create");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_01_01() throws Exception {
      openCreateMDSection();
      pause(500);
      createValidMD();

      waitForElementPresence(
            "//span[contains(@class, 'x-window-header-text')][text()='Template for OpenWIS']",
            true, 5);
      pause(1000);

      // Check results in DB
      ITable metadataTable = databaseTester.getConnection().createQueryTable(
            "metadata_without_templates", "select * from metadata where istemplate='n'");
      ITable productMetadataTable = databaseTester.getConnection().createTable(
            "openwis_product_metadata");

      // Load expected data from an XML dataset
      IDataSet expectedDataSet = loadDataSet("/admin/metadata/create/final-dataset_01.xml");

      assertFilteredTable(expectedDataSet, metadataTable, "metadata");
      assertFilteredTable(expectedDataSet, productMetadataTable, "openwis_product_metadata");
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_01_02() throws Exception {
      openCreateMDSection();
      createValidMD();
      
      waitForMetadataViewer("Template for OpenWIS", 5);
      pause(1000);

      // close the editor
      closeMetadataViewer(true);

      createValidMD();

      waitForTextPresent("URN already exists.", 5);
   }
}
