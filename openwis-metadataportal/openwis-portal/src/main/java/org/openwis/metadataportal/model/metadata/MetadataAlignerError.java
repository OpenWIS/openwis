/**
 * 
 */
package org.openwis.metadataportal.model.metadata;

import java.text.MessageFormat;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetadataAlignerError {

   private String urn;

   private String message;

   /**
    * Default constructor.
    * Builds a MetadataAlignerError.
    * @param urn
    * @param message
    */
   public MetadataAlignerError(String urn, String message) {
      super();
      this.urn = urn;
      this.message = message.replaceAll("\"", "&#148;");
   }

   /**
    * Gets the urn.
    * @return the urn.
    */
   public String getUrn() {
      return urn;
   }

   /**
    * Sets the urn.
    * @param urn the urn to set.
    */
   public void setUrn(String urn) {
      this.urn = urn;
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
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("{0}##{1}", getUrn(), getMessage());
   }
   
   
}
