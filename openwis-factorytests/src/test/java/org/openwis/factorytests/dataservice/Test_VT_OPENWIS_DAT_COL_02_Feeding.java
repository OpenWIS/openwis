package org.openwis.factorytests.dataservice;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.dataservice.ProductMetadataService;

/**
 * Factory test: VT_OPENWIS_DAT_COL_01
 */
public class Test_VT_OPENWIS_DAT_COL_02_Feeding extends DataServiceFactoryTest {

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = (QueryDataSet) super.getDataSet();
      dataSet.addTable("openwis_mssfss_feeding_filter");
      dataSet.addTable("openwis_file_packer_instance");
      return dataSet;
   }

   /** Assert file has been ingested by replication */
   protected void assertFileIngested(String filename, Long metadataId) throws Exception {
      assertFileIngested(filename, metadataId, false);
   }
   
   @Test
   public void test_VT_OPENWIS_DAT_COL_01_01() throws Exception {
      FileUtils.cleanDirectory(new File(OUTGOING_DIR));
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();
      Long id = pms.createProductMetadata(pm);

      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/A_SMVF11BIRK110000_C_LFPW_"));

      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      String dstFileName = testFile.getName() + FNC_DATETIME_FORMAT.format(now.getTime()) + ".bin";
      File dstFile = new File(FROM_REPLICATION_DIR, dstFileName);

      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileIngested(dstFile.getName(), id);

      // Set feeding filter and check the file is copied in outgoing
      IDataSet dataSet = loadDataSet("/dataservice/feeding/init-dataset_02.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      dstFileName = testFile.getName() + FNC_DATETIME_FORMAT.format(now.getTime()) + ".bin";
      dstFile = new File(FROM_REPLICATION_DIR, dstFileName);

      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileIngested(dstFile.getName(), id);
      
      assertTrue(new File(OUTGOING_DIR, "DEBO00000000.b").exists());
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_02() throws Exception {
      FileUtils.cleanDirectory(new File(OUTGOING_DIR));
      ProductMetadata pm = createProductMetadata();
      pm.setFncPattern("^B_SMVF11.*");
      ProductMetadataService pms = getProductMetadataService();
      Long id = pms.createProductMetadata(pm);
      System.out.println("Waiting for md pattern to be udpated...");
      pause(10000);

      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/A_SMVF11BIRK110000_C_LFPW_"));

      // Set feeding filter and check the file is copied in outgoing
      IDataSet dataSet = loadDataSet("/dataservice/feeding/init-dataset_02.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      String dstFileName = "B_SMVF11BIRK" + WMO_DATETIME_FORMAT.format(now.getTime()) + "_C_LFPW_" + FNC_DATETIME_FORMAT.format(now.getTime()) + ".bin";
      File dstFile = new File(FROM_REPLICATION_DIR, dstFileName);

      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileIngested(dstFile.getName(), id);
      
      assertTrue(new File(OUTGOING_DIR, dstFile.getName()).exists());
   }
}
