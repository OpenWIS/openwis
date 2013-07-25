/**
 * 
 */
package org.openwis.metadataportal.model.datapolicy;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.group.Group;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataPolicyOperationsPerGroup {

   private Group group;

   private List<DataPolicyGroupPrivileges> privilegesPerOp;

   /**
    * Gets the group.
    * @return the group.
    */
   public Group getGroup() {
      return group;
   }

   /**
    * Sets the group.
    * @param group the group to set.
    */
   public void setGroup(Group group) {
      this.group = group;
   }

   /**
    * Gets the privilegePerOp.
    * @return the privilegePerOp.
    */
   public List<DataPolicyGroupPrivileges> getPrivilegesPerOp() {
      if(privilegesPerOp == null) {
         privilegesPerOp = new ArrayList<DataPolicyGroupPrivileges>();
      }
      return privilegesPerOp;
   }

   /**
    * Sets the privilegePerOp.
    * @param privilegePerOp the privilegePerOp to set.
    */
   public void setPrivilegesPerOp(List<DataPolicyGroupPrivileges> privilegesPerOp) {
      this.privilegesPerOp = privilegesPerOp;
   }

}
