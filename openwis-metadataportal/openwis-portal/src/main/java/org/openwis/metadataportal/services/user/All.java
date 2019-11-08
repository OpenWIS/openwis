/**
 * 
 */
package org.openwis.metadataportal.services.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jeeves.exceptions.MissingParameterEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.securityservice.OpenWISGroup;

/**
 * List all groups. <P>
 * Explanation goes here. <P>
 * 
 */
public class All implements Service {

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
   @SuppressWarnings("unchecked")
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      GroupManager groupManager = new GroupManager(dbms);
      UserManager userManager = new UserManager(dbms);
      
      List<User> results = new ArrayList<User>();

      ArrayList<OpenWISGroup> openwisGroups = null;
      String groupsFilter = null;
      try {
         groupsFilter = Util.getParam(params, "groups");
         
         if (StringUtils.isNotBlank(groupsFilter)) {
            List<Group> groupsPortal = groupManager.getAllGroupsById(Arrays.asList(StringUtils
                  .split(groupsFilter)));
            Collection<OpenWISGroup> openwisGroupsColl = CollectionUtils.collect(groupsPortal,
                  new Transformer() {
                     @Override
                     public Object transform(Object input) {
                        return GroupManager.buildOpenWisGroupFromGroup((Group) input);
                     }
                  });
            openwisGroups = new ArrayList<OpenWISGroup>(openwisGroupsColl);
         }
      } catch (MissingParameterEx e) {
         // No exception to catch
      }
       
      String userFilter = null;
      try {
         userFilter = Util.getParam(params, "userFilter");
      } catch (MissingParameterEx e) {
         // No exception to catch
      }
     
      if (openwisGroups == null) {
         results = userManager.getAllUserLike(userFilter);
      } else {
         results = userManager.getAllUserLike(userFilter, openwisGroups);
      }

      return JeevesJsonWrapper.send(results);
   }
}
