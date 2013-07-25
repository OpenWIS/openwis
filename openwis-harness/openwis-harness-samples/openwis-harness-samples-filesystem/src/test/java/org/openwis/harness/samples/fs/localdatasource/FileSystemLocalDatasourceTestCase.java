/**
 *
 */
package org.openwis.harness.samples.fs.localdatasource;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.localdatasource.Status;
import org.openwis.harness.samples.common.parameters.ParameterUtils;
import org.openwis.harness.samples.fs.localdatasource.utils.SampleFileSystemBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileSystemLocalDatasourceTestCase. <P>
 *
 * Use a mock FS:
 * <ul>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::EOXF42MSGG</b> one file by day at 12:00 from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX01EGRR</b> one file by hour from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX02EGRR</b> four file by day from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX03EGRR</b> one file by minutes Day from 12 dec. 2010 06:00 to 18:00</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX04EGRR</b> one file by seconds from 17 jan. 2011 12:00 to 12:15</li>
 * </ul>
 */
public class FileSystemLocalDatasourceTestCase {

   /** The Constant INTERVAL_PATTERN. */
   private static final String INTERVAL_PATTERN = "{0}/{1}";

   /** The Constant WAITING_TIMEOUT. */
   public static final long WAITING_TIMEOUT = 500L;

   /** The Constant MAX_TRY. */
   public static final int MAX_TRY = 10;

   /** The Constant URI_PREFIX. */
   public static final String URI_PREFIX = "test/test-";

   /** The id. */
   private static long id = 0;

   /**
    * Next id.
    *
    * @return the long
    */
   public static long nextId() {
      return id++;
   }

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(FileSystemLocalDatasourceTestCase.class);

   /** The local data source. */
   private static FileSystemLocalDatasource localDataSource;

   /**
    * Gets the localDataSource.
    * @return the localDataSource.
    */
   public static FileSystemLocalDatasource getLocalDataSource() {
      return localDataSource;
   }

   /** The feeder. */
   private static SampleFileSystemBuilder feeder;

   /**
    * Initialize test.
    */
   @BeforeClass
   public static void initializeTest() {
      // get local datasource
      localDataSource = new FileSystemLocalDatasource();

      // initialize FS
      feeder = new SampleFileSystemBuilder(localDataSource.getLocalDatasourceFileUtils());
      feeder.clearTestFileSystem();
      feeder.createTestFileSystem();
   }

   /**
    * Before test.
    */
   @Before
   public void beforeTest() {
      waitFor(WAITING_TIMEOUT);
      System.out.println("================================================================");
   }

   /**
    * Test get availability with null.
    */
   @Test
   public void testGetAvailabilityWithNull() {
      logger.info("testGetAvailabilityWithNull");
      List<String> availability = localDataSource.getAvailability(null);
      Assert.assertNotNull(availability);
      Assert.assertEquals("Should find all metadata", 5, availability.size());
   }

   /**
    * Test get availability.
    */
   @Test
   public void testGetAvailability() {
      logger.info("testGetAvailability");

      String timestamp;
      List<String> availability;
      // Get availability from 18 Jan. 2011
      timestamp = "2011-01-18T00:00:00Z";
      availability = localDataSource.getAvailability(timestamp);
      Assert.assertNotNull(availability);
      Assert.assertEquals(3, availability.size());
      Assert.assertFalse(availability.isEmpty());

      // need to find MD_0, MD_1, MD_2
      Assert.assertTrue(availability.contains(SampleFileSystemBuilder.METADATA_URN_PREFIX
            + SampleFileSystemBuilder.MD_0));
      Assert.assertTrue(availability.contains(SampleFileSystemBuilder.METADATA_URN_PREFIX
            + SampleFileSystemBuilder.MD_1));
      Assert.assertTrue(availability.contains(SampleFileSystemBuilder.METADATA_URN_PREFIX
            + SampleFileSystemBuilder.MD_2));

      // Get availability from 21 Jan. 2011
      timestamp = "2011-01-21T00:00:00Z";
      availability = localDataSource.getAvailability(timestamp);
      Assert.assertNotNull(availability);
      Assert.assertTrue(availability.isEmpty());

      // Get availability from 07 Aug. 2010
      timestamp = "2010-08-07T00:00:00Z";
      availability = localDataSource.getAvailability(timestamp);
      Assert.assertNotNull(availability);
      Assert.assertFalse(availability.isEmpty());
      Assert.assertEquals(5, availability.size());
   }

