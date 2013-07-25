/**
 *
 */
package org.openwis.harness.samples.fs.localdatasource.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openwis.dataservice.ProductArrivalListener;
import org.openwis.dataservice.ProductArrivalListener_Service;
import org.openwis.harness.samples.fs.localdatasource.FsLocalDatasourceFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileSystemFeeder. <P>
 * Create a mock FS:
 * <ul>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::EOXF42MSGG</b> one file by day at 12:00 from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX01EGRR</b> one file by hour from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX02EGRR</b> four file by day from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX03EGRR</b> one file by minutes Day from 12 dec. 2010 06:00 to 18:00</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX04EGRR</b> one file by seconds from 17 jan. 2011 12:00 to 12:15</li>
 * </ul>
 */
public class SampleFileSystemBuilder {

   /** The Constant CREATE_FILE_FOR. */
   private static final String CREATE_FILE_FOR = "Create file for {}";

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SampleFileSystemBuilder.class);

   /** The Constant METADATA_URN_PREFIX. */
   public static final String METADATA_URN_PREFIX = "urn:x-wmo:md:int.wmo.wis::";

   /** The Constant md0. */
   public static final String MD_0 = "EOXF42MSGG";

   /** The Constant md1. */
   public static final String MD_1 = "FVXX01EGRR";

   /** The Constant md2. */
   public static final String MD_2 = "FVXX02EGRR";

   /** The Constant md3. */
   public static final String MD_3 = "FVXX03EGRR";

   /** The Constant md4. */
   public static final String MD_4 = "FVXX04EGRR";

   /** The local datasource file utils. */
   private final FsLocalDatasourceFileUtils localDatasourceFileUtils;

   /** The sdf1. */
   private final SimpleDateFormat sdf1;

   /** The sdf2. */
   private final SimpleDateFormat sdf2;

   /** The dummy data. */
   private final String dummyData;

   /** The product arrival listener. */
   private ProductArrivalListener productArrivalLstn;

   /**
    * Default constructor.
    * Builds a FileSystemFeeder.
    *
    * @param localDatasourceFileUtils the local datasource file utils
    */
   public SampleFileSystemBuilder(FsLocalDatasourceFileUtils localDatasourceFileUtils) {
      super();
      this.localDatasourceFileUtils = localDatasourceFileUtils;
      sdf1 = new SimpleDateFormat("FFHHmm", Locale.ENGLISH);
      sdf2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);

      try {
         ResourceBundle bundle = ResourceBundle.getBundle("FileSystemLocalDatasource");
         String dsWdsl = bundle.getString("ProductArrivalListener.url");
         ProductArrivalListener_Service port;
         port = new ProductArrivalListener_Service(new URL(dsWdsl));
         productArrivalLstn = port.getProductArrivalListenerPort();
      } catch (Exception e) {
         logger.warn("Could not retrieve a ProductArrivalListener from WebService", e);
      }

      String dummy = "";
      InputStream in = null;
      StringWriter sw = null;
      try {
         in = this.getClass().getResourceAsStream("/dummy.txt");
         sw = new StringWriter();
         IOUtils.copy(in, sw);
         dummy = sw.toString();
      } catch (IOException ioe) {
         dummy = "No Data";
         logger.error("Error reading dummy data", ioe);
      } finally {
         dummyData = dummy;
         IOUtils.closeQuietly(sw);
         IOUtils.closeQuietly(in);
      }
   }

   /**
    * Creates the test file system.
    */
   public void createTestFileSystem() {

      Calendar from = Calendar.getInstance();
      // Start at 01 September 2010 at 00:00
      from.clear();
      from.set(2010, Calendar.SEPTEMBER, 1);

      // End at 20 January 2011
      Calendar to = Calendar.getInstance();
      to.clear();
      to.set(2011, Calendar.JANUARY, 20);

      String urn;
      // EOXF42MSGG one file / day at 12:00
      urn = MD_0;
      from.set(Calendar.HOUR, 12);
      logger.info(CREATE_FILE_FOR, urn);
      createTestFile(urn, from, to, Calendar.DAY_OF_MONTH, 1);

      // FVXX01EGRR one file / hour
      urn = MD_1;
      from.clear(Calendar.HOUR);
      logger.info(CREATE_FILE_FOR, urn);
      createTestFile(urn, from, to, Calendar.HOUR, 1);

      // FVXX02EGRR files at 00:00, 06:00, 12:00, 18:00 each day
      urn = MD_2;
      logger.info(CREATE_FILE_FOR, urn);
      createTestFile(urn, from, to, Calendar.HOUR, 6);

      // FVXX03EGRR files each minutes between 06:00 and 18:00 the 12st Dec 2010
      urn = MD_3;
      from.clear();
      from.set(2010, Calendar.DECEMBER, 12, 06, 00);

      to.clear();
      to.set(2010, Calendar.DECEMBER, 12, 18, 00);
      logger.info(CREATE_FILE_FOR, urn);
      createTestFile(urn, from, to, Calendar.MINUTE, 1);

      // FVXX04EGRR files each seconds between 12:00 and 12:15 the 17th Jan 2011
      urn = MD_4;
      from.clear();
      from.set(2011, Calendar.JANUARY, 17, 12, 00);

      to.clear();
      to.set(2011, Calendar.JANUARY, 17, 12, 15);
      logger.info(CREATE_FILE_FOR, urn);
      createTestFile(urn, from, to, Calendar.SECOND, 1);
   }

   /**
    * Creates Test file.
    *
    * @param urn the urn
    * @param from the from
    * @param to the to
    * @param calendarField the calendar field
    * @param increment the increment
    */
   private void createTestFile(String urn, Calendar from, Calendar to, int calendarField,
         int increment) {
      Calendar current = Calendar.getInstance();
      current.clear();
      current.setTime(from.getTime());

      while (current.before(to)) {
         createFile(urn, current);
         current.add(calendarField, increment);
      }
   }

   /**
    * Clear test file system.
    */
   public void clearTestFileSystem() {
      File metadataFile = localDatasourceFileUtils.getMetadataFile("aa");
      File rootFolder = metadataFile.getParentFile();
      try {
         FileUtils.deleteDirectory(rootFolder);
      } catch (IOException e) {
         logger.error("Could not delete test FS", e);
      }
   }

   /**
    * Creates the file.
    *
    * @param metadata the metadata
    * @param timestamp the timestamp
    */
   private void createFile(String metadata, Calendar timestamp) {
      File folder = localDatasourceFileUtils.getProductFolder(METADATA_URN_PREFIX + metadata,
            timestamp);
      String dataFileName = MessageFormat.format("X_{0}{1}_C_{2}.txt", metadata,
            sdf1.format(timestamp.getTime()), sdf2.format(timestamp.getTime()));
      File urnTestFile = new File(folder, dataFileName);
      try {
         FileUtils.touch(urnTestFile);
         FileUtils.writeStringToFile(urnTestFile, dummyData);

         // Notify product arrival
         if (productArrivalLstn != null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));

            String ts = df.format(timestamp.getTime());
            productArrivalLstn.onProductArrival(ts, Collections.singletonList(metadata));
         }
         logger.trace("Created file: {}", urnTestFile);
      } catch (IOException e) {
         logger.error("Could not create the test URN file", e);
      }
   }
}
