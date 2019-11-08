/**
 * 
 */
package org.openwis.metadataportal.services.harvest.dto;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class ActivationDTO {

   private Integer id;

   private boolean activate;

   /**
    * Gets the id.
    * @return the id.
    */
   public Integer getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(Integer id) {
      this.id = id;
   }

   /**
    * Gets the activate.
    * @return the activate.
    */
   public boolean isActivate() {
      return activate;
   }

   /**
    * Sets the activate.
    * @param activate the activate to set.
    */
   public void setActivate(boolean activate) {
      this.activate = activate;
   }
}
