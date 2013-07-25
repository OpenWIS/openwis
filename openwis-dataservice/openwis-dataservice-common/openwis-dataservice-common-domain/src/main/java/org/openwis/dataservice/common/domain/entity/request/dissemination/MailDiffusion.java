package org.openwis.dataservice.common.domain.entity.request.dissemination;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openwis.dataservice.common.domain.entity.enumeration.MailAttachmentMode;
import org.openwis.dataservice.common.domain.entity.enumeration.MailDispatchMode;


/**
 * The mail diffusion entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailDiffusion")

@Entity
@DiscriminatorValue(value = "MAIL")
public class MailDiffusion extends Diffusion {

   /** */
   @Column(name = "ADDRESS")
   private String address;

   /** */
   @Column(name = "FILE_NAME")
   private String fileName;

   /** */
   @Column(name = "HEADER_LINE")
   private String headerLine;

   /** */
   @Column(name = "SUBJECT")
   private String subject;

   /** */
   @Enumerated(EnumType.STRING)
   @Column(name = "MAIL_DISPATCH_MODE")
   private MailDispatchMode mailDispatchMode;

   /** */
   @Enumerated(EnumType.STRING)
   @Column(name = "MAIL_ATTACHMENT_MODE")
   private MailAttachmentMode mailAttachmentMode;

   /**
    * Default constructor.
    */
   public MailDiffusion() {
      super();
   }

   /**
    * @return the address
    */
   public String getAddress() {
      return address;
   }

   /**
    * @param address
    *            the address to set
    */
   public void setAddress(String address) {
      this.address = address;
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
    * @return the headerLine
    */
   public String getHeaderLine() {
      return headerLine;
   }

   /**
    * @param headerLine
    *            the headerLine to set
    */
   public void setHeaderLine(String headerLine) {
      this.headerLine = headerLine;
   }

   /**
    * @return the subject
    */
   public String getSubject() {
      return subject;
   }

   /**
    * @param subject
    *            the subject to set
    */
   public void setSubject(String subject) {
      this.subject = subject;
   }

   /**
    * @return the mailDispatchMode
    */
   public MailDispatchMode getMailDispatchMode() {
      return mailDispatchMode;
   }

   /**
    * @param mailDispatchMode
    *            the mailDispatchMode to set
    */
   public void setMailDispatchMode(MailDispatchMode mailDispatchMode) {
      this.mailDispatchMode = mailDispatchMode;
   }

   /**
    * @return the mailAttachmentMode
    */
   public MailAttachmentMode getMailAttachmentMode() {
      return mailAttachmentMode;
   }

   /**
    * @param mailAttachmentMode
    *            the mailAttachmentMode to set
    */
   public void setMailAttachmentMode(MailAttachmentMode mailAttachmentMode) {
      this.mailAttachmentMode = mailAttachmentMode;
   }

}
