package org.openwis.factorytests.user.homepage.search.normal;

import java.text.MessageFormat;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.factorytests.user.homepage.search.AbstractSearchTest;

/**
 * Factory test: VT_OPENWIS_USR_SCH_02
 */
public class TestGeoSearch extends AbstractSearchTest {

   /**
   * Search for metadata overlaps North East.
   * @throws Exception the exception
   */
   @Test
   public void test_VT_OPENWIS_USR_SCH_02_01() throws Exception {
      Point p0 = new Point(1.0, 1.0);
      Point p1 = new Point(180.0, 90.0);

      // Search NE
      search(p0, p1);
      checkUuid("urn:x-wmo:md:int.wmo.wis::ECEU83LFRO", "urn:x-wmo:md:int.wmo.wis::EVEU83LFRO",
            "urn:x-wmo:md:int.wmo.wis::EVEU84LFRO");
   }

   /**
    * Search for metadata overlaps South East.
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_02_02() throws Exception {
      Point p0 = new Point(1.0, -90.0);
      Point p1 = new Point(180.0, -1.0);

      // Search SE
      search(p0, p1);
      checkUuid("urn:x-wmo:md:int.wmo.wis::ECEU84LFRO", "urn:x-wmo:md:int.wmo.wis::ECEU83LFRO",
            "urn:x-wmo:md:int.wmo.wis::EVEU83LFRO", "urn:x-wmo:md:int.wmo.wis::EVEU84LFRO");
   }

   /**
    * Search for metadata overlaps South West.
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_02_03() throws Exception {
      Point p0 = new Point(-180.0, -90.0);
      Point p1 = new Point(-1.0, -1.0);

      // Search SW
      search(p0, p1);
      checkUuid("urn:x-wmo:md:int.wmo.wis::EIEU83LFRO", "urn:x-wmo:md:int.wmo.wis::EIEU84LFRO",
            "urn:x-wmo:md:int.wmo.wis::EVEU83LFRO", "urn:x-wmo:md:int.wmo.wis::EVEU84LFRO");
   }

   /**
    * Search for metadata overlaps North West.
    *
    * @throws Exception the exception
    */
   @Test
   public void test_VT_OPENWIS_USR_SCH_02_04() throws Exception {
      Point p0 = new Point(-180.0, 1.0);
      Point p1 = new Point(-1.0, 90.0);

      // Search NW
      search(p0, p1);
      checkUuid("urn:x-wmo:md:int.wmo.wis::EIEU83LFRO",
            "urn:x-wmo:md:int.wmo.wis::EVEU83LFRO", "urn:x-wmo:md:int.wmo.wis::EVEU84LFRO");
   }

   /**
    * Search.
    *
    * @param p0 the p0 bottom right point
    * @param p1 the p1 top left point
    * @throws Exception the exception
    */
   private void search(Point p0, Point p1) throws Exception {
      // HACK Igor local
      loadMetadata("src/test/resources/user/homepage/searchgeo");
      openUserHomePage();

      // Launch search
      setNormalGeo(p0.x, p0.y, p1.x, p1.y);
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
