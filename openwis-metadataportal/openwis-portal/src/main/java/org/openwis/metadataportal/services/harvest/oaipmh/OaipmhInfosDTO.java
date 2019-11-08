/**
 * 
 */
package org.openwis.metadataportal.services.harvest.oaipmh;

import java.util.ArrayList;
import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class OaipmhInfosDTO {

   private List<String> formats;

   private List<SetDTO> sets;

   private List<String> compressions;

   private boolean deletionSupport;

   /**
    * Gets the formats.
    * @return the formats.
    */
   public List<String> getFormats() {
      if (formats == null) {
         formats = new ArrayList<String>();
      }
      return formats;
   }

   /**
    * Sets the formats.
    * @param formats the formats to set.
    */
   public void setFormats(List<String> formats) {
      this.formats = formats;
   }

   /**
    * Gets the sets.
    * @return the sets.
    */
   public List<SetDTO> getSets() {
      if (sets == null) {
         sets = new ArrayList<SetDTO>();
      }
      return sets;
   }

   /**
    * Sets the sets.
    * @param sets the sets to set.
    */
   public void setSets(List<SetDTO> sets) {
      this.sets = sets;
   }

   /**
    * Gets the compressions.
    * @return the compressions.
    */
   public List<String> getCompressions() {
      if (compressions == null) {
         compressions = new ArrayList<String>();
      }
      return compressions;
   }

   /**
    * Sets the compressions.
    * @param compressions the compressions to set.
    */
   public void setCompressions(List<String> compressions) {
      this.compressions = compressions;
   }

   /**
    * Gets the deletionSupport.
    * @return the deletionSupport.
    */
   public boolean isDeletionSupport() {
      return deletionSupport;
   }

   /**
    * Sets the deletionSupport.
    * @param deletionSupport the deletionSupport to set.
    */
   public void setDeletionSupport(boolean deletionSupport) {
      this.deletionSupport = deletionSupport;
   }

}
