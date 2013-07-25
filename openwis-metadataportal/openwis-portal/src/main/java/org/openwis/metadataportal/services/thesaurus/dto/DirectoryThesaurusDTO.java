/**
 * 
 */
package org.openwis.metadataportal.services.thesaurus.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * The Directory Thesaurus DTO. <P>
 * Explanation goes here. <P>
 * 
 */
public class DirectoryThesaurusDTO {

   /**
    * The label.
    */
   private String label;

   /**
    * The thesaurusListDTO.
    */
   private List<ThesaurusDTO> thesaurusListDTO;

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

   /**
    * Gets the thesaurusListDTO.
    * @return the thesaurusListDTO.
    */
   public List<ThesaurusDTO> getThesaurusListDTO() {
      if (thesaurusListDTO == null)
      {
         thesaurusListDTO = new ArrayList<ThesaurusDTO>();
      }
      return thesaurusListDTO;
   }

   /**
    * Sets the thesaurusListDTO.
    * @param thesaurusListDTO the thesaurusListDTO to set.
    */
   public void setThesaurusListDTO(List<ThesaurusDTO> thesaurusListDTO) {
      this.thesaurusListDTO = thesaurusListDTO;
   }
}
