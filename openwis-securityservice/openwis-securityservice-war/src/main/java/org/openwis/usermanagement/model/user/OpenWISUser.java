package org.openwis.usermanagement.model.user;

import java.io.Serializable;
import java.util.List;

import org.openwis.usermanagement.model.group.OpenWISGroup;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class OpenWISUser implements Serializable {

   /**
    * @member: name The user name
    */
   private String name;

   /**
    * @member: userName The user username
    */
   private String userName;

   /**
    * @member: surName The user surname
    */
   private String surName;

   /**
    * @member: password The user password
    */
   private String password;

   /**
    * @member: emailContact The user email contact
    */
   private String emailContact;

   /**
    * @member: emails List of emails (favourites) for dissemination parameters
    */
   private List<OpenWISEmail> emails;

   /**
    * @member: ftps List of ftps (favourites) for dissemination parameters
    */
   private List<OpenWISFTP> ftps;

   /**
    * @member: address The user address (address, zip, state, city, country)
    */
   private OpenWISAddress address;

   /**
    * @member: needUserAccount True if the user need to have a local account to access to a centre.
    */
   private boolean needUserAccount;

   /**
    * @member: profile The user profile : Admin / Operator / Editor / Registered User
    */
   private String profile;

   /**
    * The class Of Service : Gold, Silver or Bronze.
    * @member: classOfService
    */
   private ClassOfService classOfService;

   /**
    * List of deployments on which the user can be backed up.
    * @member: backUps
    */
   private List<String> backUps;

   /**
    * @member: groups The user's groups.
    */
   private List<OpenWISGroup> groups;

   /**
    * Gets the name.
    * @return the name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * @param name the name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the userName.
    * @return the userName.
    */
   public String getUserName() {
      return userName;
   }

   /**
    * Sets the userName.
    * @param userName the userName to set.
    */
   public void setUserName(String userName) {
      this.userName = userName;
   }

   /**
    * Gets the surName.
    * @return the surName.
    */
   public String getSurName() {
      return surName;
   }

   /**
    * Sets the surName.
    * @param surName the surName to set.
    */
   public void setSurName(String surName) {
      this.surName = surName;
   }

   /**
    * Gets the password.
    * @return the password.
    */
   public String getPassword() {
      return password;
   }

   /**
    * Sets the password.
    * @param password the password to set.
    */
   public void setPassword(String password) {
      this.password = password;
   }

   /**
    * Gets the emailContact.
    * @return the emailContact.
    */
   public String getEmailContact() {
      return emailContact;
   }

   /**
    * Sets the emailContact.
    * @param emailContact the emailContact to set.
    */
   public void setEmailContact(String emailContact) {
      this.emailContact = emailContact;
   }

   /**
    * Gets the address.
    * @return the address.
    */
   public OpenWISAddress getAddress() {
      return address;
   }

   /**
    * Sets the address.
    * @param address the address to set.
    */
   public void setAddress(OpenWISAddress address) {
      this.address = address;
   }

   /**
    * Gets the emails.
    * @return the emails.
    */
   public List<OpenWISEmail> getEmails() {
      return emails;
   }

   /**
    * Sets the emails.
    * @param emails the emails to set.
    */
   public void setEmails(List<OpenWISEmail> emails) {
      this.emails = emails;
   }

   /**
    * Gets the ftps.
    * @return the ftps.
    */
   public List<OpenWISFTP> getFtps() {
      return ftps;
   }

   /**
    * Sets the ftps.
    * @param ftps the ftps to set.
    */
   public void setFtps(List<OpenWISFTP> ftps) {
      this.ftps = ftps;
   }

   /**
    * Gets the profile.
    * @return the profile.
    */
   public String getProfile() {
      return profile;
   }

   /**
    * Sets the profile.
    * @param profile the profile to set.
    */
   public void setProfile(String profile) {
      this.profile = profile;
   }

   /**
    * Gets the groups.
    * @return the groups.
    */
   public List<OpenWISGroup> getGroups() {
      return groups;
   }

   /**
    * Sets the groups.
    * @param groups the groups to set.
    */
   public void setGroups(List<OpenWISGroup> groups) {
      this.groups = groups;
   }

   /**
    * Gets the needUserAccount.
    * @return the needUserAccount.
    */
   public boolean isNeedUserAccount() {
      return needUserAccount;
   }

   /**
    * Sets the needUserAccount.
    * @param needUserAccount the needUserAccount to set.
    */
   public void setNeedUserAccount(boolean needUserAccount) {
      this.needUserAccount = needUserAccount;
   }

   /**
    * Gets the classOfService.
    * @return the classOfService.
    */
   public ClassOfService getClassOfService() {
      return classOfService;
   }

   /**
    * Sets the classOfService.
    * @param classOfService the classOfService to set.
    */
   public void setClassOfService(ClassOfService classOfService) {
      this.classOfService = classOfService;
   }

   /**
    * Gets the backUps.
    * @return the backUps.
    */
   public List<String> getBackUps() {
      return backUps;
   }

   /**
    * Sets the backUps.
    * @param backUps the backUps to set.
    */
   public void setBackUps(List<String> backUps) {
      this.backUps = backUps;
   }

}
