package org.openwis.factorytests.user.homepage.search.normal;

import java.text.MessageFormat;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.factorytests.user.homepage.search.AbstractSearchTest;

/**
 * Factory test: VT_OPENWIS_USR_SCH_03
 */
public class TestSortingSearch extends AbstractSearchTest {

   /**
   * Search for metadata with 'lorem' sorted by changeDate.
   * Should return 5 metadata:
   * <ul>
   * <li>urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f</li>
   * <li>urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2</li>
   * <li>urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63</li>
   * <li>urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a</li>
   * <li>urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357</li>
   * </ul>
   * @throws Exception the exception
   */
   @Test
   public void test_VT_OPENWIS_USR_SCH_03_01() throws Exception {
      search("lorem", "Change date");
      pause(1000);
      checkSortedUuid("urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a",
            "urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f",
            "urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357",
            "urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2",
            "urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63");
   }

   /**
   * Search for metadata with 'lorem' sorted by title.
   * Should return 5 metadata:
   * <ul>
   * <li>urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f</li>
   * <li>urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2</li>
   * <li>urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63</li>
   * <li>urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a</li>
   * <li>urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357</li>
   * </ul>
   * @throws Exception the exception
   */
   @Test
   public void test_VT_OPENWIS_USR_SCH_03_02() throws Exception {
      search("lorem", "Title");
      checkSortedUuid("urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f",
            "urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2",
            "urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63",
            "urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a",
            "urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357");
   }

   /**
   * Search for metadata with 'lorem' sorted by relevance.
   * Should return 5 metadata:
   * <ul>
   * <li>urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2</li>
   * <li>urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f</li>
   * <li>urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357</li>
   * <li>urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a</li>
   * <li>urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63</li>
   * </ul>
   * @throws Exception the exception
   */
   @Test
   public void test_VT_OPENWIS_USR_SCH_03_03() throws Exception {
      search("lorem", "Relevance");
      checkSortedUuid("urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63",
            "urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f",
            "urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a",
            "urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2",
            "urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357");
   }

   /**
    * Search.
    *
    * @param what the what field
    */
   private void search(String what, String sort) throws Exception {
      // HACK Igor local
      loadMetadata("src/test/resources/user/homepage/search");
      openUserHomePage();

      // Launch search
      setWhatValue(what);
      setSortValue(sort);
      search();
   }

   /**
    * Check uuid.
    *
    * @param uuidList the uuid list
    */
   private void checkSortedUuid(String... uuidList) {
      List<String> list = getAllHitsUuid();
      Assert.assertNotNull(list);
      Assert.assertEquals(uuidList.length, list.size());

      for (int i = 0; i < uuidList.length; i++) {
         Assert.assertEquals(MessageFormat.format(
               "The ''{0}'' metadata should be into the search result: {1} at index {2}",
               uuidList[i], list, i), uuidList[i], list.get(i));
      }
   }
}
