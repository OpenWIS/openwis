package org.openwis.harness.samples.script.localdatasource;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.localdatasource.Status;
import org.openwis.harness.samples.common.parameters.ParameterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DatabaseLocalDatasourceTestCase. <P>
 *
 * Use a mock DB:
 * <ul>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::EOXF42MSGG</b> one file by day at 12:00 from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX01EGRR</b> one file by hour from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX02EGRR</b> four file by day from 01 sep. 2010 to 20 jan. 2011</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX03EGRR</b> one file by minutes Day from 12 dec. 2010 06:00 to 18:00</li>
 *   <li><b>urn:x-wmo:md:int.wmo.wis::FVXX04EGRR</b> one file by seconds from 17 jan. 2011 12:00 to 12:15</li>
 * </ul>
 */
public class ScriptLocalDatasourceTestCase {
   /** The Constant INTERVAL_PATTERN. */
   private static final String INTERVAL_PATTERN = "{0}/{1}";

   /** The Constant WAITING_TIMEOUT. */
   private static final long WAITING_TIMEOUT = 500L;

   /** The Constant MAX_TRY. */
   private static final int MAX_TRY = 10;

   /** The Constant URI_PREFIX. */
   private static final String URI_PREFIX = "test/test-";

   /** The local data source. */
   private static ScriptLocalDatasource localDataSource;

   /** The Constant METADATA_URN_PREFIX. */
   private static final String METADATA_URN_PREFIX = "urn:x-wmo:md:int.wmo.wis::";

   /** The Constant md0. */
   private static final String MD_0 = "EOXF42MSGG";

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ScriptLocalDatasourceTestCase.class);

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
    * Initialize test.
    */
   @BeforeClass
   public static void initializeTest() {
      // get local datasource
      localDataSource = new ScriptLocalDatasource();
   }

   /**
    * Before test.
    */
   @Before
   public void beforeTest() {
      System.out.println("================================================================");
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
      Assert.assertFalse(availability.isEmpty());
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
      urn = METADATA_URN_PREFIX + MD_0;
      parameters = new ArrayList<Parameter>();
      parameters.add(buildDateParameter("2010-09-01", "2010-10-01"));
      testSimpleExtraction(urn, parameters);
   }

   /**
    * Test extraction.
    *
    * @param urn the urn
    * @param parameters the parameters
    * @param nbExpectedResult the nb expected result
    * @throws IOException Signals that an I/O exception has occurred.
    */
   private void testSimpleExtraction(String urn, List<Parameter> parameters) throws IOException {
      logger.info("testSimpleExtraction for {}", urn);
      long requestId = nextId();
      String uri = URI_PREFIX + requestId;
      MonitorStatus status;

      // clean previous extraction
      File folder = localDataSource.getLocalDatasourceScriptUtils().getStagingPostFile(uri);
      FileUtils.deleteDirectory(folder);

      // extract
      logger.debug("Extraction {}", requestId);
      status = localDataSource.extract(urn, parameters, requestId, uri);

      Assert.assertEquals(Status.ONGOING_EXTRACTION, status.getStatus());

      // Wait result
      int nbTry = 0;
      while (nbTry <= MAX_TRY) {
         status = localDataSource.monitorExtraction(requestId);
         logger.debug("Extraction {} : {} - {}", new Object[] {requestId, status.getStatus(),
               status.getMessage()});
         if (!Status.ONGOING_EXTRACTION.equals(status.getStatus())) {
            break;
         }
         logger.debug("Extraction {} - Not yet ended, try in {}ms", requestId, WAITING_TIMEOUT);
         waitFor(WAITING_TIMEOUT);
         nbTry++;
      }
      if (nbTry > MAX_TRY) {
         Assert.fail("Could not retrieve extracted data for " + requestId);
      }
      // Check result
      Assert.assertTrue(folder.exists());
      Assert.assertTrue(folder.isDirectory());
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
    * Gets the local data source.
    *
    * @return the local data source
    */
   public static ScriptLocalDatasource getLocalDataSource() {
      return localDataSource;
   }

}
