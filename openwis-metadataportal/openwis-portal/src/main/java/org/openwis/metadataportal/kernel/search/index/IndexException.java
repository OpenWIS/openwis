package org.openwis.metadataportal.kernel.search.index;

/**
 * The Class IndexException. <P>
 * Explanation goes here. <P>
 */
public class IndexException extends Exception {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 7415312020721228658L;

   /**
    * Instantiates a new index exception.
    */
   public IndexException() {
      super();
   }

   /**
    * Instantiates a new index exception.
    *
    * @param message the message
    */
   public IndexException(String message) {
      super(message);
   }

   /**
    * Instantiates a new index exception.
    *
    * @param cause the cause
    */
   public IndexException(Throwable cause) {
      super(cause);
   }

   /**
    * Instantiates a new index exception.
    *
    * @param message the message
    * @param cause the cause
    */
   public IndexException(String message, Throwable cause) {
      super(message, cause);
   }

}
