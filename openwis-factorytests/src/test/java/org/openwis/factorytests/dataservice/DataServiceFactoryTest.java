package org.openwis.factorytests.dataservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.dataservice.ProductMetadataService;
import org.openwis.dataservice.ProductMetadataService_Service;
import org.openwis.factorytests.OpenWisFactoryTest;

/**
 * Abstract class for data service tests.
 */
public abstract class DataServiceFactoryTest extends OpenWisFactoryTest {

   public static final String OPENWIS_DIR = "/var/opt/openwis";

   public static final String INCOMING_DIR = OPENWIS_DIR + "/harness/incoming";

   public static final String OUTGOING_DIR = OPENWIS_DIR + "/harness/outgoing";

   public static final String WORKING_DIR = OPENWIS_DIR + "/harness/working";

   public static final String FROM_REPLICATION_DIR = OPENWIS_DIR
         + "/harness/working/fromReplication";

   public static final String TEMP_DIR = OPENWIS_DIR + "/temp";

   public static final SimpleDateFormat FNC_DATETIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

   public static final SimpleDateFormat WMO_DATETIME_FORMAT = new SimpleDateFormat("ddHHmm");

   static {
      OpenWisFactoryTest.disableSelenium();
      FNC_DATETIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
      OpenWisFactoryTest.disableSelenium();
      WMO_DATETIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

   }

   private ProductMetadataService productMetadataService;

   @Override
   protected String getWebappName() {
      return null;
   }

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
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
      dataSet.addTable("openwis_mapped_metadata");
      dataSet.addTable("openwis_mssfss_ingestion_filter");
      return dataSet;
   }

   /**
    * Gets the product metadata service.
    * @return the product metadata service.
    */
   protected ProductMetadataService getProductMetadataService() {
      try {
         if (productMetadataService == null) {
            String wsdl = "http://localhost:8180/openwis-dataservice-openwis-dataservice-server-ejb-1.0-SNAPSHOT/ProductMetadataService?wsdl";
            ProductMetadataService_Service service = new ProductMetadataService_Service(new URL(
                  wsdl));
            productMetadataService = service.getProductMetadataServicePort();
         }
         return productMetadataService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   protected ProductMetadata createProductMetadata() {
      ProductMetadata pm = new ProductMetadata();
      pm.setUrn("urn:x-wmo:md:int.wmo.wis::SMVF11BIRK");
      pm.setDataPolicy("public");
      pm.setGtsCategory("WMO Essential");
      pm.setLocalDataSource("");
      pm.setOriginator("");
      pm.setPriority(2);
      pm.setProcess("LOCAL");
      pm.setTitle("");
      pm.setFed(false);
      pm.setIngested(false);
      pm.setStopGap(false);
      return pm;
   }

   /** 
    * Assert the given file has been ingested: not in incoming dir / fromreplication dir, 
    * in cache and in db  and associated to the metadata id
    */
   protected void assertFileIngested(String filename, Long metadataId, boolean fromGts)
         throws Exception {
      // Check file is not in incoming/working/temp
      if (fromGts) {
         assertFalse(new File(INCOMING_DIR, filename).exists());
      } else {
         assertFalse(new File(FROM_REPLICATION_DIR, filename).exists());
      }
      assertFalse(workingContainsFile(filename));
      assertFalse(tempContainsFile(filename));

      // Check db values
      ITable cachedFileTable = databaseTester.getConnection().createQueryTable(
            "openwis_cached_file",
            "select * from openwis_cached_file where filename='" + filename + "'");

      assertEquals(1, cachedFileTable.getRowCount());
      if (fromGts) {
         assertEquals(Boolean.TRUE, cachedFileTable.getValue(0, "received_from_gts"));
      } else {
         assertEquals(Boolean.FALSE, cachedFileTable.getValue(0, "received_from_gts"));
      }

      // Check file is in cache folder
      String path = String.valueOf(cachedFileTable.getValue(0, "path"));
      String internalFilename = String.valueOf(cachedFileTable.getValue(0, "internal_filename"));
      File cachedFile = new File(path, internalFilename);
      assertTrue(cachedFile.exists());

      // Check md association
      ITable mappedMetadataTable = databaseTester.getConnection().createQueryTable(
            "openwis_mapped_metadata",
            "select * from openwis_mapped_metadata where filename='" + filename + "'");
      assertEquals(1, mappedMetadataTable.getRowCount());
      assertEquals(BigInteger.valueOf(metadataId),
            mappedMetadataTable.getValue(0, "product_metadata_id"));

   }

   /** 
    * Assert the given file has not been ingested (rejected): not in incoming dir, in temp dir, alarm is raised
    */
   protected void assertFileRejected(String filename) throws Exception {
      assertFileIgnored(filename);

      assertTrue(tempContainsFile(filename));
   }
   
   /** 
    * Assert the given file has not been ingested (rejected): not in incoming dir, 
    */
   protected void assertFileIgnored(String filename) throws Exception {
      // Check file has been removed from incoming
      assertFalse(new File(INCOMING_DIR, filename).exists());

      // Check db values
      ITable cachedFileTable = databaseTester.getConnection().createQueryTable(
            "openwis_cached_file",
            "select * from openwis_cached_file where filename='" + filename + "'");
      assertEquals(0, cachedFileTable.getRowCount());
   }

   /** Check if the given file is contained in the temp dir */
   protected boolean tempContainsFile(String filename) {
      File tempFile = new File(TEMP_DIR);
      return dirContainsFile(tempFile, filename);
   }

   /** Check if the given file is contained in the working dir */
   protected boolean workingContainsFile(String filename) {
      File tempFile = new File(WORKING_DIR);
      return dirContainsFile(tempFile, filename);
   }

   /** Check if the given file is contained in the given dir (recurse in subfolders) */
   protected boolean dirContainsFile(File dir, String filename) {
      return findFileInDir(dir, filename) != null;
   }

   /** 
    * Returns a {@link File} of an existing file with the given file name,  contained in the given dir (recurse in subfolders)
    * or <code>null</code> if not found 
    */
   protected File findFileInDir(File dir, String filename) {
      File f = new File(dir, filename);
      if (f.exists()) {
         return f;
      }

      File[] files = dir.listFiles();
      for (File subdir : files) {
         if (subdir.isDirectory()) {
            File tempF = findFileInDir(subdir, filename);
            if (tempF != null) {
               return tempF;
            }
         }
      }
      return null;
   }

   /** Copy wmo file to incoming, replacing date/time info for bulletins */
   protected void prepareAndCopyWMOFileForIngestion(File testFile, Calendar date) throws Exception {
      FileInputStream in = new FileInputStream(testFile);
      byte[] content = new byte[in.available()];
      in.read(content);
      in.close();

      String contentStr = new String(content);
      contentStr = contentStr.replace("$$$$$$", WMO_DATETIME_FORMAT.format(date.getTime()));
      content = contentStr.getBytes();

      FileOutputStream out = new FileOutputStream(new File(INCOMING_DIR, testFile.getName()));
      out.write(content);
      out.flush();
      out.close();
   }

   protected String getFncFileName(String ttaaii, String origin, String extension, Calendar date) {
      String dateWmo = WMO_DATETIME_FORMAT.format(date.getTime());
      String fnc = "A_" + ttaaii + dateWmo + "_C_" + origin + "_"
            + FNC_DATETIME_FORMAT.format(date.getTime()) + "." + extension;
      return fnc;
   }

}
