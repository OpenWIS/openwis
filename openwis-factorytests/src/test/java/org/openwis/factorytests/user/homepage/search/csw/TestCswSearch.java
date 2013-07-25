
/*
 *
 */
package org.openwis.factorytests.user.homepage.search.csw;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.Assert;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.Test;
import org.openwis.factorytests.user.homepage.search.AbstractSearchTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory test: VT_OPENWIS_USR_SCH_06
 */
public class TestCswSearch extends AbstractSearchTest {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(TestCswSearch.class);

   /**
   * Search for all metadata with  summary
   * Should return 10 first metadata
   * @throws Exception the exception
   */
   @Test
   public void test_VT_OPENWIS_USR_SCH_06_01() throws Exception {
      openCswTest();
      Document doc = search("csw-GetRecords | no filter | results");
      checkXPath(doc, "/csw:GetRecordsResponse/csw:SearchResults[@numberOfRecordsMatched='12']");
      checkXPathCount(doc, "/csw:GetRecordsResponse/csw:SearchResults/csw:SummaryRecord", 10);
   }

   /**
    * Search with a filter on title with 'lorem'
    * Expected to find 2 metadata
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_06_02() throws Exception {
      openCswTest();
      Document doc = search("csw-GetRecords | filter 'lorem'");
      checkXPath(doc, "/csw:GetRecordsResponse/csw:SearchResults[@numberOfRecordsMatched='1']");
   }

   /**
    * Search with a filter on geographical extends intersect (47, 0) -> (51, 5)
    * Expected to find 2 metadata
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_06_03() throws Exception {
      openCswTest();
      Document doc = search("csw-GetRecords | filter ogc:Intersects+gml:Box");
      checkXPath(doc, "/csw:GetRecordsResponse/csw:SearchResults[@numberOfRecordsMatched='2']");
   }

   /**
    * Check x path count.
    *
    * @param data the data
    * @param xp the xp
    * @param count the count
    * @throws JDOMException the jDOM exception
    */
   @SuppressWarnings("rawtypes")
   private void checkXPathCount(Document data, String xp, int count) throws JDOMException {
      XPath xpath = XPath.newInstance(xp);
      xpath.addNamespace("csw", "http://www.opengis.net/cat/csw/2.0.2");
      List list = xpath.selectNodes(data);
      int result = -1;
      if (list != null) {
         result = list.size();
      }
      Assert.assertEquals(count, result);
   }

   /**
    * Check document with XPath.
    *
    * @param data the data
    * @param xp the xpath
    * @throws JDOMException the jDOM exception
    */
   private void checkXPath(Document data, String xp) throws JDOMException {
      XPath xpath = XPath.newInstance(xp);
      xpath.addNamespace("csw", "http://www.opengis.net/cat/csw/2.0.2");
      Object result = xpath.selectSingleNode(data);
      Assert.assertNotNull(result);
   }

   /**
    * Search.
    *
    * @param requestName the request name
    * @return the element
    * @throws IOException
    * @throws JDOMException
    */
   private Document search(String requestName) throws JDOMException, IOException {
      selenium.select("xpath=//select[@id='request']", requestName);
      pause(500);
      selenium.click("xpath=//input[@value='Send request (POST)']");
      pause(1000);
      String result = selenium.getValue("id=response");
      logger.info("CSW result: \n{}", result);
      return buildElement(result);
   }

   /**
    * Builds the element.
    *
    * @param data the data
    * @return the element
    * @throws IOException
    * @throws JDOMException
    */
   private Document buildElement(String data) throws JDOMException, IOException {
      SAXBuilder builder = new SAXBuilder();
      Document jdoc = builder.build(new StringReader(data));
      return jdoc;
   }

   /**
    * Open CSW test page.
    *
    * @throws Exception the exception
    */
   private void openCswTest() throws Exception {
      // HACK Igor local
      loadMetadata("src/test/resources/user/homepage/search");
      open("/srv/en/test.csw");
   }

}
