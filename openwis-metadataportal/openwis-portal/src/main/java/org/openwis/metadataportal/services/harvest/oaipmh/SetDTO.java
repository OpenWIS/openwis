/**
 * 
 */
package org.openwis.metadataportal.services.harvest.oaipmh;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SetDTO {

   private String name;

   private String label;

   /**
    * Default constructor.
    * Builds a SetDTO.
    * @param name
    * @param label
    */
   public SetDTO(String name, String label) {
      super();
      this.name = name;
      this.label = label;
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

   /**
    * Gets the label.
    * @return the label.
    */
   public String getLabel() {
      return label;
   }

   /**
    * Sets the label.
    * @param label the label to set.
    */
   public void setLabel(String label) {
      this.label = label;
   }
}