   /**
    * Test md0 extraction.
    *
    * @throws IOException Signals that an I/O exception has occurred.
    */
   @Test
   public void testMD0Extraction() throws IOException {
      logger.info("testMD0Extraction");
      String urn;
      List<Parameter> parameters;

      // extract MD0 in September 2010
      urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_0;
      parameters = new ArrayList<Parameter>();
      parameters.add(buildDateParameter("2010-09-01", "2010-10-01"));
      testSimpleExtraction(urn, parameters, 30);
   }

   /**
    * Test MD1 extraction.
    *
    * @throws IOException Signals that an I/O exception has occurred.
    */
   @Test
   public void testMD1Extraction() throws IOException {
      logger.info("testMD1Extraction");
      String urn;
      List<Parameter> parameters;

      // extract MD1 in Dec 2010 to 1 Jan. 12:00
      urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_1;
      parameters = new ArrayList<Parameter>();

      parameters.add(buildDateParameter("2010-12-01", "2011-01-01"));
      parameters.add(buildTimeParameter("00:00Z", "12:00Z"));

      int nbExpectedResult = (24 * 31) + 13; // December + 13 first January hours
      testSimpleExtraction(urn, parameters, nbExpectedResult);
   }

   /**
    * Test MD2 extraction.
    *
    * @throws IOException Signals that an I/O exception has occurred.
    */
   @Test
   public void testMD2Extraction() throws IOException {
      logger.info("testMD2Extraction");
      String urn;
      List<Parameter> parameters;

      // extract MD2 from 15 Sep 2010 12:00 to 15 Oct. 14:00
      urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_2;
      parameters = new ArrayList<Parameter>();

      parameters.add(buildDateParameter("2010-09-15", "2010-10-15"));
      parameters.add(buildTimeParameter("12:00Z", "14:00Z"));

      int nbExpectedResult = 2 + (4 * 29) + 3;
      testSimpleExtraction(urn, parameters, nbExpectedResult);
   }

   /**
    * Test MD3 extraction.
    *
    * @throws IOException Signals that an I/O exception has occurred.
    */
   @Test
   public void testMD3Extraction() throws IOException {
      logger.info("testMD3Extraction");
      String urn;
      List<Parameter> parameters;

      // extract MD3 from 15 Sep 2010 00:00 to 12 Dec. 14:00
      urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_3;
      parameters = new ArrayList<Parameter>();

      parameters.add(buildDateParameter("2010-09-15", "2010-12-12"));
      parameters.add(buildTimeParameter("00:00Z", "14:00Z"));

      int nbExpectedResult = 60 * (14 - 6) + 1;
      testSimpleExtraction(urn, parameters, nbExpectedResult);
   }

   /**
    * Test MD4 extraction.
    *
    * @throws IOException Signals that an I/O exception has occurred.
    */
   @Test
   public void testMD4Extraction() throws IOException {
      logger.info("testMD4Extraction");
      String urn;
      List<Parameter> parameters;

      // extract MD4 from 17 Jan 2011 11:50 to 17 Jan. 12:10
      urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_4;
      parameters = new ArrayList<Parameter>();

      parameters.add(buildDateParameter("2011-01-17", "2011-01-17"));
      parameters.add(buildTimeParameter("11:50Z", "12:10Z"));

      int nbExpectedResult = 60 * 11;
      testSimpleExtraction(urn, parameters, nbExpectedResult);
   }

