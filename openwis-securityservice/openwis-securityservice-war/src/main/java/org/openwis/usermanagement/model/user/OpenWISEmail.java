package org.openwis.usermanagement.model.user;

import java.io.Serializable;

/**
 * Defines an OpenWIS Email (favourite) for dissemination parameters. <P>
 * 
 */
public class OpenWISEmail implements Serializable {

   /**
    * @member: address The email address
    */
   private String address;

   /**
    * @member: headerLine The header line
    */
   private String headerLine;

   /**
    * @member: mailAttachmentMode The send mode : To / CC / BCC.
    */
   private String mailAttachmentMode;

   /**
    * @member: subject The email subject.
    */
   private String subject;

   /**
    * @member: mailDispatchMode The email attachment Mode : As Attachment or Embedded in Body
    */
   private String mailDispatchMode;

   /**
    * @member: fileName The email file name
    */
   private String fileName;

   /**
    * @member: disseminationTool The dissemination which can be RMDCN or Public
    */
   private DisseminationTool disseminationTool;

   /**
    * Gets the address.
    * @return the address.
    */
   public String getAddress() {
      return address;
   }

   /**
    * Sets the address.
    * @param address the address to set.
    */
   public void setAddress(String address) {
      this.address = address;
   }

   /**
    * Gets the headerLine.
    * @return the headerLine.
    */
   public String getHeaderLine() {
      return headerLine;
   }

   /**
    * Sets the headerLine.
    * @param headerLine the headerLine to set.
    */
   public void setHeaderLine(String headerLine) {
      this.headerLine = headerLine;
   }

   /**
    * Gets the mailAttachmentMode.
    * @return the mailAttachmentMode.
    */
   public String getMailAttachmentMode() {
      return mailAttachmentMode;
   }

   /**
    * Sets the mailAttachmentMode.
    * @param mailAttachmentMode the mailAttachmentMode to set.
    */
   public void setMailAttachmentMode(String mailAttachmentMode) {
      this.mailAttachmentMode = mailAttachmentMode;
   }

   /**
    * Gets the subject.
    * @return the subject.
    */
   public String getSubject() {
      return subject;
   }

   /**
    * Sets the subject.
    * @param subject the subject to set.
    */
   public void setSubject(String subject) {
      this.subject = subject;
   }

   /**
    * Gets the mailDispatchMode.
    * @return the mailDispatchMode.
    */
   public String getMailDispatchMode() {
      return mailDispatchMode;
   }

   /**
    * Sets the mailDispatchMode.
    * @param mailDispatchMode the mailDispatchMode to set.
    */
   public void setMailDispatchMode(String mailDispatchMode) {
      this.mailDispatchMode = mailDispatchMode;
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
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + ((disseminationTool == null) ? 0 : disseminationTool.hashCode());
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
      if (!(obj instanceof OpenWISEmail)) {
         return false;
      }
      OpenWISEmail other = (OpenWISEmail) obj;
      if (address == null) {
         if (other.address != null) {
            return false;
         }
      } else if (!address.equals(other.address)) {
         return false;
      }
      if (disseminationTool != other.disseminationTool) {
         return false;
      }
      return true;
   }
}
