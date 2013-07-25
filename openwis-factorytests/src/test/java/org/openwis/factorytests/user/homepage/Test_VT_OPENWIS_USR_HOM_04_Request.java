package org.openwis.factorytests.user.homepage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;

import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

/**
 * Factory test: VT-OPENWIS-USR-HOM-04
 */
public class Test_VT_OPENWIS_USR_HOM_04_Request extends AbstractRequestTest {

   private static final String TITLE_CREATE_REQUEST_FROM_CACHE = "Create a request from cache";

   protected void clickOnRequest(String hittitle) {
      selenium.click("//div[contains(@class,'hittitle') and contains(text(),'" + hittitle
            + "')]/following-sibling::div[2]/div[1]");
   }

   @Test
   public void test_VT_OPENWIS_USR_HOM_04_01() throws Exception {
      loadMetadata("src/test/resources/user/homepage/md_01");
      performSearch();
      clickOnRequest("ECEU82LFRO");

      waitForTextPresent("You must log in", 3);
   }

   @Test
   public void test_VT_OPENWIS_USR_HOM_04_02() throws Exception {
      IDataSet dataSet = loadDataSet("/user/homepage/request/init-dataset_02.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);
      loadMetadata("src/test/resources/user/homepage/request/md_02");
      loginAs("myinstitutional");
      performSearch();
      clickOnRequest("ECEU82LFRO");
      waitForTextPresent(TITLE_CREATE_REQUEST_FROM_CACHE, 2);
      clickOnButton(BTN_CANCEL);

      clickOnRequest("ECEU83LFRO");
      // TODO check resolution message
      waitForTextPresent(TITLE_CREATE_REQUEST_FROM_CACHE, 2);
      clickOnButton(BTN_CANCEL);

      clickOnRequest("ECEU84LFRO");
      waitForTextPresent("Access denied.", 3);
      clickOnButton(BTN_OK);

      logout();

      loginAs("mylocal");
      performSearch();
      clickOnRequest("ECEU82LFRO");
      waitForTextPresent(TITLE_CREATE_REQUEST_FROM_CACHE, 2);
      clickOnButton(BTN_CANCEL);

      clickOnRequest("ECEU83LFRO");
      waitForTextPresent("Access denied.", 3);
      clickOnButton(BTN_OK);

      int nbWindows = selenium.getAllWindowIds().length;
      clickOnRequest("ECEU84LFRO");
      assertEquals(nbWindows + 1, selenium.getAllWindowIds().length);
   }

   @Test
   public void test_VT_OPENWIS_USR_HOM_04_03() throws Exception {
      IDataSet dataSet = loadDataSet("/user/homepage/request/init-dataset_02.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);
      loadMetadata("src/test/resources/user/homepage/request/md_02");
      Calendar now = Calendar.getInstance();
      now.add(Calendar.MINUTE, -10);
      copyFileForIngestion(now);

      loginAs("myinstitutional");
      performSearch();
      clickOnRequest("ECEU82LFRO");
      waitForTextPresent(TITLE_CREATE_REQUEST_FROM_CACHE, 2);

      selectGridCell(ID_CACHE_FILE_GRID, 1, 1);
      pause(200);
      clickOnButton(BTN_NEXT);

      pause(200);
      clickOnButton(BTN_SAVE);

      pause(5000);
      // click on refresh
      refreshProcessedRequestsGrid();
      pause(1000);
      String completedOn = getGridCellText(ID_PROCESSED_REQUESTS_GRID, 1, 4);
      assertFalse(completedOn == null || completedOn.equals(""));
   }

}
