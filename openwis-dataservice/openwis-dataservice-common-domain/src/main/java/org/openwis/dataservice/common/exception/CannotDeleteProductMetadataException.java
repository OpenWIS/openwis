package org.openwis.dataservice.common.exception;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.WebFault;

/**
 * The Class CannotDeleteProductMetadataException.
 */
@WebFault(name = "CannotDeleteProductMetadataException")
@XmlAccessorType(XmlAccessType.FIELD)
public class CannotDeleteProductMetadataException extends Exception {

   /** The urn. */
   private final String urn;

   /**
    * Instantiates a new cannot delete product metadata exception.
    *
    * @param urn the urn
    */
   public CannotDeleteProductMetadataException(String urn) {
      this(urn, null);
   }

   /**
    * Instantiates a new cannot delete product metadata exception.
    *
    * @param id the id
    * @param cause the cause
    */
   public CannotDeleteProductMetadataException(String urn, Throwable cause) {
      super(cause);
      this.urn = urn;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Throwable#toString()
    */
   @Override
   public String toString() {
      String msg;
      Throwable cause = getCause();
      if (cause != null) {
         msg = MessageFormat.format("Cannot delete the {0} product metadata! (Caused by: {1})",
               urn, cause);
      } else {
         msg = MessageFormat.format("Cannot delete the {0} product metadata!", urn);
      }
      return msg;
   }

   /**
    * Gets the urn.
    *
    * @return the urn
    */
   public String getUrn() {
      return urn;
   }

}
