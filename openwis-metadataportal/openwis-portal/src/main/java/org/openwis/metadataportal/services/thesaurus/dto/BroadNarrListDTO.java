/**
 * 
 */
package org.openwis.metadataportal.services.thesaurus.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * The BroadNarrListDTO. <P>
 * Explanation goes here. <P>
 * 
 */
public class BroadNarrListDTO {

   /**
    * The keywordType.
    */
   private String keywordType;

   /**
    * The keywordListDTO.
    */
   private List<KeywordDTO> keywordListDTO;

   /**
    * Gets the keywordType.
    * @return the keywordType.
    */
   public String getKeywordType() {
      return keywordType;
   }
   
   /**
    * Sets the keywordType.
    * @param keywordType the keywordType to set.
    */
   public void setKeywordType(String keywordType) {
      this.keywordType = keywordType;
   }

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
