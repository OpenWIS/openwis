package org.openwis.factorytests.user.homepage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;
import java.util.TimeZone;

import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

/**
 * Factory test: VT-OPENWIS-USR-HOM-05
 */
public class Test_VT_OPENWIS_USR_HOM_05_Subscription extends AbstractRequestTest {
   private static final String TITLE_CREATE_SUBSCRIPTION_FROM_CACHE = "Create a subscription from cache";

   protected void clickOnSusbcription(String hittitle) {
      selenium.click("//div[contains(@class,'hittitle') and contains(text(),'" + hittitle
            + "')]/following-sibling::div[2]/div[2]");
   }

   @Test
   public void test_VT_OPENWIS_USR_HOM_05_01() throws Exception {
      loadMetadata("src/test/resources/user/homepage/md_01");
      performSearch();
      clickOnSusbcription("ECEU82LFRO");

      waitForTextPresent("You must log in", 3);
   }

   @Test
   public void test_VT_OPENWIS_USR_HOM_05_02() throws Exception {
      IDataSet dataSet = loadDataSet("/user/homepage/request/init-dataset_02.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);
      loadMetadata("src/test/resources/user/homepage/request/md_02");
      loginAs("myinstitutional");
      performSearch();
      clickOnSusbcription("ECEU82LFRO");
      waitForTextPresent(TITLE_CREATE_SUBSCRIPTION_FROM_CACHE, 2);
      clickOnButton(BTN_CANCEL);

      clickOnSusbcription("ECEU83LFRO");
      // TODO check resolution message
      waitForTextPresent(TITLE_CREATE_SUBSCRIPTION_FROM_CACHE, 2);
      clickOnButton(BTN_CANCEL);

      clickOnSusbcription("ECEU84LFRO");
      waitForTextPresent("Access denied.", 3);
      clickOnButton(BTN_OK);

      logout();

      loginAs("mylocal");
      performSearch();
      clickOnSusbcription("ECEU82LFRO");
      waitForTextPresent(TITLE_CREATE_SUBSCRIPTION_FROM_CACHE, 2);
      clickOnButton(BTN_CANCEL);

      clickOnSusbcription("ECEU83LFRO");
      waitForTextPresent("Access denied.", 3);
      clickOnButton(BTN_OK);

      int nbWindows = selenium.getAllWindowIds().length;
      clickOnSusbcription("ECEU84LFRO");
      assertEquals(nbWindows + 1, selenium.getAllWindowIds().length);
   }

   private void selectInterval(String interval) {
      selenium.click("//label[contains(text(),'" + interval + "')]/preceding-sibling::input");
   }

   @Test
   public void test_VT_OPENWIS_USR_HOM_05_03() throws Exception {
      IDataSet dataSet = loadDataSet("/user/homepage/request/init-dataset_02.xml");
      DatabaseOperation.REFRESH.execute(databaseTester.getConnection(), dataSet);
      loadMetadata("src/test/resources/user/homepage/request/md_02");

      loginAs("myinstitutional");
      performSearch();
      clickOnSusbcription("ECEU82LFRO");
      waitForTextPresent(TITLE_CREATE_SUBSCRIPTION_FROM_CACHE, 2);

      Calendar now = Calendar.getInstance();
      now.add(Calendar.MINUTE, -10);
      now.setTimeZone(TimeZone.getTimeZone("UTC"));
      int hour = now.get(Calendar.HOUR_OF_DAY);
      String interval = "[" + hour + "," + (hour + 1) + "[";
      selectInterval(interval);
      pause(200);
      clickOnButton(BTN_NEXT);

      pause(500);
      clickOnButton(BTN_SAVE);

      pause(2000);
      
      now = Calendar.getInstance();
      copyFileForIngestion(now);

      pause(10000);
      refreshProcessedRequestsGrid();
      pause(1000);
      
      String completedOn = getGridCellText(ID_PROCESSED_REQUESTS_GRID, 1, 4);
      assertFalse(completedOn == null || completedOn.equals(""));
   }

}
