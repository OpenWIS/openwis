/**
 * 
 */
package org.openwis.metadataportal.services.thesaurus.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class KeywordsDTO {

   /**
    * The keywordListDTO.
    */
   private List<KeywordDTO> keywordListDTO;

   /**
    * Gets the keywordListDTO.
    * @return the keywordListDTO.
    */
   public List<KeywordDTO> getKeywordListDTO() {
      if (keywordListDTO == null)
      {
         keywordListDTO = new ArrayList<KeywordDTO>();
      }
      return keywordListDTO;
   }

   /**
    * Sets the keywordListDTO.
    * @param keywordListDTO the keywordListDTO to set.
    */
   public void setKeywordListDTO(List<KeywordDTO> keywordListDTO) {
      this.keywordListDTO = keywordListDTO;
   }
}
