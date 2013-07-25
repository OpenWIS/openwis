/**
 * 
 */
package org.openwis.metadataportal.services.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jeeves.utils.Log;

import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;

import com.google.common.base.Strings;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.profile.SPCache;
import com.sun.identity.saml2.profile.SPSSOFederate;

/**
 * Class for initialize authorization and authentication.
 */
@SuppressWarnings("serial")
public class OpenWisInit extends HttpServlet {

   /**
    * Language selected by the user for the page to be redirected on after login
    * @member: lang
    */
   private String lang = null;
   
   /**
    * {@inheritDoc}
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @SuppressWarnings({"rawtypes", "unchecked"})
   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      
      String idpEntityID = null;
      String metaAlias = null;
      Map paramsMap = null;
      boolean isConnectingToIDP = false;
      try {
         String reqID = request.getParameter(LoginConstants.REQUEST_ID);
         
         if (reqID != null) {
            //get the preferred idp
            idpEntityID = SAML2Utils.getPreferredIDP(request);
            paramsMap = (Map) SPCache.reqParamHash.get(reqID);

            metaAlias = (String) paramsMap.get(LoginConstants.META_ALIAS);
            
            SPCache.reqParamHash.remove(reqID);
         } else {
            // this is an original request check
            // get the metaAlias ,idpEntityID
            // if idpEntityID is null redirect to IDP Discovery
            // Service to retrieve.
            metaAlias = request.getParameter(LoginConstants.META_ALIAS);
            if ((metaAlias == null) || (metaAlias.length() == 0)) {
               SAML2MetaManager manager = new SAML2MetaManager();
               List<String> spMetaAliases = manager
                     .getAllHostedServiceProviderMetaAliases(LoginConstants.REALM);
               if ((spMetaAliases != null) && !spMetaAliases.isEmpty()) {
                  // get first one
                  metaAlias = (String) spMetaAliases.get(0);
               }
               if ((metaAlias == null) || (metaAlias.length() == 0)) {
                  Log.error(LoginConstants.LOG,
                        SAML2Utils.bundle.getString(LoginConstants.ERROR_NULL_SP_ENTITY_ID));
                  throw new OpenWisLoginEx();
               }
            }

            idpEntityID = request.getParameter(LoginConstants.IDP_ENTITY_ID);

            paramsMap = SAML2Utils.getParamsMap(request);
            // always use transient
            List<String> list = new ArrayList<String>();
            list.add(SAML2Constants.NAMEID_TRANSIENT_FORMAT);
            paramsMap.put(SAML2Constants.NAMEID_POLICY_FORMAT, list);
            if (paramsMap.get(SAML2Constants.BINDING) == null) {
               // use POST binding
               list = new ArrayList<String>();
               list.add(SAML2Constants.HTTP_POST);
               paramsMap.put(SAML2Constants.BINDING, list);
            }

            // language
            List<String> relayStates = new ArrayList<String>();
            if (paramsMap.get(SAML2Constants.RELAY_STATE) != null) {
               relayStates = (List<String>) paramsMap.get(SAML2Constants.RELAY_STATE);
            }
            
            if (Strings.isNullOrEmpty(request.getParameter(LoginConstants.LANG))) {
               // en by default, if not found in parameter
               lang = "en";
            } else {
               lang = request.getParameter(LoginConstants.LANG);
            }
            relayStates.add(LoginConstants.LANG + ":" + lang);
            paramsMap.put(SAML2Constants.RELAY_STATE, relayStates);

            // Connect to IdP discovery Service
            isConnectingToIDP = connectToIdpDiscoveryService(request, response, idpEntityID,
                  metaAlias, paramsMap);

         }

         //Redirect, throw error or authenticate
         authenticateOrRedirect(isConnectingToIDP, request, response, idpEntityID, metaAlias,
               paramsMap);

      } catch (SAML2Exception sse) {
         Log.error(LoginConstants.LOG, LoginConstants.ERROR_AUTH_REQUEST + sse.getMessage());
         forwardError(request, response, "Error during login init process - " + sse.getMessage());
      } catch (OpenWisLoginEx e) {
         Log.error(LoginConstants.LOG, LoginConstants.ERROR_AUTH_REQUEST + e.getMessage());
         forwardError(request, response, "Error during login init process - " + e.getMessage());
      }
   }

   /**
    * Authenticate, Redirect or throw error.
    * @param isConnectingToIDP True if the user is redirect to idp
    * @param request The HTTP request
    * @param response The HTTP response
    * @param idpEntityID The idp entity id
    * @param metaAlias The meta alias
    * @param paramsMap The parameters map
    * @throws IOException if an error occurs.
    * @throws SAML2Exception if an error occurs.
    * @throws OpenWisLoginEx if an error occurs.
    */
   @SuppressWarnings({"unchecked", "rawtypes"})
   private void authenticateOrRedirect(boolean isConnectingToIDP, HttpServletRequest request,
         HttpServletResponse response, String idpEntityID, String metaAlias, Map paramsMap)
         throws SAML2Exception, OpenWisLoginEx, IOException {
      if (!isConnectingToIDP) {
         if ((idpEntityID == null) || (idpEntityID.length() == 0)) {
            // Choose domain
            SAML2MetaManager manager = new SAML2MetaManager();
            List<String> idpEntities = manager
                  .getAllRemoteIdentityProviderEntities(LoginConstants.REALM);
            if ((idpEntities == null) || idpEntities.isEmpty()) {
               // No idpEntities -> Error
               Log.error(LoginConstants.LOG,
                     SAML2Utils.bundle.getString(LoginConstants.IDP_NOT_FOUND));
               throw new OpenWisLoginEx();
            } else if (idpEntities.size() == 1) {
               // only one IDP, just use it
               idpEntityID = (String) idpEntities.get(0);
               SPSSOFederate.initiateAuthnRequest(request, response, metaAlias, idpEntityID,
                     paramsMap);
            } else {
               // redirect to choose domain pages.
               List relayState = (List) paramsMap.get(LoginConstants.RELAY_STATE);
               String redirectURL = LoginConstants.REDIRECT_URL;
               if (relayState != null && !relayState.isEmpty()) {
                  redirectURL = redirectURL + "?" + LoginConstants.RELAY_STATE+ "=" + relayState.get(0);
               }
               response.sendRedirect(redirectURL);
            }

         } else {
            SPSSOFederate
                  .initiateAuthnRequest(request, response, metaAlias, idpEntityID, paramsMap);
         }
      }
   }

