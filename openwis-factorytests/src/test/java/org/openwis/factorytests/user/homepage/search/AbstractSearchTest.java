package org.openwis.factorytests.user.homepage.search;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.openwis.factorytests.user.UserFactoryTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractSearchTest. <P>
 * Explanation goes here. <P>
 */
public class AbstractSearchTest extends UserFactoryTest {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(AbstractSearchTest.class);

   /** Waiting for Search test */
   public static final String METADATA_SEARCHING_MSG = "SEARCHING FOR METADATA";

   /**
    * {@inheritDoc}
    * @see org.openwis.factorytests.OpenWisFactoryTest#getDataSet()
    */
   @Override
   protected IDataSet getDataSet() throws Exception {
      QueryDataSet dataSet = new QueryDataSet(databaseTester.getConnection());
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

      return dataSet;
   }

   /**
    * Load metadata.
    *
    * @param dir the dir
    * @throws Exception the exception
    */
   protected void loadMetadata(String dir) throws Exception {
      loginAsAdmin();
      // clean index
      openWindow("/srv/metadata.admin.index.rebuild");
      super.loadMetadata(dir);
      pause(2000);
      logout();
   }

   /**
    * Count search results.
    *
    * @return the int
    */
   protected int countSearchResults() {
      return selenium.getXpathCount("//div[@class='hit']").intValue();
   }

   /**
    * Gets the hit title.
    *
    * @param index the index
    * @return the hit title
    */
   protected String getHitTitle(int index) {
      return selenium.getText("//div[contains(@class, 'hit')][" + index
            + "]//div[contains(@class,'hittitle')]");
   }

   /**
    * Search.
    *
    * @param timeout the timeout in seconds
    */
   public void search(int timeout) {
      clickOnButton("Search");
      waitForTextDisappear(METADATA_SEARCHING_MSG, timeout);
   }

   /**
    * Search.
    */
   public void search() {
      search(5);
   }

   /**
    * Gets the all hits uuid.
    *
    * @return the all hits uuid
    */
   protected List<String> getAllHitsUuid() {
      List<String> result = new ArrayList<String>();
      String uuid;
      for (int i = 1; i <= countSearchResults(); i++) {
         uuid = selenium.getAttribute(MessageFormat.format(
               "//div[contains(@class, ''hit'')][{0}]//div[contains(@class,''hit'')]/a/@name", i));
         result.add(uuid);
      }
      return result;
   }

   /**
    * Gets the current page.
    *
    * @return the current page
    */
   protected int getCurrentPage() {
      int result = -1;
      String pageText = selenium.getAttribute("//div[contains(@class, 'results_header')]/a/@name");
      if (pageText != null) {
         Pattern pattern = Pattern.compile("page_([0-9])");
         Matcher matcher = pattern.matcher(pageText);
         if (matcher.matches()) {
            result = Integer.valueOf(matcher.group(1));
         }
      }
      return result;
   }

   /**
    * Switch to page.
    *
    * @param page the page
    */
   protected void switchToPage(int page) {
      selenium.click("id=searchPage_" + page);
      waitForTextDisappear(METADATA_SEARCHING_MSG, 5);
   }

   /**
    * Sets the search field value.
    *
    * @param fieldName the field name
    * @param value the value
    */
   protected void setSearchFieldValue(String fieldName, String value) {
      selenium.type("//div[text()='" + fieldName + "']/following-sibling::input", value);
   }

   /**
    * Sets the what value.
    *
    * @param value the new what value
    */
   protected void setWhatValue(String value) {
      setSearchFieldValue("WHAT?", value);
   }

   /**
    * Sets the sort value.
    *
    * @param value the new sort value
    */
   protected void setSortValue(String value) {
      try {
         setComboValue("Sort by:", value);
      } catch (Exception e) {
         logger.error("Could not set sorting" + value, e);
      }
   }

   /**
    * Sets the hit value.
    *
    * @param hitsPerPage the new hit value
    */
   protected void setHitsValue(int hitsPerPage) {
      try {
         setComboValue("Hits per page:", String.valueOf(hitsPerPage));
      } catch (Exception e) {
         logger.error("Could not set hits per pages" + hitsPerPage, e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.factorytests.OpenWisFactoryTest#setComboValue(java.lang.String, java.lang.String)
    */
   @Override
   protected void setComboValue(String comboLabel, String value) {
      // Open Options fieldset
      selenium.click("//legend[span/text()='Options']/div");
      // Open Combo
      selenium.click("//div[label/text()='" + comboLabel
            + "']/div/div/descendant::img[contains(@class, 'x-form-arrow-trigger')]");
      // select element
      // wait for a drop down list of options to be visible
      pause(500);
      selenium.click("//div[contains(@class, 'x-combo-list-item')][text()='" + value + "']");
      pause(500);
   }

   /**
    * Sets the geo.
    *
    * @param left the left
    * @param bottom the bottom
    * @param right the right
    * @param top the top
    */
   protected void setNormalGeo(double left, double bottom, double right, double top) {
      String bounds = MessageFormat.format("'{' left: {0}, bottom: {1}, right: {2}, top: {3}'}'",
            String.valueOf(left), String.valueOf(bottom), String.valueOf(right),
            String.valueOf(top));
      evals(MessageFormat.format(
            "window.homePageViewport.getSearchPanel().getNormalSearchPanel().getMapPanel().drawExtent({0});",
            bounds),
            MessageFormat
                  .format(
                        "window.homePageViewport.getSearchPanel().getNormalSearchPanel().getMapPanel().zoomToExtent({0});",
                        bounds),
            MessageFormat
                  .format(
                        "window.homePageViewport.getSearchPanel().getNormalSearchPanel().updateMapFields({0}, false);",
                        bounds));
      pause(8000);
   }

   /**
    * Switch advanced search.
    */
   protected void switchAdvancedSearch() {
      selenium.click("xpath=//span[text()='Advanced Search']");
      pause(500);
   }

   /**
    * Evals.
    *
    * @param scripts the scripts
    */
   private void evals(String... scripts) {
      String result;
      for (String js : scripts) {
         result = selenium.getEval(js);
         logger.debug(MessageFormat.format("Running \"{0}\" with result= {1}", js, result));
      }
   }
}
