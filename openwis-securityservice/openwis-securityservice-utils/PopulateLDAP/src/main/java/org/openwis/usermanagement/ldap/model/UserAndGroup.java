/**
 * 
 */
package org.openwis.usermanagement.ldap.model;

import java.util.List;

import org.openwis.securityservice.OpenWISGroup;
import org.openwis.securityservice.OpenWISUser;


/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class UserAndGroup {
   
   /**
    * @member: users List of users
    */
   private List<OpenWISUser> users;
   
   /**
    * @member: groups List of groups
    */
   private List<OpenWISGroup> groups;

   /**
    * Gets the users.
    * @return the users.
    */
   public List<OpenWISUser> getUsers() {
      return users;
   }

   /**
    * Sets the users.
    * @param users the users to set.
    */
   public void setUsers(List<OpenWISUser> users) {
      this.users = users;
   }

   /**
    * Gets the groups.
    * @return the groups.
    */
   public List<OpenWISGroup> getGroups() {
      return groups;
   }

   /**
    * Sets the groups.
    * @param groups the groups to set.
    */
   public void setGroups(List<OpenWISGroup> groups) {
      this.groups = groups;
   }

}
