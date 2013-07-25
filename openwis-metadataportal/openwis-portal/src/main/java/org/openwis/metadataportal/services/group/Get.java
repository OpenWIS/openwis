/**
 * 
 */
package org.openwis.metadataportal.services.group;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Get implements Service {

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        Group group = JeevesJsonWrapper.read(params, Group.class);

        if (StringUtils.isNotBlank(group.getName())) {
            Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
            
            GroupManager gm = new GroupManager(dbms);
            group = gm.getGroupByName(group.getName(), group.isGlobal());
        }
        return JeevesJsonWrapper.send(group);
    }

}
