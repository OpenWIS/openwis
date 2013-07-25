package org.openwis.factorytests.user.homepage.search.advanced;

import java.text.MessageFormat;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.factorytests.user.homepage.search.AbstractSearchTest;

/**
 * Factory test: VT_OPENWIS_USR_SCH_05
 */
public class TestAdvancedSearch extends AbstractSearchTest {

   /**
   * Search for metadata with  title 'lorem'.
   * Should return 2 metadata:
   * <ul>
   * <li>urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2</li>
   * <li>urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f</li>
   * </ul>
   * @throws Exception the exception
   */
   @Test
   public void test_VT_OPENWIS_USR_SCH_05_01() throws Exception {
      openAdvancedSearch();

      // set title to 'lorem'
      selenium.type("xpath=//input[@name='title']", "lorem");
      this.search();
      checkUuid("urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2",
            "urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f");
   }

   /**
   * Search for metadata with abstract 'lorem'.
   * Should return 3 metadata:
   * <ul>
   * <li>urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357</li>
   * <li>urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63</li>
   * <li>urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a</li>
   * </ul>
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_05_02() throws Exception {
      openAdvancedSearch();

      // set abstract to 'lorem'
      selenium.type("xpath=//input[@name='abstract']", "lorem");
      this.search();
      checkUuid("urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357",
            "urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63",
            "urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a");
   }

   /**
   * Search for metadata with BBox to (-5,47) (1,52).
   * Should return 2 metadata:
   * <ul>
   * <li>urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63</li>
   * <li>urn:uuid:9a669547-b69b-469f-a11f-2d875366bbdc</li>
   * </ul>
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_05_03() throws Exception {
      openAdvancedSearch();

      // set BBox to (-5,47) (1,52)
      selenium.type("xpath=//input[@name='southBL']", "47");
      selenium.type("xpath=//input[@name='westBL']", "-5");
      selenium.type("xpath=//input[@name='northBL']", "52");
      selenium.type("xpath=//input[@name='eastBL']", "1");
      this.search();
      checkUuid("urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63",
            "urn:uuid:9a669547-b69b-469f-a11f-2d875366bbdc");
   }

   /**
    * Search metadata without the 'lorem' string
    * Expected to found 5 metadata
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_05_04() throws Exception {
      openAdvancedSearch();

      // Expend Advanced text search options
      selenium.click("xpath=//legend[span/text()='Advanced text search options']/div");

      // set without 'lorem'
      selenium.type("xpath=//input[@name='without']", "lorem");
      this.search();
      Assert.assertEquals(7, countSearchResults());
   }

   /**
    * Open advanced search.
    *
    * @throws Exception the exception
    */
   private void openAdvancedSearch() throws Exception {
      // HACK Igor local
      //      loadMetadata("src/test/resources/user/homepage/search");
      openUserHomePage();
      switchAdvancedSearch();
   }

   /**
    * Check uuid.
    *
    * @param uuidList the uuid list
    */
   private void checkUuid(String... uuidList) {
      List<String> list = getAllHitsUuid();
      Assert.assertNotNull(list);
      Assert.assertEquals(uuidList.length, list.size());

      for (String uuid : uuidList) {
         Assert.assertTrue(MessageFormat.format(
               "The ''{0}'' metadata should be present into the search result: {1}", uuid, list),
               list.contains(uuid));
      }
   }
}
