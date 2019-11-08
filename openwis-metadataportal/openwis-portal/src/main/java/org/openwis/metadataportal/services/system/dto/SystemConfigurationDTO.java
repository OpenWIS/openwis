/**
 *
 */
package org.openwis.metadataportal.services.system.dto;

import java.util.List;

import org.openwis.metadataportal.model.user.User;

/**
 * The System Configuration DTO. <P>
 * Explanation goes here. <P>
 *
 */
public class SystemConfigurationDTO {

   /**
    * The siteName.
    */
   private String siteName;

   /**
    * The serverHost.
    */
   private String serverHost;

   /**
    * The serverPort.
    */
   private String serverPort;

   /** The index enable. */
   private boolean indexEnable;

   /** The index run at hour. */
   private String indexRunAtHour;

   /** The index run at minute. */
   private String indexRunAtMinute;

   /** The index run again. */
   private String indexRunAgain;

   /**
    * The z3950ServerEnable.
    */
   private boolean z3950ServerEnable;

   /**
    * The z3950ServerPort.
    */
   private String z3950ServerPort;

   /**
    * The xlinkResolverEnable.
    */
   private boolean xlinkResolverEnable;

   /**
    * The cswEnable.
    */
   private boolean cswEnable;

   /**
    * The cswContactAllUsers.
    */
   private List<User> cswContactAllUsers;

   /**
    * The cswContactId.
    */
   private String cswContactId;

   /**
    * The cswTitle.
    */
   private String cswTitle;

   /**
    * The cswAbstract.
    */
   private String cswAbstract;

   /**
    * The cswFees.
    */
   private String cswFees;

   /**
    * The cswAccess.
    */
   private String cswAccess;

   /**
    * The inspireEnable.
    */
   private boolean inspireEnable;

   /**
    * The proxyUse.
    */
   private boolean proxyUse;

   /**
    * The proxyHost.
    */
   private String proxyHost;

   /**
    * The proxyPort.
    */
   private String proxyPort;

   /**
    * The proxyUserName.
    */
   private String proxyUserName;

   /**
    * The proxyPassword.
    */
   private String proxyPassword;

   /**
    * The feedBackEmail.
    */
   private String feedBackEmail;

   /**
    * The feedBackSmtpHost.
    */
   private String feedBackSmtpHost;

   /**
    * The feedBackSmtpPort.
    */
   private String feedBackSmtpPort;

   /**
    * The userSelfRegistrationEnable.
    */
   private boolean userSelfRegistrationEnable;

   /**
    * Gets the siteName.
    * @return the siteName.
    */
   public String getSiteName() {
      return siteName;
   }

   /**
    * Sets the siteName.
    * @param siteName the siteName to set.
    */
   public void setSiteName(String siteName) {
      this.siteName = siteName;
   }

   /**
    * Gets the serverHost.
    * @return the serverHost.
    */
   public String getServerHost() {
      return serverHost;
   }

   /**
    * Sets the serverHost.
    * @param serverHost the serverHost to set.
    */
   public void setServerHost(String serverHost) {
      this.serverHost = serverHost;
   }

   /**
    * Gets the serverPort.
    * @return the serverPort.
    */
   public String getServerPort() {
      return serverPort;
   }

   /**
    * Sets the serverPort.
    * @param serverPort the serverPort to set.
    */
   public void setServerPort(String serverPort) {
      this.serverPort = serverPort;
   }

   /**
    * Checks if is index enable.
    *
    * @return true, if is index enable
    */
   public boolean isIndexEnable() {
      return indexEnable;
   }

   /**
    * Sets the index enable.
    *
    * @param indexEnable the new index enable
    */
   public void setIndexEnable(boolean indexEnable) {
      this.indexEnable = indexEnable;
   }

   /**
    * Gets the index run at hour.
    *
    * @return the index run at hour
    */
   public String getIndexRunAtHour() {
      return indexRunAtHour;
   }

   /**
    * Sets the index run at hour.
    *
    * @param indexRunAtHour the new index run at hour
    */
   public void setIndexRunAtHour(String indexRunAtHour) {
      this.indexRunAtHour = indexRunAtHour;
   }

   /**
    * Gets the index run at minute.
    *
    * @return the index run at minute
    */
   public String getIndexRunAtMinute() {
      return indexRunAtMinute;
   }

