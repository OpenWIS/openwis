/**
 * 
 */
package org.openwis.metadataportal.services.group.dto;

/**
 * Class for know the state of the synchronization : prepare or perform.
 * 
 */
public class SynchronizeDTO {

   /**
    * True if the synchronization is performed, false if it is prepared.
    * @member: isPerform
    */
   private boolean perform;

   /**
    * Gets the isPerform.
    * @return the isPerform.
    */
   public boolean isPerform() {
      return perform;
   }

   /**
    * Sets the isPerform.
    * @param isSyncPerform the isPerform to set.
    */
   public void setPerform(boolean isSyncPerform) {
      this.perform = isSyncPerform;
   }
}
