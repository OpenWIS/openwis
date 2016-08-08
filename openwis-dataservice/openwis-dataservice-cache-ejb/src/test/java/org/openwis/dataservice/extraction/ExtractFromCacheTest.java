/**
 *
 */
package org.openwis.dataservice.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openwis.dataservice.CacheTestHelpers;
import org.openwis.dataservice.common.domain.bean.MessageStatus;
import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.util.FileNameInfoFilter;
import org.openwis.dataservice.util.FileNameParser;
import org.openwis.dataservice.util.WMOFNC;

/**
 * Short Description goes here. <p>
 * Explanation goes here. <p>
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public class ExtractFromCacheTest extends CacheTestHelpers {

   private static File workingDirectory;

   /*
      private static final String[] filenames = {
          "T_PGBE07_C_KWBC_20020610180000_D241_SIG_WEATHER_250-600_VT_06Z.tif",
         "A_HPWZ89LFPW131200RRA_C_LFPW_20020913160300.bin",
         "Z_IDN60000_C_AMMC_20020617000000.gif",
         "Z_LWDA_C_EGRR_20020617000000_LWDA16_0000.bin.Z",
         "T_SDCN50_C_CWAO_200204201530--_WKR_ECHOTOP,2-0,100M,AGL,78,N.gif",
         "Z__C_CWAO_2002032812----_CMC_reg_TMP_ISBL_500_ps60km_2002032812_P036.bin"
      };

      private static final String[][] TIME_FILTERS = {
         {"17:00Z/18:00Z", "18:00Z/19:00Z", "17:00Z/19:00Z"},
         {"15:00Z/16:30Z", "16:00Z/17:00Z", "15:00Z/17:00Z"},
         {"00:00Z/01:00Z", "00:00Z/23:00Z"},
         {"14:00Z/15:30Z", "15:00Z/16:00Z", "14:00Z/16:00Z", "00:00Z/23:00Z"},
         {"11:00Z/12:00Z", "12:00Z/13:00Z", "11:00Z/13:00Z", "00:00Z/23:00Z"},
         {"09:30Z/10:30Z", "10:00Z/11:00Z", "09:00Z/11:00Z", "00:00Z/23:00Z"},
      };
   */

   /**
    * Description goes here.
    * @throws java.lang.Exception
    */
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {

      String tmpdir = System.getProperty("java.io.tmpdir");
      File directory = new File(tmpdir, "openwis-dataservice-cache");

      // check existence
      if (directory.isDirectory()) {
         workingDirectory = directory;
      }
      else {
         if (directory.mkdirs()) {
            workingDirectory = directory;
            workingDirectory.deleteOnExit();
         }
      }

      // create source and target directories
      if (workingDirectory != null) {
         new File(workingDirectory, "target").mkdir();

         File sourceDir = new File(workingDirectory, "source");
         if (sourceDir.mkdir() || sourceDir.exists()) {
            createFiles(sourceDir.getAbsolutePath(), FILENAMES);
            createFiles(sourceDir.getAbsolutePath(), FILENAMES);
         }
      }
   }

   /**
    * Description goes here.
    * @throws java.lang.Exception
    */
   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      // remove all entries in working directory
      if (workingDirectory != null) {
         deleteFiles(workingDirectory);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Test
   public void testExtract() {
      // testExtract(filenames, TIME_FILTERS);
   }

   /**
    * Test method for {@link FileNameInfoFilter#createProductDateFilter(String)}.
    * @throws Exception
    */
   void testExtract(final String[] filenames, final String[][] periods) {
      try {
         ExtractFromCacheImpl service = new ExtractFromCacheImpl();

         String stagingPostURI = "target";
         Long processedRequestId = Long.valueOf(0L);
         File sourcePath = new File(workingDirectory, "source");

         service.setCacheDirectory(sourcePath.getAbsolutePath());
         service.setStagingPostDirectory(workingDirectory.getAbsolutePath());

         for (int index = 0; index < filenames.length; index++) {
            String filename = filenames[index];

            WMOFNC info = FileNameParser.parseFileName(filename);


            assertNotNull(info);

            String metadataURN = info.getMetadataURN();

            List<Parameter> parameters = new ArrayList<Parameter>();
            Parameter parameter = new Parameter();
            parameters.add(parameter);

            for (String period : periods[index]) {

               Value value = new Value();
               value.setValue(period);
               parameter.getValues().add(value);

            }

            MessageStatus status = service.extract("",
                  metadataURN, parameters, processedRequestId, stagingPostURI, null);

            assertNotNull(status);
            assertNotNull(status.getStatus());
            assertEquals(Status.EXTRACTED, status.getStatus());
         }
      }
      catch (Exception e) {
         fail(e.getMessage());
      }
   }

}
