package org.openwis.metadataportal.services.request.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.server.UserSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;

public class OperationEnumUtils {

   @SuppressWarnings("unchecked")
   public static Collection<Integer> getOperationEnum(UserSession session, Dbms dbms, String urn)
         throws Exception {
      GroupManager gm = new GroupManager(dbms);
      Collection<Integer> operationsEnum = null;
      List<Group> groups = null;
      if (session.isAuthenticated()) {
         if (session.getProfile().equals(Geonet.Profile.ADMINISTRATOR)) {
            groups = gm.getAllGroups();
         } else {
            groups = gm.getAllUserGroups(session.getUserId());
         }
         IDataPolicyManager dpm = new DataPolicyManager(dbms);
         Collection<Operation> operations = dpm.getAllOperationAllowedByMetadataUrn(urn, groups);

         operationsEnum = CollectionUtils.collect(operations, new Transformer() {

            @Override
            public Object transform(Object arg0) {
               return ((Operation) arg0).getId();
            }
         });
      } else {
         // Always grant VIEW privileges for non authenticated users.
         operationsEnum = Arrays.asList(OperationEnum.VIEW.getId());
      }
      return operationsEnum;
   }

}