   /**
    * Connect to IDP Discovery Service.
    * @param request The HTTP request
    * @param response The HTTP response
    * @param idpEntityID The idp entity id
    * @param metaAlias The meta alias
    * @param paramsMap The parameters map
    * @return True if the response send redirect to idp discovery.
    * @throws IOException if an error occurs.
    */
   private boolean connectToIdpDiscoveryService(HttpServletRequest request,
         HttpServletResponse response, String idpEntityID, String metaAlias,
         Map<String, String> paramsMap) throws IOException {
      boolean result = false;
      // Connect to IdP discovery Service
      if ((idpEntityID == null) || (idpEntityID.length() == 0)) {
         // get reader url
         String readerURL = SAML2Utils.getReaderURL(metaAlias);
         if (readerURL != null) {
            String rID = SAML2Utils.generateID();
            String redirectURL = SAML2Utils.getRedirectURL(readerURL, rID, request);
            if (redirectURL != null) {
               paramsMap.put(LoginConstants.META_ALIAS, metaAlias);
               SPCache.reqParamHash.put(rID, paramsMap);
               response.sendRedirect(redirectURL);
               result = true;
            }
         }
      }
      return result;
   }
   
   private void forwardError(HttpServletRequest request, HttpServletResponse response,
         String message) throws ServletException, IOException {
      if (lang == null) {
         // en by default, if not found
         lang = "en";
      }
      request.getRequestDispatcher("/srv/" + lang + "/show.error?message=" + message).forward(request,
            response);
   }

}
