/**
 * 
 */
package org.openwis.metadataportal.services.user.dto;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.model.user.User;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class UserDTO {

   /**
    * The user.
    * @member: user
    */
   private User user;

   /**
    * The profiles list.
    * @member: profiles
    */
   private List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();;

   /**
    * The class of services list.
    * @member: classOfServices
    */
   private List<ClassOfServiceDTO> classOfServices = new ArrayList<ClassOfServiceDTO>();
   
   /**
    * All groups availables
    * @member: groups
    */
   private List<Group> groups = new ArrayList<Group>();
   
   /**
    * All centres.
    * @member: backups
    */
   private List<BackUpDTO> backups = new ArrayList<BackUpDTO>();
   
   /**
    * True if the user will be created, false if the user will be updated.
    * @member: isCreation
    */
   private boolean creationMode;
   
   /**
    * True if the user is editing perso info
    * @member: isEditingPersoInfo
    */
   private boolean editingPersoInfo;

   /**
    * Gets the editingPersoInfo.
    * @return the editingPersoInfo.
    */
   public boolean isEditingPersoInfo() {
      return editingPersoInfo;
   }

   /**
    * Sets the editingPersoInfo.
    * @param editingPersoInfo the editingPersoInfo to set.
    */
   public void setEditingPersoInfo(boolean editingPersoInfo) {
      this.editingPersoInfo = editingPersoInfo;
   }

   /**
    * Gets the creationMode.
    * @return the creationMode.
    */
   public boolean isCreationMode() {
      return creationMode;
   }

   /**
    * Sets the creationMode.
    * @param creationMode the creationMode to set.
    */
   public void setCreationMode(boolean creationMode) {
      this.creationMode = creationMode;
   }

   /**
    * Gets the backups.
    * @return the backups.
    */
   public List<BackUpDTO> getBackups() {
      return backups;
   }

   /**
    * Sets the backups.
    * @param backups the backups to set.
    */
   public void setBackups(List<BackUpDTO> backups) {
      this.backups = backups;
   }

   /**
    * Gets the groups.
    * @return the groups.
    */
   public List<Group> getGroups() {
      return groups;
   }

   /**
    * Sets the groups.
    * @param groups the groups to set.
    */
   public void setGroups(List<Group> groups) {
      this.groups = groups;
   }

   /**
    * Gets the profiles.
    * @return the profiles.
    */
   public List<ProfileDTO> getProfiles() {
      return profiles;
   }

   /**
    * Sets the profiles.
    * @param profiles the profiles to set.
    */
   public void setProfiles(List<ProfileDTO> profiles) {
      this.profiles = profiles;
   }

   /**
    * Gets the classOfServices.
    * @return the classOfServices.
    */
   public List<ClassOfServiceDTO> getClassOfServices() {
      return classOfServices;
   }

   /**
    * Sets the classOfServices.
    * @param classOfServices the classOfServices to set.
    */
   public void setClassOfServices(List<ClassOfServiceDTO> classOfServices) {
      this.classOfServices = classOfServices;
   }

   /**
    * Gets the user.
    * @return the user.
    */
   public User getUser() {
      return user;
   }

   /**
    * Sets the user.
    * @param user the user to set.
    */
   public void setUser(User user) {
      this.user = user;
   }
}