   /**
    * Sets the index run at minute.
    *
    * @param indexRunAtMinute the new index run at minute
    */
   public void setIndexRunAtMinute(String indexRunAtMinute) {
      this.indexRunAtMinute = indexRunAtMinute;
   }

   /**
    * Gets the index run again.
    *
    * @return the index run again
    */
   public String getIndexRunAgain() {
      return indexRunAgain;
   }

   /**
    * Sets the index run again.
    *
    * @param indexRunAgain the new index run again
    */
   public void setIndexRunAgain(String indexRunAgain) {
      this.indexRunAgain = indexRunAgain;
   }

   /**
    * Gets the z3950ServerEnable.
    * @return the z3950ServerEnable.
    */
   public boolean isZ3950ServerEnable() {
      return z3950ServerEnable;
   }

   /**
    * Sets the z3950ServerEnable.
    * @param z3950ServerEnable the z3950ServerEnable to set.
    */
   public void setZ3950ServerEnable(boolean z3950ServerEnable) {
      this.z3950ServerEnable = z3950ServerEnable;
   }

   /**
    * Gets the z3950ServerPort.
    * @return the z3950ServerPort.
    */
   public String getZ3950ServerPort() {
      return z3950ServerPort;
   }

   /**
    * Sets the z3950ServerPort.
    * @param z3950ServerPort the z3950ServerPort to set.
    */
   public void setZ3950ServerPort(String z3950ServerPort) {
      this.z3950ServerPort = z3950ServerPort;
   }

   /**
    * Gets the xlinkResolverEnable.
    * @return the xlinkResolverEnable.
    */
   public boolean isXlinkResolverEnable() {
      return xlinkResolverEnable;
   }

   /**
    * Sets the xlinkResolverEnable.
    * @param xlinkResolverEnable the xlinkResolverEnable to set.
    */
   public void setXlinkResolverEnable(boolean xlinkResolverEnable) {
      this.xlinkResolverEnable = xlinkResolverEnable;
   }

   /**
    * Gets the cswEnable.
    * @return the cswEnable.
    */
   public boolean isCswEnable() {
      return cswEnable;
   }

   /**
    * Sets the cswEnable.
    * @param cswEnable the cswEnable to set.
    */
   public void setCswEnable(boolean cswEnable) {
      this.cswEnable = cswEnable;
   }

   /**
    * Gets the cswContactAllUsers.
    * @return the cswContactAllUsers.
    */
   public List<User> getCswContactAllUsers() {
      return cswContactAllUsers;
   }

   /**
    * Sets the cswContactAllUsers.
    * @param cswContactAllUsers the cswContactAllUsers to set.
    */
   public void setCswContactAllUsers(List<User> cswContactAllUsers) {
      this.cswContactAllUsers = cswContactAllUsers;
   }

   /**
    * Gets the cswContactId.
    * @return the cswContactId.
    */
   public String getCswContactId() {
      return cswContactId;
   }

   /**
    * Sets the cswContactId.
    * @param cswContactId the cswContactId to set.
    */
   public void setCswContactId(String cswContactId) {
      this.cswContactId = cswContactId;
   }

   /**
    * Gets the cswTitle.
    * @return the cswTitle.
    */
   public String getCswTitle() {
      return cswTitle;
   }

   /**
    * Sets the cswTitle.
    * @param cswTitle the cswTitle to set.
    */
   public void setCswTitle(String cswTitle) {
      this.cswTitle = cswTitle;
   }

   /**
    * Gets the cswAbstract.
    * @return the cswAbstract.
    */
   public String getCswAbstract() {
      return cswAbstract;
   }

   /**
    * Sets the cswAbstract.
    * @param cswAbstract the cswAbstract to set.
    */
   public void setCswAbstract(String cswAbstract) {
      this.cswAbstract = cswAbstract;
   }

   /**
    * Gets the cswFees.
    * @return the cswFees.
    */
   public String getCswFees() {
      return cswFees;
   }

   /**
    * Sets the cswFees.
    * @param cswFees the cswFees to set.
    */
   public void setCswFees(String cswFees) {
      this.cswFees = cswFees;
   }

   /**
    * Gets the cswAccess.
    * @return the cswAccess.
    */
   public String getCswAccess() {
      return cswAccess;
   }

