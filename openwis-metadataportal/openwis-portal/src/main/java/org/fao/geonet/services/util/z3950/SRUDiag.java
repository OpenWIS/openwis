package org.fao.geonet.services.util.z3950;

/**
 * The Class SRUDiag.
 */
public class SRUDiag {

   /** The url. */
   private final String url;

   /** The details. */
   private final String details;

   /** The message. */
   private final String message;

   /**
    * Instantiates a new sRU diag.
    *
    * @param url the url
    * @param message the message
    * @param details the details
    */
   public SRUDiag(String url, String message, String details) {
      this.url = url;
      this.details = details;
      this.message = message;
   }

   /**
    * Gets the url.
    *
    * @return the url
    */
   public String getUrl() {
      return url;
   }

   /**
    * Gets the details.
    *
    * @return the details
    */
   public String getDetails() {
      return details;
   }

   /**
    * Gets the message.
    *
    * @return the message
    */
   public String getMessage() {
      return message;
   }

}
