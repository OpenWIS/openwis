/**
 *
 */
package org.openwis.dataservice.common.domain.bean;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 */
@XmlRootElement(name = "MessageStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageStatus implements Serializable {

   /** The message. */
   @XmlElement
   private String message;

   /** The status. */
   @XmlElement
   private Status status;

   /**
    * Instantiates a new message status.
    */
   public MessageStatus() {
      super();
   }

   @Override
   public String toString() {
      return MessageFormat.format("[{0}] {1}", status, message);
   }

   /**
    * Gets the message.
    * @return the message.
    */
   public String getMessage() {
      return message;
   }

   /**
    * Sets the message.
    * @param message the message to set.
    */
   public void setMessage(String message) {
      this.message = message;
   }

   /**
    * Gets the status.
    * @return the status.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * Sets the status.
    * @param status the status to set.
    */
   public void setStatus(Status status) {
      this.status = status;
   }

}
