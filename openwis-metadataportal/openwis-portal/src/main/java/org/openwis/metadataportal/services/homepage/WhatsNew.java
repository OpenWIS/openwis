/**
 * 
 */
package org.openwis.metadataportal.services.homepage;

import java.util.ArrayList;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class WhatsNew implements Service {

    private int maxItems;

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    @Override
    public Element exec(Element arg0, ServiceContext context) throws Exception {
       GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

       ISearchManager searchMan = gc.getSearchmanager();
       
       List<Metadata> ms = new ArrayList<Metadata>();
       try {
          ms = searchMan.getAllLatestMetadata(maxItems);
       } catch (Exception e) {
         Log.error(Geonet.OPENWIS, e.getMessage());
       }
       
        
        return JeevesJsonWrapper.send(ms);
    }

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    @Override
    public void init(String arg0, ServiceConfig config) throws Exception {
        String sMaxItems = config.getValue("maxItems", "10");

        maxItems = Integer.parseInt(sMaxItems);
    }

}