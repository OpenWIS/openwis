package org.openwis.usermanagement.model.user;

import java.io.Serializable;

/**
 * Defines an OpenWIS FTP (favourite) for dissemination parameters. <P>
 * 
 */
public class OpenWISFTP implements Serializable {

   /**
    * @member: host The ftp host.
    */
   private String host;

   /**
    * @member: path The ftp path.
    */
   private String path;

   /**
    * @member: user The ftp user.
    */
   private String user;

   /**
    * @member: password The ftp password.
    */
   private String password;

   /**
    * @member: port  The ftp port.
    */
   private String port;

   /**
    * If passive is True, the ftp is in passive mode
    * @member: passive  The ftp passive mode.
    */
   private boolean passive;

   /**
    * If encrypted is True, the ftp is in encrypted mode
    * @member: encrypted  The encrypted mode.
    */
   private boolean encrypted;

   /**
    * @member: checkFileSize The check file size
    */
   private boolean checkFileSize;

   /**
    * @member: fileName The file Name
    */
   private String fileName;

   /**
    * The disseminationTool
    * @member: disseminationTool The dissemination which can be RMDCN or Public
    */
   private DisseminationTool disseminationTool;

   /**
    * Gets the host.
    * @return the host.
    */
   public String getHost() {
      return host;
   }

   /**
    * Sets the host.
    * @param host the host to set.
    */
   public void setHost(String host) {
      this.host = host;
   }

   /**
    * Gets the path.
    * @return the path.
    */
   public String getPath() {
      return path;
   }

   /**
    * Sets the path.
    * @param path the path to set.
    */
   public void setPath(String path) {
      this.path = path;
   }

   /**
    * Gets the user.
    * @return the user.
    */
   public String getUser() {
      return user;
   }

   /**
    * Sets the user.
    * @param user the user to set.
    */
   public void setUser(String user) {
      this.user = user;
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
    * Gets the port.
    * @return the port.
    */
   public String getPort() {
      return port;
   }

   /**
    * Sets the port.
    * @param port the port to set.
    */
   public void setPort(String port) {
      this.port = port;
   }

   /**
    * Gets the passive.
    * @return the passive.
    */
   public boolean isPassive() {
      return passive;
   }

   /**
    * Sets the passive.
    * @param passive the passive to set.
    */
   public void setPassive(boolean passive) {
      this.passive = passive;
   }

   /**
    * Gets the encrypted.
    * @return the encrypted.
    */
   public boolean isEncrypted() {
      return encrypted;
   }

   /**
    * Sets the encrypted.
    * @param encrypted the encrypted to set.
    */
   public void setEncrypted(boolean encrypted) {
      this.encrypted = encrypted;
   }

   /**
    * Gets the checkFileSize.
    * @return the checkFileSize.
    */
   public boolean isCheckFileSize() {
      return checkFileSize;
   }

   /**
    * Sets the checkFileSize.
    * @param checkFileSize the checkFileSize to set.
    */
   public void setCheckFileSize(boolean checkFileSize) {
      this.checkFileSize = checkFileSize;
   }

   /**
    * Gets the fileName.
    * @return the fileName.
    */
   public String getFileName() {
      return fileName;
   }

   /**
    * Sets the fileName.
    * @param fileName the fileName to set.
    */
   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   /**
    * Gets the disseminationTool.
    * @return the disseminationTool.
    */
   public DisseminationTool getDisseminationTool() {
      return disseminationTool;
   }

   /**
    * Sets the disseminationTool.
    * @param disseminationTool the disseminationTool to set.
    */
   public void setDisseminationTool(DisseminationTool disseminationTool) {
      this.disseminationTool = disseminationTool;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((disseminationTool == null) ? 0 : disseminationTool.hashCode());
      result = prime * result + ((host == null) ? 0 : host.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof OpenWISFTP)) {
         return false;
      }
      OpenWISFTP other = (OpenWISFTP) obj;
      if (disseminationTool != other.disseminationTool) {
         return false;
      }
      if (host == null) {
         if (other.host != null) {
            return false;
         }
      } else if (!host.equals(other.host)) {
         return false;
      }
      return true;
   }

}
