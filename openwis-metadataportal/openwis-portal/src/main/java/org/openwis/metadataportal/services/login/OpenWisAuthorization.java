package org.openwis.metadataportal.services.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jeeves.server.UserSession;
import jeeves.utils.Log;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Params;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;

import com.sun.identity.plugin.session.SessionException;
import com.sun.identity.saml2.assertion.impl.NameIDImpl;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.jaxb.metadata.IDPSSODescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.SingleSignOnServiceElement;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.profile.SPACSUtils;

/**
 * Class for authorization. <P>
 */
@SuppressWarnings("serial")
public class OpenWisAuthorization extends HttpServlet {

   /**
    * {@inheritDoc}
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @SuppressWarnings({"rawtypes", "unchecked"})
   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      String sName = new String();
      String sSurname = new String();
      String sProfile = new String();
      String username = new String();
      String email = new String();
      String classOfService = "";
      String language = "";

      List<String> isMemberOf = new ArrayList<String>();
      boolean needLocalAccount = false;

      try {
         Map map = getResponseForFedlet(request, response);
         Map<String, HashSet<String>> attrs = (Map<String, HashSet<String>>) map
               .get(SAML2Constants.ATTRIBUTE_MAP);

         Log.debug(LoginConstants.LOG, "setting Fedlet attributes" + attrs);

         if (attrs.containsKey(LoginConstants.CN)) {
            username = getAttribute(attrs, LoginConstants.CN);
            Log.debug(LoginConstants.LOG, "setFedletAttributes - username=" + username);
         }
         if (attrs.containsKey(LoginConstants.SN)) {
            sSurname = getAttribute(attrs, LoginConstants.SN);
            Log.debug(LoginConstants.LOG, "setFedletAttributes - sSurname=" + sSurname);
         }
         if (attrs.containsKey(LoginConstants.GIVEN_NAME)) {
            sName = getAttribute(attrs, LoginConstants.GIVEN_NAME);
            Log.debug(LoginConstants.LOG, "setFedletAttributes - sName=" + sName);
         }
         if (attrs.containsKey(LoginConstants.PROFILE)) {
            sProfile = getAttribute(attrs, LoginConstants.PROFILE);
            Log.debug(LoginConstants.LOG, "setFedletAttributes - sProfile=" + sProfile);
         }
         if (attrs.containsKey(LoginConstants.IS_MEMBER_OF)) {
            isMemberOf = getAttributes(attrs, LoginConstants.IS_MEMBER_OF);
            Log.debug(LoginConstants.LOG, "setFedletAttributes - isMemberOf=" + isMemberOf);
         }

         if (attrs.containsKey(LoginConstants.NEED_USER_ACCOUNT)) {
            String needUserAccount = getAttribute(attrs, LoginConstants.NEED_USER_ACCOUNT);
            needLocalAccount = Boolean.valueOf(needUserAccount);
            Log.debug(LoginConstants.LOG, "setFedletAttributes - need User Account="
                  + needLocalAccount);
         }

         if (attrs.containsKey(LoginConstants.MAIL)) {
            email = getAttribute(attrs, LoginConstants.MAIL);
            Log.debug(LoginConstants.LOG, "setFedletAttributes - email =" + email);
         }
         
         if (attrs.containsKey(LoginConstants.CLASS_OF_SERVICE)) {
            classOfService = getAttribute(attrs, LoginConstants.CLASS_OF_SERVICE);
            Log.debug(LoginConstants.LOG, "setFedletAttributes - classOfService =" + classOfService);
         }
         
         // Check if user belongs to group
         List<String> applyGroup = new ArrayList<String>();
         boolean foundLocalGroup = false;
         for (String group : isMemberOf) {
            
            if (groupContainsCentre(group,
                  OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME))) {
               applyGroup.add(group);
               foundLocalGroup = true;
            } else if (groupContainsCentre(group, LoginConstants.GLOBAL)) {
               applyGroup.add(group);
            }
         }
         
         // Check need user account constraint
         // if needUserAccount enabled, the list of groups must contain at least one local group
         // otherwise we ignore the global groups
         if (needLocalAccount && !foundLocalGroup) {
            // clean the list of global groups
            applyGroup.clear();
         }

         HttpSession httpSession = request.getSession();
         Log.debug(LoginConstants.LOG, "Session id is " + httpSession.getId());

         UserSession session = (UserSession) httpSession.getAttribute(LoginConstants.SESSION);
         if (session == null) {
            session = new UserSession();
            httpSession.setAttribute(LoginConstants.SESSION, session);
         }

         session.setProperty(LoginConstants.IDP_ENTITY_ID, map.get(LoginConstants.IDP_ENTITY_ID));
         session.setProperty(LoginConstants.SP_ENTITY_ID, map.get(LoginConstants.SP_ENTITY_ID));
         session.setProperty(LoginConstants.SESSION_INDEX, map.get(LoginConstants.SESSION_INDEX));
         session.setProperty(LoginConstants.NAME_ID,
               ((NameIDImpl) map.get(LoginConstants.NAME_ID)).getValue());
         session.setProperty(Params.GROUPS, applyGroup);

         session.setProperty(LoginConstants.NOT_CONNECTED_TO_CONNECTED,
               LoginConstants.NOT_CONNECTED_TO_CONNECTED);

         Object relayState = map.get(LoginConstants.RELAY_STATE);
         if (relayState != null && !(((String) relayState).equals("null"))
               && StringUtils.isNotBlank((String) relayState)) {
            session.setProperty(LoginConstants.RELAY_STATE, map.get(LoginConstants.RELAY_STATE));

//            // retrieve language
//            String strRelayState = (String) relayState;
//            if (strRelayState.contains(LoginConstants.LANG + ":")) {
//               int start = strRelayState.indexOf(LoginConstants.LANG) + LoginConstants.LANG.length() + 1;
//               String lang = strRelayState.substring(start, start + 2);
//               session.setProperty(LoginConstants.LANG, lang);
//            }
            
         }

         //Store preferred IdP URL
         session.setProperty(LoginConstants.PREFERRED_IDP_URL, getPreferredIdPURL(session));
         
         session.setProperty(LoginConstants.CLASS_OF_SERVICE, classOfService);
         
         session.authenticate(null, username, sName, sSurname, sProfile, email);

         // Get the token.
         String requestURL = StringUtils.remove(request.getRequestURL().toString(),
               request.getServletPath());
         requestURL = requestURL.concat("/openWisGetToken?token=");
         String idpUrl = session.getProperty(LoginConstants.PREFERRED_IDP_URL)
               + "/spGetToken.jsp?spTokenAddress=" + requestURL;

         session.setProperty(LoginConstants.IS_APPLY_GROUP_EMPTY, applyGroup.isEmpty());
         
         // store language
         session.setProperty(LoginConstants.LANG, language);

         response.sendRedirect(idpUrl);

      } catch (SAML2Exception e) {
         Log.error(LoginConstants.LOG, e.getMessage(), e);
         forwardError(request, response, "Error during saml process - " + e.getMessage());
      } catch (SessionException e) {
         Log.error(LoginConstants.LOG, e.getMessage(), e);
         forwardError(request, response, "Error during session - " + e.getMessage());
      }
   }
   
   /**
    * Determine if the given centre is contained in the group line.
    * Kind of group line: cn=DEFAULT,ou=Centre,ou=groups,dc=opensso,dc=java,dc=net
    */
   private boolean groupContainsCentre(String group, String centre) {
      String[] groupItems = group.split(",");
      if (groupItems.length > 2) {
         for (String item : groupItems) {
            if (item.startsWith(LoginConstants.OU)) {
               return centre.equals(item.substring(3));
            }
         }
      }
      
      return false;
   }

