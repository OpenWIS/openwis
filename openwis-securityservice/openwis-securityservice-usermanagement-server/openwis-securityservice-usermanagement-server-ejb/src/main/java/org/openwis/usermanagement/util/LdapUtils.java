package org.openwis.usermanagement.util;


/**
 * Utilities for configure User Management and access to LDAP. <P>
 * 
 */
public final class LdapUtils {

   /**
    * Default constructor.
    * Builds a LdapUtils.
    */
   private LdapUtils() {
   }

   /**
    * @member: NAME
    */
   public static final String NAME = "givenName";

   /**
    * @member: SURNAME
    */
   public static final String SURNAME = "sn";

   /**
    * @member: PASSWORD
    */
   public static final String PASSWORD = "userPassword";

   /**
    * @member: CONTACT_EMAIL
    */
   public static final String CONTACT_EMAIL = "mail";

   /**
    * @member: ADDRESS_ADDRESS
    */
   public static final String ADDRESS_ADDRESS = "OpenWISAddress";

   /**
    * @member: ADDRESS_COUNTRY
    */
   public static final String ADDRESS_COUNTRY = "OpenWISAddressCountry";

   /**
    * @member: ADDRESS_STATE
    */
   public static final String ADDRESS_STATE = "OpenWISAddressState";

   /**
    * @member: ADDRESS_CITY
    */
   public static final String ADDRESS_CITY = "OpenWISAddressCity";

   /**
    * @member: ADDRESS_ZIP
    */
   public static final String ADDRESS_ZIP = "OpenWISAddressZip";

   /**
    * @member: EMAILS
    */
   public static final String EMAILS = "OpenWISEmails";

   /**
    * @member: FTPS
    */
   public static final String FTPS = "OpenWISFTPs";

   /**
    * @member: SECURITY_LEVEL
    */
   public static final String SECURITY_LEVEL = "OpenWISSecurityLevel";
   
   /**
    * @member: CLASSOFSERVICE
    */
   public static final String CLASSOFSERVICE ="OpenWISClassOfService";
   
   /**
    * @member: BACKUPS
    */
   public static final String BACKUPS ="OpenWISBackUps";
   
   /**
    * @member: NEEDUSERACCOUNT
    */
   public static final String NEEDUSERACCOUNT ="OpenWISNeedUserAccount";

   /**
    * @member: PROFILE
    */
   public static final String PROFILE = "OpenWISProfile";

   /**
    * @member: OBJECT_CLASS
    */
   public static final String OBJECT_CLASS = "objectclass";

   /**
    * @member: UID
    */
   public static final String UID = "uid";

   /**
    * @member: CN
    */
   public static final String CN = "cn";

   /**
    * @member: INET_USER_STATUS
    */
   public static final String INET_USER_STATUS = "inetUserStatus";

   /**
    * @member: INET_USER_STATUS_ACTIVE
    */
   public static final String INET_USER_STATUS_ACTIVE = "Active";
   
   /**
    * @member: OPEN_WIS_USER
    */
   public static final String OPEN_WIS_USER ="OpenWisUser";

   /**
    * @member: PEOPLE
    */
   public static final String PEOPLE = "ou=people,dc=opensso,dc=java,dc=net";

   /**
    * @member: COMMA
    */
   public static final String COMMA=",";
   /**
    * @member: GROUP
    */
   public static final String GROUP = "ou=groups,dc=opensso,dc=java,dc=net";

   /**
    * @member: GROUP_OF_UNIQUE_NAMES
    */
   public static final String GROUP_OF_UNIQUE_NAMES = "groupofuniquenames";
   
   /**
    * @member: ORGANIZATIONAL_UNIT
    */
   public static final String ORGANIZATIONAL_UNIT = "organizationalUnit";

   /**
    * @member: UNIQUE_MEMBER
    */
   public static final String UNIQUE_MEMBER = "uniqueMember";
   
   /**
    * @member: IS_MEMBER_OF
    */
   public static final String IS_MEMBER_OF = "isMemberOf";

   /**
    * @member: EQUAL
    */
   public static final String EQUAL = "=";
   
   /**
    * @member: OU
    */
   public static final String OU = "ou";

   /**
    * @member: EQUAL
    */
   public static final String STAR = "*";
   
   /**
    * @member: GLOBAL
    */
   public static final String GLOBAL = "GLOBAL";

   /**
    * @member: DEFAULT
    */
   public static final String DEFAULT = "DEFAULT";
}
