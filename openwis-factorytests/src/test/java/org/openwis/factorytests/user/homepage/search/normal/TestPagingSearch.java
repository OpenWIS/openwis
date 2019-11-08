package org.openwis.factorytests.user.homepage.search.normal;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.factorytests.user.homepage.search.AbstractSearchTest;

/**
 * Factory test: VT_OPENWIS_USR_SCH_04
 */
public class TestPagingSearch extends AbstractSearchTest {

   /**
    * Search with 10 results by page
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_04_01() throws Exception {
      int hits = 10;
      // HACK Igor local
      loadMetadata("src/test/resources/user/homepage/search");
      openUserHomePage();

      // Search
      setHitsValue(hits);
      search();

      int count;
      Set<String> uuid = new HashSet<String>();

      count = countSearchResults();
      Assert.assertEquals(hits, count);
      uuid.addAll(getAllHitsUuid());

      // Go to page 2
      switchToPage(2);

      count = countSearchResults();
      Assert.assertEquals(2, count);
      uuid.addAll(getAllHitsUuid());

      // Check all data
      Assert.assertEquals(12, uuid.size());
   }

   /**
    * Search with 20 results by page
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_04_02() throws Exception {
      int hits = 20;
      // HACK Igor local
      loadMetadata("src/test/resources/user/homepage/search");
      openUserHomePage();

      // Search
      setHitsValue(hits);
      search();

      int count;
      Set<String> uuid = new HashSet<String>();

      count = countSearchResults();
      Assert.assertEquals(12, count);
      uuid.addAll(getAllHitsUuid());

      // Check all data
      Assert.assertEquals(12, uuid.size());
   }
}
