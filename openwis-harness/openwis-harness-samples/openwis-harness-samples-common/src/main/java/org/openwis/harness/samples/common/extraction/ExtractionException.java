/**
 * 
 */
package org.openwis.harness.samples.common.extraction;

/**
 * An Extraction Exception. <P>
 * Explanation goes here. <P>
 * 
 */
public class ExtractionException extends Exception {

   /**
    * Default constructor.
    * Builds a ExtractionException.
    */
   public ExtractionException() {
      super();
   }

   /**
    * Builds a ExtractionException.
    *
    * @param message the message
    */
   public ExtractionException(String message) {
      super(message);
   }

   /**
    * Builds a ExtractionException.
    *
    * @param cause the cause
    */
   public ExtractionException(Throwable cause) {
      super(cause);
   }

   /**
    * Builds a ExtractionException.
    *
    * @param message the message
    * @param cause the cause
    */
   public ExtractionException(String message, Throwable cause) {
      super(message, cause);
   }

}