   /**
    * Test parallel extractions.
    */
   @Test
   public void testParallelExtraction() {
      ExecutorService executorService = Executors.newCachedThreadPool();

      // Prepare extractions
      Map<File, ExctactionCallable> callables = new LinkedHashMap<File, ExctactionCallable>();

      String urn;
      List<Parameter> parameters;
      ExctactionCallable callable;
      int nbExpectedResult;

      try {
         // extract MD0 in September 2010
         urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_0;
         parameters = new ArrayList<Parameter>();
         parameters.add(buildDateParameter("2010-09-01", "2010-10-01"));
         nbExpectedResult = 30;
         callable = new ExctactionCallable(urn, parameters, nbExpectedResult);
         callables.put(callable.getFile(), callable);

         // extract MD1 in Dec 2010 to 1 Jan. 12:00
         urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_1;
         parameters = new ArrayList<Parameter>();
         parameters.add(buildDateParameter("2010-12-01", "2011-01-01"));
         parameters.add(buildTimeParameter("00:00Z", "12:00Z"));
         nbExpectedResult = (24 * 31) + 13; // December + 13 first January hours
         callable = new ExctactionCallable(urn, parameters, nbExpectedResult);
         callables.put(callable.getFile(), callable);

         // extract MD2 from 15 Sep 2010 12:00 to 15 Oct. 14:00
         urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_2;
         parameters = new ArrayList<Parameter>();
         parameters.add(buildDateParameter("2010-09-15", "2010-10-15"));
         parameters.add(buildTimeParameter("12:00Z", "14:00Z"));
         nbExpectedResult = 2 + (4 * 29) + 3;
         callable = new ExctactionCallable(urn, parameters, nbExpectedResult);
         callables.put(callable.getFile(), callable);

         // extract MD3 from 15 Sep 2010 00:00 to 12 Dec. 14:00
         urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_3;
         parameters = new ArrayList<Parameter>();
         parameters.add(buildDateParameter("2010-09-15", "2010-12-12"));
         parameters.add(buildTimeParameter("00:00Z", "14:00Z"));
         nbExpectedResult = 60 * (14 - 6) + 1;
         callable = new ExctactionCallable(urn, parameters, nbExpectedResult);
         callables.put(callable.getFile(), callable);

         // extract MD4 from 17 Jan 2011 11:50 to 17 Jan. 12:10
         urn = SampleFileSystemBuilder.METADATA_URN_PREFIX + SampleFileSystemBuilder.MD_4;
         parameters = new ArrayList<Parameter>();
         parameters.add(buildDateParameter("2011-01-17", "2011-01-17"));
         parameters.add(buildTimeParameter("11:50Z", "12:10Z"));
         nbExpectedResult = 60 * 11;
         callable = new ExctactionCallable(urn, parameters, nbExpectedResult);
         callables.put(callable.getFile(), callable);

         // Launch all extraction
         List<Future<File>> invokeAll = executorService.invokeAll(callables.values());
         // Checks
         File file;
         long timeout = callables.size() * MAX_TRY * WAITING_TIMEOUT;
         for (Future<File> futur : invokeAll) {
            Assert.assertTrue(futur.isDone());
            try {
               file = futur.get(timeout, TimeUnit.MILLISECONDS);
               logger.info("Check {}", file);
               Assert.assertNotNull(file);
               callable = callables.get(file);

               Assert.assertTrue(
                     MessageFormat.format("[Extraction {0}] File should exist: {1} ",
                           callable.getRequestId(), file), file.exists());
               Assert.assertTrue(
                     MessageFormat.format("[Extraction {0}] File is directory: {1} ",
                           callable.getRequestId(), file), file.isDirectory());
               Assert.assertEquals(
                     MessageFormat.format("[Extraction {0}] Expected result",
                           callable.getRequestId()), callable.getNbExpectedResult(),
                     file.list().length);
            } catch (ExecutionException e) {
               logger.error(e.getMessage(), e);
               Assert.fail(e.getMessage());
            } catch (TimeoutException e) {
               logger.error(e.getMessage(), e);
               Assert.fail(e.getMessage());
            }
         }
      } catch (InterruptedException e) {
         logger.error(e.getMessage(), e);
         Assert.fail(e.getMessage());
      } catch (IOException e) {
         logger.error(e.getMessage(), e);
         Assert.fail(e.getMessage());
      }
   }

