package org.openwis.metadataportal.search.solr.spatial;

/**
 * The Class GeometryException. <P>
 * Explanation goes here. <P>
 */
public class GeometryException extends RuntimeException {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -4429657910166791365L;

   /**
    * Instantiates a new geometry exception.
    *
    * @param message the message
    */
   public GeometryException(String message) {
      super(message);
   }

   /**
    * Instantiates a new geometry exception.
    *
    * @param cause the cause
    */
   public GeometryException(Throwable cause) {
      super(cause);
   }
}
