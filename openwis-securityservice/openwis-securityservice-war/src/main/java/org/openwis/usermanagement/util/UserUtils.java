/**
 * 
 */
package org.openwis.usermanagement.util;

import static org.openwis.usermanagement.util.LdapUtils.COMMA;
import static org.openwis.usermanagement.util.LdapUtils.EQUAL;
import static org.openwis.usermanagement.util.LdapUtils.PEOPLE;
import static org.openwis.usermanagement.util.LdapUtils.UID;

import org.openwis.usermanagement.exception.UserManagementException;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class UserUtils {

   /**
    * Default constructor.
    * Builds a UserUtils.
    */
   private UserUtils() {
      
   }
   
   /**
    * Get DN of a user.
    * @param userName The user name
    * @return the DN of this user.
    */
   public static String getDn(String userName) {
      return UID + EQUAL + userName + COMMA + PEOPLE;
   }
   
   /**
    * Get User Name By Dn.
    * @param dn the DN of this user
    * @return the user name
    * @throws UserManagementException if an error occurs.
    */
   public static String getUserNameByDn(String dn) throws UserManagementException{
      String[] ldapPath = dn.split(COMMA + PEOPLE);
      if (ldapPath.length == 1) {
         //'ldapPath[0]' == 'uid=userName'
         String[] userName = ldapPath[0].split(UID + EQUAL);
         if (userName.length == 2) {
            return userName[1];
         }
      }
      throw new UserManagementException();
   }

}
