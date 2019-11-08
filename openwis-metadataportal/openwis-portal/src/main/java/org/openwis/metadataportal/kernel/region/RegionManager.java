/**
 * 
 */
package org.openwis.metadataportal.kernel.region;

import java.util.ArrayList;
import java.util.List;

import jeeves.resources.dbms.Dbms;

import org.jdom.Element;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.model.region.CardinalExtent;
import org.openwis.metadataportal.model.region.Region;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class RegionManager extends AbstractManager {

    /**
     * Default constructor.
     * Builds a RegionManager.
     * @param dbms
     */
    public RegionManager(Dbms dbms) {
        super(dbms);
    }
    
    @SuppressWarnings("unchecked")
    public List<Region> getAllRegions(String lang) throws Exception {
        String query = "SELECT reg.*, des.label FROM Regions reg, RegionsDes des " +
        		"WHERE des.langid = ? AND reg.id = des.iddes " +
        		"ORDER BY des.label";
        List<Element> records = getDbms().select(query, lang).getChildren();
        List<Region> regions = new ArrayList<Region>();
        for (Element e : records) {
            Region region = new Region();
            region.setId(Integer.parseInt(e.getChildText("id")));
            region.setName(e.getChildText("label"));
            
            float north = Float.parseFloat(e.getChildText("north"));
            float south = Float.parseFloat(e.getChildText("south"));
            float west = Float.parseFloat(e.getChildText("west"));
            float east = Float.parseFloat(e.getChildText("east"));
            region.setExtent(new CardinalExtent(north, south, west, east));
            regions.add(region);
        }
        return regions;
    }

}
