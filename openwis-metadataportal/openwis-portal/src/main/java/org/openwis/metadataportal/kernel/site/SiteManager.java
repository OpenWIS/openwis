/**
 * 
 */
package org.openwis.metadataportal.kernel.site;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.model.site.Site;

/**
 * The Setting Manager <P>
 * 
 */
public class SiteManager extends AbstractManager {

   /**
   * @member: sm Setting Manager
   */
   private SettingManager sm;

   /**
    * Default constructor.
    * Builds a RegionManager.
    * @param dbms
    */
   public SiteManager(Dbms dbms, SettingManager sm) {
      super(dbms);
      this.sm = sm;
   }

   /**
   * Get all sites.
   * @return the list of sites
   * @throws Exception if an error 
   */
   @SuppressWarnings("unchecked")
   public List<Site> getAllSites() throws Exception {
      String name = sm.getValue("system/site/name");
      String siteId = sm.getValue("system/site/siteId");

      String query = "SELECT uuid, name FROM Sources";
      List<Element> records = getDbms().select(query).getChildren();
      List<Site> sites = new ArrayList<Site>();
      sites.add(new Site(siteId, name));
      for (Element e : records) {
         Site site = new Site();
         site.setId(e.getChildText("uuid"));
         site.setName(e.getChildText("name"));
         sites.add(site);
      }
      return sites;
   }

}
