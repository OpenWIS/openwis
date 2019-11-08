/**
 * 
 */
package org.openwis.metadataportal.services.login;

/**
 * Class for extract constant. <P>
 * 
 */
public final class LoginConstants {

   /**
    * Default constructor.
    * Builds a LoginConstants.
    */
   private LoginConstants() {

   }

   /**
    * @member: IDP_ENTITY_ID The idp entity Id
    */
   public static final String IDP_ENTITY_ID = "idpEntityID";

   /**
    * @member: SP_ENTITY_ID The Sp entity Id
    */
   public static final String SP_ENTITY_ID = "spEntityID";

   /**
    * @member: SESSION_INDEX The session index
    */
   public static final String SESSION_INDEX = "SessionIndex";

   /**
    * @member: PREFERRED_IDP_URL
    */
   public static final String PREFERRED_IDP_URL = "preferredIdPUrl";

   /**
    * @member: TOKEN The token
    */
   public static final String TOKEN = "token";

   /**
    * @member: NAME_ID The name Id
    */
   public static final String NAME_ID = "NameID";

   /**
    * @member: RELAY_STATE The data to keep from authentication to authorization
    */
   public static final String RELAY_STATE = "RelayState";
   
   /**
    * @member: RELAY_STATE_SEPARATOR
    */
   public static final String RELAY_STATE_SEPARATOR = "/";
   
   /**
    * @member: URN The metadata URN.
    */
   public static final String URN = "urn";
   
   /**
    * @member: REQUEST_TYPE The request type.
    */
   public static final String REQUEST_TYPE = "requestType";
   
   /**
    * @member: IS_APPLY_GROUP_EMPTY
    */
   public static final String IS_APPLY_GROUP_EMPTY = "APPLYGROUPEMPTY";
   
   /**
    * @member: NOT_CONNECTED_TO_CONNECTED
    */
   public static final String NOT_CONNECTED_TO_CONNECTED = "NOT_CONNECTED_TO_CONNECTED";
   
   public static final String MAIN_SEARCH = "main.search";
   
   /**
    * @member: LOG use for logging.
    */
   public static final String LOG = "Login";

   /**
    * Comment for <code>REQUEST_ID</code>
    * @member: REQUEST_ID
    */
   public static final String REQUEST_ID = "requestID";

   /**
    * Comment for <code>META_ALIAS</code>
    * @member: META_ALIAS
    */
   public static final String META_ALIAS = "metaAlias";

   /**
    * Comment for <code>REALM</code>
    * @member: REALM
    */
   public static final String REALM = "/";

   /**
    * Comment for <code>SESSION</code>
    * @member: SESSION
    */
   public static final String SESSION = "session";

   /**
    * Comment for <code>REDIRECT_URL</code>
    * @member: REDIRECT_URL
    */
   public static final String REDIRECT_URL = "srv/user.choose.domain";

   /**
    * Comment for <code>ERROR_NULL_SP_ENTITY_ID</code>
    * @member: ERROR_NULL_SP_ENTITY_ID
    */
   public static final String ERROR_NULL_SP_ENTITY_ID = "nullSPEntityID";

   /**
    * Comment for <code>ERROR_AUTH_REQUEST</code>
    * @member: ERROR_AUTH_REQUEST
    */
   public static final String ERROR_AUTH_REQUEST = "Error sending AuthnRequest  : ";

   /**
    * @member: ERROR_FAILED_AUTH_PROCESS
    */
   public static final String ERROR_FAILED_AUTH_PROCESS = "failedToProcessSSOResponse";

   /**
    * @member: ERROR_DURING_LOGIN_PROCESS_MSG
    */
   public static final String ERROR_DURING_LOGIN_PROCESS_MSG = "An error occurs during the login process";

   /**
    * @member: IDP_NOT_FOUND
    */
   public static final String IDP_NOT_FOUND = "idpNotFound";

   /**
    * @member: ACCESS_DENIED_MSG
    */
   public static final String ACCESS_DENIED_MSG = "Access denied. You don't have enough credentials to perform this action.";

   /**
    * @member: OU
    */
   public static final String OU = "ou=";
   
   /**
    * @member: UID
    */
   public static final String UID = "uid";

   /**
    * @member: CN
    */
   public static final String CN = "cn";

   /**
    * @member: SN
    */
   public static final String SN = "sn";

   /**
    * @member: GIVEN_NAME
    */
   public static final String GIVEN_NAME = "givenname";

   /**
    * @member: PROFILE
    */
   public static final String PROFILE = "OpenWISProfile";

   /**
    * @member: ADDRESS
    */
   public static final String ADDRESS = "OpenWISAddress";

   /**
    * @member: IS_MEMBER_OF
    */
   public static final String IS_MEMBER_OF = "isMemberOf";

   /**
    * @member: EMAILS
    */
   public static final String EMAILS = "OpenWISEmails";

   /**
    * @member: FTPS
    */
   public static final String FTPS = "OpenWISFTPs";
   
   /**
    * @member: NEED_USER_ACCOUNT
    */
   public static final String NEED_USER_ACCOUNT = "OpenWISNeedUserAccount";

   /**
    * @member: CLASS_OF_SERVICE
    */
   public static final String CLASS_OF_SERVICE = "OpenWISClassOfService";

   /**
    * @member: MAIL
    */
   public static final String MAIL = "mail";

   /**
    * @member: HOME_PAGE
    */
   public static final String HOME_PAGE = "index.html";
   
   /**
    * @member: GLOBAL
    */
   public static final String GLOBAL = "GLOBAL";
   
   /**
    * @member: DEFAULT
    */
   public static final String DEFAULT = "DEFAULT";
   

   /**
    * @member: LANG
    */
   public static final String LANG = "lang";
}
