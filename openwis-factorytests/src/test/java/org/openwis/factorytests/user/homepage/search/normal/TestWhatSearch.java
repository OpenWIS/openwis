package org.openwis.factorytests.user.homepage.search.normal;

import java.text.MessageFormat;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.factorytests.user.homepage.search.AbstractSearchTest;

/**
 * Factory test: VT_OPENWIS_USR_SCH_01
 */
public class TestWhatSearch extends AbstractSearchTest {

   /**
   * Search for metadata with 'lorem'.
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
   public void test_VT_OPENWIS_USR_SCH_01_01() throws Exception {
      search("lorem");
      checkUuid("urn:uuid:a06af396-3105-442d-8b40-22b57a90d2f2",
            "urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f",
            "urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357",
            "urn:uuid:ab42a8c4-95e8-4630-bf79-33e59241605a",
            "urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63");
   }

   /**
    * Search for metadata with 'Fuscé vitae ligulä'.
    * Should return the 'urn:uuid:e9330592-0932-474b-be34-c3a3bb67c7db' metadata (found in title)
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_01_02() throws Exception {
      search("Fuscé vitae ligulä");
      checkUuid("urn:uuid:e9330592-0932-474b-be34-c3a3bb67c7db");
   }

   /**
    * Search for metadata with 'Physiography-Landforms'.
    * Should return the 'urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357' metadata (found in subject)
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_01_03() throws Exception {
      search("Physiography-Landforms");
      checkUuid("urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357");
   }

   /**
    * Search for metadata with 'scelerisque'.
    * Should return the 'urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357' and
    * 'urn:uuid:784e2afd-a9fd-44a6-9a92-a3848371c8ec' metadata (abstract)
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_01_04() throws Exception {
      search("scelerisque");
      checkUuid("urn:uuid:784e2afd-a9fd-44a6-9a92-a3848371c8ec",
            "urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357");
   }

   /**
    * Empty search result.
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_01_05() throws Exception {
      search("AZEERTTY");
      checkUuid();
   }

   /**
    * Search.
    *
    * @param what the what field
    */
   private void search(String what) throws Exception {
      // HACK Igor local
      loadMetadata("src/test/resources/user/homepage/search");
      openUserHomePage();

      // Launch search
      setWhatValue(what);
      search();
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
