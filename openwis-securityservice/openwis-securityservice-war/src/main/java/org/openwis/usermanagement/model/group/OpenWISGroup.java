package org.openwis.usermanagement.model.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines an OpenWIS Group Model. <P>
 * A user belongs to one or several DCPC/GISC and of course belongs 
 * to at least one group id for each of them.
 */
public class OpenWISGroup implements Serializable {

   /**
    * If true, the group is Global.
    * @member: isGlobal
    */
   private boolean isGlobal;

   /**
    * The centre name (GLOBAL, GISC, DCPC, ...)
    * @member: center The centre name
    */
   private String centreName;

   /**
    * List of group Identifier.
    * @member: groupIds The group Ids
    */
   private List<String> groupIds = new ArrayList<String>();

   /**
    * Default constructor.
    * Builds a OpenWISGroup.
    */
   public OpenWISGroup() {

   }

   /**
    * Default constructor.
    * Builds a OpenWISGroup.
    * @param isGlobal True if the group is Global
    */
   public OpenWISGroup(boolean isGlobal) {
      this.isGlobal = isGlobal;
      groupIds = new ArrayList<String>();
   }

   /**
    * Gets the groupIds.
    * @return the groupIds.
    */
   public List<String> getGroupIds() {
      return groupIds;
   }

   /**
    * Sets the groupIds.
    * @param groupIds the groupIds to set.
    */
   public void setGroupIds(List<String> groupIds) {
      this.groupIds = groupIds;
   }

   /**
    * Get the centre name.
    * @return the centre name.
    */
   public String getCentreName() {
      return centreName;
   }

   /**
    * Sets the centreName.
    * @param centreName the centreName to set.
    */
   public void setCentreName(String centreName) {
      if (!isGlobal) {
         this.centreName = centreName;
      }
   }

   /**
    * Gets the isGlobal.
    * @return the isGlobal.
    */
   public boolean isIsGlobal() {
      return isGlobal;
   }

   /**
    * Sets the isGlobal.
    * @param globalGroup True if the group is global, false otherwise.
    */
   public void setIsGlobal(boolean globalGroup) {
      this.isGlobal = globalGroup;
   }

}
