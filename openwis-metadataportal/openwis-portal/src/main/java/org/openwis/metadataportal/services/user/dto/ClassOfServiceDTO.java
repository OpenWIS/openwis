/**
 * 
 */
package org.openwis.metadataportal.services.user.dto;

/**
 * Class Of Service DTO. <P>
 * 
 */
public class ClassOfServiceDTO {

   /**
    * The class of service Id.
    * @member: id
    */
   private String id;
   
   /**
    * The class of service Name.
    * @member: name
    */
   private String name;

   /**
    * Gets the id.
    * @return the id.
    */
   public String getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * Gets the name.
    * @return the name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * @param name the name to set.
    */
   public void setName(String name) {
      this.name = name;
   }
}
