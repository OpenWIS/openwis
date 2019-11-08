/**
 * 
 */
package org.openwis.metadataportal.services.user;

import java.util.ArrayList;
import java.util.List;

import jeeves.exceptions.MissingParameterEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.securityservice.OpenWISUser;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class AllImport implements Service {

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
       Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

       UserManager userManager = new UserManager(dbms);
       
       List<OpenWISUser> results = new ArrayList<OpenWISUser>();

        
       String userFilter = null;
       try {
          userFilter = Util.getParam(params, "userFilter");
       } catch (MissingParameterEx e) {
          // No exception to catch
       }
       
       if (StringUtils.isNotBlank(userFilter)) {
          results = userManager.getAllImportedUsersLike(userFilter);
       } else {
          results = userManager.getAllImportedUsers();
       }

       return JeevesJsonWrapper.send(results);
   }

}