   /**
    * Get response For fedlet. 
    * @param request the HTTP request
    * @param response the HTTP response
    * @return a Map which contains Attributes.
    * @throws SessionException 
    * @throws SAML2Exception 
    * @throws OpenWisLoginEx if an error occurs.
    */
   @SuppressWarnings("rawtypes")
   private Map getResponseForFedlet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException, SAML2Exception, SessionException {

      // invoke the Fedlet processing logic. this will do all the
      // necessary processing conforming to SAMLv2 specifications,
      // such as XML signature validation, Audience and Recipient
      // validation etc.  
      return SPACSUtils.processResponseForFedlet(request, response, response.getWriter());
   }

   /**
    * Get attribute for a given attribute name.
    * @param attrs Map of key, attributes
    * @param attributeName The attribute name.
    * @return the attribute.
    */
   private String getAttribute(Map<String, HashSet<String>> attrs, String attributeName) {
      Set<String> attrVals = (HashSet<String>) attrs.get(attributeName);
      String result = new String();
      Iterator<String> it = attrVals.iterator();
      while (it.hasNext()) {
         result = (String) it.next();
      }
      return result;
   }

   /**
    * Get Attributes for a given attribute name
    * @param attrs Map of key, attributes
    * @param attributeName The attribute name.
    * @return attributes list.
    */
   private List<String> getAttributes(Map<String, HashSet<String>> attrs, String attributeName) {
      Set<String> attrVals = (HashSet<String>) attrs.get(attributeName);
      List<String> result = new ArrayList<String>();
      Iterator<String> it = attrVals.iterator();
      while (it.hasNext()) {
         result.add((String) it.next());
      }
      return result;
   }

   /**
    * Get preferred IdP URL.
    * @param session The user session
    * @return the preferred IdP URL
    * @throws SAML2MetaException 
    */
   @SuppressWarnings("rawtypes")
   private String getPreferredIdPURL(UserSession session) throws SAML2MetaException {
      // Get RealM
      String realm = LoginConstants.REALM;

      // Get Preferred Idp Entity Id stored in the user session
      String idpEntityID = (String) session.getProperty(LoginConstants.IDP_ENTITY_ID);

      String urlSSO = new String();
      //      try {
      SAML2MetaManager sm = new SAML2MetaManager();

      // Get IDP Descriptor
      IDPSSODescriptorElement idpsso = sm.getIDPSSODescriptor(realm, idpEntityID);

      // Get All the SSO Service
      List ssoServiceList = idpsso.getSingleSignOnService();

      // Get the first service. The service location URL is like this : http://idp-hostname:<port>/opensso/<sssoService>
      SingleSignOnServiceElement ssoServiceElement = (SingleSignOnServiceElement) ssoServiceList
            .get(0);

      // Split with "opensso" string so the result is like this : http://idp-hostname:<port>
      urlSSO = ssoServiceElement.getLocation().split("/openam/")[0];

      // Add /opensso for get the IdP URL
      urlSSO = urlSSO + "/openam";
      //         
      //      } catch (SAML2MetaException e) {
      //         throw e;
      //      }
      return urlSSO;
   }

   private void forwardError(HttpServletRequest request, HttpServletResponse response,
         String message) throws ServletException, IOException {
      request.getRequestDispatcher("/srv/en/show.error?message=" + message).forward(request,
            response);
   }
}
