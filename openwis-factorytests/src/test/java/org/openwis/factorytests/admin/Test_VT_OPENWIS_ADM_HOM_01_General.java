package org.openwis.factorytests.admin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.junit.Test;

/**
 * Factory test: VT-OPENWIS-ADM-HOM-01
 */
public class Test_VT_OPENWIS_ADM_HOM_01_General extends AdminFactoryTest {

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      return dataSet;
   }

   @Test
   public void test_VT_OPENWIS_ADM_HOM_01_01() throws Exception {
      openAdminHomePage();
      waitForTextPresent("You must be logged in", 3);
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_01_02() throws Exception {
      loginAsOperator();
      openAdminHomePage();
      // TODO check available functions
      assertTrue(selenium.isTextPresent(SECTION_METADATA_SERVICE));
      assertFalse(selenium.isTextPresent(SECTION_SECURITY_SERVICE));
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_01_03() throws Exception {
      loginAsAccessAdmin();
      openAdminHomePage();
      // TODO check available functions
      //assertFalse(selenium.isTextPresent(SECTION_METADATA_SERVICE));
      assertTrue(selenium.isTextPresent(SECTION_SECURITY_SERVICE));
   }

   @Test
   public void test_VT_OPENWIS_ADM_MDS_01_04() throws Exception {
      loginAsAdmin();
      openAdminHomePage();
      pause(1000);

      // TODO check available functions
      assertTrue(selenium.isTextPresent(SECTION_METADATA_SERVICE));
      assertTrue(selenium.isTextPresent(SECTION_SECURITY_SERVICE));
   }

}
