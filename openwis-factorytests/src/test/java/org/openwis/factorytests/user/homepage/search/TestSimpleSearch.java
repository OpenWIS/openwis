package org.openwis.factorytests.user.homepage.search;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Factory test: VT-OPENWIS-USR-HOM-01
 */
public class TestSimpleSearch extends AbstractSearchTest {

   /**
    * Test search with no criteria, should return all data (none template)
    * @throws Exception
    */
   @Test
   public void test_VT_OPENWIS_USR_HOM_02_01() throws Exception {
      loadMetadata("src/test/resources/user/homepage/md_01");
      openUserHomePage();
      clickOnButton("Search");
      waitForTextDisappear(METADATA_SEARCHING_MSG, 5);

      String hitTitle;
      int count;

      count = countSearchResults();
      assertEquals(3, count);
      hitTitle = getHitTitle(1);
      assertEquals("ECEU82LFRO", hitTitle);
      hitTitle = getHitTitle(2);
      assertEquals("ECEU83LFRO", hitTitle);
      hitTitle = getHitTitle(3);
      assertEquals("ECEU84LFRO", hitTitle);
   }

   /**
    * Test search with a specific data
    * @throws Exception
    */
   @Test
   public void test_VT_OPENWIS_USR_HOM_02_02() throws Exception {
      loadMetadata("src/test/resources/user/homepage/md_01");
      openUserHomePage();

      setWhatValue("ECEU82LFRO");
      clickOnButton("Search");
      waitForTextDisappear(METADATA_SEARCHING_MSG, 5);

      assertEquals(1, countSearchResults());
      assertEquals("ECEU82LFRO", getHitTitle(1));
   }

}
