/**
 * 
 */
package org.openwis.usermanagement.util;


/**
 * JNDI Utils
 * 
 */
public final class JNDIUtils {
   
   /**
    * Instance of singleton
    * @member: instance Instance of singleton
    */
   private static JNDIUtils instance = null;

   /**
    * @member: jndiConnection The JNDI connection 
    */
   private JNDIConnectionUtils jndiConnection = null;
   
   /**
    * @member: ldapHost The LDAP host
    */
   private final String ldapHost = "ldap_host";

   /**
    * @member: ldapPort The LDAP port.
    */
   private final String ldapPort = "ldap_port";

   /**
    * @member: ldapUser The LDAP user name
    */
   private final String ldapUser = "ldap_user";

   /**
    * @member: LDAP_PASSWORD The LDAP password
    */
   private final String ldapPassword = "ldap_password";
   
   /**
    * @member: globalGroups The global groups
    */
   private final String globalGroups ="global_groups";
   
   /**
    * @member: logTimerURL The log timer URL
    */
   private final String logTimerURL = "log.timer.url";

   /**
    * @member: logTimerPeriod The log timer period
    */
   private final String logTimerPeriod = "log.timer.period";
   /**
    * @member: logTimerFile The log timer file
    */
   private final String logTimerFile = "log.timer.file";
   
   /**
    * @member: logTimerFile The log timer file
    */
   private final String logTimerSplitIndexForLogin = "log.timer.splitIndexForLogin";
   
   /**
    * Default constructor.
    * Builds a LdapDataSource.
    */
   private JNDIUtils() {
      jndiConnection = new JNDIConnectionUtils();
   }
   
   /**
    * Get singleton instance.
    * @return singleton instance
    */
   public static JNDIUtils getInstance() {
      if(instance == null) {
         instance = new JNDIUtils();
      }
      return instance;
   }
   
   /**
    * Get Ldap Host.
    * @return the ldap host.
    */
   public String getLdapHost(){
      return jndiConnection.getString(ldapHost);
   }
   
   /**
    * Get Ldap Port.
    * @return the ldap port.
    */
   public int getLdapPort(){
      return jndiConnection.getInt(ldapPort);
   }
   
   /**
    * Get Ldap User.
    * @return the ldap user.
    */
   public String getLdapUser(){
      return jndiConnection.getString(ldapUser);
   }
   
   /**
    * Get Ldap Password.
    * @return the ldap host.
    */
   public String getLdapPassword(){
      return jndiConnection.getString(ldapPassword);
   }

   /**
    * Gets the globalGroups.
    * @return the globalGroups.
    */
   public String[] getGlobalGroups() {
      String globalGroupResults = jndiConnection.getString(globalGroups);
      return globalGroupResults.split(",");
   }
   
   /**
    * Gets the log timer file
    * @return the log timer file
    */
   public String getLogTimerFile(){
      return jndiConnection.getString(logTimerFile);
   }
   
   /**
    * Gets the log timer period
    * @return the log timer period
    */
   public long getLogTimerPeriod(){
      return jndiConnection.getLong(logTimerPeriod);
   }
   
   /**
    * Gets the log timer URL
    * @return the log timer URL
    */
   public String getLogTimerUrl(){
      return jndiConnection.getString(logTimerURL);
   }
   
   public int getLogTimerSplitIndexForLogin() {
      return jndiConnection.getInt(logTimerSplitIndexForLogin);
   }
   
}
