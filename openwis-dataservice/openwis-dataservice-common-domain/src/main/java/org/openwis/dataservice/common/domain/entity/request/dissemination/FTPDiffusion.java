package org.openwis.dataservice.common.domain.entity.request.dissemination;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The FTP diffusion entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ftpDiffusion")

@Entity
@DiscriminatorValue(value = "FTP")
public class FTPDiffusion extends Diffusion {

   /** */
   @Column(name = "IS_CHECK_FILE_SIZE")
   private Boolean checkFileSize;

   /** */
   @Column(name = "FILE_NAME")
   private String fileName;

   /** */
   @Column(name = "HOST")
   private String host;

   /** */
   @Column(name = "IS_PASSIVE")
   private Boolean passive;

   /** */
   @Column(name = "IS_ENCRYPTED")
   private Boolean encrypted;

   /** */
   @Column(name = "PASSWORD")
   private String password;

   /** */
   @Column(name = "PATH")
   private String path;

   /** */
   @Column(name = "PORT")
   private String port;

   /** */
   @Column(name = "USER_ID")
   private String user;

   /**
    * Default constructor.
    */
   public FTPDiffusion() {
      super();
   }

   /**
    * @return the checkFileSize
    */
   public Boolean getCheckFileSize() {
      return checkFileSize;
   }

   /**
    * @param checkFileSize
    *            the checkFileSize to set
    */
   public void setCheckFileSize(Boolean checkFileSize) {
      this.checkFileSize = checkFileSize;
   }

   /**
    * @return the fileName
    */
   public String getFileName() {
      return fileName;
   }

   /**
    * @param fileName
    *            the fileName to set
    */
   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   /**
    * @return the host
    */
   public String getHost() {
      return host;
   }

   /**
    * @param host
    *            the host to set
    */
   public void setHost(String host) {
      this.host = host;
   }

   /**
    * @return the passive
    */
   public Boolean getPassive() {
      return passive;
   }

   /**
    * @param passive
    *            the passive to set
    */
   public void setPassive(Boolean passive) {
      this.passive = passive;
   }
   
   /**
    * @return the encrypted
    */
   public Boolean getEncrypted() {
      return encrypted;
   }

   /**
    * @param encrypted
    *            the encrypted to set
    */
   public void setEncrypted(Boolean encrypted) {
      this.encrypted = encrypted;
   }

   /**
    * @return the password
    */
   public String getPassword() {
      return password;
   }

   /**
    * @param password
    *            the password to set
    */
   public void setPassword(String password) {
      this.password = password;
   }

   /**
    * @return the path
    */
   public String getPath() {
      return path;
   }

   /**
    * @param path
    *            the path to set
    */
   public void setPath(String path) {
      this.path = path;
   }

   /**
    * @return the port
    */
   public String getPort() {
      return port;
   }

   /**
    * @param port
    *            the port to set
    */
   public void setPort(String port) {
      this.port = port;
   }

   /**
    * @return the user
    */
   public String getUser() {
      return user;
   }

   /**
    * @param user
    *            the user to set
    */
   public void setUser(String user) {
      this.user = user;
   }

}
