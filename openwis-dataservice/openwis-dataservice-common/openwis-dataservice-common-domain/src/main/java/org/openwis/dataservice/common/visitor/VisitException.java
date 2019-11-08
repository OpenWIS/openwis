/**
 * 
 */
package org.openwis.dataservice.common.visitor;

/**
 * The Class VisitException. <P>
 * Explanation goes here. <P>
 */
public class VisitException extends Exception {

   /**
    * Default constructor.
    * Builds a VisitException.
    */
   public VisitException() {
      super();
   }

   /**
    * Default constructor.
    * Builds a VisitException.
    *
    * @param message the message
    * @param cause the cause
    */
   public VisitException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Default constructor.
    * Builds a VisitException.
    *
    * @param message the message
    */
   public VisitException(String message) {
      super(message);
   }

   /**
    * Default constructor.
    * Builds a VisitException.
    *
    * @param cause the cause
    */
   public VisitException(Throwable cause) {
      super(cause);
   }

}
