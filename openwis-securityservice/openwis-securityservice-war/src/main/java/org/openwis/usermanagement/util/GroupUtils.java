/**
 * 
 */
package org.openwis.usermanagement.util;

import static org.openwis.usermanagement.util.LdapUtils.CN;
import static org.openwis.usermanagement.util.LdapUtils.COMMA;
import static org.openwis.usermanagement.util.LdapUtils.EQUAL;
import static org.openwis.usermanagement.util.LdapUtils.GROUP;
import static org.openwis.usermanagement.util.LdapUtils.OU;
import static org.openwis.usermanagement.util.LdapUtils.GLOBAL;

import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.model.group.OpenWISGroup;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class GroupUtils {

   /**
    * Default constructor.
    * Builds a GroupUtils.
    */
   private GroupUtils() {

   }

   /**
    * Get DN of a group.
    * @param groupCentre The group centre name
    * @param groupId The group id
    * @return the DN of this group.
    */
   public static String getGroupDn(String groupCentre, String groupId) {
      return CN + EQUAL + groupId + COMMA + OU + EQUAL + groupCentre + COMMA + GROUP;
   }

   /**
    * Get group centre
    * @param groupCentre The group centre name.
    * @return The DN for a group centre.
    */
   public static String getGroupCentreDn(String groupCentre) {
      return OU + EQUAL + groupCentre + COMMA + GROUP;
   }

   /**
    * Get Open Wis Group By CN.
    * @param cn the CN of the group
    * @return the group.
    * @throws UserManagementException if an error occurs.
    */
   public static OpenWISGroup getOpenWisGroupByDn(String cn) throws UserManagementException {
      //cn=<groupId>,ou=<centreGroup>,ou=groups,dc=opensso,dc=java,dc=net

      OpenWISGroup openWISGroup = new OpenWISGroup();

      String[] ldapPath = cn.split(COMMA + OU + EQUAL);

      if (ldapPath.length == 3) {

         //Get the centre group name = CN value
         String[] groupId = ldapPath[0].split(CN + EQUAL);
         if (groupId.length == 2) {
            openWISGroup.getGroupIds().add(groupId[1]);
         }

         //Get the centre name = OU value
         openWISGroup.setCentreName(ldapPath[1]);
         
         //test if the group is Global.
         openWISGroup.setIsGlobal(GLOBAL.equals(ldapPath[1]));

      }

      return openWISGroup;
   }
}
