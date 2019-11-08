package org.openwis.metadataportal.kernel.search.query;

/**
 * The Class SearchException. <P>
 * Explanation goes here. <P>
 */
public class SearchException extends Exception {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 3714777464647807994L;

   /**
    * Instantiates a new search exception.
    */
   public SearchException() {
      super();
   }

   /**
    * Instantiates a new search exception.
    *
    * @param message the message
    */
   public SearchException(String message) {
      super(message);
   }

   /**
    * Instantiates a new search exception.
    *
    * @param cause the cause
    */
   public SearchException(Throwable cause) {
      super(cause);
   }

   /**
    * Instantiates a new search exception.
    *
    * @param message the message
    * @param cause the cause
    */
   public SearchException(String message, Throwable cause) {
      super(message, cause);
   }

}
