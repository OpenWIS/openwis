/**
 * 
 */
package org.openwis.metadataportal.services.common.json;

/**
 * A DTO to acknowledge of the result of the operation. <P>
 * Explanation goes here. <P>
 * 
 */
public class AcknowledgementDTO {

   /**
    * <code>true</code> if the result of the operation is OK, <code>false</code> otherwise.
    */
   private boolean ok;

   /**
    * An object for complementary information.
    */
   private Object o;

   /**
    * Default constructor.
    * Builds a AcknowledgementDTO.
    */
   public AcknowledgementDTO() {
      super();
   }

   /**
    * Default constructor.
    * Builds a AcknowledgementDTO.
    * @param ok
    */
   public AcknowledgementDTO(boolean ok) {
      super();
      this.ok = ok;
   }

   /**
    * Default constructor.
    * Builds a AcknowledgementDTO.
    * @param ok
    * @param o
    */
   public AcknowledgementDTO(boolean ok, Object o) {
      super();
      this.ok = ok;
      this.o = o;
   }

   /**
    * Gets the ok.
    * @return the ok.
    */
   public boolean isOk() {
      return ok;
   }

   /**
    * Sets the ok.
    * @param ok the ok to set.
    */
   public void setOk(boolean ok) {
      this.ok = ok;
   }

   /**
    * Gets the o.
    * @return the o.
    */
   public Object getO() {
      return o;
   }

   /**
    * Sets the o.
    * @param o the o to set.
    */
   public void setO(Object o) {
      this.o = o;
   }
}
