package org.fao.geonet.services.util.z3950;

/**
 * The Class SRUArgumentParseException.
 */
public class SRUArgumentParseException extends Exception {

   /** The type. */
   private final String type;

   /** The val. */
   private final String val;

   /**
    * Instantiates a new sRU argument parse exception.
    *
    * @param type the type
    * @param val the val
    * @param e the e
    */
   public SRUArgumentParseException(String type, String val, Exception e) {
      super(e);
      this.val = val;
      this.type = type;
   }

   /**
    * Gets the type.
    *
    * @return the type
    */
   public String getType() {
      return type;
   }

   /**
    * Gets the val.
    *
    * @return the val
    */
   public String getVal() {
      return val;
   }
}