   /**
    * Test extraction.
    *
    * @param urn the urn
    * @param parameters the parameters
    * @param nbExpectedResult the nb expected result
    * @throws IOException Signals that an I/O exception has occurred.
    */
   private void testSimpleExtraction(String urn, List<Parameter> parameters, int nbExpectedResult)
         throws IOException {
      logger.info("testSimpleExtraction for {}", urn);
      long requestId = nextId();
      String uri = URI_PREFIX + requestId;
      MonitorStatus status;

      // clean previous extraction
      File folder = localDataSource.getLocalDatasourceFileUtils().getStagingPostFile(uri);
      FileUtils.deleteDirectory(folder);

      // extract
      logger.debug("Extraction {}", requestId);
      status = localDataSource.extract(urn, parameters, requestId, uri);

      Assert.assertEquals(Status.ONGOING_EXTRACTION, status.getStatus());

      // Wait result
      int nbTry = 0;
      while (nbTry <= MAX_TRY) {
         waitFor(WAITING_TIMEOUT);
         status = localDataSource.monitorExtraction(requestId);
         logger.debug("Extraction {} : {} - {}", new Object[] {requestId, status.getStatus(),
               status.getMessage()});
         if (!Status.ONGOING_EXTRACTION.equals(status.getStatus())) {
            break;
         }
         logger.debug("Extraction {} - Not yet ended, try in {}ms", requestId, WAITING_TIMEOUT);
         nbTry++;
      }
      if (nbTry > MAX_TRY) {
         Assert.fail("Could not retrieve extracted data for " + requestId);
      }

      // Take a breath
      waitFor(WAITING_TIMEOUT);

      // Check result
      if (nbExpectedResult == 0) {
         Assert.assertEquals(Status.NO_RESULT_FOUND, status.getStatus());
      } else {
         Assert.assertEquals(Status.EXTRACTED, status.getStatus());
      }
      Assert.assertTrue(folder.exists());
      Assert.assertTrue(folder.isDirectory());
      Assert.assertEquals(
            MessageFormat.format("Expected {0} resuts but found {1}", nbExpectedResult,
                  folder.list().length), nbExpectedResult, folder.list().length);
   }

   /**
    * Wait for.
    *
    * @param duration the duration
    */
   public static void waitFor(long duration) {
      try {
         Thread.sleep(duration);
      } catch (InterruptedException e) {
         logger.warn("Interuption", e);
         Assert.fail();
      }
   }

   /**
    * Builds the date parameter.
    *
    * @param from the from
    * @param to the to
    * @return the parameter
    */
   private Parameter buildDateParameter(String from, String to) {
      Parameter result = new Parameter();
      result.setCode(ParameterUtils.DATE_PARAMETER);
      String dateInterval = MessageFormat.format(INTERVAL_PATTERN, from, to);
      result.getValues().add(dateInterval);
      return result;
   }

   /**
    * Builds the time parameter.
    *
    * @param from the from
    * @param to the to
    * @return the parameter
    */
   private Parameter buildTimeParameter(String from, String to) {
      Parameter result = new Parameter();
      result.setCode(ParameterUtils.TIME_PARAMETER);
      String dateInterval = MessageFormat.format(INTERVAL_PATTERN, from, to);
      result.getValues().add(dateInterval);
      return result;
   }
}