   /**
    * Sets the cswAccess.
    * @param cswAccess the cswAccess to set.
    */
   public void setCswAccess(String cswAccess) {
      this.cswAccess = cswAccess;
   }

   /**
    * Gets the inspireEnable.
    * @return the inspireEnable.
    */
   public boolean isInspireEnable() {
      return inspireEnable;
   }

   /**
    * Sets the inspireEnable.
    * @param inspireEnable the inspireEnable to set.
    */
   public void setInspireEnable(boolean inspireEnable) {
      this.inspireEnable = inspireEnable;
   }

   /**
    * Gets the proxyUse.
    * @return the proxyUse.
    */
   public boolean isProxyUse() {
      return proxyUse;
   }

   /**
    * Sets the proxyUse.
    * @param proxyUse the proxyUse to set.
    */
   public void setProxyUse(boolean proxyUse) {
      this.proxyUse = proxyUse;
   }

   /**
    * Gets the proxyHost.
    * @return the proxyHost.
    */
   public String getProxyHost() {
      return proxyHost;
   }

   /**
    * Sets the proxyHost.
    * @param proxyHost the proxyHost to set.
    */
   public void setProxyHost(String proxyHost) {
      this.proxyHost = proxyHost;
   }

   /**
    * Gets the proxyPort.
    * @return the proxyPort.
    */
   public String getProxyPort() {
      return proxyPort;
   }

   /**
    * Sets the proxyPort.
    * @param proxyPort the proxyPort to set.
    */
   public void setProxyPort(String proxyPort) {
      this.proxyPort = proxyPort;
   }

   /**
    * Gets the proxyUserName.
    * @return the proxyUserName.
    */
   public String getProxyUserName() {
      return proxyUserName;
   }

   /**
    * Sets the proxyUserName.
    * @param proxyUserName the proxyUserName to set.
    */
   public void setProxyUserName(String proxyUserName) {
      this.proxyUserName = proxyUserName;
   }

   /**
    * Gets the proxyPassword.
    * @return the proxyPassword.
    */
   public String getProxyPassword() {
      return proxyPassword;
   }

   /**
    * Sets the proxyPassword.
    * @param proxyPassword the proxyPassword to set.
    */
   public void setProxyPassword(String proxyPassword) {
      this.proxyPassword = proxyPassword;
   }

   /**
    * Gets the feedBackEmail.
    * @return the feedBackEmail.
    */
   public String getFeedBackEmail() {
      return feedBackEmail;
   }

   /**
    * Sets the feedBackEmail.
    * @param feedBackEmail the feedBackEmail to set.
    */
   public void setFeedBackEmail(String feedBackEmail) {
      this.feedBackEmail = feedBackEmail;
   }

   /**
    * Gets the feedBackSmtpHost.
    * @return the feedBackSmtpHost.
    */
   public String getFeedBackSmtpHost() {
      return feedBackSmtpHost;
   }

   /**
    * Sets the feedBackSmtpHost.
    * @param feedBackSmtpHost the feedBackSmtpHost to set.
    */
   public void setFeedBackSmtpHost(String feedBackSmtpHost) {
      this.feedBackSmtpHost = feedBackSmtpHost;
   }

   /**
    * Gets the feedBackSmtpPort.
    * @return the feedBackSmtpPort.
    */
   public String getFeedBackSmtpPort() {
      return feedBackSmtpPort;
   }

   /**
    * Sets the feedBackSmtpPort.
    * @param feedBackSmtpPort the feedBackSmtpPort to set.
    */
   public void setFeedBackSmtpPort(String feedBackSmtpPort) {
      this.feedBackSmtpPort = feedBackSmtpPort;
   }

   /**
    * Gets the userSelfRegistrationEnable.
    * @return the userSelfRegistrationEnable.
    */
   public boolean isUserSelfRegistrationEnable() {
      return userSelfRegistrationEnable;
   }

   /**
    * Sets the userSelfRegistrationEnable.
    * @param userSelfRegistrationEnable the userSelfRegistrationEnable to set.
    */
   public void setUserSelfRegistrationEnable(boolean userSelfRegistrationEnable) {
      this.userSelfRegistrationEnable = userSelfRegistrationEnable;
   }
}
