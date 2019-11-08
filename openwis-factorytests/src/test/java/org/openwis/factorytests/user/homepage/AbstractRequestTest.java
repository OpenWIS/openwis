package org.openwis.factorytests.user.homepage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.openwis.factorytests.user.homepage.search.AbstractSearchTest;

public abstract class AbstractRequestTest extends AbstractSearchTest {
   public static final String ID_CACHE_FILE_GRID = "cacheFileGrid";

   public static final String BTN_NEXT = "Next";

   public static final String ID_PROCESSED_REQUESTS_GRID = "processedRequestsGridPanel";

   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
      dataSet.addTable("groups");
      dataSet.addTable("usergroups");
      dataSet.addTable("datapolicy",
            "select * from datapolicy where name<>'public' and name<>'additional-default'");
      dataSet.addTable("operationallowed");
      dataSet.addTable("datapolicyalias");

      dataSet.addTable("metadata", "select * from metadata where istemplate='n'");
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

      return dataSet;
   }

   protected void performSearch() throws Exception {
      openUserHomePage();
      clickOnButton("Search");
      waitForTextDisappear(METADATA_SEARCHING_MSG, 5);
   }

   protected void refreshProcessedRequestsGrid() {
      selenium.click("//div[@id='" + ID_PROCESSED_REQUESTS_GRID
            + "']//tr[contains(@class, 'x-toolbar-left-row')]/td[11]//td[@class='x-btn-mc']");
   }

   protected void copyFileForIngestion(Calendar productDate) throws Exception {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      String dateStr = dateFormat.format(productDate.getTime());
      String fileSrc = getAbsolutePath("src/test/resources/user/homepage/request/data_03/A_ECEU82LFRO111100_C_EGRR_20110407090000.bin");
      String harnessIncomingDir = "/var/opt/openwis/harness/incoming";
      String dstName = "A_ECEU82LFRO111100_C_EGRR_" + dateStr + ".bin";
      FileUtils.copyFile(new File(fileSrc), new File(harnessIncomingDir, dstName));
   }

}
