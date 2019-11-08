/**
 * 
 */
package org.openwis.metadataportal.services.user.dto;

import java.util.List;

import org.openwis.metadataportal.model.user.User;

/**
 * User DTO class. <P>
 * 
 */
public class UsersDTO {

   /**
    * The user list.
    * @member: users
    */
   private List<User> users;

   /**
    * Gets the users.
    * @return the users.
    */
   public List<User> getUsers() {
      return users;
   }

   /**
    * Sets the users.
    * @param users the users to set.
    */
   public void setUsers(List<User> users) {
      this.users = users;
   }
}
