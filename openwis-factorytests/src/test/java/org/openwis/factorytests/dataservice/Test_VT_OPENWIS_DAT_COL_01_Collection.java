package org.openwis.factorytests.dataservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Statement;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.dataservice.ProductMetadataService;

/**
 * Factory test: VT_OPENWIS_DAT_COL_01
 */
public class Test_VT_OPENWIS_DAT_COL_01_Collection extends DataServiceFactoryTest {

   /** Assert file has been ingested by collection from gts */
   protected void assertFileIngested(String filename, Long metadataId) throws Exception {
      assertFileIngested(filename, metadataId, true);
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_01() throws Exception {
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();
      Long id = pms.createProductMetadata(pm);

      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/A_SMVF11BIRK110000_C_LFPW_"));

      Calendar nowMinus2Sec = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      nowMinus2Sec.add(Calendar.SECOND, -5);
      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

      // fnc file
      String dstFileName = testFile.getName() + FNC_DATETIME_FORMAT.format(now.getTime()) + ".bin";
      File dstFile = new File(INCOMING_DIR, dstFileName);
      // fnc file in sub-folder
      String dstFileName2 = testFile.getName() + FNC_DATETIME_FORMAT.format(nowMinus2Sec.getTime())
            + ".bin";
      File dstFile2 = new File(INCOMING_DIR + "/test", dstFileName2);

      FileUtils.copyFile(testFile, dstFile);
      FileUtils.copyFile(testFile, dstFile2);
      pause(4000);
      assertFileIngested(dstFile.getName(), id);
      assertFileIngested(dstFile2.getName(), id);

      // fnc file without md
      String noMdDstFileName = "A_AAAA12BIRK110000_C_LFPW_"
            + FNC_DATETIME_FORMAT.format(now.getTime()) + ".bin";
      File noMdDstFile = new File(INCOMING_DIR, noMdDstFileName);
      FileUtils.copyFile(testFile, noMdDstFile);
      pause(3000);
      assertFileRejected(noMdDstFile.getName());

      // bad formatted file 
      String badDstFileName = "A_AA12BIRK110000_C_LFPW_"
            + FNC_DATETIME_FORMAT.format(now.getTime()) + ".bin";
      File badDstFile = new File(INCOMING_DIR, badDstFileName);
      FileUtils.copyFile(testFile, badDstFile);
      pause(3000);
      assertFileRejected(badDstFile.getName());
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_02() throws Exception {
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();
      Long id = pms.createProductMetadata(pm);
      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      // wmo format does not have seconds
      now.set(Calendar.SECOND, 0);

      // Copy valid wmo file
      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/EGRR00000000.b"));
      prepareAndCopyWMOFileForIngestion(testFile, now);

      pause(4000);

      String fnc = getFncFileName("SMVF11BIRK", "EGRR", "bin", now);
      assertFileIngested(fnc, id);
      String fncRejected = getFncFileName("AAAA99KDDL", "EGRR", "bin", now);
      assertFileRejected(fncRejected);

      // Copy invalid wmo file (date/time) not filtered
      FileUtils.copyFileToDirectory(testFile, new File(INCOMING_DIR));
      pause(2000);
      assertTrue(tempContainsFile(testFile.getName()));
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_03() throws Exception {
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();

      Long id = pms.createProductMetadata(pm);
      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/A_SMVF11BIRK110000_C_LFPW_"));
      File dstFile = new File(INCOMING_DIR, getFncFileName("SMVF11BIRK", "EGRR", "bin", now));
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileIngested(dstFile.getName(), id);

      // Set ingestion filter and check the file is rejected
      IDataSet dataSet = loadDataSet("/dataservice/collection/init-dataset_03.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);

      now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      dstFile = new File(INCOMING_DIR, getFncFileName("SMVF11BIRK", "EGRR", "bin", now));
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileIgnored(dstFile.getName());
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_04() throws Exception {
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();

      pms.createProductMetadata(pm);
      Calendar date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      date.add(Calendar.HOUR, -1);

      String testCollectionDir = getAbsolutePath("src/test/resources/dataservice/collection");
      File testFile1 = new File(testCollectionDir, "checksum_test-1");
      File testFile2 = new File(testCollectionDir, "checksum_test-2");
      File testFile3 = new File(testCollectionDir, "checksum_test-3");

      File dstFile1 = new File(INCOMING_DIR, getFncFileName("SMVF11BIRK", "EGRR", "bin", date));
      FileUtils.copyFile(testFile1, dstFile1);

      date.add(Calendar.MINUTE, 1);
      File dstFile2 = new File(INCOMING_DIR, getFncFileName("SMVF11BIRK", "EGRR", "bin", date));
      FileUtils.copyFile(testFile2, dstFile2);

      date.add(Calendar.MINUTE, 1);
      File dstFile3 = new File(INCOMING_DIR, getFncFileName("SMVF11BIRK", "EGRR", "bin", date));
      FileUtils.copyFile(testFile3, dstFile3);

      pause(4000);

      // Check db values
      ITable cachedFileTable = databaseTester.getConnection().createQueryTable(
            "openwis_cached_file",
            "select * from openwis_cached_file where filename='" + dstFile1.getName() + "'");
      String checksum1 = String.valueOf(cachedFileTable.getValue(0, "checksum"));
      cachedFileTable = databaseTester.getConnection().createQueryTable("openwis_cached_file",
            "select * from openwis_cached_file where filename='" + dstFile2.getName() + "'");
      String checksum2 = String.valueOf(cachedFileTable.getValue(0, "checksum"));
      cachedFileTable = databaseTester.getConnection().createQueryTable("openwis_cached_file",
            "select * from openwis_cached_file where filename='" + dstFile3.getName() + "'");
      String checksum3 = String.valueOf(cachedFileTable.getValue(0, "checksum"));

      assertFalse(checksum1.equals(checksum2));
      assertFalse(checksum1.equals(checksum3));
      assertTrue(checksum2.equals(checksum3));
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_05() throws Exception {
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();

      pms.createProductMetadata(pm);
      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/A_SMVF11BIRK110000_C_LFPW_"));
      File dstFile = new File(INCOMING_DIR, getFncFileName("SMVF11BIRK", "EGRR", "tmp", now));
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);

      // check file has not been injected
      assertTrue(dstFile.exists());
      dstFile.delete();
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_06() throws Exception {
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();
      Long id = pms.createProductMetadata(pm);
      pm.setId(id);

      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/A_SMVF11BIRK110000_C_LFPW_"));
      File dstFile = new File(INCOMING_DIR, getFncFileName("MYTESTFILE", "EGRR", "bin", now));
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileRejected(dstFile.getName());

      // Check fnc pattern
      pm.setFncPattern("^A_MYTEST.*");
      pms.updateProductMetadata(pm);
      System.out.println("Waiting for md pattern to be udpated...");
      pause(10000);

      now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      dstFile = new File(INCOMING_DIR, getFncFileName("MYTESTFILE", "EGRR", "bin", now));
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileIngested(dstFile.getName(), id);

      // Check overridden fnc pattern
      pm.setFncPattern("^A_FILE.*");
      pms.updateProductMetadata(pm);
      System.out.println("Waiting for md pattern to be udpated...");
      pause(10000);

      now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      dstFile = new File(INCOMING_DIR, getFncFileName("MYTESTFILE", "EGRR", "bin", now));
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileRejected(dstFile.getName());
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_07() throws Exception {
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();
      Long id = pms.createProductMetadata(pm);
      pm.setId(id);

      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/A_SMVF11BIRK110000_C_LFPW_"));
      File dstFile = new File(INCOMING_DIR, getFncFileName("SMVF11BIRK", "EGRR", "bin", now));
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);
      assertFileIngested(dstFile.getName(), id);

      // Set received from gts as false to simulate a file received from replication
      Statement s = databaseTester.getConnection().getConnection().createStatement();
      s.execute("update openwis_cached_file set received_from_gts='FALSE' where filename='"
            + dstFile.getName() + "'");
      s.close();

      // try to ingest again
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);

      // Duplicate: check file has been removed from incoming and is not in temp
      assertFalse(dstFile.exists());
      assertFalse(tempContainsFile(dstFile.getName()));
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_08() throws Exception {
      ProductMetadataService pms = getProductMetadataService();

      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

      // high prio product with no md
      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/A_SMVF11BIRK110000_C_LFPW_"));
      File dstFile = new File(INCOMING_DIR, getFncFileName("SMVF11BIRK", "EGRR", "bin", now));
      FileUtils.copyFile(testFile, dstFile);
      pause(4000);

      // check stop-gap md has been created
      ProductMetadata pm = pms.getProductMetadataByUrn("urn:x-wmo:md:int.wmo.wis::SMVF11BIRK");
      assertNotNull(pm);
      assertEquals(pm.isStopGap(), Boolean.TRUE);

      // stop-gap file is also copied in temp, remove to get assert working
      File tempFile = findFileInDir(new File(TEMP_DIR), dstFile.getName());
      tempFile.delete();

      assertFileIngested(dstFile.getName(), pm.getId());
   }

   @Test
   public void test_VT_OPENWIS_DAT_COL_01_09() throws Exception {
      ProductMetadata pm = createProductMetadata();
      ProductMetadataService pms = getProductMetadataService();
      Long id = pms.createProductMetadata(pm);
      pm.setId(id);

      Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      // wmo format does not have seconds
      now.set(Calendar.SECOND, 0);

      // Copy wmo file
      now.add(Calendar.HOUR, -2);
      File testFile = new File(
            getAbsolutePath("src/test/resources/dataservice/collection/EGRR00000000.b"));
      prepareAndCopyWMOFileForIngestion(testFile, now);
      pause(5000);
      // Check fnc and file extension
      String fnc = getFncFileName("SMVF11BIRK", "EGRR", "bin", now);
      assertFileIngested(fnc, id);

      // Update file extension of pm
      pm.setFileExtension("gif");
      pms.updateProductMetadata(pm);

      now.add(Calendar.HOUR, 1);
      prepareAndCopyWMOFileForIngestion(testFile, now);
      pause(4000);
      // Check fnc and file extension
      fnc = getFncFileName("SMVF11BIRK", "EGRR", "gif", now);
      assertFileIngested(fnc, id);

      // Test for OWT-100
      // Update overridden file extension of pm
      pm.setOverridenFileExtension("txt");
      pms.updateProductMetadata(pm);

      now.add(Calendar.HOUR, 1);
      prepareAndCopyWMOFileForIngestion(testFile, now);
      pause(4000);
      // Check fnc and file extension
      fnc = getFncFileName("SMVF11BIRK", "EGRR", "txt", now);
      assertFileIngested(fnc, id);
   }
}
