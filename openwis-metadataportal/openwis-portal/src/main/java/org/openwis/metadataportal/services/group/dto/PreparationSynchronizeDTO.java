/**
 * 
 */
package org.openwis.metadataportal.services.group.dto;

import java.util.List;

/**
 * Preparation Synchronize DTO. <P>
 * 
 */
public class PreparationSynchronizeDTO {

   /**
    * List of all preparation of synchronization
    * @member: prepSynchro
    */
   private List<String> prepSynchro;

   /**
    * Gets the prepSynchro.
    * @return the prepSynchro.
    */
   public List<String> getPrepSynchro() {
      return prepSynchro;
   }

   /**
    * Sets the prepSynchro.
    * @param prepSynchro the prepSynchro to set.
    */
   public void setPrepSynchro(List<String> prepSynchro) {
      this.prepSynchro = prepSynchro;
   }
}
