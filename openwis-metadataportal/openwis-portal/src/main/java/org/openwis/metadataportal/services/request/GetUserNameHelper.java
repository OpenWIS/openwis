/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.openwis.metadataportal.services.login.TokenUtilities;
import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

/**
 * Get User name Helper. <P>
 * 
 */
public class GetUserNameHelper {

   /**
    * Return the user name.
    * 
    * @param context The service context.
    * @param idpUrl the IDP URL.
    * @param token The token ID.
    * @return the user name
    * @throws OpenWisLoginEx if an error occurs.
    */
   public static String getUserName(Element params, ServiceContext context) throws Exception {

      String userName = "";

      if (StringUtils.isNotBlank(context.getUserSession().getUsername())) {
         userName = context.getUserSession().getUsername();
      } else {
         String idpUrl = Util.getParam(params, "idpURL");
         String token = URLEncoder.encode(Util.getParam(params, "token"), "UTF-8");
         if (idpUrl != null && token != null) {
            TokenUtilities tokenUtilities = new TokenUtilities();
            if (tokenUtilities.isTokenValid(idpUrl, token)) {
               userName = tokenUtilities.getUserByToken(idpUrl, token);
            } else {
               throw new OpenWisLoginEx("Error during validating token");
            }
         }
      }
      if (userName.isEmpty()) {
         throw new OpenWisLoginEx("Error during getting username");
      }
      return userName;
   }

}
