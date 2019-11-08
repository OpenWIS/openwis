/**
 * 
 */
package org.openwis.dataservice.common.exception;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class OpenWisException extends RuntimeException {

   /**
    * Instantiates a new open wis exception.
    */
   public OpenWisException() {
      super();
   }

   /**
    * Instantiates a new open wis exception.
    *
    * @param message the message
    */
   public OpenWisException(String message) {
      super(message);
   }

   /**
    * Instantiates a new open wis exception.
    *
    * @param message the message
    * @param cause the cause
    */
   public OpenWisException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Instantiates a new open wis exception.
    *
    * @param cause the cause
    */
   public OpenWisException(Throwable cause) {
      super(cause);
   }

}
